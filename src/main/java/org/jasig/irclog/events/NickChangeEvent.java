/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events;

import org.jasig.irclog.IrcBot;
import org.jibble.pircbot.PircBot;

/**
 * @see PircBot#onNickChange(String, String, String, String)
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class NickChangeEvent extends IrcEvent {
    private static final long serialVersionUID = 1L;
    
    protected final String oldNick;
    protected final String login;
    protected final String hostname;
    protected final String newNick;
    
    public NickChangeEvent(final IrcBot source, final String oldNick, final String login, final String hostname, final String newNick) {
        super(source);
        this.oldNick = oldNick;
        this.login = login;
        this.hostname = hostname;
        this.newNick = newNick;
    }

    /**
     * @return the hostname
     */
    public String getHostname() {
        return this.hostname;
    }

    /**
     * @return the login
     */
    public String getLogin() {
        return this.login;
    }

    /**
     * @return the newNick
     */
    public String getNewNick() {
        return this.newNick;
    }

    /**
     * @return the oldNick
     */
    public String getOldNick() {
        return this.oldNick;
    }

    @Override
    public String toString() {
        return "NickChangeEvent [ircbot=" + this.source + "date=" + this.date + "oldNick=" + this.oldNick + ", login=" + 
                this.login + ", hostname=" + this.hostname + ", newNick=" + this.newNick + "]";
    }
}
