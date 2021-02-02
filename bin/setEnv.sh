if [ "${JAVA_DIR}" = "" ] ; then
	JAVA_DIR=$JAVA_HOME
fi

if [ "${APPLICATION_PORT}" = "" ] ; then
	APPLICATION_PORT="8080"
fi

if [ "${RUN_MODE}" = "" ] ; then
	RUN_MODE="local"
fi

if [ "${LOG_PATH}" = "" ] ; then
	LOG_PATH="../logs"
fi

if [ "${LICENSE_PATH}" = "" ] ; then
	LICENSE_PATH="../license"
fi

if [ "${DEBUG_PORT}" = "" ] ; then
	DEBUG_PORT="8787"
fi

if [ "${RUN_MODE}" = "production" ] ; then
	DEBUG_FLAG="false"
fi

MEM_ARGS="-Xms2048m -Xmx3072m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -XX:SurvivorRatio=8"
GC_ARGS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200"

if [[ "${OPEN_GC_LOG}" = "true" ]] && [[ "${RUN_MODE}" != "production" ]] ; then
    GC_ARGS="-XX:+PrintGCDetails -Xloggc:${LOG_PATH}/gc/gc.log -XX:+PrintGCTimeStamps"
fi

OOM_ARGS="-XX:+HeapDumpOnOutOfMemoryError"

if [ "${DEBUG_FLAG}" = "true" ] ; then
	JAVA_DEBUG="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=${DEBUG_PORT},server=y,suspend=n -Djava.compiler=NONE"
fi

if [ "${SBA_MONITOR_FLAG}" = "true" ] ; then
    . ./sba_security.properties
	SPRING_ADMIN_CLIENT_OPTIONS="--spring.boot.admin.client.url=${url} --spring.boot.admin.client.username=${username} --spring.boot.admin.client.username=${password}";
fi

JAVA_OPTIONS="${MEM_ARGS} ${GC_ARGS} ${MEM_ARGS} ${JAVA_DEBUG} -Dfile.encoding=UTF-8 "
