#!/bin/sh

echo "Cleaning.."
mvn clean -q
echo "Packaging.."
mvn -T 4 package -q -Dmaven.test.skip=true
#mvn -T 4 package -q
echo "Done, do ./run.sh to run this project"
#mvn install
