#!/bin/bash

while read line
do
    echo ${line} >> /tmp/store/tmp/file.txt
done

echo "Content-type: text/plain"
echo ""
echo "Yess!"