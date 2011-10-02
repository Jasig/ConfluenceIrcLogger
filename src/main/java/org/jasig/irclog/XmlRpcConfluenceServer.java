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

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Encapsulates basic Confluence RPC operations needed for updating pages
 * 
 * @author  Eric Dalquist
 * @version  $Revision$
 */
public class XmlRpcConfluenceServer implements InitializingBean, ConfluenceServer<String> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private XmlRpcClient client;

    private String rpcEndpoint;
    private String username;
    private String password;
    
    /**
     * The Confluence XML-RPC endpoint URL
     */
    public void setRpcEndpoint(String rpcEndpoint) {
        this.rpcEndpoint = rpcEndpoint;
    }
    /**
     * The Confluence account to use
     */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * Password for the confluence account
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
        clientConfig.setServerURL(new URL(this.rpcEndpoint));
        
        this.client = new XmlRpcClient();
        this.client.setConfig(clientConfig);
        
        //TODO eventually use httpclient via XmlRpcCommonsTransportFactory
    }
    
    /* (non-Javadoc)
     * @see org.jasig.irclog.ConfluenceServer#login()
     */
    @Override
    public String login() {
        return this.call("login", this.username, this.password);
    }
    
    /* (non-Javadoc)
     * @see org.jasig.irclog.ConfluenceServer#logout(java.lang.String)
     */
    @Override
    public void logout(String token) {
        this.call("logout", token);
    }
    
    /* (non-Javadoc)
     * @see org.jasig.irclog.ConfluenceServer#getPage(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, Object> getPage(String token, String spaceKey, String title) {
        return this.call("getPage", token, spaceKey, title);
    }
    
    /* (non-Javadoc)
     * @see org.jasig.irclog.ConfluenceServer#storePage(java.lang.String, java.util.Map)
     */
    @Override
    public Map<String, Object> storePage(String token, Map<String, Object> page) {
        return this.call("storePage", token, page);
    }
    
    /* (non-Javadoc)
     * @see org.jasig.irclog.ConfluenceServer#updatePage(java.lang.String, java.util.Map, java.util.Map)
     */
    @Override
    public Map<String, Object> updatePage(String token, Map<String, Object> page, Map<String, Object> updateOptions) {
        return this.call("updatePage", token, page, updateOptions);
    }
    
    /* (non-Javadoc)
     * @see org.jasig.irclog.ConfluenceServer#convertWikiToStorageFormat(java.lang.String, java.lang.String)
     */
    @Override
    public String convertWikiToStorageFormat(String token, String message) {
        return message;
    }
    
    @SuppressWarnings("unchecked")
    private <T> T call(String method, Object... args) {
        final List<Object> argList = Arrays.asList(args);
        try {
            this.logger.trace("Calling " + method + " on " + this.rpcEndpoint + " with " + argList);
            return (T)this.client.execute("confluence1." + method, new Vector<Object>(argList));
        }
        catch (XmlRpcException e) {
            //Check for page not existing exception and return null
            if (e.getMessage().contains("You're not allowed to view that page, or it does not exist")) {
                return null;
            }
            
            throw new RuntimeException("Call to " + method + " on " + this.rpcEndpoint + " with " + argList + " failed with code: " + e.code, e);
        }
    }
}