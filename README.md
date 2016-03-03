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

#### Obtaining source code

Start by cloning the kurator-akka, kurator-validation and kurator-jython repositories on your local machine. These are dependencies of kurator-web:

    $ git clone https://github.com/kurator-org/kurator-akka.git
    $ git clone https://github.com/kurator-org/kurator-validation.git
    $ git clone https://opensource.ncsa.illinois.edu/bitbucket/scm/kurator/kurator-jython.git

The kurator-jython project contains jython code used by kurator-akka and the only requirement is that it be cloned into a directory at the same level as the other projects.

Use maven to build kurator-akka and kurator-validation:

    $ cd kurator-akka
    $ mvn install

    $ cd kurator-validation
    $ mvn install

Once you have successfully built the dependecies clone the kurator-web repository on your local machine:

    $ git clone https://github.com/kurator-org/kurator-web.git

#### Configuration

The web application configuration file can be found at conf/application.conf. Edit this file to set the database and smpt server connection information.

By default the play application is configured to use the embedded in memory H2 database.

If you plan on using MySQL as the production database comment out the two lines in conf/application.conf that configure the h2 database and uncomment the lines for mysql configuration instead.

You must also create the kurator user and database with privileges in MySQL:

    CREATE DATABASE kurator;
    GRANT ALL PRIVILEGES ON kurator.* TO 'kurator'@'localhost' IDENTIFIED BY 'password';

The play mailer is used to send notifications to users when results are available for their workflow runs. Configure the smtp server connection settings in the "play.mailer" block in application.conf.

#### Build and run

In the project directory, build the project using the package goal. You may also specify the play2:run goal after package if you would like to start the embedded server.

    $ cd kurator-web
    $ mvn package play2:ebean-enhance play2:run

By default the Play server will listen on port 9000. Open http://localhost:9000/ in your browser after starting the server to test the web application.

To invoke actors implemented in python, you will need to (currently) set an environment variables:

    export KURATOR_LOCAL_PACKAGES=~/somepath/kurator-validation/src/main/python/

These two might also need to be set:

    export KURATOR_LOCAL_PYTHON=~/somepath/jython2.7.0/Lib/
    export PYTHONPATH="/home/somepath/kurator-validation/src/main/python/:$PYTHONPATH"

#### Stopping the Play server

Use the play2:stop maven goal to shutdown the Play server.

#### Deploying to Tomcat ####

The maven package goal will create a war file by default that can be deployed to Tomcat 8. JAVA_HOME must be set to point to an installation of Java 8 prior to deployment.

Once you have configured the database and JAVA_HOME variable deploy the war file to your tomcat webapps directory.
