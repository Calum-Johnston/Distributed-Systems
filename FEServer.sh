#!/bin/bash
#Runs the three backup servers
javac FrontEndServerInterface.java updateRequest.java queryRequest.java queryReturn.java FrontEndServer.java 
java FrontEndServer
$SHELL
