/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events.handlers.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jasig.irclog.events.ChannelEvent;
import org.jasig.irclog.events.IrcEvent;

/**
 * If the IrcEvent is of type ChannelEvent and the channel exists in the
 * channels Set true is returned.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class ChannelEventFilter implements EventFilter {
    private Set<String> channels = new HashSet<String>(0);
    
    public ChannelEventFilter() {
        
    }
    
    public ChannelEventFilter(String... channel) {
        this.channels.addAll(Arrays.asList(channel));
    }
    
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
        Validate.notNull(channels, "channels Set cannot be null");
        this.channels = channels;
    }


    /**
     * @see org.jasig.irclog.events.handlers.filter.EventFilter#filterEvent(org.jasig.irclog.events.IrcEvent)
     */
    public boolean filterEvent(IrcEvent event) {
        if (event instanceof ChannelEvent) {
            final ChannelEvent channelEvent = (ChannelEvent)event;
            final String channel = channelEvent.getChannel();
            return this.channels.contains(channel);
        }
        
        return false;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .appendSuper(super.toString())
            .append("channels", this.channels)
            .toString();
    }
}
