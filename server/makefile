server : ballmerpeak/turtlenet/remoteserver/*.java ../web_interface/src/ballmerpeak/turtlenet/server/Message.java
	cp ../web_interface/src/ballmerpeak/turtlenet/server/Message.java Message.java
	sed s/ballmerpeak.turtlenet.server/ballmerpeak.turtlenet.remoteserver/ Message.java > Message2.java
	rm Message.java
	mv Message2.java ballmerpeak/turtlenet/remoteserver/Message.java
	javac -cp . ballmerpeak/turtlenet/remoteserver/*.java
clean : ballmerpeak/turtlenet/remoteserver/*.class
	rm ballmerpeak/turtlenet/remoteserver/*.class
	rm ballmerpeak/turtlenet/remoteserver/Message.java
run : ballmerpeak/turtlenet/remoteserver/Server.class
	java -cp . ballmerpeak.turtlenet.remoteserver.Server
