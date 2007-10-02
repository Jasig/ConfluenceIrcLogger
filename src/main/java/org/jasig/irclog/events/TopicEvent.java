/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jibble.pircbot.PircBot;

/**
 * @see PircBot#onTopic(String, String, String, long, boolean)
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class TopicEvent extends ChannelEvent {
    private static final List<String> PROPERTIES = Collections.unmodifiableList(Arrays.asList(new String[] { "topic", "setBy", "topicDate", "changed" }));
    
    protected final String topic;
    protected final String setBy;
    protected final Date topicDate;
    protected final boolean changed;
    
    public TopicEvent(final PircBot source, final String channel, final String topic, final String setBy, final long topicDate, final boolean changed) {
        super(source, channel);
        this.topic = topic;
        this.setBy = setBy;
        this.topicDate = new Date(topicDate);
        this.changed = changed;
    }

    /**
     * @see org.jasig.irclog.events.IrcEvent#getArgumentProperties()
     */
    @Override
    protected List<String> getArgumentProperties() {
        final List<String> parentProps = super.getArgumentProperties();
        final List<String> props = new ArrayList<String>(PROPERTIES.size() + parentProps.size());
        
        props.addAll(parentProps);
        props.addAll(PROPERTIES);
        
        return props;
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
    public Date getTopicDate() {
        return this.topicDate;
    }

    /**
     * @return the topic
     */
    public String getTopic() {
        return this.topic;
    }
}