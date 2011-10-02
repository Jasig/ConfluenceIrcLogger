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
 * @see PircBot#onAction(String, String, String, String, String)
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class ActionEvent extends TargetedEvent {
    private static final long serialVersionUID = 1L;

    protected final String sender;
    protected final String login;
    protected final String hostname;
    protected final String action;
    
    public ActionEvent(final IrcBot source, final String sender, final String login, final String hostname, final String target, final String action) {
        super(source, target);
        this.sender = sender;
        this.login = login;
        this.hostname = hostname;
        this.action = action;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return this.action;
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

    @Override
    public String toString() {
        return "ActionEvent [ircbot=" + this.source + "date=" + this.date + "target=" + this.target + "sender=" + 
                this.sender + ", login=" + this.login + ", hostname=" + this.hostname + ", action=" + this.action + "]";
    }
    
}
