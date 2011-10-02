/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.irclog.events;

import org.jasig.irclog.IrcBot;
import org.jibble.pircbot.PircBot;

/**
 * @see PircBot#onPrivateMessage(String, String, String, String)
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class PrivateMessageEvent extends IrcEvent {
    private static final long serialVersionUID = 1L;
    
    protected final String sender;
    protected final String login;
    protected final String hostname;
    protected final String message;
    
    public PrivateMessageEvent(final IrcBot source, final String sender, final String login, final String hostname, final String message) {
        super(source);
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
        return "PrivateMessageEvent [ircbot=" + this.source + "date=" + this.date + "hostname=" + this.hostname + 
                ", login=" + this.login + ", message=" + this.message + ", sender=" + this.sender + "]";
    }
}
