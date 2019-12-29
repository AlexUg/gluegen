#! /bin/sh

if [ -e /usr/local/etc/profile.ant ] ; then
    . /usr/local/etc/profile.ant
fi

#    -Dc.compiler.debug=true 
#
#    -Dtarget.sourcelevel=1.6 \
#    -Dtarget.targetlevel=1.6 \
#    -Dtarget.rt.jar=/opt-share/jre1.6.0_30/lib/rt.jar \

# Force OSX SDK 10.6, if desired
# export SDKROOT=macosx10.6

JAVA_HOME=`/usr/libexec/java_home -version 8`
PATH=$JAVA_HOME/bin:$PATH
export JAVA_HOME PATH

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
    -Drootrel.build=build-macosx \
    $* 2>&1 | tee make.gluegen.all.macosx.log
