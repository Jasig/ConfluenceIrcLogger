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
import org.springframework.context.ApplicationEvent;

/**
 * Base class for all IRC events
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public abstract class IrcEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    protected final long date;
    
    public IrcEvent(IrcBot source) {
        super(source);
        this.date = System.currentTimeMillis();
    }
    
    /**
     * @return The IrcBot that this event originates from
     */
    public IrcBot getIrcBot() {
        return (IrcBot)super.getSource();
    }

    /**
     * @return the date the event occurred
     */
    public long getDate() {
        return this.date;
    }

    @Override
    public String toString() {
        return "IrcEvent [ircbot=" + this.source + "date=" + this.date + "]";
    }
}
