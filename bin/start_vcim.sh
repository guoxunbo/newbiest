JAVA_DIR=""
APPLICATION_PORT=""
RUN_MODE=""
LOG_PATH=""
DEBUG_FLAG="true"
OPEN_GC_LOG="true"
. ./setEnv.sh
. ./sba_security.properties

JAVA_HOME=$JAVA_DIR

SPRING_ADMIN_CLIENT_OPTIONS="--spring.boot.admin.client.username=${username} --spring.boot.admin.client.username=${password}";

SPRING_OPTIONS="--spring.profiles.active=${RUN_MODE} --logging.path=${LOG_PATH} --server.port=${APPLICATION_PORT} ${SPRING_ADMIN_CLIENT_OPTIONS}"

ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"
APP_PATH=$(find $ROOT -name starter*.jar)

help() {
    echo "help: sh [start|stop_im|stop|restart|status]"
    exit 1
}

is_exist(){
    # Get PID
    PID=$(ps -ef |grep ${APP_PATH} | grep -v $0 |grep -v grep |awk '{print $2}')
    if [ -z "${PID}" ]; then
        return 1
    else
        return 0
    fi
}

start(){
    is_exist
    if [ $? -eq "0" ]; then
        echo "${APP_PATH} is already running, PID=${PID}"
    else
        START_CMD="${JAVA_HOME}/bin/java -jar ${JAVA_OPTIONS} ${APP_PATH} ${SPRING_OPTIONS}"
        echo "${START_CMD}"
        nohup ${START_CMD} &
        PID=$(echo $!)
        echo "${APP_PATH} starting success, PID=$!"
    fi
}

stop(){
    is_exist
    if [ $? -eq "0" ]; then
        curl -X POST "http://localhost:$APPLICATION_PORT/actuator/shutdown"
    else
        echo "There is not the process of ${APP_PATH}"
    fi
}

stop_im(){
    is_exist
    if [ $? -eq "0" ]; then
        kill -9 ${PID}
        echo "${APP_PATH} process stop, PID=${PID}"
    else
        echo "There is not the process of ${APP_PATH}"
    fi
}

restart(){
    stop
    sleep 3
    start
}

status(){
    is_exist
    if [ $? -eq "0" ]; then
            echo "${APP_PATH} is running, PID=${PID}"
    else
            echo "There is not the process of ${APP_PATH}"
    fi
}

case $1 in
"start")
        start
        ;;
"stop_im")
        stop_im
        ;;
"stop")
        stop
        ;;
"restart")
        restart
        ;;
"status")
       status
        ;;
	*)
	help
	;;
esac
exit 0
