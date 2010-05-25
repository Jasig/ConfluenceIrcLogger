/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events.handlers;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.irclog.events.ConnectEvent;
import org.jasig.irclog.events.DisconnectEvent;
import org.jasig.irclog.events.IrcEvent;
import org.jasig.irclog.events.KickEvent;
import org.jasig.irclog.events.QuitEvent;
import org.jasig.irclog.events.TargetedEvent;
import org.jibble.pircbot.PircBot;

/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class ChannelManager implements IrcEventHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private Set<String> channels = new HashSet<String>(0);
    private long reconnectDelay = 1000;
    private volatile boolean quit = false;
    
    /**
     * @return the channels
     */
    public Set<String> getChannels() {
        return this.channels;
    }
    /**
     * @param channels the channels to set
     */
    public void setChannels(Set<String> channels) {
        Validate.notNull(channels, "Channels Set can not be null");
        this.channels = channels;
    }
    /**
     * @param channel Adds this channel to the set of channels
     */
    public void addChannel(String channel) {
        this.channels.add(channel);
    }

    /**
     * @return the reconnectDelay
     */
    public long getReconnectDelay() {
        return this.reconnectDelay;
    }
    /**
     * @param reconnectDelay the reconnectDelay to set
     */
    public void setReconnectDelay(long reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
    }


    public void dispose() {
    }
    
    /**
     * @see org.jasig.irclog.events.handlers.IrcEventHandler#handleEvent(org.jasig.irclog.events.IrcEvent)
     */
    public void handleEvent(IrcEvent event) {
        final PircBot bot = event.getSource();
        
        //Join configured channels on connect
        if (event instanceof ConnectEvent) {
            for (final String channel : this.channels) {
                this.logger.info("Joining '" + channel + "'");
                bot.joinChannel(channel);
            }
        }
        else if (event instanceof QuitEvent) {
            final TargetedEvent targetedEvent = (TargetedEvent)event;
            final String target = targetedEvent.getTarget();
            if (bot.getNick().equals(target) || bot.getName().equals(target)) {
                this.logger.info("Quit server, will not attempt to reconnect.");
                this.quit = true;
            }
        }
        //Rejoin the server when disconnected
        else if (!this.quit && event instanceof DisconnectEvent) {
            this.logger.info("Disconnected from server.");
            
            while (!bot.isConnected()) {
                try {
                    this.logger.info("Attempting to reconnect to server.");
                    bot.reconnect();
                }
                catch (Exception e) {
                    this.logger.error("Failed to reconnect", e);
                    
                    try {
                        Thread.sleep(this.reconnectDelay);
                    }
                    catch (InterruptedException ie) {
                        this.logger.error("Failed to wait to reconnect", ie);
                    }
                }
            }
        }
        //Rejoin a channel if kicked
        else if (event instanceof KickEvent) {
            final String nick = bot.getNick();
            final KickEvent kickEvent = (KickEvent)event;
            final String recipientNick = kickEvent.getRecipientNick();
            final String channel = kickEvent.getChannel();
            
            if (this.channels.contains(channel) && nick.equalsIgnoreCase(recipientNick)) {
                this.logger.info("Kicked from channel '" + channel + "' attempting to rejoin.");
                bot.joinChannel(kickEvent.getChannel());
            }
        }
    }
}
