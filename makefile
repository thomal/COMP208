LUKE_GWTPATH="/home/luke/Downloads/gwt-2.6.0"
LUKEWIN_GWTPATH="C\:/Users/luke/Downloads/gwt-2.6.0"
PETER_GWTPATH="i dunno"
AISHIAH_GWTPATH="this is for you"
LEON_GWTPATH="to fill in, not me."
MIKE_GWTPATH="see my example prior"
LOUIS_GWTPATH="so you know what to do."

ifeq ($(HOSTNAME), slowbox)
	GWTPATH=$(LUKEWIN_GWTPATH)
else ifeq ($(USERNAME), luke)
	GWTPATH=$(LUKE_GWTPATH)
else
	GWTPATH="SET YO GODDAMN PATH"
endif

all : clean web rserver config
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


web : config
	sed -e `echo 's:___GWTPATH___:'$(GWTPATH)':g'` web_interface/protobuild.xml > web_interface/build.xml
	ant -f web_interface/build.xml build
	rm web_interface/build.xml

rserver : config
	javac -cp src src/ballmerpeak/turtlenet/remoteserver/*.java
	
clean : config
	sed -e `echo 's:___GWTPATH___:'$(GWTPATH)':g'` web_interface/protobuild.xml > web_interface/build.xml
	rm -f src/ballmerpeak/turtlenet/*/*.class
	rm -f src/ballmerpeak/turtlenet/remoteserver/Message.java
	ant -f web_interface/build.xml clean
	rm web_interface/build.xml
	
run_server : config
	mkdir -p data
	java -cp src ballmerpeak.turtlenet.remoteserver.Server
	
run_client : config
	sed -e `echo 's:___GWTPATH___:'$(GWTPATH)':g'` web_interface/protobuild.xml > web_interface/build.xml
	ant -f web_interface/build.xml devmode
	rm web_interface/build.xml
	
config:
	@echo "Path: $(GWTPATH)"
