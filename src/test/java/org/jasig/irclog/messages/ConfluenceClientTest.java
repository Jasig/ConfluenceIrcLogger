/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package org.jasig.irclog.messages;

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.junit.Test;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ConfluenceClientTest {
    @Test
    public void testPageUpdate() throws Exception {
//        final XmlRpcClient client = new XmlRpcClient("https://wiki.jasig.org/rpc/xmlrpc");
//        
//        final String token = this.call(client, "login", "irclogbot", "");
//        System.out.println(token);
//        
//        final Map<String, Object> page = this.call(client, "getPage", token, "SND", "uPortal TEST IRC Logs-2010-05-25");
//        System.out.println(page);
//        
//        //Add log data
//        final String newContent = page.get("content") + "\ntesting log data";
//        page.put("content", newContent);
//        
//        /*
//         * Page  updatePage(String token, Page  page, PageUpdateOptions  pageUpdateOptions)  
//         */
//        final Map<String, Object> pageUpdateOptions = new Hashtable<String, Object>();
//        pageUpdateOptions.put("minorEdit", true);
//        this.call(client, "updatePage", token, page, pageUpdateOptions);
//        
//        this.call(client, "logout", token);
    }
    
    private <T> T call(XmlRpcClient client, String method, Object... args) throws XmlRpcException, IOException {
        return (T)client.execute("confluence1." + method, new Vector<Object>(Arrays.asList(args)));
    }
}
