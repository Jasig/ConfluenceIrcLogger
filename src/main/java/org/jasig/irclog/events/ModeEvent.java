/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events;

import org.jibble.pircbot.PircBot;

/**
 * @see PircBot#onMode(String, String, String, String, String)
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class ModeEvent extends ChannelEvent {
    private static final long serialVersionUID = 1L;
    
    protected final String sourceNick;
    protected final String sourceLogin;
    protected final String sourceHostname;
    protected final String mode;
    
    public ModeEvent(final PircBot source, final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String mode) {
        super(source, channel);
        this.sourceNick = sourceNick;
        this.sourceLogin = sourceLogin;
        this.sourceHostname = sourceHostname;
        this.mode = mode;
    }

    /**
     * @return the mode
     */
    public String getMode() {
        return this.mode;
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
}
