#! /bin/sh

#    -Dc.compiler.debug=true 
#    -Dgluegen.cpptasks.detected.os=true \
#    -DisUnix=true \
#    -DisLinux=true \
#    -DisLinuxX86=true \
#    -DisX11=true \

# arm-linux-gnueabihf == armhf triplet
export TARGET_PLATFORM_USRROOT=
export TARGET_PLATFORM_USRLIBS=$TARGET_PLATFORM_USRROOT/usr/lib/arm-linux-gnueabihf
export TARGET_JAVA_LIBS=$TARGET_PLATFORM_USRROOT/usr/lib/jvm/java-11-openjdk-armhf/jre/lib/arm

export GLUEGEN_CPPTASKS_FILE="lib/gluegen-cpptasks-linux-armv6hf-ontarget.xml"

if [ -z ${SOURCE_LEVEL} ]
then
    export SOURCE_LEVEL=1.8
fi
if [ -z ${TARGET_LEVEL} ]
then
    export TARGET_LEVEL=${SOURCE_LEVEL}
fi
if [ -z ${JAVA_HOME} ]
then
    export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
fi
if [ -z ${TARGET_RT_JAR} ]
then
    if [ -f ${JAVA_HOME}/lib/rt.jar ]
    then
        export TARGET_RT_JAR=${JAVA_HOME}/lib/rt.jar
    elif [ -f ${JAVA_HOME}/jre/lib/rt.jar ]
    then
        export TARGET_RT_JAR=${JAVA_HOME}/jre/lib/rt.jar
    fi
fi

if [ -z ${JOGAMP_JAR_CODEBASE} ]
then
    export JOGAMP_JAR_CODEBASE="Codebase: *.jogamp.org"
fi


ant \
    -Drootrel.build=build-linux-armv6hf \
    $* 2>&1 | tee make.gluegen.all.linux-armv6hf.log
