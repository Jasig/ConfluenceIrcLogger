/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.messages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class MessageHandlerWrapper implements MessageHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    protected MessageHandler messageHandler;
    
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
     * 
     * @see org.jasig.irclog.messages.MessageHandler#flush()
     */
    public void flush() {
        this.messageHandler.flush();
    }

    /**
     * @param messages
     * @see org.jasig.irclog.messages.MessageHandler#handleMessages(java.lang.String[])
     */
    public void handleMessages(String... messages) {
        this.messageHandler.handleMessages(messages);
    }
}
