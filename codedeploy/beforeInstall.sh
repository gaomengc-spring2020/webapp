#!/bin/bash -v

# shellcheck disable=SC2046
if [ -z "$(sudo lsof -t -i:8080)" ]
then
      echo "nothing runs on 8080"
else
      sudo systemctl stop tomcat8
fi

# shellcheck disable=SC2164
cd /opt
sudo rm /opt/cloudwatch-config.json
