# THIS TUTORIAL IS DEPRECATED. PLEASE USE [THIS GUIDE](http://code.google.com/p/oryx-editor/wiki/SetupDevelopmentEnvironment) TO SET UP A DEVELOPMENT ENVIRONMENT FOR ORYX. #

# Introduction #

Oryx Editor is a web application that is designed to run well in Firefox, beginning with version 2.0. Most of the application is written in JavaScript, the server is written in Java and JRuby. In addition to that, you will need to get familiar with SVG and JSON to design or edit additional stencil sets. To get more information of all technologies used in this project, have a look at [the Ohloh project site](http://www.ohloh.net/projects/oryx-editor).

Following the steps in this document will set up a development environment for Oryx on Mircosoft Windows. But most steps are exactly the same on MacOSX and on Linux and all required software is available for all platforms. So, with these instructions you should also be able to set up a development environment on any system. In this environment, you will be able to test changes in Oryx on a local test page. However, the environment does not include the complete server. You will be able to use server-side plugins like PDF export, but e.g. the process repository and the ability to save models are not supported. For some server-side plugins more steps might be necessary before using them. Instructions for deploying the whole project will be available shortly.

## Installing JDK ##

The Java SE Development Kit is required for building the project. You can download it [here](http://java.sun.com/javase/downloads/index.jsp).

## Installing a Server ##

Oryx requires a server that supports Java Servlets for server-side plugins like PDF export. You won't need a server, if you don't want to use or develop server-side plugins. We recommend using [Apache Tomcat](http://tomcat.apache.org/). Download the latest version and follow the installation instructions. On Windows Vista do not use the Windows Service Installer! Just download the zip archive and unpack it in a folder in your user folder.

## Installing Eclipse IDE ##

You will need a JavaScript development environment to start Oryx Editor development. You may prefer a simple text editor or one that has special support for JavaScript. However, we recommend you get yourself the [Aptana IDE](http://www.aptana.com/), which has served the original development team well. This instructions will guide you to a working setup as an Eclipse plugin:
  1. [Download](http://www.eclipse.org/downloads/) and extract/install Eclipse 3.2. You may prefer the 'Classic' distribution. If you decide to use another distribution, make sure that Ant is included. When using a newer version of Eclipse, check for Aptana IDE compatibility.
  1. Start Eclipse.
  1. With the help of the integrated Software Update mechanism, install the plugins from the following update site URL: [http://update.aptana.com/update/studio/3.2/](http://update.aptana.com/update/studio/3.2/). Open the link in your browser to get more information on how to install the Aptana plugin.
  1. You will have to restart Eclipse.
  1. Unless your Elipse version has integrated support for SVN, install it from the update site URL [http://subclipse.tigris.org/update\_1.2.x](http://subclipse.tigris.org/update_1.2.x) using the same mechanism.
  1. Again, you will have to restart Eclipse.

Make sure that Eclipse uses the previously installed JDK. To check this, do the following:
  1. Open _Window_ -> _Preferences..._ .
  1. In the tree view on the left open _Java_ -> _Installed JREs_.
  1. If the JDK is included in the list of installed JREs, mark it as the default JRE by checking the box in the first column. If the JDK is not included in the list, you have to add it first.
  1. Click on _OK_ to apply the changes.

## Installing Firefox ##

Make sure you have an Oryx Editor compatible version of Firefox on your system. If there is none, [download](http://www.mozilla.com/) and install version 2.0.

## Checkout the Project ##

  1. Start Eclipse.
  1. Open _File_ -> _Import_.
  1. Under the node _Other_ choose _Checkout Projects from SVN_.
  1. Enter the URL of the trunk. You can find the URL [here](http://code.google.com/p/oryx-editor/source/checkout). The trunk contains an Eclipse project file and will automatically be found. Downloading the trunk may take a while.

## Build the Project ##

For building the project you have to adjust the _build.xml_ file and set up an Ant task.

  1. In Eclipse open the file _build.xml_ in the project's root folder.
  1. Add the following lines at the end of the file, but before the `</project>` tag:
```
        <target name="copywar" depends="all">
                <delete dir="C:\Programme\Apache Software Foundation\Tomcat 6.0\webapps\oryx"/>
                <copy todir='C:\Programme\Apache Software Foundation\Tomcat 6.0\webapps'>
            <fileset dir='dist' includes='oryx.war'/>
        </copy>
        </target>
        
        <target name="tomcat-start">
            <java jar="C:\Programme\Apache Software Foundation\Tomcat 6.0\bin\bootstrap.jar" fork="true">
                <jvmarg value="-Dcatalina.home=C:\Programme\Apache Software Foundation\Tomcat 6.0"/>
                <jvmarg value="-Xdebug"/>
                <jvmarg value="-Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"/>
            </java>
        </target>
        
        <target name="tomcat-stop">
            <java jar="C:\Programme\Apache Software Foundation\Tomcat 6.0\bin\bootstrap.jar" fork="true">
                <jvmarg value="-Dcatalina.home=C:\Programme\Apache Software Foundation\Tomcat 6.0"/>
                <arg line="stop"/>
            </java>
        </target>
        
        <target name="deploy" depends="tomcat-stop, copywar, tomcat-start" /> 
```
  1. Adjust the paths to the Tomcat server, if necessary. This addition will automatically deploy Oryx to the web server in debug mode. Of course, if you use another server than Tomcat, this will not work!
  1. Save your changes.
  1. In the Eclipse toolbar open _External Tools..._ .
  1. Create a new _Ant Build_ and enter a name like "Oryx deploy".
  1. On tab _Main_ browse the workspace to add the buildfile (_build.xml_) and the base directory (the project's root directory).
  1. On tab _Targets_ check the target _deploy_ and click on _Apply_.
  1. Click on _Run_ to build and deploy the project.

If you do not want to use or develop server-side plugins, just select the target _all_ in the Ant build and run the file _./build/editor.xhtml_.

## Run ##

If Oryx is deployed successfully, you can open the example page _editor.xhtml_ that loads Oryx with the BPMN stencil set. In the default configuration of Tomcat, the URL would be [http://localhost:8080/oryx/editor.xhtml](http://localhost:8080/oryx/editor.xhtml).

Congratulation! You have now set up your development environment for Oryx.

## Debug ##

For debugging server-side plugins you have to do the following:
  1. In Eclipse open _Debug..._ .
  1. Add a new _Java Remote Application_ and enter a name like "Oryx debug".
  1. On tab _Connect_ set the project to Oryx.
  1. Click on _Apply_ and then _Debug_.

Now you should be able to set breakpoints and debug your Java code.