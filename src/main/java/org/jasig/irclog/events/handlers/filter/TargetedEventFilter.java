/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events.handlers.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jasig.irclog.events.IrcEvent;
import org.jasig.irclog.events.TargetedEvent;

/**
 * If the IrcEvent is of type TargetEvent and the target exists in the
 * targets Set true is returned.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class TargetedEventFilter implements EventFilter {
    private Set<String> targets = new HashSet<String>(0);
    
    public TargetedEventFilter() {
    }
    
    public TargetedEventFilter(String... target) {
        this.targets.addAll(Arrays.asList(target));
    }
    
    /**
     * @return the targets
     */
    public Set<String> getTargets() {
        return this.targets;
    }
    /**
     * @param targets the targets to set
     */
    public void setTargets(Set<String> targets) {
        Validate.notNull(targets, "targets Set cannot be null");
        this.targets = targets;
    }


    /**
     * @see org.jasig.irclog.events.handlers.filter.EventFilter#filterEvent(org.jasig.irclog.events.IrcEvent)
     */
    public boolean filterEvent(IrcEvent event) {
        if (event instanceof TargetedEvent) {
            final TargetedEvent targetedEvent = (TargetedEvent)event;
            final String target = targetedEvent.getTarget();
            return this.targets.contains(target);
        }
        
        return false;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .appendSuper(super.toString())
            .append("targets", this.targets)
            .toString();
    }
}
