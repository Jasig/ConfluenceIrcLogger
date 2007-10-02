/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package org.jasig.irclog;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.irclog.config.ChannelLogger;
import org.jasig.irclog.config.ConfluenceServer;
import org.jasig.irclog.config.IrcServer;
import org.jasig.irclog.config.LogBotConfig;
import org.jasig.irclog.config.Nick;
import org.jasig.irclog.events.handlers.ChannelManager;
import org.jasig.irclog.events.handlers.ChannelNotifier;
import org.jasig.irclog.events.handlers.FilteringEventHandler;
import org.jasig.irclog.events.handlers.FormattingEventHandler;
import org.jasig.irclog.events.handlers.NickAuthHandler;
import org.jasig.irclog.events.handlers.SplittingEventHandler;
import org.jasig.irclog.events.handlers.filter.ChannelEventFilter;
import org.jasig.irclog.events.handlers.filter.OrFilter;
import org.jasig.irclog.events.handlers.filter.SelfTargetedEventFilter;
import org.jasig.irclog.events.handlers.filter.TargetedEventFilter;
import org.jasig.irclog.messages.BufferingMessageHandler;
import org.jasig.irclog.messages.ConfluenceMessageHandler;
import org.jasig.irclog.messages.LoggingMessageHandler;
import org.jasig.irclog.messages.XmlEscapingMessageHandler;
import org.jasig.irclog.messages.support.MessageHandlerFlusher;

