/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package org.jasig.irclog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Log4jConfigurer;

import com.googlecode.shutdownlistener.ShutdownHandler;

/**
 * Starts log4j then the spring app context and waits for a shutdown call via the {@link ShutdownHandler}.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class BotRunner {
    private static final Logger LOG = LoggerFactory.getLogger(BotRunner.class);
    
    public static void main(String[] args) throws Exception {
        Log4jConfigurer.initLogging("classpath:log4j.properties", 10000);
        try {
            final ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml", "/loggerContext.xml");
            
            final ShutdownHandler shutdownHandler = applicationContext.getBean(ShutdownHandler.class);
            
            shutdownHandler.waitForShutdown();
            applicationContext.close();
        }
        catch (Exception e) {
            LOG.error("Failed to start ClassPathXmlApplicationContext", e);
        }
        finally {
            Log4jConfigurer.shutdownLogging();
        }
    }
}
