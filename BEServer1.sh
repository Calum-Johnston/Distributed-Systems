#!/bin/bash
#Runs the three backup servers
javac BackEndServerInterface.java logRecord.java updateRecord.java BackEndServer1.java 
java BackEndServer1
$SHELL
