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
 * @see PircBot#onKick(String, String, String, String, String, String)
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class KickEvent extends ChannelEvent {
    private static final long serialVersionUID = 1L;
    
    protected final String kickerNick;
    protected final String kickerLogin;
    protected final String kickerHostname;
    protected final String recipientNick;
    protected final String reason;
    
    public KickEvent(final IrcBot source, final String channel, final String kickerNick, final String kickerLogin, final String kickerHostname, final String recipientNick, final String reason) {
        super(source, channel);
        this.kickerNick = kickerNick;
        this.kickerLogin = kickerLogin;
        this.kickerHostname = kickerHostname;
        this.recipientNick = recipientNick;
        this.reason = reason;
    }

    /**
     * @return the kickerHostname
     */
    public String getKickerHostname() {
        return this.kickerHostname;
    }

    /**
     * @return the kickerLogin
     */
    public String getKickerLogin() {
        return this.kickerLogin;
    }

    /**
     * @return the kickerNick
     */
    public String getKickerNick() {
        return this.kickerNick;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * @return the recipientNick
     */
    public String getRecipientNick() {
        return this.recipientNick;
    }

    @Override
    public String toString() {
        return "KickEvent [ircbot=" + this.source + "date=" + this.date + "channel=" + this.channel + "kickerNick=" + 
                this.kickerNick + ", kickerLogin=" + this.kickerLogin + ", kickerHostname=" + this.kickerHostname + 
                ", recipientNick=" + this.recipientNick + ", reason=" + this.reason + "]";
    }
    
    
    
}
