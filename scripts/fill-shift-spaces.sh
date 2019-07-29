#!/bin/bash

for i in {1..15}
do 
	echo "Car number $i entered Shift Workers Parking region"
	echo
	curl http://localhost:7071/traffic/General/true
	curl http://localhost:7071/traffic/Shift/true
	sleep 5
done

echo
echo

sleep 10

for i in {1..15}
do 
	echo "Car number $i exited Shift Workers Parking region"
	echo
	curl http://localhost:7071/traffic/Shift/false
	curl http://localhost:7071/traffic/General/false
	sleep 5
done
