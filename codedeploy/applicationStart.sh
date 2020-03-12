#!/bin/bash -v

# shellcheck disable=SC2046
if [ -z "$(lsof -t -i:8080)" ]
then
      echo "nothing runs on 8080"
else
      sudo kill -9 $(lsof -t -i:8080)
fi



sudo java -jar /home/webapp.jar