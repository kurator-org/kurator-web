Kurator Web Application
=======================

***Kurator-Web*** is a Play implementation of the web application front-end for kurator-Akka

Quickstart
----------

The following quickstart describes the process of setting up a local instance of the web app for testing and demonstration of workflows.

Download the latest distribution zip file from the releases page on GitHub https://github.com/kurator-org/kurator-web/releases, and unzip:

    unzip kurator-web-1.0.0.zip

Rename the template config file at `conf/application.conf.example` to `application.conf`. Edit the file and uncomment the line ``kurator.autoInstall = true` to enable the autoinstall.

    cp conf/application.conf.example conf/application.conf
    nano conf/application.conf

Run `bin/kurator-web` to perform the initial auto installation of jython. This will also automatically create the packages and workspace directories.

Obtain the latest release of the kurator-validation packages zip file from https://github.com/kurator-org/kurator-validation/releases. This file contains the python actors, workflow yaml and web application descriptors. Copy the downloaded zip file to the packages directory.

    cp ~/Downloads/kurator-validation-1.0.0-packages.zip packages

Use the pip installer at `jython/bin/pip` to install any python dependencies (check documentation for python workflows).

Re-run `bin/kurator-web` to start the play server and auto unpack/deploy the workflows in packages. Once the server starts the web app should be accessible at http://localhost:9000/kurator-web/. Login with the default "admin" account using password "admin".

Building and Testing Kurator-Web
--------------------------------

Follow these instructions to set up a build environment and run the application using the embedded Play server for development and testing purposes. This is the default method of deployment for the firuta.huh.harvard.edu test instance as well as the kurator.acis.ufl.edu production environments.

#### Prerequisites

Kurator-Web requires Java version 1.8 or higher. To determine the version of java installed on your computer use the `-version` option to the `java` command. For example,

    $ java -version
    java version "1.8.0_66"
    Java(TM) SE Runtime Environment (build 1.8.0_66-b17)
    Java HotSpot(TM) 64-Bit Server VM (build 25.66-b17, mixed mode)

See the following for instructions regarding the installation of Oracle Java 8 in Debian: http://www.webupd8.org/2014/03/how-to-install-oracle-java-8-in-debian.html

Or download and manually install via the Oracle website: http://www.oracle.com/technetwork/java/javase/downloads/index.html

Other development prerequisites include maven and git. If you do not currently have them installed you can use the following command.

    sudo apt-get install git maven

For production environments the default database used is MySQL. If MySQL is not already installed, install it now via apt-get:

    sudo apt-get install mysql-client mysql-server

Create the kurator user and database with privileges in MySQL:

    CREATE DATABASE kurator;
    GRANT ALL PRIVILEGES ON kurator.* TO 'kurator'@'localhost' IDENTIFIED BY 'password';

Kurator-web support sending of email notifications upon new user registration and activation. In order to use this feature, the server hosting the web app will need an outgoing smtp server. 

Debian has the exim4 mail server installed by default. To configure the mail server for kurator-web run:

    dpkg-reconfigure exim4-config

When you reach the "Mail Server configuration" dialog, select "internet site" as the option. Next, when prompted to enter the FQDN or system mail name enter your domain (e.g. firuta.huh.harvard.edu, kurator.acis.ufl.edu, etc)

Since the web app will access smtp via local host only, the default list of ip addresses to listen on are sufficient if kurator-web is the only application requiring use of the mail server (127.0.0.1 : ::1)

For the rest of the configuration process deault values will work unless you wish to change them.

Test the mail server via:

    mail -s "Test Subject" user@example.com < /dev/null

See https://www.digitalocean.com/community/tutorials/how-to-install-the-send-only-mail-server-exim-on-ubuntu-12-04 for more info

Kurator web requires a kurator home directory that will contain the workspace and Python packages. Create a kurator user via the useradd command and use this user's home directory:

    sudo useradd -m -U kurator
    sudo passwd kurator
    
The Python workflows that use the native actor require Python 2.7 and the pip installer. Additionally, the kurator native libraries provided by kurator-akka require the python-dev and r-base packages:

    sudo apt-get install python python-pip python-dev r-base 

Python workflows that use the Jython actor require installation of Jython. Download the [Jython 3.7.1.b3 installer jar](http://search.maven.org/remotecontent?filepath=org/python/jython-installer/2.7.1b3/jython-installer-2.7.1b3.jar) and run the installer from the command line as root.

    sudo java -jar jython-installer-2.7.1b3.jar

Select the standard installation when prompted (option 2) and when asked to provide the target directory enter "/opt/jython". This will install jython to "/opt/jython".

Log in as the kurator user created previously and create a directory for the projects and another directory for deployments in the user's home directoy:

    cd /home/kurator
    mkdir projects
    mkdir deployments
    
#### Clone projects from GitHub

Login as the kurator user for the following steps:

Clone the kurator-akka project:

    git clone https://github.com/kurator-org/kurator-akka.git

Clone prerequisites for ffdq and dq reports:

    git clone https://github.com/kurator-org/ffdq-api.git
    git clone https://github.com/kurator-org/kurator-ffdq.git

and the event_date_qc and geo_ref_qc projects:

    git clone https://github.com/FilteredPush/event_date_qc.git
    git clone https://github.com/FilteredPush/geo_ref_qc.git

Clone the kurator-validation and kurator-fp-validation projects containing the workflows:

    git clone https://github.com/kurator-org/kurator-validation.git
    git clone https://github.com/kurator-org/kurator-fp-validation.git
    
The kurator-fp-validation project also depends on the FP-CurationServices project:

     git clone https://github.com/FilteredPush/FP-KurationServices.git

Finally clone the web app project found in this repository:

    git clone https://github.com/kurator-org/kurator-web.git
    
#### FFDQ and QC actor libraries

The projects that make up kurator and the set of workflows standard to the production deployments are shown below with links between them to indicate the dependency graph. Projects are listed from left to right in the build order.

The first projects to build are the ffdq library and api projects as well as the event_date_qc and geo_ref_qc projects that depend on ffdq:
 
    ffdq-api --> kurator-ffdq --> event_date_qc
                                  geo_ref_qc

Starting from the kurator user's home directory (/home/kurator/), build these projects using maven install:

    cd ffdq-api
    mvn clean install
    
    cd kurator-ffdq
    mvn clean install
    
    cd event_date_qc
    mvn clean install
    
    cd geo_ref_qc
    mvn clean install

NOTE: the latest stable version of all of the projects above are also available via maven central and local clones of the projects are not required if working only on the other projects in a development environment (e.g. kurator-akka, kurator-validation, kurator-fp-validation and kurator-web below). These dependencies and the qc libraries are downloaded automatically when running maven install on kurator-validation.

1) https://mvnrepository.com/artifact/org.datakurator
2) https://mvnrepository.com/artifact/org.filteredpush

#### Kurator-akka and workflows

Second is the kurator-akka top level project and the kurator-validation/kurator-fp-validation projects that contain the Python and Java actors

    kurator-akka --> kurator-validation --> kurator-fp-validation

Starting from the kurator user's home directory (/home/kurator/), build these projects using maven install:

    cd kurator-akka
    mvn clean install
    
    cd kurator-validation
    mvn clean install
    
The packages directory of the kurator-validation project contains all the Python actors and configuration that are currently deployed in production. In order to install the Python dependencies via pip, use the requirements.txt file provided in <code>packages/kurator_dwca</code> as an argument to pip:

    pip install -r kurator-validation/packages/kurator-dwca/requirements.txt
    
In order to build the kurator-fp-validation workflows via maven install, first build the FP-CurationServices dependency followed by kurator-fp-validation:

    cd FP-CurationServices
    mvn install

    cd kurator-fp-validation
    mvn install

#### Configuration

Once you have successfully built the dependencies the next step is to configure the web app.

A template web application configuration file can be found at conf/application.conf.template. Make a copy of this file named application.conf in the same directory and edit to set the database and smtp server connection information:

    cd kurator-web/conf
    cp application.conf.example application.conf
    vi application.conf

By default the play application is configured to use the embedded in memory H2 database. If you plan on using MySQL as the production database comment out the two lines in conf/application.conf that configure the h2 database and uncomment the lines for [mysql configuration](https://github.com/kurator-org/kurator-web/blob/master/conf/application.conf.example#L53-L57) instead.

Set the values of db.default.user and db.default.password to the username and password used when creating the kurator database in MySQL. 

Next configure the host and user for smtp (localhost, and the kurator user if exim4 was configured according to the prerequisites) in the [mailer section of the config](https://github.com/kurator-org/kurator-web/blob/master/conf/application.conf.example#L61-L64). These are the settings the web app will use when sending the notification emails.

Also set the [kurator.email](https://github.com/kurator-org/kurator-web/blob/master/conf/application.conf.example#L73) property to the user@hostname according to settings that your mail server is using (e.g. kurator@kurator1.acis.ufl.edu). This property is used as the sender email for notifications that the web app sends out.
 
The [python.path](https://github.com/kurator-org/kurator-web/blob/master/conf/application.conf.example#L76) property by default should point to the  packages directory of kurator-validation or kurator-fp-validation created earlier via git clone (e.g. home/kurator/projects/kurator-validation/packages).  

Lastly, if you would like the play server to accept connections from all hosts instead of just localhost, set the value of the [http.address](https://github.com/kurator-org/kurator-web/blob/master/conf/application.conf.example#L20) property to 0.0.0.0.

###  Build and Run

Once the web application is configured, build a distribution zip file via the included activator utility:

    cd kurator-web/
    bin/activator dist

Unzip the distribution archive to the deployments directory in /home/kurator and create a symbolic link "kurator-web" for the current deployment:

    cd /home/kurator
    unzip kurator-web/target/universal/kurator-web-1.0.2.zip -d deployments
    ln -s deployments/kurator-web-1.0.2 kurator-web

By default, kurator-web expects to find the "packages" directory relative to the deployment root directory (e.g. /deployments/kurator-web/packages). Create a symbolic link in the deployment that points to the packages directory in the kurator-validation project to deploy workflows:

    cd /home/kurator/deployments/kurator-web
    ln -s /home/kurator/projects/kurator-validation/packages

This ensures that when the kurator-validation project is updated via git pull, any updates to the python workflows are automatically redeployed.

NOTE: in order to update the Java workflows, which are contained in the kurator-validation jar file, rebuild and redeploy the web app via bin/activator dist by repeating the steps described above. 

Run the play production server from the distribution directory unzipped within deployments. Use

    cd deployments/kurator-web
    bin/kurator_web -Dhttp.port=80 -Dkurator.jar=/home/kurator/projects/kurator-validation/target/kurator-validation-1.0.2-jar-with-dependencies.jar

By default the Play server will listen on port 9000 however the -Dhttp.port used in the command above to set the port to 80 can be used to change the default. Open http://localhost/kurator-web/ in your browser after starting the server to test the web application. 
The -Dkurator.jar option is required and should point to a copy of the kurator-validation jar and is used by the command-line workflow runner in the web app to run workflows.  

### Systemd startup script

Create a unit file for the kurator web systemd service at `/etc/systemd/system/kurator.service` with the following contents:

    [Unit]
    After=network.target
    
    [Service]
    EnvironmentFile=/home/kurator/kurator-web/conf/env
    MemoryLimit=8G
    PIDFile=/home/kurator/kurator-web/RUNNING_PID
    WorkingDirectory=/home/kurator/kurator-web
    ExecStart=/home/kurator/kurator-web/bin/kurator-web -Dhttp.port=80 -Dkurator.jar=/home/kurator/projects/kurator-validation/target/kurator-validation-1.0.2-jar-with-dependencies.jar
    Restart=on-failure
    User=root
    Group=kurator

    # See http://serverfault.com/a/695863
    SuccessExitStatus=143
    
    [Install]
    WantedBy=multi-user.target

If using the same directories according to the config the defaults in the example above can be used. Otherwise replace the paths with the ones you are using for your deployment. By default the systemd script is configured to start the web app listening on port 80, the -Dhttp.port option in the command set as the value of ExecStart can be used to change the port.

Once you are done with this file, enable the service via:

    systemctl enable kurator.service

Reboot the machine or start the service manually by using:

    sudo systemctl start kurator
    
<!---

Feature is not currently in use

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

    $ keytool -keystore keystore.ks -importcert -alias <certificate-alias> -file cert.crt

Once both remote and local keystores have been configured, deploy the signed packages zip file via the Admin > Deploy Workflows page.
--->
