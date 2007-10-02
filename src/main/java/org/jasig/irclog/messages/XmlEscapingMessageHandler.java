/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.messages;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class XmlEscapingMessageHandler extends MessageHandlerWrapper {
    private static final Map<Character, CharSequence> CHAR_LOOKUP = new HashMap<Character, CharSequence>();
    static {
        CHAR_LOOKUP.put('&', "&amp;");
        CHAR_LOOKUP.put('<', "&lt;");
        CHAR_LOOKUP.put('>', "&gt;");
        CHAR_LOOKUP.put('"', "&#034;");
        CHAR_LOOKUP.put('\'', "&#039;");
    }
    
    /**
     * @see org.jasig.irclog.messages.MessageHandlerWrapper#handleMessages(java.lang.String[])
     */
    @Override
    public void handleMessages(String... messages) {
        final String[] escapedMessages = new String[messages.length];
        
        for (int index = 0; index < messages.length; index++) {
            final String message = messages[index];
            final String escapedMessage = this.escapeXml(message);
            escapedMessages[index] = escapedMessage;
        }
        
        super.handleMessages(escapedMessages);
    }
    
    protected String escapeXml(String message) {
        if (message == null) {
            return null;
        }

        final int messageLength = message.length();
        int lastReplacement = 0;
        StringBuilder escapedBuilder = null;
        
        for (int index = 0; index < messageLength; index++) {
            final char c = message.charAt(index);
            final CharSequence replacement = CHAR_LOOKUP.get(c);
            
            if (replacement != null) {
                //First replacement, create the StringBuilder to build the escaped string in
                if (lastReplacement == 0) {
                    escapedBuilder = new StringBuilder(messageLength + 8);
                }
                
                //add the good text since the last replacement
                if (lastReplacement < index) {
                    final String subSeq = message.substring(lastReplacement, index);
                    escapedBuilder.append(subSeq);
                }
                lastReplacement = index + 1;

                //Add the escaped XML
                escapedBuilder.append(replacement);
            }
        }
        
        //No escaping was needed, just return the original message
        if (lastReplacement == 0) {
            this.logger.debug("No XML escaping required, returning original message '" + message + "'");
            return message;
        }
        
        //Append the rest of message
        if (lastReplacement < messageLength) {
            final CharSequence subSeq = message.subSequence(lastReplacement, messageLength);
            escapedBuilder.append(subSeq);
        }

        final String escapedMessage = escapedBuilder.toString();
        this.logger.debug("Returning escaped message '" + escapedMessage + "' created from '" + message + "'");
        return escapedMessage;
    }
}
