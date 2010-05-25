/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.irclog.messages.support;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.irclog.messages.MessageHandler;

/**
 * Flushes a wrapped MessageHandler every specified seconds
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class MessageHandlerFlusher {
    private static final Random RND = new Random();
    
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private final Timer timer = new Timer("MessageHandlerTimer[" + this.toString() + "]", true);
    private TimerTask flushTask;
    
    private MessageHandler messageHandler;
    private long period;

    /**
     * @return the period
     */
    public int getPeriod() {
        return (int)(this.period / 1000);
    }
    /**
     * @param period the period to set in seconds
     */
    public synchronized void setPeriod(int period) {
        if (period <= 0) {
            period = 60;
        }
        this.period = period * 1000;
        
        if (this.flushTask != null) {
            this.flushTask.cancel();
        }
        
        this.flushTask = new FlushMessageHandlerTask();
        final int baseDelay = (int)(this.period * 0.1d);
        this.timer.schedule(this.flushTask, RND.nextInt((int)this.period) + baseDelay, this.period);
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
    
    
    private class FlushMessageHandlerTask extends TimerTask {
        @Override
        public void run() {
            MessageHandlerFlusher.this.logger.debug("Flushing MessageHandler[" + MessageHandlerFlusher.this.messageHandler + "'");
            MessageHandlerFlusher.this.messageHandler.flush();
        }
    }
}
