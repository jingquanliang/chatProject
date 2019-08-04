#!/bin/bash

gradle clean
gradle build

# docker stop jcore
# docker rm jcore
# docker rmi -f jing/core

# docker build -t jing/core .

# docker run -d -p 9999:9999 --name jcore  jing/core

docker-compose down
docker-compose up -d