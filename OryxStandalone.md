# Get Xulrunner #

The Oryx Standalone version is based on <a href='https://developer.mozilla.org/En/XULRunner'>Mozilla Xulrunner</a>. Currently the 1.9.2pre Version of Xulrunner is used, which is build with an actual check-out of <a href='http://hg.mozilla.org/mozilla-central/'>Mozilla Repository</a>. To build xulrunner one just has to setup the whole build enviroment of Mozilla and configure the .mozconfig file (see <a href='https://developer.mozilla.org/En/XULRunner/Build_Instructions'>build instructions</a>).
To avoid setting up the buidl enviroment, one can use the <a href='http://releases.mozilla.org/pub/mozilla.org/xulrunner/releases/1.9.0.7/runtimes/'>XULRunner 1.9</a>, a released as a stable version of xulrunner and tested with oryx standalone.

# Making a Xul application #
First of all one has to create the following directory structure. All files in the chrome directory are adressable by <a href='https://developer.mozilla.org/En/Chrome_Registration'>chrome-uri's</a> and contains all Xul code.
<pre>
+ /Oryx-Editor<br>
|<br>
+-+ /chrome<br>
| |<br>
| +-+ /content<br>
| | |<br>
| | +- main.xul      // main window<br>
| | +- editor.xul    // editor window<br>
| | +- options.xul   // option window<br>
| | +- connection.js // proxy preference functions<br>
| | +- about.xul     // about window<br>
| | +- about.js      // externalLoad-function used in about.xul<br>
| |<br>
| +- chrome.manifest<br>
|<br>
+-+ /defaults<br>
| |<br>
| +-+ /preferences<br>
|   |<br>
|   +- prefs.js     // defines all default preference of xulrunner<br>
|<br>
|<br>
+-+ /xulrunner      // xulrunners dist directory, contains xulrunner.exe<br>
+- application.ini  // defines app name and min/max Gecko Version (1.8/2.0)<br>
</pre>

To run the application, just open a command line and execute "path/to/xulrunner.exe your/application.ini -jsconsole" (-jsconsole for getting a error console). There are severall warnings for methods of the ext-library, but this do not influence any functionalities Oryx is using.
To get an executable, just copy the xulrunner-stub.exe to the root-directory and rename it.
To modify the icon of the executable, use <a href='http://angusj.com/resourcehacker/'>Resource Hacker</a>, a tool for manupulating binaries.

**Problems solved:**
<ul>
<li>tooltip                                  --> Created a FillInHTMLTooltip js-function, which creates an rudimentary tool tip and registered it at the browser element.</li>
<li>proxy and defferent Oryx-Server          --> Added an option.xul with a Dialog window, which offer all necessary options (to get in the about:config window, only uncomment the button in the main.xul). Change the pref oryx.start.url to get an other Oryx url </li>
<li>Open the help-url in an external browser --> use of the @mozilla.org/uriloader/external-protocol-service component, see about.js</li>
<li>handle editor windows in an new window --> set default preference "browser.chromeURL" to the editor.xul, new window will be loaded in the browser type "content-primary"</li>
<li>word-wrapping                          --> is implemented in xulrunner 1.9.2pre</li>
<li>unsaved editor close                   --> catch the onclose-event of the xul window and use the  browser.contentWindow.close() to close the browser. If the browser close, the xul window will close, too.</li>
</ul>

# Making an Installer #

I used <a href='http://nsis.sourceforge.net/Main_Page'>NSIS</a> to get an setup for Oryx, because contrary to the Inno Setup tool, one can define a silent install, so the minmal installation get in the background. User can imediately start to work.

To allow non-admin user to install Oryx, one has to add some special prefs to the skript file. Modifications also bypass UAC requests of Windows Vista.
**Preferences:**
<ul>
<li> RequestExecutionLevel user --> setup starts with normal user right</li>
<li> Set installation directory to $LOCALAPPDATA --> has write access for current user, also used by Google Chrome</li>
<li>Write in the "HKCU" registration key --> defines the uninstaller and has write access for current user</li>
</ul>

# Download Oryx Standalone #
<a href='http://oryx-editor.googlecode.com/files/Oryx.exe'>Click here to download the Oryx Standalone Installer</a>