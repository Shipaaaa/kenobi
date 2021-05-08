# Quick build and deploy manual

## Required tools

* Unix ex. Centos 7 or Mac OS
* git
* jdk 1.8 or later
* docker

## Build

### Clone project

```shell script
git clone https://github.com/Shipaaaa/DS-BDA.git
```

### Run start script

```shell script
./start_service.sh
```

### Or start the service manually

#### Build project

```shell script
./gradlew build
```

#### Generate data

```shell script
./generate_syslog_data.sh $line_count > $logs_file_path
```

#### Setup env

```shell script
export KAFKA_BOOTSTRAP_SERVERS_IP="127.0.0.1:9092"
```

#### Start Kafka in docker-compose

```shell script
docker compose -f kafka-docker/docker-compose.yml up
```

You can see details in [kafka-docker readme](../kafka-docker/README.md) or [kafka-docker guide](./docker_kafka_setup.md).

#### Run ignite persistence module

```shell script
java -jar ./ignite-persistence/build/libs/ignite-persistence-1.0-SNAPSHOT.jar
```

#### Run kafka producer module

```shell script
java -jar ./kafka-producer/build/libs/kafka-producer-1.0-SNAPSHOT.jar <logs_file_path>
```

#### Run ignite compute module

```shell script
java -jar ./ignite-compute/build/libs/ignite-compute-1.0-SNAPSHOT.jar
```

#### Shutdown Kafka in docker after your work

```shell script
docker-compose -f kafka-docker/docker-compose.yml down
```
