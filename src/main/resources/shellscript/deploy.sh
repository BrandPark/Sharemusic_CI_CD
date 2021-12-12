#!/bin/bash

REPOSITORY=/home/ec2-user/app
PROJECT_NAME=sharemusic

echo "> Git Pull"
cd $REPOSITORY/$PROJECT_NAME/
git pull

echo "> Start Build"
cd src/main/resources/static/
npm install
cd $REPOSITORY/$PROJECT_NAME/
./gradlew build

echo "> Copy .jar file"
cp $REPOSITORY/$PROJECT_NAME/build/libs/*.jar $REPOSITORY/

echo "> Find running App PID"
CURRENT_PID=$(pgrep -f ${PROJECT_NAME}.*.jar)

if [ -z "$CURRENT_PID" ]; then echo "> 현재 구동 중인 애플리케이션이 없으므로 바로 실행합니다."
else
  echo "> Running App PID: $CURRENT_PID"
  echo "> 실행 중인 애플리케이션을 종료합니다."
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/ | grep jar | tail -n 1)
nohup java -jar \
-Dspring.config.location=classpath:/application.yml,$REPOSITORY/$PROJECT_NAME/config/application-dev.yml \
-Dspring.profiles.active=dev \
$REPOSITORY/$JAR_NAME 2>&1 &