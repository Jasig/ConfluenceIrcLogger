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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.irclog.events.ChannelEvent;
import org.jasig.irclog.events.ConnectEvent;
import org.jasig.irclog.events.IrcEvent;
import org.jasig.irclog.events.JoinEvent;
import org.jasig.irclog.events.KickEvent;
import org.jasig.irclog.events.MessageEvent;
import org.jasig.irclog.events.ModeEvent;
import org.jasig.irclog.events.PartEvent;
import org.jasig.irclog.events.TopicEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.Scheduled;

import com.googlecode.shutdownlistener.ShutdownListener;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ChannelLogger implements ApplicationListener<IrcEvent>, ShutdownListener, Ordered {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private final Queue<IrcEvent> eventQueue = new ConcurrentLinkedQueue<IrcEvent>();
    private volatile long nextFlush = System.currentTimeMillis() + new Random().nextInt((int)TimeUnit.SECONDS.toMillis(30));
    
    private IrcServer ircServer;
    private WikiUpdater wikiUpdater;
    private String channel;
    private String notification;
    private String spaceKey;
    private List<String> pageNames;
    private long flushPeriod = TimeUnit.MINUTES.toMillis(1);
    
    private boolean logJoinEvents = false;
    private boolean logKickEvents = false;
    private boolean logModeEvents = false;
    private boolean logPartEvents = false;
    private boolean logTopicEvents = true;
    
    
    public void setIrcServer(IrcServer ircServer) {
        this.ircServer = ircServer;
    }

    public void setWikiUpdater(WikiUpdater wikiUpdater) {
        this.wikiUpdater = wikiUpdater;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setPageNames(List<String> pageNames) {
        this.pageNames = pageNames;
    }

    public void setFlushPeriod(long flushPeriod) {
        this.flushPeriod = flushPeriod;
    }
    
    public void setLogJoinEvents(boolean logJoinEvents) {
        this.logJoinEvents = logJoinEvents;
    }

    public void setLogKickEvents(boolean logKickEvents) {
        this.logKickEvents = logKickEvents;
    }

    public void setLogModeEvents(boolean logModeEvents) {
        this.logModeEvents = logModeEvents;
    }

    public void setLogPartEvents(boolean logPartEvents) {
        this.logPartEvents = logPartEvents;
    }

    public void setLogTopicEvents(boolean logTopicEvents) {
        this.logTopicEvents = logTopicEvents;
    }

    public void onApplicationEvent(IrcEvent event) {
        //Only pay attention to events from the IRC server we're associated with
        if (!this.ircServer.equals(event.getSource())) {
            return;
        }
        
        //Channel membership management
        if (event instanceof ConnectEvent) {
            this.logger.info("Joining channel: " + channel);
            this.ircServer.joinChannel(this.channel);
        }
        else if (event instanceof KickEvent) {
            final KickEvent kickEvent = (KickEvent) event;
            if (this.channel.equals(kickEvent.getChannel()) && this.ircServer.getNick().equalsIgnoreCase(kickEvent.getRecipientNick())) {
                this.logger.info("Kicked from channel '" + this.channel + "' attempting to rejoin.");
                this.ircServer.joinChannel(this.channel);
            }
        }
        
        //Send the notification if one is set and a JoinEvent happens
        if (this.notification != null && event instanceof JoinEvent) {
            final JoinEvent joinEvent = (JoinEvent)event;
            final String sender = joinEvent.getSender();
            
            if (sender.equalsIgnoreCase(this.ircServer.getNick())) {
                final String channel = joinEvent.getChannel();
                this.ircServer.sendNotice(channel, this.notification);
            }
            else {
                this.ircServer.sendNotice(sender, this.notification);
            }
        }
        
        //Event logging, only targeted events are 
        if (event instanceof ChannelEvent) {
            final ChannelEvent channelEvent = (ChannelEvent)event;
            
            //Only pay attention to events targeting this channel
            if (this.channel.equals(channelEvent.getChannel()) &&
                    (channelEvent instanceof MessageEvent ||
                    (this.logJoinEvents && channelEvent instanceof JoinEvent) ||
                    (this.logKickEvents && channelEvent instanceof KickEvent) ||
                    (this.logModeEvents && channelEvent instanceof ModeEvent) ||
                    (this.logPartEvents && channelEvent instanceof PartEvent) ||
                    (this.logTopicEvents && channelEvent instanceof TopicEvent))) {
                
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Logging event " + event + " for channel " + this.channel);
                }

                this.eventQueue.offer(event);
            }
        }
    }
    
    @Override
    public void shutdown() {
        this.flushEvents();
    }
    
    @Override
    public int getOrder() {
        return 1000;
    }

    /* (non-Javadoc)
     * @see org.jasig.irclog.EventQueue#flushEvents()
     */
    @Scheduled(fixedDelay=1000)
    public void flushEvents() {
        if (this.eventQueue.isEmpty()) {
            this.nextFlush = System.currentTimeMillis() + this.flushPeriod;
            return;
        }
        
        if (this.nextFlush > System.currentTimeMillis()) {
            return;
        }
        
        final List<IrcEvent> eventBuffer = new LinkedList<IrcEvent>();
        while (!this.eventQueue.isEmpty()) {
            final IrcEvent event = this.eventQueue.poll();
            eventBuffer.add(event);
        }
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Flushing " + eventBuffer.size() + " events");
        }
        this.wikiUpdater.update(eventBuffer, spaceKey, pageNames);
        
        this.nextFlush = System.currentTimeMillis() + this.flushPeriod;
    }
}
