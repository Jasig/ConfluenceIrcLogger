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
public abstract class TargetedEvent extends IrcEvent {
    private static final long serialVersionUID = 1L;
    
    protected final String target;
    
    public TargetedEvent(final PircBot source, final String target) {
        super(source);
        this.target = target;
    }

    /**
     * @return the target the event pertains to.
     */
    public String getTarget() {
        return this.target;
    }
}