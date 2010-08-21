/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events;

import org.jasig.irclog.IrcBot;
import org.jibble.pircbot.PircBot;

/**
 * @see PircBot#onMessage(String, String, String, String, String)
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class MessageEvent extends ChannelEvent {
    private static final long serialVersionUID = 1L;

    protected final String sender;
    protected final String login;
    protected final String hostname;
    protected final String message;
    
    public MessageEvent(final IrcBot source, final String channel, final String sender, final String login, final String hostname, final String message) {
        super(source, channel);
        this.sender = sender;
        this.login = login;
        this.hostname = hostname;
        this.message = message;
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
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return the sender
     */
    public String getSender() {
        return this.sender;
    }

    @Override
    public String toString() {
        return "MessageEvent [ircbot=" + this.source + "date=" + this.date + "channel=" + this.channel + "sender=" + 
                this.sender + ", login=" + this.login + ", hostname=" + this.hostname + ", message=" + this.message + "]";
    }
    
}
