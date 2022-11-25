#!/bin/sh

mvn exec:java -Dexec.mainClass=com.swp.KarteikartenAG -Dexec.args="$*"
