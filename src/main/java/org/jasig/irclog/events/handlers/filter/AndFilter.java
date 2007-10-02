/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events.handlers.filter;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jasig.irclog.events.IrcEvent;

/**
 * If all fitlers return true, this filter returns true.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class AndFilter implements EventFilter {
    private List<EventFilter> filters;
    
    /**
     * @return the filters
     */
    public List<EventFilter> getFilters() {
        return this.filters;
    }
    /**
     * @param filters the filters to set
     */
    public void setFilters(List<EventFilter> filters) {
        this.filters = filters;
    }

    /**
     * @see org.jasig.irclog.events.handlers.filter.EventFilter#filterEvent(org.jasig.irclog.events.IrcEvent)
     */
    public boolean filterEvent(IrcEvent event) {
        for (final EventFilter filter : this.filters) {
            
            final boolean passed = filter.filterEvent(event);
            if (!passed) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .appendSuper(super.toString())
            .append("filters", this.filters)
            .toString();
    }
}
