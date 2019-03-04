#!/bin/bash
#Runs the three backup servers
javac BackEndServerInterface.java logRecord.java updateRequest.java queryRequest.java queryReturn.java BackEndServer1.java 
java BackEndServer1
$SHELL
