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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.jasig.irclog.events.ChannelEvent;
import org.jasig.irclog.events.ConnectEvent;
import org.jasig.irclog.events.IrcEvent;
import org.jasig.irclog.events.JoinEvent;
import org.jasig.irclog.events.KickEvent;
import org.jasig.irclog.events.ModeEvent;
import org.jasig.irclog.events.PartEvent;
import org.jasig.irclog.events.TargetedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.Scheduled;

import com.google.common.collect.Sets;
import com.googlecode.shutdownlistener.ShutdownListener;

/**
 * Handles filtering, logging, queuing and flushing events for a specific IRC channel.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ChannelLogger implements ApplicationListener<IrcEvent>, ShutdownListener, Ordered {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final Queue<IrcEvent> eventQueue = new ConcurrentLinkedQueue<IrcEvent>();
    private volatile long nextFlush = System.currentTimeMillis() + new Random().nextInt((int)TimeUnit.SECONDS.toMillis(30));
    
    private IrcBot ircBot;
    private Collection<EventWriter> eventWriters;
    private String channel;
    private String notification;
    private long flushPeriod = TimeUnit.MINUTES.toMillis(1);
    @SuppressWarnings("unchecked")
    private Set<Class<? extends IrcEvent>> ignoredEvents = Sets.<Class<? extends IrcEvent>>newHashSet(
            JoinEvent.class, 
            KickEvent.class,
            ModeEvent.class,
            PartEvent.class);
            
    private boolean logNonTargetedEvents = false;
    
    /**
     * The IrcBot to use for logging the channel.
     */
    public void setIrcServer(IrcBot ircBot) {
        this.ircBot = ircBot;
    }

    /**
     * The EventWriters to flush queued events to
     */
    public void setEventWriters(Collection<EventWriter> eventWriters) {
        this.eventWriters = eventWriters;
    }

    /**
     * The IRC channel to log
     */
    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * The notification to send to the channel when joining and to send to any users that join
     * the channel
     */
    public void setNotification(String notification) {
        this.notification = notification;
    }

    /**
     * Frequency with which to flush queued events to the EventWriter in milliseconds. Defaults to
     * 1 Minute, minimum value is 1 Second.
     */
    public void setFlushPeriod(long flushPeriod) {
        this.flushPeriod = Math.min(1000, flushPeriod);
    }
    
    /**
     * Set {@link IrcEvent} classes that should not be logged. By default {@link JoinEvent}, {@link KickEvent}
     * {@link PartEvent}, and {@link ModeEvent} are ignored. Providing any Set here overrides these defaults.
     */
    public void setIgnoredEvents(Set<Class<? extends IrcEvent>> ignoredEvents) {
        if (ignoredEvents == null) {
            this.ignoredEvents = Collections.emptySet();
        }
        else {
            this.ignoredEvents = ignoredEvents;
        }
    }
    
    /**
     * If events other than {@link ChannelEvent} and {@link TargetedEvent} that target the configured channel
     * should be logged. Defaults to false.
     * 
     * WARNING, if you set this to true all events the {@link IrcBot} recieves will be logged unless it is listed
     * in {{@link #setIgnoredEvents(Set)} 
     */
    public void setLogNonTargetedEvents(boolean logNonTargetedEvents) {
        this.logNonTargetedEvents = logNonTargetedEvents;
    }

    @Override
    public void onApplicationEvent(IrcEvent event) {
        //Only pay attention to events from the IRC server we're associated with
        if (!this.ircBot.equals(event.getIrcBot())) {
            return;
        }
        
        //Channel membership management
        if (event instanceof ConnectEvent) {
            this.logger.info("Joining channel: " + channel);
            this.ircBot.joinChannel(this.channel);
        }
        else if (event instanceof KickEvent) {
            final KickEvent kickEvent = (KickEvent) event;
            if (this.channel.equals(kickEvent.getChannel()) && this.ircBot.getNick().equalsIgnoreCase(kickEvent.getRecipientNick())) {
                this.logger.info("Kicked from channel '" + this.channel + "' attempting to rejoin.");
                this.ircBot.joinChannel(this.channel);
            }
        }
        
        //Send the notification if one is set and a JoinEvent happens for this channel
        if (this.notification != null && event instanceof JoinEvent) {
            final JoinEvent joinEvent = (JoinEvent)event;
            
            if (this.channel.equals(joinEvent.getChannel())) {
                final String sender = joinEvent.getSender();
                
                if (sender.equalsIgnoreCase(this.ircBot.getNick())) {
                    final String channel = joinEvent.getChannel();
                    this.ircBot.sendNotice(channel, this.notification);
                }
                else {
                    this.ircBot.sendNotice(sender, this.notification);
                }
            }
        }
        
        //Check if the log message is ignored
        if (this.ignoredEvents.contains(event.getClass())) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Ignoring event " + event + ". It is in the IgnoredEvents Set");
            }
            
            return;
        }
        
        //Event logging, only targeted events are logged
        if (    this.logNonTargetedEvents ||
                (event instanceof ChannelEvent && this.channel.equals(((ChannelEvent)event).getChannel())) ||
                (event instanceof TargetedEvent && this.channel.equals(((TargetedEvent)event).getTarget()))
           ) {
            
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Logging event " + event + " for channel " + this.channel);
            }

            this.eventQueue.offer(event);
        }
    }
    
    /**
     * Write out any queued events on shutdown
     */
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
        //If the queue is empty update the nextFlush time, this always waits about the flushPeriod after seeing
        //a new event before writing them out 
        if (this.eventQueue.isEmpty()) {
            this.nextFlush = System.currentTimeMillis() + this.flushPeriod;
            return;
        }
        
        if (this.nextFlush > System.currentTimeMillis()) {
            return;
        }
        
        //Update nextFlush at the start to prevent concurrent execution if the write takes too long
        this.nextFlush = System.currentTimeMillis() + this.flushPeriod;
        
        final List<IrcEvent> eventBuffer = new LinkedList<IrcEvent>();
        while (!this.eventQueue.isEmpty()) {
            final IrcEvent event = this.eventQueue.poll();
            eventBuffer.add(event);
        }
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Flushing " + eventBuffer.size() + " events");
        }
        
        for (final EventWriter eventWriter : this.eventWriters) { 
            eventWriter.write(eventBuffer);
        }
        
        
        //Update nextFlush after everything is done
        this.nextFlush = System.currentTimeMillis() + this.flushPeriod;
    }
}
