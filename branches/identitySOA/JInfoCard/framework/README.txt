==========
Framework:
==========

I've migrated the build system to use Maven (http://maven.apache.org/) instead of Ant, which resulted in a very much simplified deployment process. To get the framework set up, you only need to execute

mvn install

from within the framework directory you checked out as above. This will download all the dependencies, build the jar file for the framework and install it into your local maven repository (necessary for the website setup).

=========
MiniShop:
=========

The Website requires a little more setup to adapt it to your environment. I'm deploying it on Tomcat 6 (http://tomcat.apache.org/) here, so I recommend you use that, too.

-------------
Prerequisites
-------------

Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 6
--------------------------------------------------------------------------------

JInfoCard requires strong cryptography support from the JVM, so you'll have to download and install the "Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 6", available from http://java.sun.com/javase/downloads/index.jsp (under "Other Downloads" at the bottom of the page)

Endorsed libraries
------------------

In addition, you'll need to endorse a current version of the Apache Xalan and Xerces libraries as well as the BouncyCastle JCE provider. To do this, run the command

mvn dependency:copy-dependencies

in  the minishop directory. This retrieves all dependencies and places them in the directory target/dependency, from which you can copy the files listed below into either the directory $TOMCAT_HOME/endorsed ($TOMCAT_HOME/common/endorsed for older versions of Tomcat) or the JRE endorsed directory.

List of libraries to endorse:

- bcprov-jdk15-138.jar
- resolver.jar
- serializer.jar
- xalan.jar
- xercesImpl.jar
- xml-apis.jar

-----------
Basic setup
-----------

Copy (or rename) the file example-web.xml to web.xml in the src/main/webapp/WEB-INF directory and edit it to reflect your local setup (it's commented, but please let me know if you require any clarifications!).

-----------
SSL support
-----------

If you're planning to enable SSL support for your deployment, you'll also need to copy the Keystore (in PKCS#12 or JKS format) containing your site's certificate into src/main/resources and edit the web.xml file (see above) to allow the framework to access the private key.

Build the MiniShop
------------------

Just execute the command

mvn package

in the directory containing the minishop source. This will again retrieve necessary dependencies (including the framework you built above) and build everything. You can then deploy the generated Web Application, located at target/minishop2.war into your Tomcat installation.
