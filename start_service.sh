#!/bin/bash
set -e

line_count=10
logs_file_path=./testData/syslogs.txt

echo "Starting kafka in docker..."
docker-compose -f kafka-docker/docker-compose.yml up -d

printf "\n"

echo "Started services:"
docker ps --format "{{ .Names }}"
printf "\n"

echo "Building project..."
./gradlew build
printf "\n\n\n"

echo "Generating test data..."
./generate_syslog_data.sh $line_count > $logs_file_path

echo "Set ENV"
export KAFKA_BOOTSTRAP_SERVERS_IP="127.0.0.1:9092"

printf "\nRunning ignite-persistence..."
java -jar ./ignite-persistence/build/libs/ignite-persistence-1.0-SNAPSHOT.jar &

sleep 10
printf "\n\n\nRunning kafka-producer..."
java -jar ./kafka-producer/build/libs/kafka-producer-1.0-SNAPSHOT.jar $logs_file_path &

sleep 30
printf "\n\n\nRunning ignite-compute..."
java -jar ./ignite-compute/build/libs/ignite-compute-1.0-SNAPSHOT.jar &

sleep 10
docker-compose -f kafka-docker/docker-compose.yml down

trap 'kill $(jobs -p)' EXIT
