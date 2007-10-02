/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events.handlers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.jasig.irclog.events.IrcEvent;
import org.jasig.irclog.events.JoinEvent;
import org.jibble.pircbot.PircBot;

/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class ChannelNotifier implements IrcEventHandler {
    private String defaultOnJoinMessage;
    private Map<String, String> onJoinMessages = new HashMap<String, String>(0);
    
    /**
     * @return the defaultOnJoinMessage
     */
    public String getDefaultOnJoinMessage() {
        return this.defaultOnJoinMessage;
    }
    /**
     * @param defaultOnJoinMessage the defaultOnJoinMessage to set
     */
    public void setDefaultOnJoinMessage(String defaultOnJoinMessage) {
        this.defaultOnJoinMessage = defaultOnJoinMessage;
    }

    /**
     * @return the onJoinMessages
     */
    public Map<String, String> getOnJoinMessages() {
        return this.onJoinMessages;
    }

    /**
     * @param onJoinMessages Channel to Message map for on-join messages
     */
    public void setOnJoinMessages(Map<String, String> onJoinMessages) {
        Validate.notNull(onJoinMessages, "OnJoinMessages Map can not be null");
        this.onJoinMessages = onJoinMessages;
    }
    /**
     * @param channel Channel to send message to
     * @param message Message to send to channel
     */
    public void addOnJoinMessage(String channel, String message) {
        this.onJoinMessages.put(channel, message);
    }

    /**
     * @see org.jasig.irclog.events.handlers.IrcEventHandler#handleEvent(org.jasig.irclog.events.IrcEvent)
     */
    public void handleEvent(IrcEvent event) {
        final PircBot bot = event.getSource();
        
        if (event instanceof JoinEvent) {
            final JoinEvent joinEvent = (JoinEvent)event;
            final String sender = joinEvent.getSender();
            final String channel = joinEvent.getChannel();
            
            final String message = this.getOnJoinMessage(channel);
            if (sender.equalsIgnoreCase(bot.getNick())) {
                bot.sendNotice(channel, message);
            }
            else {
                bot.sendNotice(sender, message);
            }
        }
    }
    
    protected String getOnJoinMessage(String channel) {
        if (this.onJoinMessages != null) {
            final String message = this.onJoinMessages.get(channel);
            
            if (message != null) {
                return message;
            }
        }
        
        return this.defaultOnJoinMessage;
    }
}
