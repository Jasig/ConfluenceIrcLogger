/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package org.jasig.irclog.events.handlers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.irclog.events.ConnectEvent;
import org.jasig.irclog.events.IrcEvent;
import org.jibble.pircbot.PircBot;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class NickAuthHandler implements IrcEventHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private Map<String, String> passwords = new HashMap<String, String>(0);
    
    /**
     * @return the passwords
     */
    public Map<String, String> getPasswords() {
        return this.passwords;
    }
    /**
     * @param passwords Map of Nicks to Passwords to be used for identifications.
     */
    public void setPasswords(Map<String, String> passwords) {
        Validate.notNull(passwords, "Passwords Map can not be null");
        this.passwords = passwords;
    }
    public void addPassword(String nick, String password) {
        this.passwords.put(nick, password);
    }


    /* (non-Javadoc)
     * @see org.jasig.irclog.events.handlers.IrcEventHandler#handleEvent(org.jasig.irclog.events.IrcEvent)
     */
    public void handleEvent(IrcEvent event) {
        if (event instanceof ConnectEvent) {
            final PircBot bot = event.getSource();
            final String nick = bot.getNick();
            final String password = this.passwords.get(nick);
            
            if (password != null) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Authenticating '" + nick + "'.");
                }

                bot.identify(password);
            }
            else if (this.logger.isInfoEnabled()) {
                this.logger.info("No password set for '" + nick + "', not authenticating.");
            }
        }
    }

}
