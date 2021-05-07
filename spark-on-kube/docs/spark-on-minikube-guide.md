# Quick build and deploy manual

## Предустановленные зависимости

* Ubuntu 18.04

## Установка зависимостей

### Установка утилит

```shell script
sudo update
sudo apt install git curl 
```

### Установка kubectl

Нужен для работы с kubernates.

```shell script
# https://kubernetes.io/ru/docs/tasks/tools/install-kubectl/#%D1%83%D1%81%D1%82%D0%B0%D0%BD%D0%BE%D0%B2%D0%BA%D0%B0-kubectl-%D0%B2-linux
sudo apt-get update && sudo apt-get install -y apt-transport-https
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee -a /etc/apt/sources.list.d/kubernetes.list
sudo apt-get update
sudo apt-get install -y kubectl
```

### Установка KVM

При работе с minikube используется виртуализация. Один из драйверов - kvm2.  
Перед установкой необходимо:

1) активировать виртуализацию в BIOS;
2) активировать виртуализацию в виртуальной машине;
3) деактивировать Hyper-V для Windows 10.

```shell script
#https://kubernetes.io/ru/docs/setup/learning-environment/minikube/#%D1%83%D0%BA%D0%B0%D0%B7%D0%B0%D0%BD%D0%B8%D0%B5-%D0%B4%D1%80%D0%B0%D0%B9%D0%B2%D0%B5%D1%80%D0%B0-%D0%B2%D0%B8%D1%80%D1%82%D1%83%D0%B0%D0%BB%D1%8C%D0%BD%D0%BE%D0%B9-%D0%BC%D0%B0%D1%88%D0%B8%D0%BD%D1%8B
sudo apt update
sudo apt install cpu-checker

# Проверяем доступность KVM
kvm-ok

sudo apt install qemu-kvm libvirt-bin bridge-utils virtinst virt-manager
sudo systemctl is-active libvirtd

sudo usermod -aG libvirt $USER
sudo usermod -aG kvm $USER
```

### Установка Docker

Также можно запускать minikube в режиме без драйвера виртуализации. Для этого потребуется установить Docker. Но рещение
с KVM2 считается более правильным и безопасным.

```shell script
#https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository
sudo apt-get update

sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"

sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io

sudo usermod -aG docker ${USER}
su - ${USER}

# Проверка Docker
sudo docker run hello-world
```

### Установка Minikube

```shell script
# https://kubernetes.io/ru/docs/tasks/tools/install-minikube/

curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 \
  && chmod +x minikube
sudo apt-get install -y conntrack

# настройка автодополнения в текущую сессию bash, предварительно должен быть установлен пакет bash-completion .
source <(kubectl completion bash) 
# добавление автодополнения autocomplete постоянно в командную оболочку bash.
echo "source <(kubectl completion bash)" >> ~/.bashrc 
```

### Запуск Minikube

```shell script
sudo chown -R $USER ~/.kube
minikube config set vm-driver none
minikube config set cpus 4
minikube config set memory 8192
minikube start --vm-driver=none
minikube status
minikube dashboard

# Accessing the Default Token

kubectl -n kube-system describe secret default

#Generate command completion for a shell
apt-get install bash-completion
source /etc/bash-completion
source <(minikube completion bash) # for bash users
source <(minikube completion zsh) # for zsh users

echo "source <(minikube completion zsh)" >> ~/.zshrc 
```

### Установка JDK8

```shell script
sudo yum install java-1.8.0-openjdk
```

### Установка Spark

#### Spark с версией Hadoop 2.7

```shell script
wget https://downloads.apache.org/spark/spark-3.0.1/spark-3.0.1-bin-hadoop2.7.tgz
tar xvf spark-3.0.1-bin-hadoop2.7.tgz
sudo mv spark-3.0.1-bin-hadoop2.7 /opt/spark

cd /opt/spark
```

#### Spark с версией Hadoop 3.2

```shell script
mkdir ~/install && cd ~/install
wget https://mirror.linux-ia64.org/apache/spark/spark-3.1.1/spark-3.1.1-bin-hadoop3.2.tgz
tar xvf spark-3.1.1-bin-hadoop3.2.tgz
rm -rf spark-3.1.1-bin-hadoop3.2.tgz

sudo mv spark-3.1.1-bin-hadoop3.2 /opt/spark
sudo ln -s /opt/spark /usr/local/spark
export SPARK_HOME=/usr/local/spark

cd /opt/spark

# check spark-shell
./bin/spark-shell
```

### Сборка образов spark и загрузка его в docker registry kubernetes (-m)

```shell script
# Java
bin/docker-image-tool.sh -r k8s -m build
# Python
bin/docker-image-tool.sh -r k8s-py -p ./kubernetes/dockerfiles/spark/bindings/python/Dockerfile -m build
# List of docker images
sudo docker image ls 
```

### Добавить разрешение для запуска executor spark node в kubernetes

```shell script
kubectl create clusterrolebinding default --clusterrole=edit --serviceaccount=default:default --namespace=default
```

### Установка Python

```shell script
# https://conda.io/projects/conda/en/latest/user-guide/install/linux.html
wget https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh

bash Miniconda3-latest-Linux-x86_64.sh

conda install -c anaconda python=3.7
conda update --all
```

### Установка pyspark

```shell script
conda install -c conda-forge pyspark
```

### Проверка pyspark

```python
import pyspark

pyspark.__version__
```

Версии pyspark и spark должны совпадать. Установить нужною версию можно комадой

```shell script
conda install -n <env name>  pyspark=3.0.1
```

## Пример выполнения параллельной программы

При этом при создании контекста в Kubernetes создается указанное в конфигурации число executors, а при остановке
контекста executors удаляются из Kubernetes

```python
import os
import subprocess
import pyspark

os.environ["PYSPARK_PYTHON"] = "python3"
os.environ["PYSPARK_DRIVER_PYTHON"] = "python3"

result = subprocess.run(['minikube', 'ip'], stdout=subprocess.PIPE)
ip = result.stdout.decode('utf-8').rstrip()
master_ip = "k8s://https://" + ip + ":8443"

conf = pyspark.SparkConf()
conf.setMaster(master_ip)
conf.set("spark.kubernetes.container.image", "k8s-py/spark-py")
conf.set("spark.executor.instances", "5")
conf.set("spark.kubernetes.namespace", "default")
sc = pyspark.SparkContext(conf=conf)

sc.parallelize(range(0, 100000)).count()

sc.stop()
```
