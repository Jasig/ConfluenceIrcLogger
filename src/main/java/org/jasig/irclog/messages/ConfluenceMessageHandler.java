/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.messages;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.Page;
import org.jasig.irclog.config.ConfluenceServer;

/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class ConfluenceMessageHandler implements MessageHandler {
    protected Log logger = LogFactory.getLog(this.getClass());

    private Map<String, String> messageEscapePairs;
    private ConfluenceServer confluenceServer;
    private String spaceKey;
    private List<SimpleDateFormat> logPagesTitleFormats;
    private String childPageLinkFormat = "'{'children:depth=1'}'";
    
    public ConfluenceMessageHandler() {
        this.messageEscapePairs = new HashMap<String, String>();
        this.messageEscapePairs.put("\\[", "\\\\[");
        this.messageEscapePairs.put("\\]", "\\\\]");
    }
    
    /**
     * @return the messageEscapePairs
     */
    public Map<String, String> getMessageEscapePairs() {
        return this.messageEscapePairs;
    }
    /**
     * @param messageEscapePairs the messageEscapePairs to set
     */
    public void setMessageEscapePairs(Map<String, String> messageEscapePairs) {
        this.messageEscapePairs = messageEscapePairs;
    }
    /**
     * @return the confluenceServer
     */
    public ConfluenceServer getConfluenceServer() {
        return this.confluenceServer;
    }
    /**
     * @param confluenceServer the confluenceServer to set
     */
    public void setConfluenceServer(ConfluenceServer confluenceServer) {
        this.confluenceServer = confluenceServer;
    }
    /**
     * @return the spaceKey
     */
    public String getSpaceKey() {
        return this.spaceKey;
    }
    /**
     * @param spaceKey the spaceKey to set
     */
    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }
    /**
     * @return the childPageLinkFormat
     */
    public String getChildPageLinkFormat() {
        return childPageLinkFormat;
    }

    /**
     * A MessageFormat compatible string. Three arguments are passed to the formatter
     * {0} = Date (time of formatting)
     * {1} = Parent page title
     * {2} = Child page title
     * 
     * @param childPageLinkFormat the childPageLinkFormat to set
     */
    public void setChildPageLinkFormat(String childPageLinkFormat) {
        this.childPageLinkFormat = childPageLinkFormat;
    }

    /**
     * @return the logPagesTitleFormats
     */
    public List<String> getLogPagesTitleFormats() {
        final List<String> stringFormats = new ArrayList<String>(this.logPagesTitleFormats.size());
        
        for (final SimpleDateFormat dateFormat : this.logPagesTitleFormats) {
            stringFormats.add(dateFormat.toPattern());
        }
        
        return stringFormats;
    }
    /**
     * @param logPagesTitleFormats the logPagesTitleFormats to set
     */
    public void setLogPagesTitleFormats(List<String> logPagesTitleFormats) {
        Validate.notNull(logPagesTitleFormats, "logPagesTitleFormats List can not be null");
        
        final List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>(logPagesTitleFormats.size());
        
        for (final String stringFormat : logPagesTitleFormats) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(stringFormat);
            dateFormats.add(dateFormat);
        }
        
        this.logPagesTitleFormats = dateFormats;
    }
    
    
    /**
     * @see org.jasig.irclog.messages.MessageHandler#handleMessages(java.lang.String[])
     */
    public void handleMessages(String... messages) {
        if (messages == null || messages.length == 0) {
            return;
        }
        
        try {
            final Confluence confluence = new Confluence(this.confluenceServer.getEndpoint());
            confluence.login(this.confluenceServer.getUserName(), this.confluenceServer.getPassword());
            
            //Generate target log page & parent tree
            final Date now = new Date();
            Page parentPage = null;
            Page targetPage = null;
            for (final SimpleDateFormat pageTitleDateFormat : this.logPagesTitleFormats) {
                parentPage = targetPage;
                
                final String pageName;
                synchronized (pageTitleDateFormat) { //The SimpleDateFormat is not threadsafe :(
                    pageName = pageTitleDateFormat.format(now);
                }
                
                try {
                    targetPage = confluence.getPage(this.spaceKey, pageName);
                }
                catch (Exception e) {
                    //Assume this means the page doesn't exist
                    targetPage = null;
                }
                
                if (targetPage == null) {
                    targetPage = this.createLogPage(confluence, parentPage, pageName);
                }
            }
            
            final String content = targetPage.getContent();
            final StringBuilder contentBuilder;
            if (content == null) {
                contentBuilder = new StringBuilder();
            }
            else {
                contentBuilder = new StringBuilder(content);
            }
            
            for (final String message : messages) {
                final String escapedMessage = this.escapeMessage(message);
                contentBuilder.append(escapedMessage).append("\n");
            }
            
            targetPage.setContent(contentBuilder.toString());
        
            confluence.storePage(targetPage);
        
            confluence.logout();
        }
        catch (Exception e) {
            if (messages.length == 1) {
                this.logger.error("An exception occured while attempting to update Confluence with message '" + messages[0] + "'", e);
            }
            else {
                this.logger.warn("An exception occured while attempting to update Confluence with messages '" + Arrays.asList(messages) + "'. The updates will be attempted individually.", e);
                
                for (final String message : messages) {
                    this.handleMessages(message);
                }
            }
        }
    }
    
    /**
     * @see org.jasig.irclog.messages.MessageHandler#flush()
     */
    public void flush() {
        //noop
    }
    
    protected String escapeMessage(String message) {
        for (final Map.Entry<String, String> entry : this.messageEscapePairs.entrySet()) {
            message = message.replaceAll(entry.getKey(), entry.getValue());
        }
        
        return message;
    }

    protected Page createLogPage(final Confluence confluence, final Page parent, final String pageName) throws Exception {
        @SuppressWarnings("unchecked")
        Page page = new Page(new HashMap());
        
        page.setTitle(pageName);
        page.setSpace(this.spaceKey);
        if (parent != null) {
            page.setParentId(parent.getId());
        }
        
        page = confluence.storePage(page);
        
        if (parent != null) {
            this.addLinkToChild(confluence, parent, pageName);
        }
        
        return page;
    }

    protected void addLinkToChild(final Confluence confluence, final Page parent, final String pageName) throws Exception {
        final String content = parent.getContent();
        
        final StringBuilder contentBuilder;
        if (content == null) {
            contentBuilder = new StringBuilder();
        }
        else {
            contentBuilder = new StringBuilder(content);
        }
        
        //Date, parent name, child name
        
        if (this.childPageLinkFormat != null) {
            final String childLink = MessageFormat.format(this.childPageLinkFormat, new Date(), parent.getTitle(), pageName);
            
            if (contentBuilder.indexOf(childLink) < 0) {
                contentBuilder.append(childLink);
            }
            
            parent.setContent(contentBuilder.toString());
            
            confluence.storePage(parent);
        }
    }
}
