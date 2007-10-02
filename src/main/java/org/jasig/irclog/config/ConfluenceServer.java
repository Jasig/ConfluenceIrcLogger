/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package org.jasig.irclog.config;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ConfluenceServer {
    private String endpoint;
    private String userName;
    private String password;
    /**
     * @return the endpoint
     */
    public String getEndpoint() {
        return this.endpoint;
    }
    /**
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return this.userName;
    }
    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("userName", this.userName)
            .append("password", this.password)
            .append("endpoint", this.endpoint)
            .toString();
    }
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(1509614353, 1685863729)
            .append(this.password)
            .append(this.userName)
            .append(this.endpoint)
            .toHashCode();
    }
    /**
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof ConfluenceServer)) {
            return false;
        }
        ConfluenceServer rhs = (ConfluenceServer) object;
        return new EqualsBuilder()
            .append(this.password, rhs.password)
            .append(this.userName, rhs.userName)
            .append(this.endpoint, rhs.endpoint)
            .isEquals();
    }
}
