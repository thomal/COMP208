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
	ant -f web_interface/build.xml build

rserver:
	javac -cp src src/ballmerpeak/turtlenet/remoteserver/*.java
	
clean:
	rm -f src/ballmerpeak/turtlenet/*/*.class
	rm -f src/ballmerpeak/turtlenet/remoteserver/Message.java
	ant -f web_interface/build.xml clean
	
run_server:
	mkdir -p data
	java -cp src ballmerpeak.turtlenet.remoteserver.Server
	
run_client:
	cd web_interface
	ant -f web_interface/build.xml devmode &
	cd ..
