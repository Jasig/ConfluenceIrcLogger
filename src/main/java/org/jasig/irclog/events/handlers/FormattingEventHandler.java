/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.events.handlers;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.irclog.events.IrcEvent;
import org.jasig.irclog.messages.MessageHandler;


/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class FormattingEventHandler implements IrcEventHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());

    private MessageHandler messageHandler;
    private Properties eventFormats;
    
    /**
     * @return the eventFormats
     */
    public Properties getEventFormats() {
        return this.eventFormats;
    }
    /**
     * @param eventFormats the eventFormats to set
     */
    public void setEventFormats(Properties eventFormats) {
        this.eventFormats = eventFormats;
    }

    /**
     * @return the messageHandler
     */
    public MessageHandler getMessageHandler() {
        return this.messageHandler;
    }
    /**
     * @param messageHandler the messageHandler to set
     */
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    
    
    /**
     * Format arguments are:
     * {0}  The number of arguments (including this one)
     * {1}  The event code (from {@link IrcEvent#getEventCode()}
     * {2+} The arguments from the {@link IrcEvent#getArguments()}
     * 
     * @see org.jasig.irclog.events.handlers.IrcEventHandler#handleEvent(org.jasig.irclog.events.IrcEvent)
     */
    public void handleEvent(IrcEvent event) {
        final Object[] eventArgs = event.getArguments();
        
        final Object[] formatArgs = new Object[eventArgs.length + 1];
        formatArgs[0] = event.getEventCode();
        System.arraycopy(eventArgs, 0, formatArgs, 1, eventArgs.length);

        final String messagePattern = this.getMessageFormatPattern(event, formatArgs.length);

        try {
            final String message = MessageFormat.format(messagePattern, formatArgs);
            this.messageHandler.handleMessages(message);
        }
        catch (IllegalArgumentException iae) {
            this.logger.error("Failed to format arguments " + Arrays.asList(formatArgs) + " using pattern '" + messagePattern + "'", iae);
        }
    }
    
    /**
     * @param event The event to get a MessageFormat pattern
     * @return The MessageFormat pattern, if no pattern is found in the eventFormats Properties the defaultMessageFormat will be returned
     */
    protected String getMessageFormatPattern(IrcEvent event, int argCount) {
        final String pattern;
        if (this.eventFormats != null) {
            pattern = this.eventFormats.getProperty(event.getEventCode());
        }
        else {
            pattern = null;
        }

        if (pattern != null) {
            return pattern;
        }

        return this.getDefaultMessageFormat(argCount);
    }
    
    protected String getDefaultMessageFormat(int arguments) {
        final StringBuilder msgFormat = new StringBuilder();
        
        if (arguments > 1) {
            msgFormat.append("{0}");
        }
        
        if (arguments > 2) {
            msgFormat.append("@{1}");
        }
        
        if (arguments > 3) {
            msgFormat.append(" - ");
            
            for (int index = 2; index < arguments; index++) {
                msgFormat.append("{").append(index).append("}");
                
                if ((index + 1) < arguments) {
                    msgFormat.append(", ");
                }
            }
        }
        
        return msgFormat.toString();
    }
}
