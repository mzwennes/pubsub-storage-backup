#!/bin/bash


for msg in `seq 1 $1`;
do
	gcloud beta pubsub topics publish $2 --message $3
done
