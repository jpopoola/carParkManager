#!/bin/bash

for i in {1..20}
do 
	echo "Car number $i entered General Parking region"
	echo
	curl http://localhost:7071/traffic/General/true
	sleep 5
done

echo
echo

sleep 10

for i in {1..20}
do 
	echo "Car number $i exited General Parking region"
	echo
	curl http://localhost:7071/traffic/General/false
	sleep 5
done
