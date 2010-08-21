#!/bin/bash

## CHANGE THESE AS NEEDED FOR YOUR APPLICATION 
APP_BASE=`dirname $0`
APP_LIB=$APP_BASE/lib
APP_CONF=$APP_BASE/config
APP_CLASS=org.jasig.irclog.BotRunner
APP_OPTS=""

CMD_STOP_AND_WAIT=SHUTDOWN_AND_WAIT
CMD_STOP_NO_WAIT=SHUTDOWN_NO_WAIT
CMD_STATUS=STATUS





# Check for exactly 1 argument
if [ $# -ne 1 ]; then
    echo "Usage: $0 {start|restart|stop|stop-nowait|status}" >&2
    exit 127
fi

ACTION=$1

# Validate Configuration
if [ ! -d $JAVA_HOME ]
then
    echo "JAVA_HOME Directory '$JAVA_HOME' does not exist" >&2
    exit 127
fi

cd $APP_BASE

APP_CLASSPATH=$APP_CONF
for jarFile in $(ls $APP_LIB/*.jar); do
    APP_CLASSPATH=${APP_CLASSPATH}:$jarFile
done

APP_START="$JAVA_HOME/bin/java -cp $APP_CLASSPATH $APP_CLASS $APP_OPTS"
APP_STOP="$JAVA_HOME/bin/java -cp $APP_CLASSPATH com.googlecode.shutdownlistener.ShutdownUtility"

function start {
    echo "Starting Application ..."
    nohup $APP_START &
    echo "Started Application in background"
}
function stop {
    echo "Stopping Application ..."
    $APP_STOP $CMD_STOP_AND_WAIT
    APP_STATS=$?
    
    if [ $APP_STATS -eq 1 ]
    then
        echo "Application was not running or failed to stop"
    else
        echo "Application stopped"
    fi
}
function stopNowait {
    echo "Stopping Application ..."
    $APP_STOP $CMD_STOP_NO_WAIT
}

case "$ACTION" in
start)
    start
    ;;

stop)
    stop
    ;;

status)
    $APP_STOP $CMD_STATUS
    APP_STATS=$?
    if [ $APP_STATS -eq 1 ]
    then
        echo "Application is not running"
    fi
    
    ;;

restart)
    stop

    start
    ;;

stop-nowait)
    stopNowait
    ;;

esac
