/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jibble.pircbot.PircBot;

/**
 * @see PircBot#onMessage(String, String, String, String, String)
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class MessageEvent extends ChannelEvent {
    private static final List<String> PROPERTIES = Collections.unmodifiableList(Arrays.asList(new String[] { "sender", "login", "hostname", "message" }));
    
    protected final String sender;
    protected final String login;
    protected final String hostname;
    protected final String message;
    
    public MessageEvent(final PircBot source, final String channel, final String sender, final String login, final String hostname, final String message) {
        super(source, channel);
        this.sender = sender;
        this.login = login;
        this.hostname = hostname;
        this.message = message;
    }

    /**
     * @see org.jasig.irclog.events.IrcEvent#getArgumentProperties()
     */
    @Override
    protected List<String> getArgumentProperties() {
        final List<String> parentProps = super.getArgumentProperties();
        final List<String> props = new ArrayList<String>(PROPERTIES.size() + parentProps.size());
        
        props.addAll(parentProps);
        props.addAll(PROPERTIES);
        
        return props;
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
}
