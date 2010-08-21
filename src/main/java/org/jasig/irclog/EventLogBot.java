/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog;

import java.io.IOException;

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
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

import com.googlecode.shutdownlistener.ShutdownListener;

/**
 * Implementation of PircBot that converts all the callbacks into strongly typed event objects.
 * Also handles automatic reconnecting to the configured server.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class EventLogBot extends PircBot implements 
    IrcBot, ShutdownListener, Ordered, ApplicationListener<ContextRefreshedEvent>, ApplicationEventPublisherAware {
    
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private volatile boolean quit = false;

    private ApplicationEventPublisher applicationEventPublisher;
    private boolean stripFormatting = true;
    private String host = null;
    private int port = 6667;
    private String password = null;
    private long reconnectDelay = 10000;

    
    /**
     * Remove colors and formatting from user generated messages recieved from the server
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
    
    /**
     * The host to connect to
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * The port to connect on
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * The password to use
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * How long to wait after disconnection before reconnection
     */
    public void setReconnectDelay(long reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.logger.debug(event);
        this.logger.debug("Connecting to " + host + ":" + port);
        
        try {
            this.connect(host, port, password);
        }
        catch (NickAlreadyInUseException e) {
            logger.error(e, e);
        }
        catch (IOException e) {
            logger.error(e, e);
        }
        catch (IrcException e) {
            logger.error(e, e);
        }
        
        //TODO on connect error setup retry?
    }

    public void shutdown() {
        this.logger.debug("Shutting Down EventLogBot for " + host + ":" + port);
        this.quit = true;
        this.disconnect();
        this.dispose();
    }
    
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * @see org.jibble.pircbot.PircBot#onConnect()
     */
    @Override
    protected void onConnect() {
        if (this.password != null) {
            this.logger.info("Authenticating '" + this.getName() + "'.");
            this.identify(this.password);
        }
        
        final ConnectEvent event = new ConnectEvent(this);
        this.applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onKick(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        final KickEvent event = new KickEvent(this, channel, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
        this.applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onQuit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        if (sourceNick.equalsIgnoreCase(this.getNick()) || sourceNick.equalsIgnoreCase(this.getName())) {
            this.logger.info("Quit server, will not attempt to reconnect.");
            this.quit = true;
        }
        
        final QuitEvent event = new QuitEvent(this, sourceNick, sourceLogin, sourceHostname, reason);
        this.applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onDisconnect()
     */
    @Override
    protected void onDisconnect() {
        final DisconnectEvent event = new DisconnectEvent(this);
        this.applicationEventPublisher.publishEvent(event);
        
        while (!this.quit && !this.isConnected()) {
            try {
                this.logger.info("Attempting to reconnect to server.");
                this.reconnect();
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
    

    /**
     * @see org.jibble.pircbot.PircBot#onAction(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onAction(String sender, String login, String hostname, String target, String action) {
        final ActionEvent event = new ActionEvent(this, sender, login, hostname, target, action);
        this.applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onJoin(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onJoin(String channel, String sender, String login, String hostname) {
        final JoinEvent event = new JoinEvent(this, channel, sender, login, hostname);
        this.applicationEventPublisher.publishEvent(event);
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
        this.applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onMode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
        final ModeEvent event = new ModeEvent(this, channel, sourceNick, sourceLogin, sourceHostname, mode);
        this.applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onNickChange(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
        final NickChangeEvent event = new NickChangeEvent(this, oldNick, login, hostname, newNick);
        this.applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onNotice(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
        final NoticeEvent event = new NoticeEvent(this, sourceNick, sourceLogin, sourceHostname, target, notice);
        this.applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onPart(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onPart(String channel, String sender, String login, String hostname) {
        final PartEvent event = new PartEvent(this, channel, sender, login, hostname);
        this.applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onPing(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onPing(String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue) {
        final PingEvent event = new PingEvent(this, sourceNick, sourceLogin, sourceHostname, target, pingValue);
        this.applicationEventPublisher.publishEvent(event);
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
        this.applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onTime(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onTime(String sourceNick, String sourceLogin, String sourceHostname, String target) {
        final TimeEvent event = new TimeEvent(this, sourceNick, sourceLogin, sourceHostname, target);
        this.applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onTopic(java.lang.String, java.lang.String, java.lang.String, long, boolean)
     */
    @Override
    protected void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
        final TopicEvent event = new TopicEvent(this, channel, topic, setBy, date, changed);
        this.applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * @see org.jibble.pircbot.PircBot#onVersion(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected void onVersion(String sourceNick, String sourceLogin, String sourceHostname, String target) {
        final VersionEvent event = new VersionEvent(this, sourceNick, sourceLogin, sourceHostname, target);
        this.applicationEventPublisher.publishEvent(event);
    }
}
