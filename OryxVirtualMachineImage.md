# THIS TUTORIAL IS DEPRECATED. PLEASE USE [THIS GUIDE](http://code.google.com/p/oryx-editor/wiki/SetupDevelopmentEnvironment) TO SET UP A DEVELOPMENT ENVIRONMENT FOR ORYX. #

# Introduction #
A [VirtualBox](http://virtualbox.org) image containing a complete development environment for Oryx can be downloaded here: [oryx-vm.zip (1.8 GB)](http://myhpi.de/~fmenge/oryx-vm.zip).

The virtual machine uses username 'kubuntu' with password 'kubuntu'.The image is based on the Kubuntu 8.04 image from http://virtualbox.wordpress.com/images/ which is a default installation from the Kubuntu ISO image. In addition to that, the guest additions have been installed from VirtualBox 1.6.2 using this [guide](http://virtualbox.wordpress.com/doc/installing-guest-additions-on-ubuntu/).
A detailed description of how the virtual machine was set up can be found [here](DevelopmentEnvironmentForUbuntu.md).

# Daily Life #

For detailed instructions on how to rebuild Oryx see the [section Build and Deploy Oryx of the detailed setup description](DevelopmentEnvironmentForUbuntu#Build_and_Deploy_Oryx.md).

## Eclipse Heap Space ##

In case eclipse encounters heap size problems, you might want to adjust the respective heap size options:
  * `-Xms<size>` initial Java heap size
  * `-Xmx<size>` maximum Java heap size

You can adjust these parameters in your local eclipse configuration file: ~/.eclipse/eclipserc

This file might look similar to:
```
VM_ARGS="-Djava.library.path=/usr/lib/jni -Xms256m -Xmx1024m"
```

Make sure you start your VM with a sufficient amount of memory.

## Switching the SVN working copy to HTTPS for write access ##
The SVN working copy in the virtual machine image is a read-only working copy which has been checked out anonymously over HTTP. If you are a project member with write access to the SVN repository you may want to relocate the working copy to HTTPS and authenticate with your username to allow committing changes. There is no need to make a new checkout. You can do that by just opening a terminal and typing:

```
svn switch --relocate http://oryx-editor.googlecode.com/svn/trunk/ \
https://oryx-editor.googlecode.com/svn/trunk/ /home/kubuntu/eclipse-workspace/oryx
```

# Accessing the Server from Outside #

To be able to access the server from the host system or even from other machines you have to configure a port forwarding. Therefore, shut down the virtual machine and type the following commands in a command prompt. You have to change _OryxVM_ into the name of your virtual machine. If you are configuring multiple forwardings make sure to change the name _tomcat_ in the commands into something different describing your particular service.

```
path\to\virtualbox\VBoxManage setextradata "OryxVM" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/tomcat/Protocol" TCP
path\to\virtualbox\VBoxManage setextradata "OryxVM" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/tomcat/GuestPort" 8180
path\to\virtualbox\VBoxManage setextradata "OryxVM" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/tomcat/HostPort" 8180
```

That makes the Oryx server accessible from the host system at http://localhost:8180/.