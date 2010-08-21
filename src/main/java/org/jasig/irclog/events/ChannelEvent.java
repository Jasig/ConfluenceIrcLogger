/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events;

import org.jibble.pircbot.PircBot;

/**
 * Base class for all Channel related events
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public abstract class ChannelEvent extends IrcEvent {
    private static final long serialVersionUID = 1L;
    
    protected final String channel;
    
    public ChannelEvent(final PircBot source, final String channel) {
        super(source);
        this.channel = channel;
    }

    /**
     * @return the channel the event pertains to.
     */
    public String getChannel() {
        return this.channel;
    }
}