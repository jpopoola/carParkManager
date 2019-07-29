#!/bin/bash

for i in {1..10}
do 
	echo "Car number $i entered Reserved Parking region"
	echo
	curl http://localhost:7071/traffic/General/true
	curl http://localhost:7071/traffic/Reserved/true
	sleep 5
done

echo
echo

sleep 10

for i in {1..10}
do 
	echo "Car number $i exited Reserved Parking region"
	echo
	curl http://localhost:7071/traffic/Reserved/false
	curl http://localhost:7071/traffic/General/false
	sleep 5
done
