# Kafka-producer

Модуль отправки фотографий в kafka topic.

## Usage

1) Собираем jar

```shell
./gradlew :kafka-producer:build
````

2) Копируем jar на сервер


3) Запускаем

```shell
export KAFKA_BOOTSTRAP_SERVERS_IP="192.168.12.66:30361"

java -jar ./kafka-producer-1.0-SNAPSHOT.jar ../images
```
