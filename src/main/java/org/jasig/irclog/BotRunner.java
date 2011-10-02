/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
