# K8s dashboard

Port-forwarding:

```
ssh -L 8001:127.0.0.1:8001 root@samos.dozen.mephi.ru -p 9007
```

Create alias:

```shell
sudo ln -s ~/dashboard/dashboard.sh /usr/local/bin/dashboard
```


[Web UI](http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/#/login)
