@echo off

set ARG=%1
set ROOTDIR=%cd%

echo Cleaning..
call mvn clean -q

echo Packaging..
call mvn -T 4 package -q -Dmaven.test.skip=true

echo Done, do `.\run.bat` to run this project
