/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package org.jasig.irclog.config;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ChannelLogger {
    private String ircChannel;
    private String notification;
    
    private ConfluenceServer confluenceServer;
    private String spaceKey;
    private List<String> logPagesTitleFormats;
    private int flushPeriod = 1 * 60;
    private int bufferSize = 64;
    
    /**
     * @return the ircChannel
     */
    public String getIrcChannel() {
        return this.ircChannel;
    }
    /**
     * @param ircChannel the ircChannel to set
     */
    public void setIrcChannel(String ircChannel) {
        this.ircChannel = ircChannel;
    }
    /**
     * @return the notification
     */
    public String getNotification() {
        return this.notification;
    }
    /**
     * @param notification the notification to set
     */
    public void setNotification(String notification) {
        this.notification = notification;
    }
    /**
     * @return the confluenceServer
     */
    public ConfluenceServer getConfluenceServer() {
        return this.confluenceServer;
    }
    /**
     * @param confluenceServer the confluenceServer to set
     */
    public void setConfluenceServer(ConfluenceServer confluenceServer) {
        this.confluenceServer = confluenceServer;
    }
    /**
     * @return the spaceKey
     */
    public String getSpaceKey() {
        return this.spaceKey;
    }
    /**
     * @param spaceKey the spaceKey to set
     */
    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }
    /**
     * @return the logPagesTitleFormats
     */
    public List<String> getLogPagesTitleFormats() {
        return this.logPagesTitleFormats;
    }
    /**
     * @param logPagesTitleFormats the logPagesTitleFormats to set
     */
    public void setLogPagesTitleFormats(List<String> logPagesTitleFormats) {
        this.logPagesTitleFormats = logPagesTitleFormats;
    }
    public int getFlushPeriod() {
        return flushPeriod;
    }
    public void setFlushPeriod(int flushPeriod) {
        this.flushPeriod = flushPeriod;
    }
    public int getBufferSize() {
        return bufferSize;
    }
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("ircChannel", this.ircChannel)
            .append("notification", this.notification)
            .append("confluenceServer", this.confluenceServer)
            .append("spaceKey", this.spaceKey)
            .append("flushPeriod", this.flushPeriod)
            .append("bufferSize", this.bufferSize)
            .append("logPagesTitleFormats", this.logPagesTitleFormats)
            .toString();
    }
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(962269917, 752089237)
        .append(this.logPagesTitleFormats)
        .append(this.notification)
        .append(this.spaceKey)
        .append(this.flushPeriod)
        .append(this.bufferSize)
        .append(this.ircChannel)
        .append(this.confluenceServer)
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
        if (!(object instanceof ChannelLogger)) {
            return false;
        }
        ChannelLogger rhs = (ChannelLogger) object;
        return new EqualsBuilder()
            .append(this.logPagesTitleFormats, rhs.logPagesTitleFormats)
            .append(this.notification, rhs.notification)
            .append(this.spaceKey, rhs.spaceKey)
            .append(this.flushPeriod, rhs.flushPeriod)
            .append(this.bufferSize, rhs.bufferSize)
            .append(this.ircChannel, rhs.ircChannel)
            .append(this.confluenceServer, rhs.confluenceServer)
            .isEquals();
    }
}
