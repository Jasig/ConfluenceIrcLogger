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

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
@Ignore
public class XmlRpcConfluence4ServerTest {
    
    public ConfluenceServer<String> getConfluenceServer() throws Exception {
        final XmlRpcConfluence4Server confluenceServer = new XmlRpcConfluence4Server();
        confluenceServer.setRpcEndpoint("https://wiki.jasig.org/rpc/xmlrpc");
        confluenceServer.setUsername("irclogbot");
        confluenceServer.setPassword("");
        confluenceServer.afterPropertiesSet();
        
        return confluenceServer;
    }
    
    @Test
    public void testPageCreateUpdateWorkflow() throws Exception {
        final ConfluenceServer<String> confluenceServer = this.getConfluenceServer();
        
        final String token = confluenceServer.login();
        try {
            Map<String, Object> page = confluenceServer.getPage(token, "~irclogbot", "LogBotTest");
            
            if (page == null) {
                page = new HashMap<String, Object>();
                page.put("space", "~irclogbot");
                page.put("title", "LogBotTest");
                
                page = confluenceServer.storePage(token, page);
            }
            
            final String content = (String)page.get("content");
            
            
            
            String message = "[" + new java.text.SimpleDateFormat("HH:mm:ss z(Z)").format(new java.util.Date()) + 
                    "] {color:black} <" + "EricDalquist" + "> " + "testing" + "{color}\n";
            message = confluenceServer.convertWikiToStorageFormat(token, message);
            page.put("content", content + message);
            
            confluenceServer.updatePage(token, page, new HashMap<String, Object>());
        }
        finally {
            confluenceServer.logout(token);
        }
    }
}
