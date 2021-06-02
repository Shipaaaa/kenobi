kubectl create namespace ignite-server
kubectl apply -f ./ignite-server/ignite-service.yaml
kubectl apply -f ./ignite-server/ignite-account.yaml
./deploy-configmap.sh
kubectl apply -f ./ignite-deployment.yaml
