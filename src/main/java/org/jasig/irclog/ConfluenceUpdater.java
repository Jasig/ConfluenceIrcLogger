/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.irclog;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.jasig.irclog.events.IrcEvent;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ConfluenceUpdater implements WikiUpdater, InitializingBean {
    @SuppressWarnings("serial")
    private static final Map<Pattern, String> MESSAGE_ESCAPING = Collections.unmodifiableMap(new LinkedHashMap<Pattern, String>() { {
        this.put(Pattern.compile(Pattern.quote("&")), "&amp;");
        this.put(Pattern.compile(Pattern.quote("<")), "&lt;");
        this.put(Pattern.compile(Pattern.quote(">")), "&gt;");
        this.put(Pattern.compile(Pattern.quote("\"")), "&#034;");
        this.put(Pattern.compile(Pattern.quote("'")), "&#039;");
        this.put(Pattern.compile(Pattern.quote("[")), "\\\\[");
        this.put(Pattern.compile(Pattern.quote("]")), "\\\\]");
    }});
    
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private XmlRpcClient client;
    
    private EventFormatter eventFormatter;
    private String rpcEndpoint;
    private String username;
    private String password;
    
    public void setRpcEndpoint(String rpcEndpoint) {
        this.rpcEndpoint = rpcEndpoint;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setEventFormatter(EventFormatter eventFormatter) {
        this.eventFormatter = eventFormatter;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        final XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
        clientConfig.setServerURL(new URL(this.rpcEndpoint));
        
        this.client = new XmlRpcClient();
        this.client.setConfig(clientConfig);
        
        //TODO eventually use httpclient via XmlRpcCommonsTransportFactory
    }
    
    @Override
    public void update(List<IrcEvent> events, String spaceKey, List<String> pageNames) {
        final List<PageHolder> pageHolders = new ArrayList<PageHolder>(pageNames.size());
        for (final String stringFormat : pageNames) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(stringFormat);
            pageHolders.add(new PageHolder(dateFormat));
        }
        
        final String token = this.call("login", this.username, this.password);
        
        final Map<String, Map<String, Object>> dirtyPages = new LinkedHashMap<String, Map<String,Object>>();
        for (final IrcEvent event : events) {
            //Get the page to log the event to
            final Map<String, Object> page = this.getPageForEvent(token, spaceKey, pageHolders, event);
            if (page == null) {
                logger.warn("No page found or created in " + spaceKey + " for: " + pageHolders + ". Skipping event: " + event);
                continue;
            }
            
            //Update the page content for the event
            final String content = (String)page.get("content");
            String message = this.eventFormatter.formatEvent(event);
            for (final Map.Entry<Pattern, String> patternEntry : MESSAGE_ESCAPING.entrySet()) {
                final Pattern pattern = patternEntry.getKey();
                final String replacement = patternEntry.getValue();
                message = pattern.matcher(message).replaceAll(replacement);
            }
            page.put("content", content + message + "\n");
            
            //Track the pages that have been modified to minimize updates
            dirtyPages.put((String)page.get("title"), page);
        }

        //Store all the pages that have been updated
        for (final Map<String, Object> page : dirtyPages.values()) {
            this.call("updatePage", token, page, new Hashtable<String, Object>());
        }
        
        this.call("logout", token);
    }

    private Map<String, Object> getPageForEvent(String token, String spaceKey, final List<PageHolder> pageHolders, final IrcEvent event) {
        PageHolder parentPageHolder = null;
        for (final Iterator<PageHolder> pageHolderItr = pageHolders.iterator(); pageHolderItr.hasNext(); ) {
            final PageHolder pageHolder = pageHolderItr.next();
            
            final String title = pageHolder.titleFormat.format(new Date(event.getDate()));

            //No page or page title doesn't match what is currently in the List
            if (pageHolder.page == null || !title.equalsIgnoreCase((String)pageHolder.page.get("title"))) {
                //Try getting an existing page for the title
                Map<String, Object> page = this.call("getPage", token, spaceKey, title);
                if (page != null) {
                    pageHolder.page = page;
                }
                //Page didn't exist create it
                else {
                    pageHolder.page = this.createPage(token, spaceKey, title, parentPageHolder, pageHolderItr.hasNext());
                }
            }
            
            parentPageHolder = pageHolder;
        }
        
        if (parentPageHolder != null) {
            return parentPageHolder.page;
        }
        
        return null;
    }
    
    private Map<String, Object> createPage(String token, String spaceKey, String title, PageHolder parentPageHolder, boolean hasChild) {
        Map<String, Object> page = new Hashtable<String, Object>();
        page.put("space", spaceKey);
        page.put("title", title);
        
        // This page has a parent, setup the reference
        if (parentPageHolder != null) {
            page.put("parentId", parentPageHolder.page.get("id"));
        }
        
        // This page has children and link macro
        if (hasChild) {
            page.put("content", "{children:depth=1}");
        }
        
        return this.call("storePage", token, page);
    }
    
    @SuppressWarnings("unchecked")
    private <T> T call(String method, Object... args) {
        final List<Object> argList = Arrays.asList(args);
        try {
            this.logger.trace("Calling " + method + " on " + this.rpcEndpoint + " with " + argList);
            return (T)this.client.execute("confluence1." + method, new Vector<Object>(argList));
        }
        catch (XmlRpcException e) {
            //Check for page not existing exception and return null
            if (e.getMessage().contains("You're not allowed to view that page, or it does not exist")) {
                return null;
            }
            
            throw new RuntimeException("Call to " + method + " on " + this.rpcEndpoint + " with " + argList + " failed with code: " + e.code, e);
        }
    }

    
    private static class PageHolder {
        private final SimpleDateFormat titleFormat;
        private Map<String, Object> page;

        public PageHolder(SimpleDateFormat titleFormat) {
            this.titleFormat = titleFormat;
        }

        @Override
        public String toString() {
            return "PageHolder [titleFormat=" + this.titleFormat + ", page=" + this.page + "]";
        }
    }
}
