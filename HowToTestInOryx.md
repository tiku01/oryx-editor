# Selenium #
## Run tests ##
  1. Start selenium server from command line (take editor/lib/selenium-server.jar ):
```
java -jar selenium-server.jar -ensureCleanSession -trustAllSSLCertificates
```
  1. Run any test suite from editor/test/selenium/tests
  1. To modify browser configuration (default: Firefox), see editor/test/selenium/util/OryxSeleneseTestCase.java _(TODO: should be a settings file!)_

_TODO: Integrate with ant? http://seleniumhq.org/documentation/remote-control/languages/selenese.html_

## Write tests ##
  * Use `OryxSeleneseTestCase#proc` object if selenese helper methods aren't available for a specific selenium command, e.g.
```
String[] args = {"css=#username"};
proc.doCommand("waitForElementPresent", args);
```
_TODO: how to extend via user-extensions.js or ExtCommands_