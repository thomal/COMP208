#!/bin/bash

# For some reason the Jar tools only seem to work as you would expect when you
# are in the same directory you want your output to go/ where you want to take
# your input from. That's why this file uses ../ so much.

# Make sure all of the class files are up to date
make clean
make

#########################################
##########Create the client jar##########
#########################################

# Pack Turtlenet into a War file
mkdir -p build
cd ./build
cp -R ../web_interface/war/* ./
# Very important we have this dependency
cp ../libs/sqlite-jdbc-3.7.2.jar ./WEB-INF/lib/sqlite-jdbc-3.7.2.jar
jar -cvf ../embedded.war *
cd ..
# Clean up
rm -rf build

# Extract Winstone, Copy Turtlnet into place, Repack Winstone
mkdir -p build 
cd ./build 
jar xf ../libs/winstone.jar 
cp ../embedded.war ./embedded.war
jar cmf ./META-INF/MANIFEST.MF turtlenet.jar *
cp turtlenet.jar ../jars/turtlenet.jar
cd ..
# Clean up
rm -rf build
rm embedded.war

################################################
##########Create the remote server jar##########
################################################

# Setup directories
mkdir build
cd build
mkdir ballmerpeak
mkdir ./ballmerpeak/turtlenet
mkdir ./ballmerpeak/turtlenet/remoteserver
mkdir ./ballmerpeak/turtlenet/shared

# Copy the class files we need to the build directory
cp ../src/ballmerpeak/turtlenet/remoteserver/Server.class ./ballmerpeak/turtlenet/remoteserver/Server.class
cp ../src/ballmerpeak/turtlenet/remoteserver/Session.class ./ballmerpeak/turtlenet/remoteserver/Session.class
cp ../src/ballmerpeak/turtlenet/remoteserver/Hasher.class ./ballmerpeak/turtlenet/remoteserver/Hasher.class
cp ../src/ballmerpeak/turtlenet/shared/Conversation.class ./ballmerpeak/turtlenet/shared/Conversation.class
cp ../src/ballmerpeak/turtlenet/shared/Message.class ./ballmerpeak/turtlenet/shared/Message.class
cp ../src/ballmerpeak/turtlenet/shared/Tokenizer.class ./ballmerpeak/turtlenet/shared/Tokenizer.class

# Create a jar out of the class files
jar cmf ../src/ballmerpeak/turtlenet/remoteserver/MANIFEST.MF remoteserver.jar *
# Move the jar to the jars folder
cp remoteserver.jar ../jars/remoteserver.jar
# Clean up
cd ..
rm -rf build
