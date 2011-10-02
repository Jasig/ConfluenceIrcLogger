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
 * @see PircBot#onPing(String, String, String, String, String)
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class PingEvent extends TargetedEvent {
    private static final long serialVersionUID = 1L;
    
    protected final String sourceNick;
    protected final String sourceLogin;
    protected final String sourceHostname;
    protected final String pingValue;

    public PingEvent(final IrcBot source, final String sourceNick, final String sourceLogin, final String sourceHostname, final String target, final String pingValue) {
        super(source, target);
        this.sourceNick = sourceNick;
        this.sourceLogin = sourceLogin;
        this.sourceHostname = sourceHostname;
        this.pingValue = pingValue;
    }

    /**
     * @return the pingValue
     */
    public String getPingValue() {
        return this.pingValue;
    }

    /**
     * @return the sourceHostname
     */
    public String getSourceHostname() {
        return this.sourceHostname;
    }

    /**
     * @return the sourceLogin
     */
    public String getSourceLogin() {
        return this.sourceLogin;
    }

    /**
     * @return the sourceNick
     */
    public String getSourceNick() {
        return this.sourceNick;
    }

    @Override
    public String toString() {
        return "PingEvent [ircbot=" + this.source + "date=" + this.date + "target=" + this.target + "sourceNick=" + 
                this.sourceNick + ", sourceLogin=" + this.sourceLogin + ", sourceHostname=" + this.sourceHostname + 
                ", pingValue=" + this.pingValue + "]";
    }
    
    
}
