# set Java ENV
JAVA_HOME=/usr/java/jdk1.8.0_231-amd64
APPLICATION_PORT=""
SPRING_OPTIONS="--spring.profiles.active=production --logging.path=/app_wms/logs"
if [ "${APPLICATION_PORT}" = "" ] ; then
	APPLICATION_PORT="8080"
fi
SPRING_OPTIONS="${SPRING_OPTIONS} --server.port=${APPLICATION_PORT}"

JAVA_OPTIPNS="-XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -Xms2048m -Xmx3072m -XX:SurvivorRatio=8 -Dfile.encoding=UTF-8"

# Apps Info
APP_HOME=/app_wms/server
APP_NAME=$1

# Shell Info 
usage() {
    echo "Usage: sh [APP_NAME] [start|stop|restart|status]"
    exit 1
}

is_exist(){
        # Get PID
        PID=$(ps -ef |grep ${APP_NAME} | grep -v $0 |grep -v grep |awk '{print $2}')
        # if exist. return 0 else return 1
        if [ -z "${PID}" ]; then
                return 1
        else               
                return 0
        fi
}

start(){
        is_exist
        if [ $? -eq "0" ]; then
                echo "${APP_NAME} is already running, PID=${PID}"
        else    
				START_CMD="${JAVA_HOME}/bin/java -jar ${JAVA_OPTIPNS} ${APP_HOME}/${APP_NAME} ${SPRING_OPTIONS}"
				echo "${START_CMD}"
				#nohup ${JAVA_HOME}/bin/java -jar ${JAVA_OPTIPNS} ${APP_HOME}/${APP_NAME} ${SPRING_OPTIONS} &
                nohup ${START_CMD} &
                PID=$(echo $!)
                echo "${APP_NAME} start success, PID=$!"
        fi
}

stop(){
        is_exist
        if [ $? -eq "0" ]; then
                kill -9 ${PID}
                echo "${APP_NAME} process stop, PID=${PID}"
        else    
                echo "There is not the process of ${APP_NAME}"
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
                echo "${APP_NAME} is running, PID=${PID}"
        else    
                echo "There is not the process of ${APP_NAME}"
        fi
}

case $2 in
"start")
        start
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
	usage
	;;
esac
exit 0
