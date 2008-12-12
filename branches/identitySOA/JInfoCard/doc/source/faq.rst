****************************
 Frequently Asked Questions
****************************

We tried to cover most of the questions in the previous chapters.
However, we have collected some FAQ which might help you further.

1. Where are InformationCards used?

   InformationCards can be used where a registration about user name
   and password required to login, for example in communities, online
   banking and in modern eGovernment.

2. Which information are stored on InformationCard?

   Personal data are required by users. These data can be different and individually required by the administrator. 

3. Where is the personal data stored?

   All data about users are stored decentral on the client site. 

4. What kinds of InformationCard are available?

   There are two types available. On the one hand self issued card, on
   the other managed card.  Self issued cards are generated and
   managed by the user himself. A managed card is an InformationCard
   issued by an Identity Provider.

5. Is it possible to have more than one InformationCard?

   Yes, users can have severally identities and therewith several
   InformationCards.

6. How can web sites tell apart different users using Information Cards?

   They identify the user by the Private Personal Identifier
   (PPID). This is an ID that identifies a specific card for a certain
   :term:`relying party <Relying Party>`.

   In case of a self issued card, the user's identity selector
   calculates the PPID as a combination of the relying party
   certificate and something unique about the card.

   For managed cards, the identity selector will provide the
   :term:`identity provider <Identity Provider>` with a
   cryptographic seed which it can use to calculate the PPID.

7. What are certificates and for what are they used?

   Digital certificates are used to certify the identity of persons or
   computers.  The function similarly to identification cards such as
   passports and drivers’ licenses.  JInfoCards certificate is
   generated with installation. This happened with an ant script.

8. How to change the server.xml and why?

    Please see the section :ref:`ssl` for details.

9. How to set JAVA_HOME?

    The JAVA_HOME variable must point at the home directory of JDK
    installation.  On Microsoft Windows systems, it is can be modified
    throgh system properties -> advanced -> environment variables.

10. The JInfoCard log file contains the following error message::

      GRAVE: error setting up decryption.
      org.apache.xml.security.encryption.XMLEncryptionException: Illegal key size

    The JCE Unlimited Strength Jurisdiction Policy Files are not
    installed.  Please see the section :ref:`jce-policy-files` for
    details.
