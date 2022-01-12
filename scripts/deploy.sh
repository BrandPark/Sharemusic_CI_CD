#!/bin/bash

REPOSITORY=/home/ec2-user/app
DEPLOY_DIR=sharemusic/codedeploy-deploy

echo "> Copy .jar"
cp $REPOSITORY/$DEPLOY_DIR/*.jar $REPOSITORY

echo "> Find running App PID"
CURRENT_PID=$(pgrep -fl sharemusic | grep jar | awk '{print $1}')

if [ -z "$CURRENT_PID" ]; then echo "> 현재 구동 중인 애플리케이션이 없으므로 바로 배포합니다."
else
  echo "> Running App PID: $CURRENT_PID"
  echo "> 실행 중인 애플리케이션을 종료합니다."
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"
JAR_PATH=$(ls -tr $REPOSITORY/*.jar | grep jar | tail -n 1)

echo "> $JAR_PATH 에 실행 권한 추가"
chmod +x $JAR_PATH

echo "> $JAR_PATH 실행"
nohup java -jar \
    -Dspring.config.location=classpath:/application.yml,classpath:/application-real.yml,$REPOSITORY/$DEPLOY_DIR/application-realdb.yml,$REPOSITORY/$DEPLOY_DIR/application-secretkey.yml \
    -Dspring.profiles.active=real,realdb,secretkey\
    $JAR_PATH > $REPOSITORY/nohup.out 2>&1 &