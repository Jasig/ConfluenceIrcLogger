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
 * @see PircBot#onPart(String, String, String, String)
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class PartEvent extends ChannelEvent {
    private static final List<String> PROPERTIES = Collections.unmodifiableList(Arrays.asList(new String[] { "sender", "login", "hostname" }));
    
    protected final String sender;
    protected final String login;
    protected final String hostname;
    
    public PartEvent(final PircBot source, final String channel, final String sender, final String login, final String hostname) {
        super(source, channel);
        this.sender = sender;
        this.login = login;
        this.hostname = hostname;
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
     * @return the sender
     */
    public String getSender() {
        return this.sender;
    }
}