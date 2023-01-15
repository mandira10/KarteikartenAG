@echo off

set ARG=%1
set ROOTDIR=%cd%

mvn clean
mvn package

echo Finished creation of: KarteikartenAG
