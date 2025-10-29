#!/bin/bash

FILE_TO_CHECK=$1
KEY=$2
CHECKSUM_FILE=$3

sha256sum $FILE_TO_CHECK | cut -f 1 -d " " | sed -e "s#^#$KEY=##g" > $CHECKSUM_FILE