import com.thoughtworks.xstream.XStream;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class BotRunner {
    private static final String EVENT_FORMATING_PROPERTIES = "/confluenceEventFormating.properties";
    private static final Log LOG = LogFactory.getLog(BotRunner.class);

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        final LogBotConfig botConfig = loadConfig(args);
        if (botConfig == null) {
            return;
        }

        final EventLogBot bot = setupLogBot(botConfig);

        final IrcServer ircServer = botConfig.getIrcServer();
        final String host = ircServer.getHost();
        final String port = ircServer.getPort();
        final String password = ircServer.getPassword();

        try {
            if (port != null && password != null) {
                bot.connect(host, Integer.parseInt(port), password);
            }
            else if (password != null) {
                bot.connect(host, 6667, password);
            }
            else if (port != null) {
                bot.connect(host, Integer.parseInt(port));
            }
            else {
                bot.connect(host);
            }
        }
        catch (Exception e) {
            LOG.error(bot + " failed with following exception.", e);
        }
    }

    /**
     * Creates and configures an EventLogBot instance.
     */
    private static EventLogBot setupLogBot(final LogBotConfig botConfig) {
        final Properties eventFormats = getEventFormats();
        
        final EventLogBot bot = new EventLogBot();
        final SplittingEventHandler splittingEventHandler = new SplittingEventHandler();
        bot.setEventHandler(splittingEventHandler);

        //Keeps bot connected & in configured channels
        final ChannelManager channelManager = new ChannelManager();
        splittingEventHandler.registerEventHandler(channelManager);

        //Sends logging notifications to channels & members
        final ChannelNotifier channelNotifier = new ChannelNotifier();
        splittingEventHandler.registerEventHandler(channelNotifier);

        //Authenticates nickname onConnect 
        final NickAuthHandler nickAuthHandler = new NickAuthHandler();
        final Nick nick = botConfig.getNick();
        nickAuthHandler.addPassword(nick.getName(), nick.getPassword());
        splittingEventHandler.registerEventHandler(nickAuthHandler);
        
        final LoggingMessageHandler loggingMessageHandler = new LoggingMessageHandler();
        
        //Format the IrcEvents into String messages based on the properties
        final FormattingEventHandler logFormattingEventHandler = new FormattingEventHandler();
        logFormattingEventHandler.setEventFormats(eventFormats);
        logFormattingEventHandler.setMessageHandler(loggingMessageHandler);
        
        //Filter events to only those for this bot
        final FilteringEventHandler logFilteringEventHandler = new FilteringEventHandler();
        logFilteringEventHandler.setEventFilter(new SelfTargetedEventFilter());
        logFilteringEventHandler.setTargetHandler(logFormattingEventHandler);
        
        //Register the self event logging with the root handler
        splittingEventHandler.registerEventHandler(logFilteringEventHandler);

        //Configure each ChannelLogger
        for (final ChannelLogger channelLogger : botConfig.getChannelLoggers()) {
            final String channel = channelLogger.getIrcChannel();
            channelManager.addChannel(channel);

            final String message = channelLogger.getNotification();
            channelNotifier.addOnJoinMessage(channel, message);

            //Configure event logging chain
            final ConfluenceMessageHandler confluenceMessageHandler = new ConfluenceMessageHandler();
            confluenceMessageHandler.setConfluenceServer(channelLogger.getConfluenceServer());
            confluenceMessageHandler.setLogPagesTitleFormats(channelLogger.getLogPagesTitleFormats());
            confluenceMessageHandler.setSpaceKey(channelLogger.getSpaceKey());
//            confluenceMessageHandler.setMessageEscapePairs(messageEscapePairs); TODO config this
            
            //Configure message buffering to avoid too-frequent Confluence updates
            final BufferingMessageHandler bufferingMessageHandler = new BufferingMessageHandler();
            bufferingMessageHandler.setMessageBufferSize(64); //TODO config this
            bufferingMessageHandler.setMessageHandler(confluenceMessageHandler);
            
            //Configure Timer to flush the buffer periodically 
            final MessageHandlerFlusher messageHandlerFlusher = new MessageHandlerFlusher();
            messageHandlerFlusher.setPeriod(5 * 60); //TODO config this
            messageHandlerFlusher.setMessageHandler(bufferingMessageHandler);
            
            //XML Escape messages before buffering
            final XmlEscapingMessageHandler xmlEscapingMessageHandler = new XmlEscapingMessageHandler();
            xmlEscapingMessageHandler.setMessageHandler(bufferingMessageHandler);
            
            //Format the IrcEvents into String messages based on the properties
            final FormattingEventHandler formattingEventHandler = new FormattingEventHandler();
            formattingEventHandler.setEventFormats(eventFormats);
            formattingEventHandler.setMessageHandler(xmlEscapingMessageHandler);
            
            //Filter events to only those for this channel
            final FilteringEventHandler filteringEventHandler = new FilteringEventHandler();
            final OrFilter orFilter = new OrFilter();
            orFilter.addFilter(new ChannelEventFilter(channel));
            orFilter.addFilter(new TargetedEventFilter(channel));
            filteringEventHandler.setEventFilter(orFilter);
            filteringEventHandler.setTargetHandler(formattingEventHandler);
            
            //Add the filter chain to the root event handler
            splittingEventHandler.registerEventHandler(filteringEventHandler);
        }

        bot.setBotName(nick.getName());
        bot.setAutoNickChange(true);
        return bot;
    }
    
    private static Properties getEventFormats() {
        final InputStream eventFormatsStream = BotRunner.class.getResourceAsStream(EVENT_FORMATING_PROPERTIES);
        final Properties eventFormats = new Properties();
        try {
            eventFormats.load(eventFormatsStream);
        }
        catch (IOException ioe) {
            throw new IllegalStateException("Cannot load event formats Properties file: " + EVENT_FORMATING_PROPERTIES, ioe);
        }
        return eventFormats;
    }

    /**
     * @param args
     * @return
     * @throws FileNotFoundException
     */
    private static LogBotConfig loadConfig(String[] args) throws FileNotFoundException {
        final Options options = new Options();

        final Option configFileOpt = new Option("c", "Configuration File");
        configFileOpt.setLongOpt("config");
        configFileOpt.setRequired(true);
        configFileOpt.setArgs(1);
        options.addOption(configFileOpt);

        try {
            final CommandLineParser parser = new PosixParser();
            parser.parse(options, args);
        }
        catch (ParseException e) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java " + BotRunner.class.getName(), options, true);
            return null;
        }

        final String configFilePath = configFileOpt.getValue();
        final FileInputStream configInputStream = new FileInputStream(configFilePath);

        final XStream xstream = getConfigParser();
        return (LogBotConfig) xstream.fromXML(configInputStream);
    }

    /**
     * @return A configured XStream parser.
     */
    private static XStream getConfigParser() {
        final XStream xstream = new XStream();

        xstream.alias("logBotConfig", LogBotConfig.class);
        xstream.alias("nick", Nick.class);
        xstream.alias("ircServer", IrcServer.class);
        xstream.alias("channelLogger", ChannelLogger.class);
        xstream.alias("confluenceServer", ConfluenceServer.class);
        xstream.alias("class", Class.class);

        return xstream;
    }
}
