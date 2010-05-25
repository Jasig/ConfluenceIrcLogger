/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.irclog.events.ActionEvent;
import org.jasig.irclog.events.ConnectEvent;
import org.jasig.irclog.events.DisconnectEvent;
import org.jasig.irclog.events.JoinEvent;
import org.jasig.irclog.events.KickEvent;
import org.jasig.irclog.events.MessageEvent;
import org.jasig.irclog.events.ModeEvent;
import org.jasig.irclog.events.NickChangeEvent;
import org.jasig.irclog.events.NoticeEvent;
import org.jasig.irclog.events.PartEvent;
import org.jasig.irclog.events.PingEvent;
import org.jasig.irclog.events.PrivateMessageEvent;
import org.jasig.irclog.events.QuitEvent;
import org.jasig.irclog.events.TimeEvent;
import org.jasig.irclog.events.TopicEvent;
import org.jasig.irclog.events.VersionEvent;
import org.jasig.irclog.events.handlers.IrcEventHandler;
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;

/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class EventLogBot extends PircBot {
    protected final Log logger = LogFactory.getLog(this.getClass());

    private IrcEventHandler eventHandler = null;
    private boolean stripFormatting = true;
    
    /**
     * @return the eventHandler
     */
    public IrcEventHandler getEventHandler() {
        return this.eventHandler;
    }
    /**
     * @param eventHandler the eventHandler to set
     */
    public void setEventHandler(IrcEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    /**
     * @return the stripFormatting
     */
    public boolean isStripFormatting() {
        return this.stripFormatting;
    }
    /**
     * @param stripFormatting the stripFormatting to set
     */
    public void setStripFormatting(boolean stripColors) {
        this.stripFormatting = stripColors;
    }
    
    
    /**
     * @see #setName(String) 
     */
    public void setBotName(String name) {
        this.setName(name);
    }
    
    
    @Override
    public synchronized void dispose() {
        this.eventHandler.dispose();
        super.dispose();
    }
    /**
     * @see org.jibble.pircbot.PircBot#onConnect()
     */
    @Override
    protected void onConnect() {
        final ConnectEvent event = new ConnectEvent(this);
        this.eventHandler.handleEvent(event);
    }

    /**
     * @see org.jibble.pircbot.PircBot#onAction(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onAction(String sender, String login, String hostname, String target, String action) {
        final ActionEvent event = new ActionEvent(this, sender, login, hostname, target, action);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onJoin(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onJoin(String channel, String sender, String login, String hostname) {
        final JoinEvent event = new JoinEvent(this, channel, sender, login, hostname);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        if (this.stripFormatting) {
            message = Colors.removeFormattingAndColors(message);
        }
        
        final MessageEvent event = new MessageEvent(this, channel, sender, login, hostname, message);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onMode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
        final ModeEvent event = new ModeEvent(this, channel, sourceNick, sourceLogin, sourceHostname, mode);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onNickChange(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
        final NickChangeEvent event = new NickChangeEvent(this, oldNick, login, hostname, newNick);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onNotice(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
        final NoticeEvent event = new NoticeEvent(this, sourceNick, sourceLogin, sourceHostname, target, notice);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onPart(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onPart(String channel, String sender, String login, String hostname) {
        final PartEvent event = new PartEvent(this, channel, sender, login, hostname);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onPing(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onPing(String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue) {
        final PingEvent event = new PingEvent(this, sourceNick, sourceLogin, sourceHostname, target, pingValue);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onPrivateMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        if (this.stripFormatting) {
            message = Colors.removeFormattingAndColors(message);
        }
        
        final PrivateMessageEvent event = new PrivateMessageEvent(this, sender, login, hostname, message);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onQuit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        final QuitEvent event = new QuitEvent(this, sourceNick, sourceLogin, sourceHostname, reason);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onTime(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onTime(String sourceNick, String sourceLogin, String sourceHostname, String target) {
        final TimeEvent event = new TimeEvent(this, sourceNick, sourceLogin, sourceHostname, target);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onTopic(java.lang.String, java.lang.String, java.lang.String, long, boolean)
     */
    @Override
    protected void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
        final TopicEvent event = new TopicEvent(this, channel, topic, setBy, date, changed);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onVersion(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onVersion(String sourceNick, String sourceLogin, String sourceHostname, String target) {
        final VersionEvent event = new VersionEvent(this, sourceNick, sourceLogin, sourceHostname, target);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onKick(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        final KickEvent event = new KickEvent(this, channel, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
        this.eventHandler.handleEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onDisconnect()
     */
    @Override
    protected void onDisconnect() {
        final DisconnectEvent event = new DisconnectEvent(this);
        this.eventHandler.handleEvent(event);
    }
}
