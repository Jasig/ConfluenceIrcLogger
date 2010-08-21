/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events;

import org.jasig.irclog.IrcBot;
import org.jibble.pircbot.PircBot;

/**
 * @see PircBot#onConnect()
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class ConnectEvent extends IrcEvent {
    private static final long serialVersionUID = 1L;

    /**
     * @param date
     */
    public ConnectEvent(final IrcBot source) {
        super(source);
    }

    @Override
    public String toString() {
        return "ConnectEvent [ircbot=" + this.source + "date=" + this.date + "]";
    }
}
