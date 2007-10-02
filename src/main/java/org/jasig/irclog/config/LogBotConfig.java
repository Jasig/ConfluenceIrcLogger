/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package org.jasig.irclog.config;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class LogBotConfig {
    private Nick nick;
    private IrcServer ircServer;
    private Set<ChannelLogger> channelLoggers = Collections.emptySet();
    
    /**
     * @return the nick
     */
    public Nick getNick() {
        return this.nick;
    }
    /**
     * @param nick the nick to set
     */
    public void setNick(Nick nick) {
        this.nick = nick;
    }
    /**
     * @return the ircServer
     */
    public IrcServer getIrcServer() {
        return this.ircServer;
    }
    /**
     * @param ircServer the ircServer to set
     */
    public void setIrcServer(IrcServer ircServer) {
        this.ircServer = ircServer;
    }
    /**
     * @return the channelLoggers
     */
    public Set<ChannelLogger> getChannelLoggers() {
        return this.channelLoggers;
    }
    /**
     * @param channelLoggers the channelLoggers to set
     */
    public void setChannelLoggers(Set<ChannelLogger> channelLoggers) {
        this.channelLoggers = channelLoggers;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("ircServer", this.ircServer)
            .append("nick", this.nick)
            .append("channelLoggers", this.channelLoggers)
            .toString();
    }
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(748005581, -1936393961)
            .append(this.channelLoggers)
            .append(this.ircServer)
            .append(this.nick)
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
        if (!(object instanceof LogBotConfig)) {
            return false;
        }
        LogBotConfig rhs = (LogBotConfig) object;
        return new EqualsBuilder()
            .append(this.channelLoggers, rhs.channelLoggers)
            .append(this.ircServer, rhs.ircServer)
            .append(this.nick, rhs.nick)
            .isEquals();
    }
}
