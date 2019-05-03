#!/bin/sh

pkill -f 3D-Bin-Packing
echo "STOPPING THE SERVER, PLEASE WAIT..."

cd  ..
mvn clean install -DskipTests=true

nohup mvn spring-boot:run &

tail -f nohup.out
