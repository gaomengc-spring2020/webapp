#!/bin/bash -v

sudo systemctl stop tomcat8
# shellcheck disable=SC2046
sudo kill -9 $(lsof -t -i:8080)

sudo java -jar /home/webapp.jar