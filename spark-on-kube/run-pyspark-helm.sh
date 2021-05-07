#!/bin/bash

EXECUTOR_INSTANCES=$1

if [ -z "$EXECUTOR_INSTANCES" ]; then
  echo "Error. Please enter executor count."
  exit 1
fi

helm uninstall pyspark-app
helm install pyspark-app ~/pyspark-helm --set executor.instances="$EXECUTOR_INSTANCES"
