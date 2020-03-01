#!/bin/bash -x

#example invocation: ./run-bob.sh  Millionaires 127.0.0.1 54321
if [$# -ne 3];
then
    echo "usage: ./run-bob.sh classname hostname port";
     exit -1;
fi

#must be run in emp-jni dir
export CLASSNAME=$1
export HOST=$2
export PORT=$3
export JAVACPP_JAR=$HOME/.m2/repository/org/bytedeco/javacpp/1.4.4/javacpp-1.4.4.jar
export EMP_JNI_ROOT=$PWD
export JAVACPP_WORKING_DIRECTORY=$EMP_JNI_ROOT/target/classes

ulimit -c unlimited
cd $JAVACPP_WORKING_DIRECTORY
java -cp $JAVACPP_JAR:$JAVACPP_WORKING_DIRECTORY  org.vaultdb.compiler.emp.generated.$CLASSNAME 2 $HOST $PORT
