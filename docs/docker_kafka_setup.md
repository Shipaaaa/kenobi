# Setup kafka with wurstmeister/kafka

### Clone wurstmeister/kafka repo

```shell script
git clone https://github.com/wurstmeister/kafka-docker
```

```shell script
cd kafka-docker
```

### Edit docker-compose.yml
#### 1. Change the docker host IP in KAFKA_ADVERTISED_HOST_NAME

On macOS use the [following](https://github.com/wurstmeister/kafka-docker/issues/17#issuecomment-370237590) script to set DOCKERHOST env var.

```shell script
DOCKERHOST=$(ifconfig | grep -E "([0-9]{1,3}\.){3}[0-9]{1,3}" | grep -v 127.0.0.1 | awk '{ print $2 }' | cut -f2 -d: | head -n1)
```

Start services based on the default docker-compose.yml. You may want to use -d to detach from the terminal (daemon mode)

```shell script
docker-compose up
```

Connect to the container of the Kafka broker
```shell script
docker ps --format "{{ .Names }}"
```

output
```
kafka-docker_zookeeper_1
kafka-docker_kafka_1
```

```shell script
docker inspect --format='{{range $k, $v := .NetworkSettings.Ports}}{{range $v}}{{$k}} -> {{.HostIp}} {{.HostPort}}{{end}}{{end}}' kafka-docker_kafka_1
```

output
```
9092/tcp -> 0.0.0.0 32776
```

Check the connection from the host to the single Kafka broker

```shell script
nc -vz 0.0.0.0 32776
```

```
found 0 associations
found 1 connections:
     1:	flags=82<CONNECTED,PREFERRED>
	outif lo0
	src 127.0.0.1 port 59000
	dst 127.0.0.1 port 32778
	rank info not available
	TCP aux info available

Connection to 0.0.0.0 port 32778 [tcp/*] succeeded!
```

Print out the topics
You should see no topics listed

```shell script
docker exec -t kafka-docker_kafka_1 \
  kafka-topics.sh \
    --bootstrap-server :9092 \
    --list
```


Create a topic t1

```shell script
docker exec -t kafka-docker_kafka_1 \
  kafka-topics.sh \
    --bootstrap-server :9092 \
    --create \
    --topic t1 \
    --partitions 3 \
    --replication-factor 1
```

Describe topic t1

```shell script
docker exec -t kafka-docker_kafka_1 \
  kafka-topics.sh \
    --bootstrap-server :9092 \
    --describe \
    --topic t1
```

output

```
Topic:t1	PartitionCount:3	ReplicationFactor:1	Configs:segment.bytes=1073741824
	Topic: t1	Partition: 0	Leader: 1001	Replicas: 1001	Isr: 1001
	Topic: t1	Partition: 1	Leader: 1001	Replicas: 1001	Isr: 1001
	Topic: t1	Partition: 2	Leader: 1001	Replicas: 1001	Isr: 1001
```


Connect with the Kafka console consumer in another terminal

```shell script
docker exec -t kafka-docker_kafka_1 \
  kafka-console-consumer.sh \
    --bootstrap-server :9092 \
    --group jacek-japila-pl \
    --topic t1
```


Connect with the Kafka console producer in one terminal

```shell script
docker exec -it kafka-docker_kafka_1 \
  kafka-console-producer.sh \
    --broker-list :9092 \
    --topic t1
```


Type a message in producer window to see the message printed out in consumer's
Observe the logs of the running docker-compose up (with no -d)
Or use docker logs -f kafka-docker_kafka_1

Ctrl-C to shut down all the processes

You may also consider removing the containers
so you always start afresh
docker-compose rm