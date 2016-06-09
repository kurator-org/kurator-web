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

**Kurator-Akka** now will have access to all Python packages installed to your Jython installation.  Jython 2.7 includes the `pip` tool (in the `bin` subdirectory of the Jython installation) which makes it easy to install 3rd-party Python packages and to install their dependencies automatically.  For example, this following command installs the `suds-jurko` package which subsequently can be imported by Python actors:

    $JYTHON_HOME/bin/pip install suds-jurko

#### Make your own Python code available to **Kurator-Akka**

To make your own Python code available to **Kurator-Akka** specify the directory (or directories) containing your Python packages using the `JYTHONPATH` variable.  For example, if you have defined a Python function `add_two_numbers()` in a file named `adders.py`, and placed this Python module (file) in the `$HOME/packages/math/operators` directory, you can make your function available for use in an actor via the `math.operators.adders` module by defining `JYTHONPATH` to include the `$HOME/packages/` directory and then declaring the `module` and `onData` properties of the actor as follows:

    types:
    - id: Adder
      type: PythonClassActor
      properties:
        module: math.operators.adders
        onData: add_two_numbers

Note that Python packages always require that a file named `__init__.py` be present in each directory comprising the package.  In the above example, the directories `$HOME/packages/math` and `$HOME/packages/math/operators` must each contain a file named `__init__.py`. These files may be empty.

#### Stopping the Play server

Use the play2:stop maven goal to shutdown the Play server.

#### Deploying to Tomcat ####

The maven package goal will create a war file by default that can be deployed to Tomcat 8. JAVA_HOME must be set to point to an installation of Java 8 prior to deployment.

Once you have configured the database and JAVA_HOME variable deploy the war file to your tomcat webapps directory.
