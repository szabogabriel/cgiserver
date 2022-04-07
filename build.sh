#!/bin/sh

mvn clean install

docker build --tag cgiserver .
