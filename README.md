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

##### Optional MariaDB/MySQL Configuration

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

#### Install the Jython 2.7.1b3 distribution to support 3rd party Python packages

* Download the [Jython 2.7.1.b3 installer jar](http://search.maven.org/remotecontent?filepath=org/python/jython-installer/2.7.1b3/jython-installer-2.7.1b3.jar). The downloaded jar file will be named `jython_installer-2.7.1b3.jar`.

* The Jython installer can be started either by double-clicking the downloaded jar file (on Windows or OS X) or executing the downloaded jar at the command prompt using the `java -jar` command:

        java -jar jython_installer-2.7.1b3.jar

* Note the location of the Jython installation directory created by the installer.

#### Make installed Python packages available to Kurator

Define the environment variable `JYTHONHOME` to indicate the path to the newly installed Jython 2.7.0 distribution. **Kurator-Akka** uses this variable to locate 3rd-party Python packages that specific actors depend upon.

In a bash shell the environment variable can be assigned with the following command (assuming, for example, that Jython was installed to the `jython2.7.1b3` directory within your home directory):

    export JYTHONHOME=$HOME/jython2.7.1b3/

On Windows it is easiest to define the variable using the Advanced system settings Environment Variables dialog:

    Control Panel -> System -> Advanced system settings -> Advanced -> Environment Variables

See the documentation in Kurator-Akka on how to make your own Python code available to **Kurator-Akka**.

#### Python actor dependencies

The python actor workflows included with the webapp require the installation of a few dependencies via jython pip. Install the following with the pip tool found in $JYTHON_HOME/bin/pip error messages suggesting a missing python module (Error importing python module ... No module named ...) probably indicate that additional pip installations need to be performed, try running pip install with the module name found after "No module named".  Currently packaged workflows depend on the following modules being installed:

    $JYTHONHOME/bin/pip install requests
    $JYTHONHOME/bin/pip install python-dwca-reader
    $JYTHONHOME/bin/pip install py
    $JYTHONHOME/bin/pip install unicodecsv
    $JYTHONHOME/bin/pip install unidecode

#### Starting the Play server

Use the play2:ebean-enhance and play2:run maven goal to start the Play server.

    mvn play2:ebean-enhance play2:run 

#### Stopping the Play server

Use the play2:stop maven goal to shutdown the Play server.

    mvn play2:stop

#### Deploying to Tomcat ####

The maven package goal will create a war file by default that can be deployed to Tomcat 8. JAVA_HOME must be set to point to an installation of Java 8 prior to deployment.

Once you have configured the database and JAVA_HOME variable deploy the war file to your tomcat webapps directory.
