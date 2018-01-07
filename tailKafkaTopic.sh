#!/usr/bin/env bash
args=("$@")
docker-compose exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic ${args[0]} --from-beginning
