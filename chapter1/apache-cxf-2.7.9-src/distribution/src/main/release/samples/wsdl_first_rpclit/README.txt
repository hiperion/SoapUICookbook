Hello World Demo using RPC-Literal Style
========================================

This version of Hello World demonstrates the use
of the RPC-Literal style binding.

Please review the README in the samples directory before
continuing.


Building and running the demo using Maven
---------------------------------------

From the base directory of this sample (i.e., where this README file is
located), the pom.xml file is used to build and run the demo. 

Using either UNIX or Windows:

  mvn install   (builds the demo)
  mvn -Pserver  (from one command line window)
  mvn -Pclient  (from a second command line window)


To remove the code generated from the WSDL file and the .class
files, run "mvn clean".

