server : *.java ../client/Message.java
	@javac *.java ../client/Message.java
	@cp ../client/Message.class Message.class
clean : *.class
	@rm *.class
run : Server.class
	@java Server
