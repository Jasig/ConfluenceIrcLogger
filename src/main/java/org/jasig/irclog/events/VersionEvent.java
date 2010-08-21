/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events;

import org.jasig.irclog.IrcBot;
import org.jibble.pircbot.PircBot;

/**
 * @see PircBot#onVersion(String, String, String, String)
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class VersionEvent extends TargetedEvent {
    private static final long serialVersionUID = 1L;
    
    protected final String sourceNick;
    protected final String sourceLogin;
    protected final String sourceHostname;
    protected final String target;
    
    public VersionEvent(final IrcBot source, final String sourceNick, final String sourceLogin, final String sourceHostname, final String target) {
        super(source, target);
        this.sourceNick = sourceNick;
        this.sourceLogin = sourceLogin;
        this.sourceHostname = sourceHostname;
        this.target = target;
    }

    /**
     * @return the sourceHostname
     */
    public String getSourceHostname() {
        return this.sourceHostname;
    }

    /**
     * @return the sourceLogin
     */
    public String getSourceLogin() {
        return this.sourceLogin;
    }

    /**
     * @return the sourceNick
     */
    public String getSourceNick() {
        return this.sourceNick;
    }

    @Override
    public String toString() {
        return "VersionEvent [ircbot=" + this.source + "date=" + this.date + "target=" + this.target + "sourceNick=" + 
                this.sourceNick + ", sourceLogin=" + this.sourceLogin + ", sourceHostname=" + this.sourceHostname + 
                ", target=" + this.target + "]";
    }
}
