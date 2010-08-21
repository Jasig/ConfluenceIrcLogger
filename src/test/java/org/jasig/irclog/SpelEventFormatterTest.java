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

import static org.junit.Assert.assertEquals;

import org.jasig.irclog.events.ActionEvent;
import org.jasig.irclog.events.ConnectEvent;
import org.jasig.irclog.events.DisconnectEvent;
import org.jasig.irclog.events.JoinEvent;
import org.jasig.irclog.events.KickEvent;
import org.jasig.irclog.events.MessageEvent;
import org.jasig.irclog.events.ModeEvent;
import org.jasig.irclog.events.NickChangeEvent;
import org.jasig.irclog.events.NoticeEvent;
import org.jasig.irclog.events.PartEvent;
import org.jasig.irclog.events.PingEvent;
import org.jasig.irclog.events.PrivateMessageEvent;
import org.jasig.irclog.events.QuitEvent;
import org.jasig.irclog.events.TimeEvent;
import org.jasig.irclog.events.TopicEvent;
import org.jasig.irclog.events.VersionEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spelEventFormatterTestContext.xml")
public class SpelEventFormatterTest {
    @Autowired
    private SpelEventFormatter eventFormatter;
    
    @Test
    public void testEventFormatting() {
        final EventLogBot bot = new EventLogBot();
        
        final ActionEvent actionEvent = new ActionEvent(bot, "sender", "login", "hostname", "target", "action");
        String formattedActionEvent = this.eventFormatter.formatEvent(actionEvent);
        formattedActionEvent = formattedActionEvent.substring(formattedActionEvent.indexOf("]"));
        assertEquals("] {color:green}* sender action{color}", formattedActionEvent);
        
        final ConnectEvent connectEvent = new ConnectEvent(bot);
        String formattedConnectEvent = this.eventFormatter.formatEvent(connectEvent);
        formattedConnectEvent = formattedConnectEvent.substring(formattedConnectEvent.indexOf("]"));
        assertEquals("] {color:red}**Connected to server**{color}", formattedConnectEvent);
        
        final DisconnectEvent disconnectEvent = new DisconnectEvent(bot);
        String formattedDisconnectEvent = this.eventFormatter.formatEvent(disconnectEvent);
        formattedDisconnectEvent = formattedDisconnectEvent.substring(formattedDisconnectEvent.indexOf("]"));
        assertEquals("] {color:red}**Disconnected from server**{color}", formattedDisconnectEvent);
        
        final JoinEvent joinEvent = new JoinEvent(bot, "channel", "sender", "login", "hostname");
        String formattedJoinEvent = this.eventFormatter.formatEvent(joinEvent);
        formattedJoinEvent = formattedJoinEvent.substring(formattedJoinEvent.indexOf("]"));
        assertEquals("] {color:red}* sender (login@hostname) has joined channel{color}", formattedJoinEvent);
        
        final KickEvent kickEvent = new KickEvent(bot, "channel", "kickerNick", "kickerLogin", "kickerHostname", "recipientNick", "reason");
        String formattedKickEvent = this.eventFormatter.formatEvent(kickEvent);
        formattedKickEvent = formattedKickEvent.substring(formattedKickEvent.indexOf("]"));
        assertEquals("] {color:red}* recipientNick was kicked from channel by kickerNick (reason){color}", formattedKickEvent);
        
        final MessageEvent messageEvent = new MessageEvent(bot, "channel", "sender", "login", "hostname", "message");
        String formattedMessageEvent = this.eventFormatter.formatEvent(messageEvent);
        formattedMessageEvent = formattedMessageEvent.substring(formattedMessageEvent.indexOf("]"));
        assertEquals("] {color:black} <sender> message{color}", formattedMessageEvent);
        
        final ModeEvent modeEvent = new ModeEvent(bot, "channel", "sourceNick", "sourceLogin", "sourceHostname", "mode");
        String formattedModeEvent = this.eventFormatter.formatEvent(modeEvent);
        formattedModeEvent = formattedModeEvent.substring(formattedModeEvent.indexOf("]"));
        assertEquals("] {color:blue}* sourceNick sets mode mode{color}", formattedModeEvent);
        
        final NickChangeEvent nickChangeEvent = new NickChangeEvent(bot, "oldNick", "login", "hostname", "newNick");
        String formattedNickChangeEvent = this.eventFormatter.formatEvent(nickChangeEvent);
        formattedNickChangeEvent = formattedNickChangeEvent.substring(formattedNickChangeEvent.indexOf("]"));
        assertEquals("] {color:red}* oldNick is now known as newNick{color}", formattedNickChangeEvent);
        
        final NoticeEvent noticeEvent = new NoticeEvent(bot, "sourceNick", "sourceLogin", "sourceHostname", "target", "notice");
        String formattedNoticeEvent = this.eventFormatter.formatEvent(noticeEvent);
        formattedNoticeEvent = formattedNoticeEvent.substring(formattedNoticeEvent.indexOf("]"));
        assertEquals("] {color:brown}-sourceNick- notice{color}", formattedNoticeEvent);
        
        final PartEvent partEvent = new PartEvent(bot, "channel", "sender", "login", "hostname");
        String formattedPartEvent = this.eventFormatter.formatEvent(partEvent);
        formattedPartEvent = formattedPartEvent.substring(formattedPartEvent.indexOf("]"));
        assertEquals("] {color:red}* sender (login@hostname) has left channel{color}", formattedPartEvent);
        
        final PingEvent pingEvent = new PingEvent(bot, "sourceNick", "sourceLogin", "sourceHostname", "target", "pingValue");
        String formattedPingEvent = this.eventFormatter.formatEvent(pingEvent);
        formattedPingEvent = formattedPingEvent.substring(formattedPingEvent.indexOf("]"));
        assertEquals("] {color:brown}[sourceNick PING]{color}", formattedPingEvent);
        
        final PrivateMessageEvent privateMessageEvent = new PrivateMessageEvent(bot, "sender", "login", "hostname", "message");
        String formattedPrivateMessageEvent = this.eventFormatter.formatEvent(privateMessageEvent);
        formattedPrivateMessageEvent = formattedPrivateMessageEvent.substring(formattedPrivateMessageEvent.indexOf("]"));
        assertEquals("] {color:brown}<- *sender* message{color}", formattedPrivateMessageEvent);
        
        final QuitEvent quitEvent = new QuitEvent(bot, "sourceNick", "sourceLogin", "sourceHostname", "reason");
        String formattedQuitEvent = this.eventFormatter.formatEvent(quitEvent);
        formattedQuitEvent = formattedQuitEvent.substring(formattedQuitEvent.indexOf("]"));
        assertEquals("] {color:red}* sourceNick (sourceLogin@sourceHostname) Quit (reason){color}", formattedQuitEvent);
        
        final TimeEvent timeEvent = new TimeEvent(bot, "sourceNick", "sourceLogin", "sourceHostname", "target");
        String formattedTimeEvent = this.eventFormatter.formatEvent(timeEvent);
        formattedTimeEvent = formattedTimeEvent.substring(formattedTimeEvent.indexOf("]"));
        assertEquals("] {color:brown}[sourceNick TIME]{color}", formattedTimeEvent);
        
        final TopicEvent topicEvent = new TopicEvent(bot, "channel", "topic", "setBy", 0, true);
        String formattedTopicEvent = this.eventFormatter.formatEvent(topicEvent);
        formattedTopicEvent = formattedTopicEvent.substring(formattedTopicEvent.indexOf("]"));
        assertEquals("] {color:blue}* Topic is 'topic' set by setBy on 18:00:00 CST(-0600){color}", formattedTopicEvent);
        
        final VersionEvent versionEvent = new VersionEvent(bot, "sourceNick", "sourceLogin", "sourceHostname", "target");
        String formattedVersionEvent = this.eventFormatter.formatEvent(versionEvent);
        formattedVersionEvent = formattedVersionEvent.substring(formattedVersionEvent.indexOf("]"));
        assertEquals("] {color:brown}[sourceNick VERSION]{color}", formattedVersionEvent);
    }
}
