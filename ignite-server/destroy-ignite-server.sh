kubectl delete -f ./ignite-deployment.yaml
kubectl delete configmap ignite-config --namespace=ignite
kubectl delete -f ./ignite-service.yaml
kubectl delete -f ./ignite-account.yaml
kubectl delete namespace ignite
