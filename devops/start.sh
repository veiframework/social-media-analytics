#!/bin/bash

DIR=$(cd $(dirname $0); pwd)
echo $DIR
cd $DIR

APP=$(ls "{{APP_NAME}}.jar")
if [ "$?" != "0" ];then
  echo "jar file not exits"
  exit 66
fi




echo "execting" $APP
java $JAVA_OPTS -jar $DIR/$APP
