#!/bin/sh

#mvn exec:java -Dexec.mainClass=com.swp.KarteikartenAG -Dexec.args="$*"
java -jar ./client/target/client-1.0.0.jar -Dorg.lwjgl.glfw.libname=libglfw_wayland.so
