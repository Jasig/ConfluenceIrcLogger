/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events.handlers;

import java.util.Set;

import org.jasig.irclog.events.IrcEvent;

/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class TypeFilteringEventHandler implements IrcEventHandler {
    private Set<Class<? extends IrcEvent>> allowedEvents;
    private IrcEventHandler targetHandler;
    
    /**
     * @return the allowedEvents
     */
    public Set<Class<? extends IrcEvent>> getAllowedEvents() {
        return this.allowedEvents;
    }
    /**
     * @param allowedEvents the allowedEvents to set
     */
    public void setAllowedEvents(Set<Class<? extends IrcEvent>> allowedEvents) {
        this.allowedEvents = allowedEvents;
    }

    /**
     * @return the targetHandler
     */
    public IrcEventHandler getTargetHandler() {
        return this.targetHandler;
    }
    /**
     * @param targetHandler the targetHandler to set
     */
    public void setTargetHandler(IrcEventHandler targetHandler) {
        this.targetHandler = targetHandler;
    }

    /**
     * @see org.jasig.irclog.events.handlers.IrcEventHandler#handleEvent(org.jasig.irclog.events.IrcEvent)
     */
    public void handleEvent(IrcEvent event) {
        if (this.allowedEvents.contains(event.getClass())) {
            this.targetHandler.handleEvent(event);
        }
    }
}
