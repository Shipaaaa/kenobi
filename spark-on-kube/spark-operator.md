# Запуск спарка

* [Webinar mail.ru spark_k8s](https://github.com/stockblog/webinar_spark_k8s)
* [GoogleCloudPlatform/spark-on-k8s-operator](https://github.com/GoogleCloudPlatform/spark-on-k8s-operator)

```shell
# Reset cluster
ansible-playbook -i inventory/mycluster/hosts.yaml reset.yml --become --become-user=root
```

```shell
# Setup cluster
ansible-playbook -i inventory/mycluster/hosts.yaml cluster.yml --become --become-user=root
```

```shell
curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3
chmod 700 get_helm.sh
./get_helm.sh
```

```shell
helm install spark-app ~/spark-operator --namespace spark-operator --create-namespace --set sparkJobNamespace=default --set webhook.enable=true
```

```shell
./run-pyspark-helm.sh 5
```
