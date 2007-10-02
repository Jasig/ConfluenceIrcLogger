/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package org.jasig.irclog.events.handlers.filter;

import org.jasig.irclog.events.IrcEvent;
import org.jasig.irclog.events.TargetedEvent;
import org.jibble.pircbot.PircBot;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class SelfTargetedEventFilter implements EventFilter {

    /* (non-Javadoc)
     * @see org.jasig.irclog.events.handlers.filter.EventFilter#filterEvent(org.jasig.irclog.events.IrcEvent)
     */
    public boolean filterEvent(IrcEvent event) {
        if (event instanceof TargetedEvent) {
            final TargetedEvent targetedEvent = (TargetedEvent)event;
            final PircBot bot = targetedEvent.getSource();
            final String target = targetedEvent.getTarget();
            return bot.getNick().equals(target) || bot.getName().equals(target);
        }
        
        return false;
    }

}
