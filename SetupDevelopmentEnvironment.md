# In a Nutshell #

## Installation on Ubuntu 8.10 ##

### Tomcat ###
  * install `tomcat6-user`
  * create `tomcat4oryx` tomcat: `tomcat6-instance-create tomcat4oryx`
  * modify `bin/startup.sh`
    * add `export JPDA_ADDRESS=8001`
  * change `deploymentdir` in `build.properties` to the full path to `tomcat4oryx`
  * (quick-hack until a better solution is provided by Ubuntu) modify the last line of `/usr/share/tomcat6/bin/startup.sh` to show `exec "$PRGDIR"/"$EXECUTABLE" jpda start`

### Eclipse ###
  * Manual installation
  * Aptana-plugin
  * Subversion-plugin

## Development remarks ##

### Starting and stopping tomcat ###
  * `tomcat4oryx/bin/startup.sh`
  * `tomcat4oryx/bin/shutdown.sh`

## Oryx install and setup on Windows (Tested with Win7) by Alexander Nowak ##

  1. Install Tools in following order (also described at: http://code.google.com/p/oryx-editor/wiki/SetupDevelopmentEnvironment)
1.1) Java JDK 5 or higher (http://java.sun.com/javase/downloads/index.jsp)
    * Ensure that JAVA\_HOME Enviroment variable is set to java installation dir (e.g. "C:\program files\java\jdk1.6")
      * Ensure that PATH Enviroment variable is extended with "\bin"-folder of java installation dir (e.g. "C:\program files\java\jdk1.6\bin")
1. 2) Firefox Browser (http://www.mozilla.com/de/firefox/).
    * For debugging it is helpful to also install the Firebug Addon (https://addons.mozilla.org/de/firefox/addon/1843)
1.3) Tomcat 6 (unpack to arbitrary folder)

1.4) Eclipse IDE for Java EE Developers (http://www.eclipse.org/downloads/)
  * You also need a subversion plugin for eclipse, e.g. subclipse (http://subclipse.tigris.org/) or any other subversion client.
1.5) Python version 2.5.2
  * IMPORTANT: Use exactly this version, available here: http://www.python.org/download/releases/2.5.2
  * Just follow the install wizard. No special setup is needed here.
  * Install 32bit-Pyhton also on 64bit Windows!
1.6) PostgreSQL 8.3.x (IMPORTANT: Use exactly this version)
  * Download the pgInstaller version to enable advanced installation options
  * Use included wizard for installation
    * Within the install wizard select "install postgreSQL as service"
  * ATTENTION: The page after "Initialize database cluster" (called "Enabled procedural languages") has to show PL/python (otherwise python installation failed. (Re-)Install PL/python.)
    * Ensure that PATH Enviroment variable is extended with "\bin"-folder of postgresql installation dir (e.g. "C:\program files\PostgreSQL\8.3\bin")
    * IMPORTANT: remember the password for windows user postgres and database. You will need them later...

2) In eclipse, checkout Oryx from SVN: "http://oryx-editor.googlecode.com/svn/trunk/"
  * SVN-Link and information is also available here: http://code.google.com/p/oryx-editor/source/checkout
2.1) How to Checkout SVN in eclipse:
  * Select "File -> Import..." and choose "SVN -> Project from SVN"
    * Follow the wizard. Create a new repository location using the Oryx SVN link above. Then Checkout the "trunk"-folder.

3) Setup Database Environment:
  * Run comandline as user postgres: type "`runas /user:postgres cmd`" in run-prompt in Windows. On the Keyboard press the Windows-key and R at the same time to get the run-prompt. To logon use windows user (postgres) password from postgrSQL installation.
  * change to schema file directory which is located in "oryx-workspace/poem-jvm/data/database/db\_schema.sql"
  * Run the following commands to import schema:
    * `createuser -U postgres --echo --pwprompt --encrypted poem`
      * the best is to use password `poem`
    * `createdb -U postgres --echo --encoding utf8 --owner poem poem`
    * `psql poem < db_schema.sql postgres`

4) Change Build Properties:
  * open the file `build.properties` in root dir
  * edit "deploymentdir" and set it to your apache tomcat wepapps folder (e.g. C:/apache-tomcat-6.0.32/webapps)
  * If you used a password different than `poem` for the poem user (see 3, bullet point 3.1), edit line "`<property name="connection.password">poem</property>"` in `/poem-jvm/etc/hibernate.cfg.xml`

5) Build Oryx to produce deployable war-files:
  * Right-Click on "build.xml" in root dir and select "Run As -> External Tool Configuration"
  * Set buildfile to "build.xml", e.g. ${workspace\_loc:/oryx/build.xml}
  * Set base directory to oryx root directory, e.g. ${workspace\_loc:/oryx}
  * Choose tab "Targets" and check the following targets: "build-all" and "deploy-all"
    * Start build by clicking on run-button.
    * If the build was succsessfull, the two files "oryx.war" and "backend.war" should have been copied to your Apache Tomcat "\webapps" folder

6) Start Apache Tomcat with "apache-install-dir/bin/startup.bat".
  * If Tomcat was already running, it automatically re-deploys the war files.

7) Start your browser and open "http://localhost:8080/backend/poem/repository".

7.1) Opening the backend enables users to create new process models or to browse/manage the process models that are already stored in oryx. A double-click on a process model then opens the model in the frontend, where models can be modified.

7.2) To store models in oryx you need an open-id (use google for more information)





**ANT**
  * It is also possibile to use the build-target create-db
  * Therefore fill in your postgres installation credentials to the build.properties and run the create-db target



## Development Setup ##

The build process of this setup is much faster because we avoid building war files. Instead, we point tomcat to our build directory containing the Oryx web-application. To do this you have to build the Oryx backend and frontend separately.

Build backend:
Select target "dist" in `poem-jvm/build.xml`
The backend web-application can afterwards be found in `dist/backend`.

Build frontend:
Select targets "build, examples, compress, dist" in `editor/build.xml
The frontend web-application can afterwards be found in `dist/client`.

To run the whole Oryx web-application we have to point tomcat to the two web-application-folders we just created. This is done by an entry in the file 'server.xml'. This file is located in the `conf`folder of every tomcat installation. Insert the following tags as child tags of the `<Host>`tag:

```
<Context path="/oryx"
            docBase="/Oryx/trunk/dist/client" debug="0" reloadable="true"/>
 
<Context path="/backend"
             docBase="/Oryx/trunk/dist/backend" debug="0" reloadable="true"/>
```

Please adjust the paths in the docBase attribute according to your OS and directory structure. These are just examples.

### Enabling Debugging ###
  * Target `build-with-xhtml-test-files-flag`: enables copying of `examples\*.xhtml` to the deployment dir
  * Target `build-with-script-files-flag`: The .js files are deployed as such and not compressed into a single oryx.js.

Start Tomcat in debug mode:
  * change to tomcat-bin dir
  * `set JPDA_TRANSPORT=dt_socket`
  * `catalina jpda start`

At Eclipse:
  * Debug -> Debug Configurations...
  * Remote Java Application -> New
  * Project: oryx
  * Port: 3337

## Test ##
  * Start the server and navigate with firefox to localhost:8080/backend/poem/repository
  * Now everything done. Congratulation!

# Other Guides #
  * http://ddweerasiri.blogspot.com/2009/04/how-to-build-oryx-editor-in-ubuntu.html
  * http://code.google.com/p/oryx-editor/wiki/DeployOryxOnWindowsOrDebian


