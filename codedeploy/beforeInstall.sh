#!/bin/bash -v

# shellcheck disable=SC2046
sudo kill -9 $(lsof -t -i:8080)
source /env/properties.sh
