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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.irclog.events.IrcEvent;

/**
 * EventWriter writes to a Confluence server after doing some confluence specific message escaping. Uses a configured
 * {@link EventFormatter} to convert the events to strings for writing. Also manages creating the required page structure
 * for writing events.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ConfluenceEventWriter implements EventWriter {
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
    
    private ConfluenceServer<Object> confluenceServer;
    private EventFormatter eventFormatter;
    private String spaceKey;
    private List<FastDateFormat> pageNames;


    /**
     * {@link ConfluenceServer} to use for writing the pages
     */
    public void setConfluenceServer(ConfluenceServer<Object> confluenceServer) {
        this.confluenceServer = confluenceServer;
    }
    
    /**
     * {@link EventFormatter} used to convert the {@link IrcEvent}s to Strings
     */
    public void setEventFormatter(EventFormatter eventFormatter) {
        this.eventFormatter = eventFormatter;
    }

    /**
     * The confluence space to write to
     */
    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    /**
     * {@link SimpleDateFormat} strings that are used to generate the page structure for logging. Parent pages
     * are created automatically and links to child pages are also added. The {@link IrcEvent#getDate()} value is
     * used for generate the page name strings.
     */
    public void setPageNames(List<String> pageNames) {
        this.pageNames = new ArrayList<FastDateFormat>(pageNames.size());
        
        for (final String pageName : pageNames) {
            final FastDateFormat dateFormat = FastDateFormat.getInstance(pageName);
            this.pageNames.add(dateFormat);
        }
    }
    
    @Override
    public void write(List<IrcEvent> events) {
        final List<PageHolder> pageHolders = new ArrayList<PageHolder>(this.pageNames.size());
        for (final FastDateFormat dateFormat : this.pageNames) {
            pageHolders.add(new PageHolder(dateFormat));
        }
        
        final Object token = this.confluenceServer.login();
        try {
            
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
                message = this.confluenceServer.convertWikiToStorageFormat(token, message + "\n");
                page.put("content", content + message + "\n");
                
                //Track the pages that have been modified to minimize updates
                dirtyPages.put((String)page.get("title"), page);
            }
    
            //Store all the pages that have been updated
            for (final Map<String, Object> page : dirtyPages.values()) {
                this.confluenceServer.updatePage(token, page, new HashMap<String, Object>());
            }
        }
        catch (Exception e) {
            this.logger.error("Failed to write events to Confluence", e);
        }
        finally {
            this.confluenceServer.logout(token);
        }
    }

    private Map<String, Object> getPageForEvent(Object token, String spaceKey, final List<PageHolder> pageHolders, final IrcEvent event) {
        PageHolder parentPageHolder = null;
        for (final Iterator<PageHolder> pageHolderItr = pageHolders.iterator(); pageHolderItr.hasNext(); ) {
            final PageHolder pageHolder = pageHolderItr.next();
            
            final String title = pageHolder.titleFormat.format(event.getDate());

            //No page or page title doesn't match what is currently in the List
            if (pageHolder.page == null || !title.equalsIgnoreCase((String)pageHolder.page.get("title"))) {
                //Try getting an existing page for the title
                Map<String, Object> page = this.confluenceServer.getPage(token, spaceKey, title);
                if (page != null) {
                    this.logger.trace("Found page: " + title);
                    pageHolder.page = page;
                }
                //Page didn't exist create it
                else {
                    pageHolder.page = this.createPage(token, spaceKey, title, parentPageHolder, pageHolderItr.hasNext());
                    this.logger.trace("Creating page: " + title);
                }
            }
            
            parentPageHolder = pageHolder;
        }
        
        if (parentPageHolder != null) {
            return parentPageHolder.page;
        }
        
        return null;
    }
    
    private Map<String, Object> createPage(Object token, String spaceKey, String title, PageHolder parentPageHolder, boolean hasChild) {
        Map<String, Object> page = new HashMap<String, Object>();
        page.put("space", spaceKey);
        page.put("title", title);
        
        // This page has a parent, setup the reference
        if (parentPageHolder != null) {
            page.put("parentId", parentPageHolder.page.get("id"));
        }
        
        // This page has children, child link link macro
        if (hasChild) {
            page.put("content", "{children:depth=1}");
        }
        
        return this.confluenceServer.storePage(token, page);
    }

    
    private static class PageHolder {
        private final FastDateFormat titleFormat;
        private Map<String, Object> page;

        public PageHolder(FastDateFormat titleFormat) {
            this.titleFormat = titleFormat;
        }

        @Override
        public String toString() {
            return "PageHolder [titleFormat=" + this.titleFormat + ", page=" + this.page + "]";
        }
    }
}
