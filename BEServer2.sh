#!/bin/bash
#Runs the three backup servers
javac BackEndServerInterface.java logRecord.java updateRequest.java queryRequest.java queryReturn.java BackEndServer2.java 
java BackEndServer2
$SHELL
