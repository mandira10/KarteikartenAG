@echo off

set ARG=%1
set ROOTDIR=%cd%

echo Cleaning..
mvn clean -q

echo Packaging..
mvn package

echo Done, do ./run.bat to run this project
