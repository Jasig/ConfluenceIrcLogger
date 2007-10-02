/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibble.pircbot.PircBot;

/**
 * Base class for all IRC events
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public abstract class IrcEvent {
    private static final List<String> PROPERTIES = Collections.unmodifiableList(Arrays.asList(new String[] { "date" }));
    
    protected final Log logger = LogFactory.getLog(this.getClass());
    protected final Date date;
    protected final PircBot source;
    
    public IrcEvent(PircBot source) {
        this.date = new Date();
        this.source = source;
    }

    /**
     * @return the date the event occured
     */
    public Date getDate() {
        return this.date;
    }
    
    /**
     * @return the source
     */
    public PircBot getSource() {
        return this.source;
    }
    
    /**
     * @return the arguments
     */
    public final Object[] getArguments() {
        final List<String> properties = this.getArgumentProperties();
        final Object[] arguments = new Object[properties.size()];
        
        int index = 0;
        for (final String property : properties) {
            final Object value;
            try {
                value = PropertyUtils.getProperty(this, property);
            }
            catch (IllegalAccessException iae) {
                this.logger.error("Cannot access bean property '" + property + "' on bean '" + this + "'", iae);
                throw new RuntimeException("Cannot access bean property '" + property + "' on bean '" + this + "'", iae);
            }
            catch (InvocationTargetException ite) {
                this.logger.error("Cannot access bean property '" + property + "' on bean '" + this + "'", ite);
                throw new RuntimeException("Cannot access bean property '" + property + "' on bean '" + this + "'", ite);
            }
            catch (NoSuchMethodException nsme) {
                this.logger.error("Property '" + property + "' does not exist on bean '" + this + "'", nsme);
                throw new RuntimeException("Property '" + property + "' does not exist on bean '" + this + "'", nsme);
            }
            
            arguments[index] = value;
            index++;
        }
        
        return arguments;
    }
    
    /**
     * @return The bean properties to generate the arguments from for this class.
     */
    protected List<String> getArgumentProperties() {
        return PROPERTIES;
    }
    
    /**
     * @return The code to lookup messages with for the event
     */
    public final String getEventCode() {
        return this.getClass().getName();
    }
}
