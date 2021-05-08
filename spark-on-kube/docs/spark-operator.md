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
helm install spark-app spark-operator/spark-operator --namespace spark-operator --create-namespace --set sparkJobNamespace=default --set webhook.enable=true
```