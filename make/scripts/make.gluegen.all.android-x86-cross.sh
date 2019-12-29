#! /bin/sh

SDIR=`dirname $0` 

if [ -e ${SDIR}/setenv-build-jogamp-x86_64.sh ] ; then
    . ${SDIR}/setenv-build-jogamp-x86_64.sh
fi

LOGF=make.gluegen.all.android-x86-cross.log
rm -f ${LOGF}

export ANDROID_HOME=/opt-linux-x86_64/android-sdk-linux_x86_64
export ANDROID_API_LEVEL=24
export ANDROID_HOST_TAG=linux-x86_64
export ANDROID_ABI=x86

if [ -e ${SDIR}/setenv-android-tools.sh ] ; then
    . ${SDIR}/setenv-android-tools.sh >> $LOGF 2>&1
else
    echo "${SDIR}/setenv-android-tools.sh doesn't exist!" 2>&1 | tee -a ${LOGF}
    exit 1
fi

export GLUEGEN_CPPTASKS_FILE="lib/gluegen-cpptasks-android-x86.xml"
export PATH=${ANDROID_TOOLCHAIN_ROOT}/${ANDROID_TOOLCHAIN_NAME}/bin:${ANDROID_TOOLCHAIN_ROOT}/bin:${ANDROID_HOME}/platform-tools:${ANDROID_BUILDTOOLS_ROOT}:${PATH}
echo PATH ${PATH} 2>&1 | tee -a ${LOGF}
echo clang `which clang` 2>&1 | tee -a ${LOGF}

export NODE_LABEL=.

export HOST_UID=jogamp
# jogamp02 - 10.1.0.122
export HOST_IP=10.1.0.122
export HOST_RSYNC_ROOT=PROJECTS/JogAmp

export TARGET_UID=jogamp
export TARGET_IP=panda02
#export TARGET_IP=jautab03
#export TARGET_IP=jauphone04
export TARGET_ADB_PORT=5555
# needs executable bit (probably su)
export TARGET_ROOT=/data/projects
export TARGET_ANT_HOME=/usr/share/ant

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


#export JUNIT_DISABLED="true"
#export JUNIT_RUN_ARG0="-Dnewt.test.Screen.disableScreenMode"


#BUILD_ARCHIVE=true \
ant \
    -Drootrel.build=build-android-x86 \
    -Dgcc.compat.compiler=clang \
    $* 2>&1 | tee -a ${LOGF}

