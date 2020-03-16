#!/bin/bash -v

if [ -z "$(sudo lsof -t -i:8080)" ]
then
      echo "nothing runs on 8080"
else
      fuser -k 8080/tcp
fi