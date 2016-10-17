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

See the following for instructions regarding the installation of Oracle Java 8 in Debian: http://www.webupd8.org/2014/03/how-to-install-oracle-java-8-in-debian.html

Other prerequisites include maven, subversion and git. If you do not currently have them installed you can use the following command.

    $ sudo apt-get install maven subversion git

If using the Play production server, install the GNU screen utillity in order to run the Play server in the background:

    $ sudo apt-get install screen

For production environments the default database used is MySQL. If MySQL is not already installed, install it now via apt-get:

sudo apt-get install mysql-client mysql-server

Create the kurator user and database with privileges in MySQL:

    $ CREATE DATABASE kurator;
    $ GRANT ALL PRIVILEGES ON kurator.* TO 'kurator'@'localhost' IDENTIFIED BY 'password';

Kurator web requires a kurator home directory that will contain the workspace and Python packages. Create a kurator user via the useradd command and use this user's home directory:

    $ sudo useradd -m -U kurator
    $ sudo passwd kurator

Login as the kurator user and create the packages and workspace directories required by the workflow engine:

    $ mkdir packages
    $ mkdir workspace

Kurator-Akka requires jython to be installed. Download the [Jython 2.7.1.b3 installer jar](http://search.maven.org/remotecontent?filepath=org/python/jython-installer/2.7.1b3/jython-installer-2.7.1b3.jar). Run the installer from the command line in the kurator home directory.

    $ java -jar jython-installer-2.7.1b3.jar

Select the standard installation when prompted (option 2) and when asked to provide the target directory enter "jython2.7.1b3". This will install jython to "/home/kurator/jython2.7.1b3".

#### Python actor dependencies

The python actor workflows included with the webapp require the installation of a few dependencies via jython pip. Install the following with the pip tool found in $JYTHON_HOME/bin/pip error messages suggesting a missing python module (Error importing python module ... No module named ...) probably indicate that additional pip installations need to be performed, try running pip install with the module name found after "No module named".  Currently packaged workflows depend on the following modules being installed:

    $JYTHONHOME/bin/pip install requests
    $JYTHONHOME/bin/pip install python-dwca-reader
    $JYTHONHOME/bin/pip install py
    $JYTHONHOME/bin/pip install unicodecsv
    $JYTHONHOME/bin/pip install unidecode

#### Maven dependencies

Kurator-Web depends on multiple projects which must be built using maven first. Start by cloning the kurator-akka, kurator-validation and kurator-fp-validation repositories on your local machine.

    $ git clone https://github.com/kurator-org/kurator-akka.git
    $ git clone https://github.com/kurator-org/kurator-validation.git
    $ git clone https://github.com/kurator-org/kurator-fp-validation.git

The kurator-fp-validation project depends on the FP-CurationServices project hosted in the filteredpush svn repository on sourceforge. Check this project using subversion:

    $ svn checkout svn://svn.code.sf.net/p/filteredpush/svn/trunk/FP-Tools/FP-CurationServices

First start by building kurator-akka. In production environments you can skip tests to save time during the build.

    $ cd kurator-akka
    $ mvn install -Dmaven.test.skip=true

Build the FP-CurationServices project next:

    $ cd ../FP-CurationServices
    $ mvn install -Dmaven.test.skip=true

Then build and install kurator-validation followed by kurator-fp-validation:

    $ cd ../kurator-validation
    $ mvn install -Dmaven.test.skip=true
    $ cd ../kurator-fp-validation
    $ mvn install -Dmaven.test.skip=true

The kurator-validation and kurator-fp-validation project builds each produce a zip file artifact containing any python packages that must be deployed. These archives can be found in the kurator-validation/target and kurator-fp-validation/target directories after successful build.

Unzip and deploy the contents of the archives to the packages directory created earlier in kurator home:

    $ cd /home/kurator
    $ unzip kurator-validation/target/kurator-validation-0.5-SNAPSHOT-packages.zip -d packages
    $ unzip kurator-fp-validation/target/kurator-fp-validation-0.5-SNAPSHOT-packages.zip -d packages

#### Configuration

Once you have successfully built the dependencies clone the kurator-web repository.

    $ git clone https://github.com/kurator-org/kurator-web.git

A template web application configuration file can be found at conf/application.conf.template. Make a copy of this file named application.conf in the same directory and edit to set the database and smpt server connection information:

    $ cd kurator-web/conf
    $ cp application.conf.template application.conf
    $ vi application.conf

By default the play application is configured to use the embedded in memory H2 database. If you plan on using MySQL as the production database comment out the two lines in conf/application.conf that configure the h2 database and uncomment the lines for mysql configuration instead.

The jython.home, jython.path and jython.workspace properties by default should point to the directories we created earlier in kurator home.

Lastly, if you would like the play server to accept connections from all hosts instead of just localhost, set the value of the http.address property to 0.0.0.0.

#### Build and Run

Once the web application is configured, build a distribution zip file via the included activator utility:

    $ bin/activator dist

Create a deployments directory in /home/kurator and unzip the distribution archive:

    $ cd /home/kurator
    $ mkdir deployments
    $ unzip kurator-web/target/universal/kurator-web-0.5-SNAPSHOT.zip -d deployments

Run the play production server from the distribution directory unziped within deployments. Use

    $ cd deployments/kurator-web-0.5-SNAPSHOT
    $ bin/kurator_web

If screen was installed earlier as part of the prerequisites you may run the server in the background via the following:

    $ screen -dmS kurator-web bin/kurator-web

To view stdout for this process, restore the screen via:

    $ screen -R kurator-web

Lastly, to detach from the screen and send it to the background, press Ctrl-A + Ctrl-D

See https://www.gnu.org/software/screen/ for more info

By default the Play server will listen on port 9000. Open http://localhost:9000/ in your browser after starting the server to test the web application.

#### Deploying workflows

Deploying workflows to the web app requires generation of a certificate and keystore that the jarsigner utility will use when signing packages. Create the keystore and certificate via the keytool utility provided by the JDK:

    $ keytool -genkey -keystore keystore.ks -alias <certificate-alias> -storepass <keystore-password> -keypass <privatekey-password>

The maven build requires a settings.xml file with the keystore alias and passwords configured. Create a settings.xml file in your .m2 directory:

    $ vi /home/kurator/.m2/settings.xml

Use the following template and replace the keystore properties with the values specified in the invocation of keytool:

    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          https://maven.apache.org/xsd/settings-1.0.0.xsd">

      <profiles>
        <profile>
          <id>inject-application-home</id>
          <properties>
        <keystore.location>/home/kurator/keystore.ks</keystore.location>
        <keystore.alias>test</keystore.alias>
        <keystore.storepass>password</keystore.storepass>
        <keystore.keypass>password</keystore.keypass>
             </properties>
        </profile>
      </profiles>

      <activeProfiles>
        <activeProfile>inject-application-home</activeProfile>
      </activeProfiles>

    </settings>

With the maven settings.xml file in place build and sign a workflows projects (kurator-validation for example) via maven:

    $ cd kurator-validation
    $ mvn clean package jarsigner:sign

This should produce a signed zip artifact in the target directory, kurator-validation-0.5-SNAPSHOT-packages.zip for example, that can be deployed to a web application instance once the certificate is added to it's keystore. To export the certificate for import remotely use the keytool:

    $ keytool -exportcert -alias <certificate-alias> -file cert.crt -keystore keystore.ks -storepass <keystore-password>

On the remote server that the web application is deployed create a new keystore or use an existing one and import the cert.crt file via:

    $ keytool -importcert -alias <certificate-alias> -file cert.crt

Once both remote and local keystores have been configured, deploy the signed packages zip file via the Admin > Deploy Workflows page.