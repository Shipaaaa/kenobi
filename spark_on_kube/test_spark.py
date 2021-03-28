ip=!(kubectl get node minikube -o jsonpath='{.status.addresses[0].address}')
master_ip="k8s://https://"+ip[0]+":8443»

conf = pyspark.SparkConf()
conf.setMaster(master_ip)
conf.set("spark.kubernetes.container.image", "k8s2/spark-py")
conf.set("spark.executor.instances", "1")
conf.set("spark.kubernetes.namespace", "default")
sc = pyspark.SparkContext(conf=conf)

sc.parallelize(range(0, 1000)).count()

sc.stop()