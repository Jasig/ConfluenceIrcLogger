/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.messages;

import java.util.ArrayList;

/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class BufferingMessageHandler extends MessageHandlerWrapper {
    private final ArrayList<String> messages = new ArrayList<String>();
    private int messageBufferSize;
    
    public BufferingMessageHandler() {
        this.setMessageBufferSize(8);
    }
    
    /**
     * @return the messageBufferSize
     */
    public int getMessageBufferSize() {
        return this.messageBufferSize;
    }
    /**
     * @param messageBufferSize the messageBufferSize to set
     */
    public void setMessageBufferSize(int messageBufferSize) {
        if (messageBufferSize <= 0) {
            messageBufferSize = 64;
        }
        
        this.messageBufferSize = messageBufferSize;
        this.messages.ensureCapacity(messageBufferSize);
    }

    /**
     * @return the messageHandler
     */
    @Override
    public MessageHandler getMessageHandler() {
        return this.messageHandler;
    }
    /**
     * @param messageHandler the messageHandler to set
     */
    @Override
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * @see org.jasig.irclog.messages.MessageHandler#handleMessages(java.lang.String[])
     */
    @Override
    public void handleMessages(String... messages) {
        synchronized (this.messages) {
            //TODO this could be faster with some math & addall type work
            for (final String message : messages) {
                this.logger.debug("Adding '" + message + "' to the buffer. (" + (this.messages.size() + 1) + " of " + this.messageBufferSize + " messages in buffer)");
                this.messages.add(message);
                
                if (this.messages.size() == this.messageBufferSize) {
                    this.flush();
                }
            }
        }
        
    }

    /**
     * @see org.jasig.irclog.messages.MessageHandler#flush()
     */
    @Override
    public void flush() {
        final String[] messages;
        synchronized (this.messages) {
            if (this.messages.size() == 0) {
                return;
            }
            
            messages = this.messages.toArray(new String[this.messages.size()]);
            this.messages.clear();
        }

        this.logger.debug("Flushing '" + messages.length + "' messages from buffer");
        this.messageHandler.handleMessages(messages);
    }
}
