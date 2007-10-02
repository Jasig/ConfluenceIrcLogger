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
 * Base class for all Channel related events
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public abstract class TargetedEvent extends IrcEvent {
    private static final List<String> PROPERTIES = Collections.unmodifiableList(Arrays.asList(new String[] { "target" }));
    
    protected final String target;
    
    public TargetedEvent(final PircBot source, final String target) {
        super(source);
        this.target = target;
    }

    /**
     * @return the target the event pertains to.
     */
    public String getTarget() {
        return this.target;
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
}