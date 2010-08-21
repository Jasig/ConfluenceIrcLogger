/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package org.jasig.irclog;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.googlecode.shutdownlistener.ShutdownHandler;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class BotRunner {
    public static void main(String[] args) throws Exception {
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml", "/loggerContext.xml");
        
        final ShutdownHandler shutdownHandler = applicationContext.getBean(ShutdownHandler.class);
        
        shutdownHandler.waitForShutdown();
        applicationContext.close();
    }
}
