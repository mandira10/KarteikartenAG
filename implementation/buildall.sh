#!/bin/sh

mvn clean
mvn package #-Dmaven.test.skip=true
#mvn install
