#! /bin/sh

SDIR=`dirname $0` 

if [ -e $SDIR/setenv-build-jogamp-x86.sh ] ; then
    . $SDIR/setenv-build-jogamp-x86.sh
fi

#    -Dc.compiler.debug=true 
#    -Dgluegen.cpptasks.detected.os=true \
#    -DisUnix=true \
#    -DisLinux=true \
#    -DisLinuxX86=true \
#    -DisX11=true \
#    -Dgluegen-cpptasks.file=`pwd`/lib/gluegen-cpptasks-linux-32bit.xml \
#

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
    -Drootrel.build=build-x86 \
    $* 2>&1 | tee make.gluegen.all.linux-x86.log
