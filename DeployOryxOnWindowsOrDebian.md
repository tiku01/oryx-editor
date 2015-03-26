# THIS TUTORIAL IS DEPRECATED. PLEASE USE [THIS GUIDE](http://code.google.com/p/oryx-editor/wiki/InstallUpdateConfigureProductiveSystems) TO SET UP A PRODUCTIVE INSTALLATION OF ORYX. #

# Introduction #

This walkthrough demonstrates how you deploy the complete Oryx web application including the repository, the editor and the database schema. It is basically designed for the deployment in productive environments. If you want to build your own binaries from source, you may read this article.

# Before You Get Started #
This article focus on the deployment on Windows and Debian operation systems, although it isn't limited to these systems. Before you can deploy the system you need to install an Apache Tomcat and PostgreSQL server. You can find an quick installation guide for both servers in the end of the article.

**Checklist:**
  * [J2SE 5 Java Runtime Environment](http://www.java.com/) or higher
  * [Apache Tomcat](http://tomcat.apache.org/) Version 5.5 or higher
    * Privileges to deploy new web applications using the Tomcat-Manager or privileges to add the war files in _webapps_ directory of Tomcat and restart the Tomcat server.
  * [PostgresSQL](http://www.postgresql.org/) 8.3 or higher (with PLPython)
    * Superuser privileges for the database.
  * [Python](http://www.python.org/) Version 2.5 or higher
  * [Download the Oryx-Server](http://www.oryx-editor.org/downloads).

# PART I: Configure the Database #
## STEP 1 - Create the _poem_ User ##
The default configuration of Oryx uses a database user called _poem_ without password to connect to the database. If you create the poem user with an empty password, it will not be necessary to make any changes to the configuration file _hibernate.cfg.xml_. But if you want to change the password, you'll find the instructions in PART III. **It is strongly recommended to use a secure password for the poem user. This applies particularly if your PostgreSQL server allows TCP connections from other hosts.**
The _poem_ user may not be a superuser itself. It simply needs owner rights for the poem database. In order to create a new PostgreSQL user open the command shell (Windows: cmd.exe/Debian: bash). Now you need the user name and password from a database superuser to create the poem user. Assuming your superuser is _postgres_ you have to enter the following commands:

```
su postgres  # become the postgres user to avoid authentication problems
createuser --username postgres --echo --pwprompt --encrypted poem
exit
```

## STEP 2 - Create the _poem_ Database ##
The default name of the Oryx database is _poem_. To create the database named _poem_ from the command shell using the superuser _postgres_ and set the user _poem_ as owner enter the following command:

```
createdb --username postgres --echo --encoding utf8 --owner poem poem
```

You can use any database name you like, but if you use poem, it will not be necessary to make any changes to the Tomcat configuration.

## STEP 3 - Deploy the Database Schema ##
The file _database\_dump\_raw.sql_ in our download package contains all necessary information to initialize the poem database. The schema can also be retrieved from a development system using:

```
pg_dump --username postgres --clean --file=database_dump_raw.sql --format=plain poem
```

Enter the following command to install the schema:

```
psql --username postgres --dbname poem --file database_dump_raw.sql
```

This uses the _postgres_ superuser to add the schema from the _database\_dump\_raw.sql_ file to the _poem_ database. If the sql file isn't in the working directory of the shell, than you have to qualify the full path to the file.

You can also execute all those commands remotely if the PostgreSQL Database allows remote access. Simply add the option -H _hostname_ to each command.  [Here](http://www.postgresql.org/docs/8.3/interactive/index.html) you can find a very detailed documentation of the PostgreSQL Database.


# PART II: Deploy the WAR Files to Tomcat #
  1. Download [the latest Oryx release](http://www.oryx-editor.org/downloads) and extract the web applications from the zip file.
  1. Open the Tomcat Manager in your web browser (Default Tomcat configuration: _http://hostname:8180/manager/html_ (Debian) or _http://hostname:8080/manager/html_ (Windows)). **Notice:** The Manager isn't installed by default in all Tomcat distributions and you need a Tomcat user which have the right to access the manager. (--> Appendix B)
  1. Then use the _"Warfile to deploy"_ feature of the manager to upload the _oryx.war_ file from the download package. This may take a moment depending on your connection speed to the server. After Tomcat deployed the war file succesfully it prints an OK message on top of the manager.
  1. Go on and upload the second war file _backend.war_ .

If you used all the default options from the walkthrough, you won't need to make any changes to the configuration. If not then go on with PART III. The default url of the repository is _http://servername:port/backend/poem/repository_ .

**Notice:** The Oryx-Editor currently needs [the Firefox browser](http://getfirefox.com) and if you want to save your models you need an [OpenID](http://openid.net/). The OpenID login works without further configuration except if your server connects to the internet through an HTTP proxy. In that case, you can change the parameters _proxy-host-name_ and _proxy-port_ in the _web.xml_ file of the backend.

# PART III: Configuration #

## Optional : Change Database Server, Name, User and Password ##
The Oryx Server uses Hibernate Technology to access the database. If you deployed the backend war file once, then Tomcat creates a backend directory in the webapps folder. Assuming that _$(TOMCAT\_HOME)_ is the root path to the Tomcat folder open the _$(TOMCAT\_HOME)/webapps/backend/WEB-INF/classes/hibernate.cfg.xml_ file and edit the XML-Elements according to your server configuration.

```
        <!-- Database connection settings -->
        <property name="connection.driver_class">org.postgresql.Driver</property>
        <property name="connection.url">jdbc:postgresql://server_name/database_name</property>
        <property name="connection.username">user_name</property>
        <property name="connection.password">password</property>
```

After you finished editing the file, you have to restart the Tomcat to apply the changes. If your database doesn't run on the same server like the Tomcat, make sure that the database accepts Tcp connections from the Tomcat host.


# PART IV: Upgrading an Existing Installation and Creating a Backup #

**Windows:**
  1. Have a look at http://hostname:port/manager/html/sessions?path=/backend if there are any active users. The 'Inactive Time' of a session does NOT imply that the user has stopped modeling since then. It only shows the last time the user accessed the backend, e.g., for storing or loading a model.
  1. Use http://hostname:port/manager/html to stop the applications 'oryx' and 'backend'
  1. To make backup of the current data open a command prompt and go to 'C:\Program Files\PostgreSQL\8.3\bin'. You can also use the link 'Command Prompt' in the PostgreSQL group of the Windows Start Menu. Create a database dump using:
```
pg_dump --username postgres --clean --file=YYYY-MM-DD-database-dump.sql" --format=plain poem
```
  1. Undeploy the applications 'oryx' and 'backend' using http://hostname:port/manager/html/undeploy?path=/oryx and http://hostname:port/manager/html/undeploy?path=/backend
  1. If the 'backend' application is not completely undeployed, restart Tomcat and undeploy it again using: http://hostname:port/manager/html/undeploy?path=/backend
  1. If the Oryx team released any database migration scripts you can install them using:
```
psql --username postgres --dbname poem --file migration-script.sql
```
  1. Deploy the two web applications _oryx.war_ and _backend.war_ using the Tomcat manager http://hostname:port/manager/html as described in _Part II_.
  1. If needed, set the HTTP proxy configuration using the parameters _proxy-host-name_ and _proxy-port_ in the _web.xml_ file of the backend and restart Tomcat to make them effective. You can also do that before deployment by unzipping the backend.war, changing the _web.xml_ and zipping it again.

# APPENDIX #

**Notice:** The following quick start guides demonstrate, how to install the software packages using the default configuration. You may have to customize the given examples according to your system and security policies.

# Appendix A: Install Python #
**Debian:** If Python 2.5 isn't already installed, use the following command to download and install it:

```
apt-get install python2.5
```

**Windows:** You can download Python [here](http://python.org/download/). It's not necessary to make any changes to the default values in the wizard. But it useful to change the installation path to your _Program Files_ directory. The complete Python documentation can be found [here](http://python.org/doc/).

# Appendix B: Install Java #
## Step 01 - Download and Install Binaries ##
**Debian:** SUN Java 6 is currently only available in the unstable version of Debian. First you have to enable non-free packages in the apt configuration file _/etc/apt/sources.list_ in order to install Java using the following command:

```
apt-get install sun-java6-bin
```

The _/etc/apt/sources.list_ file on a debian unstable system may look like this:

```
deb http://ftp.de.debian.org/debian/ lenny main non-free
deb-src http://ftp.de.debian.org/debian/ lenny main

deb http://security.debian.org/ lenny/updates main
deb-src http://security.debian.org/ lenny/updates main
```

**Windows:** The files for the Java JRE for Windows can be directly downloaded from this [SUN Website](http://www.java.com/en/download/manual.jsp). Select either the online or offline version, download the file and start the installer. If you simply click on next, the installer will put all the java files in the _C:\Programme\Java_ directory.


## Step 02 - Set _JAVA\_HOME_ Environment Variable ##
**Debian:** Before you can start the Tomcat Server you have to set the JAVA\_HOME environment variable. If you want set _JAVA\_HOME_ system wide and if you installed Java 6 to the default directory, then add the following line to the _/etc/profile_ file:

```
export JAVA_HOME=/usr/lib/jvm/java-6-sun
```

**Windows:** Control Panel -> System -> Advanced -> Environment Variables -> System Variables -> New



# Appendix C - Install and Configure PostgreSQL #
**Debian:** Use the following command to download and install the binaries:

```
apt-get install postgresql-plpython-8.3
```

After the setup finished you can use `/etc/init.d/pgsql start` to start the  DB server and `/etc/init.d/pgsql stop` to stop the server. Note: Depending on the version of your PostgreSQL package, the name of the script might be `postgresql-8.1` instead of `pgsql`

**Windows:**
  1. Go to one of the [PostgreSQL mirror sites](http://wwwmaster.postgresql.org/download/mirrors-ftp) and download a binary release of at least version 8.3.0 for Win32, e.g. _binary/v8.3.3/win32/postgresql-8.3.3-1.zip_.
  1. Extract all the files from the zip archive and open the file _SETUP.bat_.
  1. You can install the packages which are selected by default.
  1. The next step is very important. You should install PostgreSQL as a service with a service user. This user is a Windows user account which is named _postgres_ by default. In addition to that you have to set a password, otherwise the installer creates long and secure one for you. The account is used to run and change the configuration of the PostgreSQL service.
  1. On the next page you have to set the password for the superuser of the database server, which is also named _postgres_ by default. This user is a PostgreSQL user used to access the actual database. You shouldn't change the other values on this page if you have no compelling reasons.
  1. Finally enable PL/Python as procedural language and finish the wizard with the default settings. After the installation completes quit the wizard. It's not necessary to install any other database connectors.
  1. If you want to use the PostgreSQL commandline tools directly from every directory, add the PostgreSQL bin directory (default: _`%ProgramFiles%\PostgreSQL\8.3\bin`_) to the environment variable _PATH_. Otherwise just use the _Start->All Programs->PostgreSQL 8.3->Command Prompt_ short cut.

# Appendix D - Install and Configure Apache Tomcat #
**Debian:** Use the following command to download and install the binaries:
```
apt-get install tomcat5.5 tomcat5.5-admin
```

Before you can access the web front end to administrate the server, you have to create a user in the _/etc/tomcat5.5/tomcat-users.xml_ file. Add the following line to the XML file to create a user named _root_ with the password _1234_ with the privileges to administrate the server and access the manager.

```
<tomcat-users>
   ...
   <user username="root" password="1234" roles="admin,manager" />
   ...
</tomcat-users>
```

After that, use `/etc/init.d/tomcat5.5 start ` or `/usr/share/tomcat5.5/bin/startup.sh` to start the server. The URL of the manager application is _http://hostname:port/manager/html_.

**Windows:** Download Tomcat 6.0 Windows Service Installer from the official [Apache Website](http://tomcat.apache.org/download-60.cgi). Start the installer and select all options except examples as shown in the following screenshot. The next important step is the basic configuration. If the server is used as productive system you should use port 80. Otherwise you can stick with port 8080. After that select an username and password for the server administrator. On the next page you have to select path to your Java JRE. The installer automatically finds the standard version. Make sure that you select Java 6 if you have installed multiple versions of Java on your system. After the setup has completed select start Tomcat and finish the wizard. Now you can begin with the deployment of oryx as shown in Part I.

## Optional: Change Listening Port ##
By default Tomcat listens on port 8180 (Debian) or 8080 (Windows) for incoming requests by default. In order to change the port open the _server.xml_ file which can be found in _/etc/tomcat5.5/_ (Debian) or _`%ProgramFiles%\Apache Software Foundation\Tomcat x.x\conf`_ (Windows).
Look for the following XML element:
```
<Connector port="8180" maxHttpHeaderSize="8192" maxThreads="150" minSpareThreads="25" maxSpareThreads="75" enableLookups="false" 
redirectPort="8443" acceptCount="100" connectionTimeout="20000" disableUploadTimeout="true" />
```
and change the attribute _port_ to the desired port number. After that you have to restart Tomcat.