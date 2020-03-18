#!/bin/bash -v

sudo systemctl stop tomcat8

# shellcheck disable=SC2164
cd ~
source /env/properties.sh
cp /home/cloudwatch-config.json /opt/cloudwatch-config.json

java -jar /home/webapp.jar > /dev/csye6225.log 2> /dev/csye6225.log < /dev/csye6225.log &
