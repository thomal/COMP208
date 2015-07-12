[![ScreenShot](http://img.youtube.com/vi/mMOWo8vBw8U/0.jpg)](https://youtu.be/mMOWo8vBw8U)]

KAWAII TURTLE~

         ------------------------------------
        /  ::PROGRAMMER'S TIP 102::          \
        |  Always pull before doing any work |
        |  lest ye anger the mercurial merge |
        |  merge gods.                       |
        \                                    /
         ---------  -------------------------
                  \|

                  ------       /^^---^^---^^\
                / o  o   \    /___/____|_____\
                \  w     /   /___ /_____|_____\ >
                   -----     u              u
--------------------------------------------
Notices
--------------------------------------------
The user manual given during the demo contains a lot of incorrect information.
A new version will be submitted in the portfolio with errors corrected.
(passwords have nothing to do with authentication at all, among other errors)
--------------------------------------------
Running TN
--------------------------------------------
Download turtlenet.jar, launch it with `java -jar turtlenet.jar' and connect to
localhost:3141 in a webbrowser.

If using the tor version ensure that tor has started a SOCKS proxy on 9050.

IMPORTANT: The servers DNS records changed on may 4th at 1425 and the old
servers data was not preserved. If you had an account on the older server please
erase your db folder and reregister on the new server.
--------------------------------------------
TODO
--------------------------------------------
Autorefresh pages on new content
Currently the users DB is erased if they enter the wrong PW
--------------------------------------------
HOW-TO
--------------------------------------------
Compile:
    - in COMP208 folder type `make'

Test:
    - in COMP208 folder type `make run_server'
    - in another terminal in COMP208 type `make run_client'
    - click "Copy to Clipboard" and visit that link in a webbrowser

Edit GUI:
    - Edit src/ballmerpeak/turtlenet/client/frontend.java
    - Edit web_interface/war/frontend.html
    - Edit web_interface/war/frontend.css

Edit (Remote) Server:
    - Edit src/ballmerpeak/turtlenet/remoteserver/Server.java

Edit Client:
    - Edit src/ballmerpeak/turtlenet/server/*.java

Create windows build environment:
    - Install cygwin with git etc.
    - download and unzip GWT
    - set path in makefile (use forward slash, escape colon)
    - download and unzip ant
    - export ANT_HOME=/cygdrive/c/Users/luke/Downloads/apache-ant-1.9.3
    - export PATH=$ANT_HOME/bin:$PATH
    - ant -version
      - make sure that a JDK is in your windows path:
      - Right click on Computer > Properties > Advanced System Settings >
      - Advanced Tab > Environment Variables > Under System Variables scroll
      - down to PATH and add your jdk to the end of the list followed by a
      - semicolon(Make sure theres a semicolon preceding it too).

        Your JDK path should look something like:
            C:\Program Files\Java\jdk1.7.0_51\bin
--------------------------------------------
Meeting #13 Minutes (Friday 02/04/2014)
--------------------------------------------
(Peter, Luke, Aishah, Leon, Mike, Louis)
- Assigned final weeks tasks and roles for submitting the portfolio
- Updates Requirements (Aishah)
- Updates Design (Aishah)
- Website (Leon)
- Source Code (Leon)
- Personal Statements (All)
- Deviations Requirements (Luke)
- Deviations Design (Luke)
- Hashes (Luke)
- User Manual (Mike)
- Future Development Continued (Mike)
- Hosting AWS (Louis)
- Testing Automated/Blackbox (Louis)

Submission for all this is WEDNESDAY 7th May.
--------------------------------------------
Meeting #12 Minutes (Saturday, 19/04/2014)
--------------------------------------------
(Peter, Luke, Aishah, Leon, Mike, Louis)
- Website finally unveiled
- Reassignment of some tasks (prioritization)
- Mostly a formality to remind people of deadlines
--------------------------------------------
Meeting #11 Minutes (Sunday, 13/04/2014)
--------------------------------------------
Present: Peter, Luke, Aishah, Louis
- Progress Recap:
  - Luke:
    - Added testing
    - Added logger
    - Implemented createDatabase to execute aishahs create table SQL queries
    - Message::XgetY methods
    - Altered frontend to start turtlenet when it starts, and stop it when the tab is closed

- Luke/Louis Re: Enable -strict for compiling frontend

Reassigned installer, updated todo:
Luke:
- Installer

Aishah/Mike:
- Store the signature (String) for posts and comments
- SQL for database methods
- Implement database methods

Leon:
- Start/Stop server via gui (leon)

Peter:
- Website Prototype

Louis:
- Call appropriate Database.getX methods

Leon, Mike not present.
--------------------------------------------
Meeting #10 Minutes (Friday, 28/03/2014)
--------------------------------------------
Present: Peter, Luke, Aishah, Leon, Mike, Louis
By next meeting:
- Fix manual stuff, add makefile, finish initial test setup (luke)
- Create tables from Database.java (aishah/mike)
- GWT Interface (louis)
- Start/Stop server via gui (leon)
- Webstie Prototype (peter)
- Document regarding hashing for various classes (luke)
--------------------------------------------
Meeting #9 Minutes (Friday, 21/03/2014)
--------------------------------------------
Present: Peter, Luke, Aishah, Mike, Louis
By next meeting:
- Everyone can build project
- DB Connection
- GWT Interface stubs
- Stubs for other classes
- Manual contents page
- Create table statements
- Compile remote server as a JAR
- Server GUI, start up and shut down

Leon absent.
--------------------------------------------
Meeting #8 Minutes (Wednesday, 19/03/2014)
--------------------------------------------
Present: Peter, Luke, Aishah, Leon, Louis

| Task                           | Assigned To  |
|--------------------------------|--------------|
| Server.java		         | Luke         |
| Client.java		         | Luke         |
| Crypto.java		         | Luke         |
| NetworkConnection.java	 | Luke         |
| Parser.java		         | Luke         |
| HTTPServer.java		 | Luke         |
| helper classes		 | Luke         |
| browser plugins		 | Luke         |
| QR Code parser		 | Luke         |
| Test harness		         | Luke         |
| Installer		         | Peter        |
| Website			 | Peter        |
| Manual			 | Peter        |
| Hardware Server		 | Peter        |
| ServerGUI.java		 | Leon         |
| First run config	         | Leon         |
| *Database.java*	         | Mike, Aishah |
|  Stubs                         |              |
| *SQLite Database*		 | Aishah       |
|  Database connection           |              |
|  Create DB                     |              |
| Logo and Graphic Design	 | Aishah       |
| *GWT interface*	         | Louis        |
|  Stubs in interface            |              |
|  Error on failure to connect   |              |
|  Add public keys               |              |
|  Categorise users              |              |
|  Post to your wall             |              |
|  Read others wall posts        |              |
|  Post to anothers wall         |              |
|  Events create and recieve     |              |
|  Chat                          |              |
|  Comment posts and comments    |              |
|  Like posts and comments       |              |

------------------------------------------
Meeting #7 Minutes (Friday, 07/03/2014)
------------------------------------------
Present: Peter, Luke, Aishah, Leon, Mike, Louis
- Business Rules (Peter)
- Gantt Chart (Mike)
- Various DB tweaks (Aishah)
- Merge work into PDF (Luke)
- Rename GUI design as storyboard
------------------------------------------
Meeting #6 Minutes (Wednesday, 19/02/2014)
------------------------------------------
Present: Peter, Luke, Aishah, Leon, Mike, Louis
- Discussed design phase, outlined what needs to be done
- Also outlined who needs to do it, tasklist:
- Mike - Use Case Diagram, Data Dictionary
- Leon - Mobile GUI, Sequence Diagram
- Louis - Web GUI Design, Java/SQLite/HTML-CSS Documentation
- Aishah - Database Design Doc
- Peter - Swing/AWT GUI Design, Server GUI Design
- Luke - Class Interfaces, Protocol, Architecture, Data Flow Diagrams, More
         Protocol, Psuedocode
------------------------------------------
Meeting #5 Minutes (Wednesday, 12/02/2014)
------------------------------------------
Present: Peter, Luke, Aishah, Leon, Mike
- Allocated left over parts
- Feedback on project and requirements so far
- Project name: Turtlenet
- Louis Prince absent from scheduled meeting.
-----------------------------------------
Meeting #4 Minutes (Friday, 07/02/2014)
-----------------------------------------
Present: Peter, Luke, Aishah, Leon, Louis
- Introduced Louis Prince to members/project
- Assigned Roles to requirement sections
- Team Review date proposed (Wed 19th, Afternoon)
- Team name: Ballmer Peak
----------------------------------------
Meeting #3 Minutes (Tuesday, 04/02/2014)
----------------------------------------
Present: Peter, Luke, Aishah, Leon, Mike
- State out the problems, criticisms on Facebook regarding user privacy issues
    (Leon to do this)
- Data flow of the system (Luke has done the draft)
- User requirements (Luke has done the draft. Refinement to be done by Aishah
    and Peter)
- Class diagram (to be completed after dataflow diagram and user requirements)
- Sketches of GUI (Peter and Mike to do this together)
- GANTT chart and risk assessment (after user requirements has been drafted out)
- Data dictionary (Aishah)
- Read about how to implement SQLite (Aishah)
---------------------------------------
Meeting #2 Minutes (Friday, 31/01/2014)
---------------------------------------
Present: Peter, Luke, Aishah, Leon, Mike
- We ate nice chinese in celebration of the new year
- If I'm honest this wasnt really a team meeting, more a hunger thing
-----------------------------------------
Meeting #1 Minutes (Thursday, 30/01/2014)
-----------------------------------------
Present: Peter, Luke, Aishah, Leon, Mike
- Introductions
- Overview of the project
- Assigned roles to members
