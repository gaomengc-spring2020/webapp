#!/bin/bash -v

sudo systemctl stop tomcat8

source /env/properties.sh
java -jar /home/webapp.jar > /dev/null 2> /dev/null < /dev/null &
