#!/bin/bash
set -e

spark-submit \
  --master k8s://https://$(kubectl get node node3 -o jsonpath='{.status.addresses[0].address}'):6443 \
  --deploy-mode cluster \
  --name spark-pi \
  --class org.apache.spark.examples.SparkPi \
  --conf spark.executor.instances=5 \
  --conf spark.kubernetes.container.image=k8s-py/spark-py \
  local:///opt/spark/examples/jars/spark-examples_2.12-3.1.1.jar
