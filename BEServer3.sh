#!/bin/bash
#Runs the three backup servers
javac BackEndServerInterface.java logRecord.java updateRequest.java queryRequest.java queryReturn.java BackEndServer3.java 
java BackEndServer3
$SHELL
