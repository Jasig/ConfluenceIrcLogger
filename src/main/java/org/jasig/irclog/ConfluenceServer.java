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

import java.util.Map;

/**
 * Describes the high level operations needed by the {@link ConfluenceEventWriter} for interacting
 * with a Confluence server.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 * @param <T> The type of the login token
 */
public interface ConfluenceServer<T> {

    /**
     * Logs into the server and returns an authentication state token.
     */
    public T login();

    /**
     * Logout for the specified token
     */
    public void logout(T token);

    /**
     * Get the specified page. Returns null if the page doesn't exist
     */
    public Map<String, Object> getPage(T token, String spaceKey, String title);

    /**
     * Store a new page, returning the server's new view of the page.
     */
    public Map<String, Object> storePage(T token, Map<String, Object> page);

    /**
     * Updates a page, returning the server's new view of the page
     */
    public Map<String, Object> updatePage(T token, Map<String, Object> page, Map<String, Object> updateOptions);
    
    /**
     * Convert the wiki markup to the correct storage format
     */
    public String convertWikiToStorageFormat(T token, String message);
}