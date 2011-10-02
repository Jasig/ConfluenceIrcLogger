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

/**
 * Base class for all Channel related events
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public abstract class TargetedEvent extends IrcEvent {
    private static final long serialVersionUID = 1L;
    
    protected final String target;
    
    public TargetedEvent(final IrcBot source, final String target) {
        super(source);
        this.target = target;
    }

    /**
     * @return the target the event pertains to.
     */
    public String getTarget() {
        return this.target;
    }

    @Override
    public String toString() {
        return "TargetedEvent [ircbot=" + this.source + "date=" + this.date + "target=" + this.target + "]";
    }
}