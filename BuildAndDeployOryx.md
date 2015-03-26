# THIS TUTORIAL IS DEPRECATED. PLEASE USE [THIS GUIDE](http://code.google.com/p/oryx-editor/wiki/SetupDevelopmentEnvironment) TO SET UP A DEVELOPMENT ENVIRONMENT FOR ORYX. #

# Introduction #

This guide explains how to build and deploy the Oryx Server Application.

**Checklist:**
  * [J2SE 5 Java Runtime Environment](http://www.java.com/) or higher
  * [Apache Tomcat](http://tomcat.apache.org/) Version 5.5 or higher
    * Privileges to deploy new web applications using the Tomcat-Manager or privileges to add the war files in _webapps_ directory of Tomcat and restart the Tomcat server.
  * [PostgresSQL](http://www.postgresql.org/) 8.3 or higher (with PLPython)
    * Superuser privileges for the database.
  * [Python](http://www.python.org/) Version 2.5 (PotgresSQL 8.3 only works with this version)
  * [ANT](http://ant.apache.org/bindownload.cgi) 1.6 or higher

# Building the WAR files #
The build process is implemented in Apache ANT. The _build.xml_ file can be found in the root directory of the svn trunk. Oryx consists of two different WAR files:
> oryx.war:
    * Editor
    * Stencilsets
    * Stand-alone Servlets

> backend.war:
    * Repository
    * Servlets/Handler with database access

In order to build the WAR files, use either the bash tool "ant" or the external tools of Eclipse. Both WAR files will be stored in the dist directory of your working copy. Please make sure that you don't commit the WAR files to the SVN! The following list shows the most common ANT build tasks:

> oryx.war: **build-editor**

> backend.war: **build-backend**

> both WAR files: **rebuild-all**


# Deploying the WAR files #
To deploy the WAR files to the Apache Tomcat web server you can copy the files manually to the webapps directory, use the Tomcat manager application or use one of the following ANT deployment tasks:

> oryx.war: **deploy-editor**

> backend.war: **deploy-backend**

> both WAR files: **deploy-all**

Before you can use the ANT tasks, you have to set the path to the webapps directory of Tomcat in the build.properties file.
In addition to that, the backend.war needs a running PostgreSQL database with the Oryx schema and a user called _poem_. The database connection options are located in the _/poem-jvm/etc/hibernate.cfg.xml_ file and the database schema in the _/poem-jvm/data/database/db\_schema.sql_ file. You may use the following commands to install the database schema:
```
# Create new database
createdb -h localhost -U postgres -O poem poem

# Deploy database schema
psql -h localhost -U postgres -f db_schema.sql -d poem
```