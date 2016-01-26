Kurator Web Application
=======================

***Kurator-Web*** is a Play implementation of the web application front-end for kurator-Akka

Building and Testing Kurator-Web
--------------------------------

Follow these instructions to set up a build environment and run the application using the embedded Play server for development and testing purposes.

#### Prerequisites

Kurator-Web requires Java version 1.8 or higher. To determine the version of java installed on your computer use the `-version` option to the `java` command. For example,

    $ java -version
    java version "1.8.0_66"
    Java(TM) SE Runtime Environment (build 1.8.0_66-b17)
    Java HotSpot(TM) 64-Bit Server VM (build 25.66-b17, mixed mode)

Other prerequisites include maven and git. If you do not currently have them installed you can use the following command.

    $ sudo apt-get install maven git

#### Build and run

Start by cloning the kurator-akka repository on your local machine:

    $ git clone https://github.com/kurator-org/kurator-akka.git

Use maven to build kurator-akka, which is a dependency of kurator-web.

    $ cd kurator-akka
    $ mvn install

Once you have successfully built kurator-akka, clone the kurator-web repository on your local machine:

    $ git clone https://github.com/kurator-org/kurator-web.git

In the project directory, build the project using the package goal. You may also specify the play2:run goal after package if you would like to start the embedded server.

    $ cd kurator-web
    $ mvn package play2:run

By default the Play server will listen on port 9000. Open http://localhost:9000/ in your browser after starting the server to test the web application.

#### Stopping the Play server

Use the play2:stop maven goal to shutdown the Play server.
