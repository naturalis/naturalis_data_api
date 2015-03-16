================================================================
             REQUIREMENTS FOR EXECUTING ANT BUILDS
================================================================
In order execute Ant builds for the NBA, the following must be
in place:



----------------------------------------------------------------
General
----------------------------------------------------------------
[1] Install Apache Ant
[2] Install Apache Ivy
[3] Copy Ivy-x.x.x.jar from Ivy install dir to Ant lib dir



----------------------------------------------------------------
Executing ant builds from command prompt
----------------------------------------------------------------
No further preparations necessary



----------------------------------------------------------------
Executing ant builds from within Eclipse
----------------------------------------------------------------
[2] Install IvyDE using Eclipse market place
[1] Create an environment variable IVY_HOME pointing to Ivy
    install dir
[3] Make Eclipse use your Ant installation (in stead of its own).
    [a] Go to Window -> Preferences -> Ant -> Runtime
    [b] On classpath tab pane, click on [Ant Home ...] button
    [c] Choose Ant install dir
[4] Go to Window -> Show View -> Other ... -> Ant -> Ant
[5] Drag build.xml files from projects you like to build to the
    Ant view
[6] Double click on the Ant target you wish to execute