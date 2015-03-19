================================================================
             REQUIREMENTS FOR EXECUTING ANT BUILDS
================================================================
In order execute Ant builds for the NBA, the following must be
in place:



----------------------------------------------------------------
General
----------------------------------------------------------------
[1] Make sure you have a Java SE Development Kit (JDK) installed
    (not just a JRE!)
[2] Add JAVA_HOME to the environment of the Ant executable and
    make sure it points to a JDK (not a JRE!). The easiest way
    to ensure this is probably is to edit the ant.bat script
    (Windows) or ant script (Linux) in Ant's bin directory.
    Insert the variable near the top of the script.
[3] Add ${JAVA_HOME}/bin to PATH
[4] Install Apache Ant
[5] Add Ant's bin directory to PATH
[6] Install Apache Ivy
[7] Copy Ivy-x.x.x.jar from Ivy install dir to Ant lib dir
[8] Add IVY_HOME to the environment of the Ant executable. The
    easiest way to do this probably is to edit the ant.bat
    script (Windows) or ant script (Linux) in Ant's bin
    directory. Insert the variable near the top of the script
    and make it point to the directory where you installed
    Ivy.



----------------------------------------------------------------
Executing ant builds from command prompt
----------------------------------------------------------------
No further preparations necessary.



----------------------------------------------------------------
Executing ant builds from within Eclipse
----------------------------------------------------------------
[1] Install IvyDE using Eclipse market place
[2] Create an environment variable IVY_HOME pointing to Ivy
    install dir
[3] Make Eclipse use your Ant installation (in stead of its own).
    [a] Go to Window -> Preferences -> Ant -> Runtime
    [b] On classpath tab pane, click on [Ant Home ...] button
    [c] Choose Ant install dir
[4] Go to Window -> Show View -> Other ... -> Ant -> Ant
[5] Drag build.xml files from projects you like to build to the
    Ant view
[6] Double click on the Ant target you wish to execute (or
    hit Run button in button bar of Ant view)