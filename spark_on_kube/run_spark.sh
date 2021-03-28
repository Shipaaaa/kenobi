#!/bin/bash
set -e

bin/spark-submit \
   --master k8s://https://$(kubectl get node minikube -o jsonpath='{.status.addresses[0].address}'):8443 \
   --deploy-mode client \
   --name spark-pi \
   --class org.apache.spark.examples.SparkPi \
   --conf spark.executor.instances=2 \
   --conf spark.kubernetes.container.image=k8s/spark \
   --conf spark.kubernetes.namespace=default \
   local:///opt/spark/examples/jars/spark-examples_2.11-2.4.4.jar
