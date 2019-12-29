#! /bin/sh

SDIR=`dirname $0` 

if [ -e $SDIR/setenv-build-jogamp-x86_64.sh ] ; then
    . $SDIR/setenv-build-jogamp-x86_64.sh
fi

# aarch64-linux-gnueabi == aarch64 triplet
PATH=`pwd`/lib/toolchain/aarch64-linux-gnueabi/bin:$PATH
export PATH

#    -Dc.compiler.debug=true 
#    -Dgluegen.cpptasks.detected.os=true \
#    -DisUnix=true \
#    -DisLinux=true \
#    -DisLinuxARM64=true \
#    -DisX11=true \

export TARGET_PLATFORM_SYSROOT=`gcc --print-sysroot`
export TARGET_PLATFORM_USRROOT=/opt-linux-arm64
export TARGET_PLATFORM_USRLIBS=$TARGET_PLATFORM_USRROOT/usr/lib
export TARGET_JAVA_LIBS=$TARGET_PLATFORM_USRROOT/jre/lib/aarch64

export GLUEGEN_CPPTASKS_FILE="lib/gluegen-cpptasks-linux-aarch64.xml"

#export JUNIT_DISABLED="true"
export JUNIT_RUN_ARG0="-Dnewt.test.Screen.disableScreenMode"

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
    -Drootrel.build=build-linux-aarch64 \
    $* 2>&1 | tee make.gluegen.all.linux-aarch64-cross.log
