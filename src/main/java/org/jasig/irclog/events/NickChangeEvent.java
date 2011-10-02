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
