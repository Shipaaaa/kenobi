#!/bin/bash
set -e

spark-submit \
  --master k8s://http://127.0.0.1:8001 \
  --deploy-mode cluster \
  --name test_spark_k8s \
  --conf spark.executor.instances=2 \
  --conf spark.kubernetes.driver.container.image=spark-py/spark \
   local:///root/spark/word_count.py