# THIS TUTORIAL IS DEPRECATED. PLEASE USE [THIS GUIDE](http://code.google.com/p/oryx-editor/wiki/SetupDevelopmentEnvironment) TO SET UP A DEVELOPMENT ENVIRONMENT FOR ORYX. #

# Introduction #

This page documents the configuration steps which have been made for setting up the [Oryx VirtualBox image](OryxVirtualMachineImage.md). It has been tested with Kubuntu 8.04 but it should mostly work for other Debian-based Linux distributions as well.

# Details #

Assuming it has been started with a newly installed system first install the latest updates, KDE 3.5 and some tools which might come handy during Oryx development:
```
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install kubuntu-desktop vim-gtk kdiff3 firefox firefox-2 inkscape
```

Since all derivatives of Ubuntu use literally the same underlying system you may easily switch to the GNOME desktop environment:
```
sudo apt-get install ubuntu-desktop
```
or Xfce desktop environment:
```
sudo apt-get install xubuntu-desktop
```
or others if that suits you better.

## Java ##
```
sudo apt-get install sun-java5-jdk sun-java6-jdk
sudo -i
echo '' >> /etc/profile
echo 'export JAVA_HOME=/usr/lib/jvm/java-6-sun' >> /etc/profile
logout
```

## Eclipse, Aptana and Subversion ##
```
sudo apt-get install eclipse subversion libsvn-java
```

  1. Create Eclipse workspace at /home/kubuntu/eclipse-workspace
  1. Add Subclipse 1.4.x update site (http://subclipse.tigris.org/update_1.4.x) to Eclipse
  1. Install Subclipse 1.4.1, Suversion Client Adapter 1.5.0, Subversion Native Library Adapter (JavaHL) 1.5.0, SVNKit Library 1.2.0.4502 and SVNKit Client Adapter 1.5.0.1 through Eclipse Update Manager
  1. Configure Subclipse to use SVNKit since JavaHL 1.5.0 is needed while the libsvn-java package is currently shipping only version 1.4.6
  1. Adde Aptana Studio update site (http://update.aptana.com/update/studio/3.2/) to Eclipse
  1. Install Aptana Web Development Tools, Aptana Editor Infrastructure, Aptana JavaScript Editor, Aptana CSS Editor and Aptana HTML Editor Version 1.1.6.009905 through Eclipse Update Manager
  1. Check out Eclipse project from http://oryx-editor.googlecode.com/svn/trunk/
  1. Change PATH in oryx.js:80 from './' to '/oryx/'

## Tomcat ##
```
sudo apt-get install tomcat5.5-admin
```
Configure permissions for the Oryx web applications
```
sudo sh -c "echo '
// The permission granted to the oryx application
grant codeBase \"file:\${catalina.base}/webapps/oryx/-\" {
        permission java.security.AllPermission;
};

// The permission granted to the backend application
grant codeBase \"file:\${catalina.base}/webapps/backend/-\" {
        permission java.security.AllPermission;
};' >> /etc/tomcat5.5/policy.d/50user.policy"
```

Additionally the following permissions can be granted to tomcat-juli.jar in /etc/tomcat5.5/policy.d/03catalina.policy to avoid some security exceptions:
```
permission java.io.FilePermission "${catalina.base}${file.separator}webapps${file.separator}oryx${file.separator}WEB-INF${file.separator}classes${file.separator}logging.properties", "read";
permission java.io.FilePermission "${catalina.base}${file.separator}webapps${file.separator}backend${file.separator}WEB-INF${file.separator}classes${file.separator}logging.properties", "read";
```

## PostgreSQL ##
```
sudo apt-get install postgresql postgresql-plpython-8.3 postgresql-contrib pgadmin3
```

Changing password of postgresql superuser to 'kubuntu'
```
sudo su postgres -c psql template1
template1=# ALTER USER postgres WITH PASSWORD 'kubuntu';
template1=# \q
sudo passwd -d postgres
sudo su postgres -c passwd
sudo su postgres -c psql < /usr/share/postgresql/8.3/contrib/adminpack.sql
```

Start pgAdmin III and try connecting
```
pgadmin3 &
```

Configure postgres to not require passwords for local tcp connections:
```
sudo -i
/etc/init.d/postgresql-8.3 stop
cd /etc/postgresql/8.3/main
mv pg_hba.conf pg_hba.conf.bak
sed 's/^host    all         all         127\.0\.0\.1\/32          md5$/host    all         all         127.0.0.1\/32          trust/' pg_hba.c
onf.bak > pg_hba.conf
/etc/init.d/postgresql-8.3 start
logout
```

Create users 'kubuntu' and 'poem' without password
```
su postgres
createuser --superuser --echo kubuntu
createuser --superuser --echo poem
exit
```

## Buildr ##
```
sudo apt-get install ruby-full ruby1.8-dev libopenssl-ruby libpq-dev build-essential 
```

The Debian package for rubygems will not allow you to install Buildr, so you need to install RubyGems from source:
```
mkdir /home/kubuntu/installed
cd /home/kubuntu/installed
sudo ruby setup.rb
sudo ln -s /usr/bin/gem1.8 /usr/bin/gem
```

Installing Buildr
```
export JAVA_HOME=/usr/lib/jvm/java-6-sun
sudo env JAVA_HOME=$JAVA_HOME gem install rjb -v 1.1.2
sudo env JAVA_HOME=$JAVA_HOME gem install hoe -v 1.6.0
sudo env JAVA_HOME=$JAVA_HOME gem install buildr
sudo env JAVA_HOME=$JAVA_HOME gem install activerecord
sudo env JAVA_HOME=$JAVA_HOME gem install pg
```

## Build and Deploy Oryx ##
Run the Ant tasks 'clean' and 'all' within Eclipse or:
```
cd /home/kubuntu/eclipse-workspace/oryx
ant clean all
```

Building the PoEM Backend:
```
cd /home/kubuntu/eclipse-workspace/oryx/poem-jvm
# build database
buildr clean-postgres migrate
# build webapp:
buildr clean package
```

Deploy Oryx using:
```
sudo /etc/init.d/tomcat5.5 stop
sudo cp /home/kubuntu/eclipse-workspace/oryx/poem-jvm/backend/target/poem-backend-1.0.war /var/lib/tomcat5.5/webapps/backend.war
sudo cp /home/kubuntu/eclipse-workspace/oryx/dist/oryx.war /var/lib/tomcat5.5/webapps/oryx.war
sudo /etc/init.d/tomcat5.5 start
```

To monitor the Tomcat logfiles you can use:
```
sudo tail -f /var/lib/tomcat5.5/logs/catalina.YYYY-MM-DD.log
```

Finally open the model repository in Firefox 3:
```
firefox http://localhost:8180/backend/poem/repository/ &
```