/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.messages;

/**
 * An interface for handling messages produced by the IRC Bot.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public interface MessageHandler {
    /**
     * @param messages An ordered array of messages to handle
     */
    public void handleMessages(String... messages);
    
    /**
     * Tells the handler to write any output if needed
     */
    public void flush();
}
