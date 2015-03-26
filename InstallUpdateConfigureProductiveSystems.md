## Deploy your System ##

see SetupDevelopmentEnvironment

## Configure your System ##

Some features of the system need configuration to run properly.

### Feedback Plugin ###
In order to use the [feedback](http://code.google.com/p/oryx-editor/source/browse/trunk/editor/client/scripts/Plugins/feedback.js) [plugin](http://code.google.com/p/oryx-editor/source/browse/trunk/editor/#editor/server/src/org/oryxeditor/mail), you need to set up SMTP credentials in the web.xml filesof the projects that use the feedback plugin, i.e. http://code.google.com/p/oryx-editor/source/browse/trunk/editor/etc/web.xml

```

    <context-param>
        <description>The SMTP server to connect to</description>
        <param-name>SMTP_HOST_NAME</param-name>
        <param-value>smtp.gmail.com</param-value>
    </context-param>

    <context-param>
        <description>The default from email address</description>
        <param-name>SMTP_EMAIL</param-name>
        <param-value>mymail@googlemail.com</param-value>
    </context-param>

    <context-param>
        <description>Default user name for SMTP</description>
        <param-name>SMTP_AUTH_USER</param-name>
        <param-value>mymail@googlemail.com</param-value>
    </context-param>

    <context-param>
        <description>Default password for SMTP</description>
        <param-name>SMTP_AUTH_PWD</param-name>
        <param-value>mypassword</param-value>
    </context-param>

    <context-param>
        <description>Default port for SMTP</description>
        <param-name>SMTP_PORT</param-name>
        <param-value>465</param-value>
    </context-param>

    <context-param>
        <description>Recipient for feedback servlet</description>
        <param-name>FEEDBACK_RECIPIENT_EMAIL</param-name>
        <param-value>mymail@googlemail.com</param-value>
    </context-param>

```

## Visitor Analytics ##

You can easily include some analytics code, such as [Google Analytics](http://www.google.com/analytics/). Simply add a 'context-param' to the web.xml, i.e.  plugin, you need to set up SMTP credentials in the web.xml files of the projects, i.e. http://code.google.com/p/oryx-editor/source/browse/trunk/editor/etc/web.xml and http://code.google.com/p/oryx-editor/source/browse/trunk/poem-jvm/etc/web.xml, that contains the complete snippet code, including HTML-script tags:

```
    <context-param>
        <description>Analytics Snippet</description>
        <param-name>ANALYTICS_SNIPPET</param-name>
        <param-value><![CDATA[
            <script type="text/javascript" src="http://www.google-analytics.com/ga.js">
            </script>
            <script type="text/javascript">
              try {
                var pageTracker = _gat._getTracker("<your google analytics key>");
                pageTracker._trackPageview();
              } catch(err) {}
            </script>	
   	]]></param-value>
    </context-param>
```

Please have in mind: 'document.write()' is not allowed in XHTML and [won't work](http://www.google.com/support/forum/p/Google+Analytics/thread?tid=1470b32bbadf2242&hl=en) in Firefox.

_We are currently updating and reviewing the documentation. If you have problems, please contact us using the official [mailing list](http://groups.google.com/group/b3mn)._