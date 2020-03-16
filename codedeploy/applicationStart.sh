#!/bin/bash -v

sudo systemctl stop tomcat8

source ~/.bash_profile
java -jar /home/webapp.jar > /dev/null 2> /dev/null < /dev/null &
