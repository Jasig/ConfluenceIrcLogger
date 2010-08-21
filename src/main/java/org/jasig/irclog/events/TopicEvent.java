/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events;

import org.jasig.irclog.IrcBot;
import org.jibble.pircbot.PircBot;

/**
 * @see PircBot#onTopic(String, String, String, long, boolean)
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class TopicEvent extends ChannelEvent {
    private static final long serialVersionUID = 1L;
    
    protected final String topic;
    protected final String setBy;
    protected final long topicDate;
    protected final boolean changed;
    
    public TopicEvent(final IrcBot source, final String channel, final String topic, final String setBy, final long topicDate, final boolean changed) {
        super(source, channel);
        this.topic = topic;
        this.setBy = setBy;
        this.topicDate = topicDate;
        this.changed = changed;
    }

    /**
     * @return the changed
     */
    public boolean isChanged() {
        return this.changed;
    }

    /**
     * @return the setBy
     */
    public String getSetBy() {
        return this.setBy;
    }
    
    /**
     * @return the topicDate
     */
    public long getTopicDate() {
        return this.topicDate;
    }

    /**
     * @return the topic
     */
    public String getTopic() {
        return this.topic;
    }

    @Override
    public String toString() {
        return "TopicEvent [ircbot=" + this.source + "date=" + this.date + "channel=" + this.channel + "topic=" + 
                this.topic + ", setBy=" + this.setBy + ", topicDate=" + this.topicDate + ", changed=" + this.changed + "]";
    }
}