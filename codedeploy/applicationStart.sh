#!/bin/bash -v

sudo systemctl stop tomcat8

# shellcheck disable=SC2164
cd ~
source /env/properties.sh
sudo cp /home/cloudwatch-config.json /opt/cloudwatch-config.json

sudo systemctl enable amazon-cloudwatch-agent.service

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
    -a fetch-config \
    -m ec2 \
    -c file:/opt/cloudwatch-config.json \
    -s

sudo systemctl restart amazon-cloudwatch-agent

java -jar /home/webapp.jar > /dev/csye6225.log 2> /dev/csye6225.log < /dev/csye6225.log &
