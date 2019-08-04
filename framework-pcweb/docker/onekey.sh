#!/bin/bash

#gradle clean
#gradle build
rm -f jar-web-content-0.1.0.jar
rm -f jdk-8u171-linux-x64.tar.gz

cp ../build/libs/jar-web-content-0.1.0.jar ./ ##工Dockerfile文件使用

cp ../../jdk-8u171-linux-x64.tar.gz ./  ##工Dockerfile文件使用


# docker stop jcore
# docker rm jcore
# docker rmi -f jing/core

# docker build -t jing/core .

# docker run -d -p 9999:9999 --name jcore  jing/core

docker-compose down --rmi all
docker-compose up -d