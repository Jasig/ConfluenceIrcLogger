/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events.handlers;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.irclog.events.IrcEvent;

/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class SplittingEventHandler implements IrcEventHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private Set<IrcEventHandler> handlers = new HashSet<IrcEventHandler>(0);
    private boolean recoverExceptions = true;
    
    /**
     * @return the handlers
     */
    public Set<IrcEventHandler> getHandlers() {
        return this.handlers;
    }
    /**
     * @param handlers the handlers to set, can not be null.
     */
    public void setHandlers(Set<IrcEventHandler> handlers) {
        Validate.notNull(handlers, "Handlers Set can not be null");
        this.handlers = handlers;
    }
    
    /**
     * @param handler Adds handler to Set of IrcEventHandlers.
     */
    public void registerEventHandler(IrcEventHandler handler) {
        this.handlers.add(handler);
    }
    
    /**
     * @return the recoverExceptions
     */
    public boolean isRecoverExceptions() {
        return this.recoverExceptions;
    }
    /**
     * @param recoverExceptions the recoverExceptions to set
     */
    public void setRecoverExceptions(boolean recoverExceptions) {
        this.recoverExceptions = recoverExceptions;
    }
    
    public void dispose() {
        for (final IrcEventHandler handler : this.handlers) {
            handler.dispose();
        }
    }
    
    /**
     * @see org.jasig.irclog.events.handlers.IrcEventHandler#handleEvent(org.jasig.irclog.events.IrcEvent)
     */
    public void handleEvent(IrcEvent event) {
        for (final IrcEventHandler handler : this.handlers) {
            try {
                handler.handleEvent(event);
            }
            catch (Throwable t) {
                if (this.recoverExceptions) {
                    this.logger.warn("Event Handler '" +  this.handlers + "' threw an exception, execution will continue.", t);
                }
                else {
                    this.logger.error("Event Handler '" +  this.handlers + "' threw an exception.", t);
                    throw new RuntimeException("Event Handler '" +  this.handlers + "' threw an exception.", t);
                }
            }
        }
    }
}
