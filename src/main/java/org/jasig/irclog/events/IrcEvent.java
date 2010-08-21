/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibble.pircbot.PircBot;
import org.springframework.context.ApplicationEvent;

/**
 * Base class for all IRC events
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public abstract class IrcEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    protected final Log logger = LogFactory.getLog(this.getClass());
    protected final long date;
    
    public IrcEvent(PircBot source) {
        super(source);
        this.date = System.currentTimeMillis();
    }

    /**
     * @return the date the event occurred
     */
    public long getDate() {
        return this.date;
    }
    
    /**
     * @return The code to lookup messages with for the event
     */
    public final String getEventCode() {
        return this.getClass().getName();
    }
}
