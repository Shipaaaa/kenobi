kubectl create namespace ignite
kubectl apply -f ./ignite-service.yaml
kubectl apply -f ./ignite-account.yaml
kubectl create configmap ignite-config --namespace=ignite --from-file=ignite-node-configuration.xml
kubectl apply -f ./ignite-deployment.yaml
