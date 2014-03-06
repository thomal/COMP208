server : *.java ../client/daemon/Message.java
	@javac *.java ../client/daemon/Message.java
	@cp ../client/daemon/Message.class Message.class
clean : *.class
	@rm *.class
run : Server.class
	@java Server
