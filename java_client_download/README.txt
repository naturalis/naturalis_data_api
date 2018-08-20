This folder contains the NBA Java client library as a standalone
"uber jar" (all dependencies are packaged within the jar).

To start using the NBA Java client, simply add a dependency on
the jar. The NBA Java client uses log4j2 for logging so you will
need a log4j2.xml somewhere on your classpath if you want the
Java client to log properly (or start your program with
-Dlog4j.configurationFile=/path/to/log4j2.xml).

For the javadocs, see here:
http://naturalis.github.io/naturalis_data_api/javadoc/v2/client/

The Examples directory contains some working examples of how you
can use the Java client to interact with the NBA.


