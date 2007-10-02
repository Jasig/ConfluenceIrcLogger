/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package org.jasig.irclog.messages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class LoggingMessageHandler implements MessageHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());

    /* (non-Javadoc)
     * @see org.jasig.irclog.messages.MessageHandler#flush()
     */
    public void flush() {
        //NOOP
    }

    /* (non-Javadoc)
     * @see org.jasig.irclog.messages.MessageHandler#handleMessages(java.lang.String[])
     */
    public void handleMessages(String... messages) {
        if (this.logger.isInfoEnabled()) {
            for (final String message : messages) {
                this.logger.info(message);
            }
        }
    }
}
