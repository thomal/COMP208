all : clean web rserver
	echo "building all"

web:
	ant -f web_interface/build.xml build

rserver : src/ballmerpeak/turtlenet/remoteserver/*.java src/ballmerpeak/turtlenet/shared/*.java
	cp src/ballmerpeak/turtlenet/server/Message.java Message.java
	sed s/ballmerpeak.turtlenet.server/ballmerpeak.turtlenet.remoteserver/ Message.java > Message2.java
	rm Message.java
	mv Message2.java src/ballmerpeak/turtlenet/remoteserver/Message.java
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
