#!/bin/bash
set -e

spark-submit \
  --master k8s://https://$(kubectl get node node3 -o jsonpath='{.status.addresses[0].address}'):6443 \
  --deploy-mode cluster \
  --name spark-pi \
  --class org.apache.spark.examples.SparkPi \
  --conf spark.executor.instances=2 \
  --conf spark.kubernetes.container.image=k8s-py/spark-py \
  --conf spark.kubernetes.namespace=default \
  local:///usr/local/spark/examples/jars/spark-examples_2.12-3.1.1.jar
