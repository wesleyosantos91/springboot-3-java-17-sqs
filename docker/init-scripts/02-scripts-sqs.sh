#!/bin/bash

for script_name in /configs/sqs/*.sh; do
    bash $script_name
done