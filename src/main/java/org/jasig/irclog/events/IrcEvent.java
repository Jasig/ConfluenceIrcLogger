/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events;

import org.jasig.irclog.IrcBot;
import org.springframework.context.ApplicationEvent;

/**
 * Base class for all IRC events
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public abstract class IrcEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    protected final long date;
    
    public IrcEvent(IrcBot source) {
        super(source);
        this.date = System.currentTimeMillis();
    }
    
    /**
     * @return The IrcBot that this event originates from
     */
    public IrcBot getIrcBot() {
        return (IrcBot)super.getSource();
    }

    /**
     * @return the date the event occurred
     */
    public long getDate() {
        return this.date;
    }

    @Override
    public String toString() {
        return "IrcEvent [ircbot=" + this.source + "date=" + this.date + "]";
    }
}
