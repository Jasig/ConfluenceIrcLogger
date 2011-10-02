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