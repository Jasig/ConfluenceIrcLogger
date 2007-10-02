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
 * @see PircBot#onNickChange(String, String, String, String)
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class NickChangeEvent extends IrcEvent {
    private static final List<String> PROPERTIES = Collections.unmodifiableList(Arrays.asList(new String[] { "oldNick", "login", "hostname", "newNick" }));
    
    protected final String oldNick;
    protected final String login;
    protected final String hostname;
    protected final String newNick;
    
    public NickChangeEvent(final PircBot source, final String oldNick, final String login, final String hostname, final String newNick) {
        super(source);
        this.oldNick = oldNick;
        this.login = login;
        this.hostname = hostname;
        this.newNick = newNick;
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
}
