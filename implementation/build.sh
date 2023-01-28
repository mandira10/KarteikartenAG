#!/bin/sh

echo "Cleaning.."
mvn clean -q
echo "Packaging.."
mvn package -q -Dmaven.test.skip=true 
echo "Done, do ./run.sh to run this project"
#mvn install
