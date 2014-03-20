all : clean web rserver
	@echo "              --------------------------"
	@echo "              successfully built project"
	@echo "              --------------------------"
	@echo ""	
	@echo "         ------------------------------------"
	@echo "        /  ::PROGRAMMER'S TIP 103::          \\"
	@echo "        |  Breaking the build upsets the     |"
	@echo "        |  turtle. Good job getting the damn |"
	@echo "        |  thing to compile.                 |"
	@echo "        \                                    /"
	@echo "         ---------  -------------------------"
	@echo "                  \|"
	@echo ""
	@echo "                  ------       /^^---^^---^^\\"
	@echo "                / o  o   \    /___/____|_____\\"
	@echo "                \  w     /   /___ /_____|_____\ >"
	@echo "                   -----     u              u"


web:
	sed -e 's:___GWTPATH___:/home/luke/Downloads/gwt-2.6.0:g' web_interface/protobuild.xml > web_interface/build.xml
	ant -f web_interface/build.xml build
	rm web_interface/build.xml

rserver:
	javac -cp src src/ballmerpeak/turtlenet/remoteserver/*.java
	
clean:
	sed -e 's:___GWTPATH___:/home/luke/Downloads/gwt-2.6.0:g' web_interface/protobuild.xml > web_interface/build.xml
	rm -f src/ballmerpeak/turtlenet/*/*.class
	rm -f src/ballmerpeak/turtlenet/remoteserver/Message.java
	ant -f web_interface/build.xml clean
	rm web_interface/build.xml
	
run_server:
	mkdir -p data
	java -cp src ballmerpeak.turtlenet.remoteserver.Server
	
run_client:
	sed -e 's:___GWTPATH___:/home/luke/Downloads/gwt-2.6.0:g' web_interface/protobuild.xml > web_interface/build.xml
	ant -f web_interface/build.xml devmode
	rm web_interface/build.xml
