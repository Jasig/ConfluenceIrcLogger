#!/bin/sh

if [ $# -ne 1 ]; then
         echo 1>&2 "Usage: $0 {start|stop}"
         exit 127
fi

# Include jasigadm settings
. /jasig/etc/bashrc

BOT_BASE=`dirname $0`
BOT_PID="$BOT_BASE/bot.pid"
LOG_DIR="$BOT_BASE"

mkdir -p $LOG_DIR

case "$1" in
start)
        echo "Starting JA-SIG Log Bot..."
        cd $BOT_BASE
        "$JAVA_HOME"/bin/java -Dservice=ircbot -jar ConfluenceIrcLogger-1.0.0-RC3/ConfluenceIrcLogger-1.0.0-RC3.jar -c botConfig.xml >> "$LOG_DIR"/IRCLogBot.out 2>&1 &
        echo $! > $BOT_PID
        ;;

stop)
        echo "Stopping JA-SIG Log Bot..."
        PID=`cat $BOT_PID`
        kill $PID
        rm $BOT_PID
        ;;

esac

exit 0
