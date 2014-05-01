#!/bin/bash

# For some reason the Jar tools only seem to work as you would expect when you
# are in the same directory you want your output to go/ where you want to take
# your input from. That's why this file uses ../ so much.

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
cp turtlenet.jar ../turtlenet.jar
cd ..
# Clean up
rm -rf build
rm embedded.war
