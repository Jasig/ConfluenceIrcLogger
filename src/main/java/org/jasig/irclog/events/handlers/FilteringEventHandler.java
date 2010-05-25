/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.irclog.events.IrcEvent;
import org.jasig.irclog.events.handlers.filter.EventFilter;

/**
 * Uses the configured EventFilter to see if the event should be propegated to the next
 * IrcEventHandler in the chain.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class FilteringEventHandler implements IrcEventHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private EventFilter eventFilter;
    private IrcEventHandler targetHandler;
    

    /**
     * @return the eventFilter
     */
    public EventFilter getEventFilter() {
        return this.eventFilter;
    }
    /**
     * @param eventFilter the eventFilter to set
     */
    public void setEventFilter(EventFilter eventFilter) {
        this.eventFilter = eventFilter;
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
        if (targetHandler == this) {
            throw new IllegalArgumentException("Setting self as targetHandler, this would create an infinite loop");
        }

        this.targetHandler = targetHandler;
    }

    public void dispose() {
        this.targetHandler.dispose();
    }

    /**
     * @see org.jasig.irclog.events.handlers.IrcEventHandler#handleEvent(org.jasig.irclog.events.IrcEvent)
     */
    public void handleEvent(IrcEvent event) {
        final boolean passed = this.eventFilter.filterEvent(event);

        if (passed) {
            this.logger.debug("Event '" + event + "' passed filter '" + this.eventFilter + "'");
            this.targetHandler.handleEvent(event);
        }
        else {
            this.logger.debug("Event '" + event + "' failed filter '" + this.eventFilter + "'");
        }
    }
}
