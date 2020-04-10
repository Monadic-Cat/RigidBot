#!/bin/bash

clear
mvn clean package
clear
java -jar target/rigidbot-1.1A-jar-with-dependencies.jar
