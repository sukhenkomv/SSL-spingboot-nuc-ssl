#!/bin/bash

docker build \
  --progress=plain -f ./Dockerfile -t ssl-check .
