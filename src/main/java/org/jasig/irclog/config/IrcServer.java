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
public class IrcServer {
    private String host;
    private String port;
    private String password;
    /**
     * @return the host
     */
    public String getHost() {
        return this.host;
    }
    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }
    /**
     * @return the port
     */
    public String getPort() {
        return this.port;
    }
    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
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
            .append("host", this.host)
            .append("port", this.port)
            .append("password", this.password)
            .toString();
    }
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(725933963, -921189647)
            .append(this.password)
            .append(this.host)
            .append(this.port)
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
        if (!(object instanceof IrcServer)) {
            return false;
        }
        IrcServer rhs = (IrcServer) object;
        return new EqualsBuilder()
            .append(this.password, rhs.password)
            .append(this.host, rhs.host)
            .append(this.port, rhs.port)
            .isEquals();
    }
}
