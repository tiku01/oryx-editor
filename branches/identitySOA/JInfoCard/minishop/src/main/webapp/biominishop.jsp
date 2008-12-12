<html>
<head>
<title>Welcome</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta name="author" content="Fraunhofer Institute FOKUS">
<meta name="publisher" content="Fraunhofer Institute FOKUS">
<meta name="copyright" content="Fraunhofer Institute FOKUS">
<meta name="description" content="Fraunhofer Institute FOKUS
Information Card on java based application">
<meta name="keywords" content="Jinformation, Card, Information, Card, Fraunhofer, FOKUS, eIdentity, Identity, Info, Card, Cardspace">
<meta name="page-topic" content="Forschung Technik">
<meta http-equiv="content-language" content="en">
<meta name="robots" content="index, follow">
<link rel="stylesheet" type="text/css" href="style.css">
<link rel="icon" href="images/favicon.png" type="image/x-icon">
<!-- style for login buttons -->
<style type="text/css">
  a {
  text-decoration: none; color: blue;
  }
  a img {
  border: none;
  }
  button {
  background: transparent url('images/InfoCard_small.png') no-repeat;
  border: none;
  padding-left: 42px;
  height: 30px;
  font: 1.2em bold;
  font-family: Arial, Helvetica, sans-serif;
  display: inline;
  cursor: pointer;
  }
  form { display: inline; text-align: left; }
  object {
    width: 0px;
    height: 0px;
    display: none;
  }
  .noIC {
  background: #f93;
  border: 2px solid #f00;
  }
</style>
<script src="js/jquery.js" ></script>
<script src="js/infocard.js" ></script>
<script type="text/javascript">
  $(document).ready(function(){
    if(informationCardsSupported()) {
      $(".ic").show();
      $(".noIC").hide();
    } else {
      $(".ic").hide();
      $(".noIC").show("slow");
    }
  });
</script>
</head>
<script language="JavaScript">

function newwindow()
{ window.open ('JInfo_flash.jsp','Name','width=1024,height=768'); }

</script>
<body bgcolor="#ffffff" text="#000000">
    
    

<div style="position:absolute; top:-2px; left:0px;; width: 1016px; height: 917px"> 
  <table class="table" width="1024" height="880px" border="0" >
    <tr> 
      <td height="67" background="images/top.jpg" colspan="3"> 
        <div style="position:absolute; left:622;top:3px;; width: 373px; height: 67px"> 
		<img src="images/rightLogo.png" width="400" height="67"></div>

        <p>&nbsp;</p>
        <p><font face="Arial, Helvetica, sans-serif" size="2" color="#000000"><i><font face="Verdana, Arial, Helvetica, sans-serif" size="1"><b> 
          </b></font></i></font></p>
      </td>
    </tr>
    <tr> 
      <td  width="112px" background="images/left.gif" height="100"> 
        <div align="left"><img src="images/spacer.gif" width="16" height="1"><img src="images/fokus.gif" width="62" height="62" border="1"></div>
      </td>
      <td  bgcolor="#EFEFEF" rowspan="11" width="62">&nbsp;</td>
      <td  bgcolor="#EFEFEF" rowspan="11" width="834" >
       
        <div class=blur> 
          <div class=shadow> 
            <div class=content> 
              <div class=text> 
                <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000"><b>Menu</b></font></p>
               <div class=blur1> 
          <div class=shadow1> 
                    <div class=content1> 
                      <table width="700px" border="0" height="64">
                        <tr>
                          <td width="105" height="150"><img src="images/logo.png" width="80" height="98" hspace="5" vspace="2"></td>
                          <td width="587" height="150"> 
                            <div class=text2> 
                              <p>In order to use the login buttons below, please 
                                download and install either the <a href="https://www.jinfocard.org/minishop2/personal_card.crds">example 
                                Personal Card</a> (use &quot;password&quot; when 
                                asked for a password) or the <a href="https://www.jinfocard.org/minishop2/managed_card.crd">example 
                                Managed Card</a>. For the managed card, you will 
                                be asked for a user name and password. Please 
                                use &quot;test&quot; for the user name and &quot;password&quot; 
                                for the password. </p>
                              <p>Use the STS to issue SAML tokens which include 
                                information about your identity. This information, 
                                embedded in the SAML token, is then bound to XML 
                                messages. This allows an access control decision 
                                based on the end user's identity. You can choose 
                                between two offerer:</p>
                              <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000">
                                <a href="https://www.jinfocard.org:30443/WSTrustStsHostFactory/">
                                  <img src="images/msdn.gif" width="68" height="37" align="absmiddle" />
                                  <b>Zermatt</b>
                                </a>
                                <img src="images/spacer.gif" width="50" height="3" />
                                <a href="https://higgins.eclipse.org/TokenService/">
                                  <img src="images/higgins.png" width="28" height="37" align="absmiddle" />
                                  <b>Higgins</b>
                                </a>
                              </font></p>
                            </div>
                          </td>
                        </tr>
                      </table>
                    </div>
                  </div></div><br>
               <div class=blur1> 
          <div class=shadow1> 
                    <div class=content1> 
                      <table width="700" border="0" height="96">
                        <tr>
                          <td width="105" height="103"><img src="images/first.png" width="100" height="100"></td>
                          <td width="600" height="103"> 
                            <div class="text2">
			      To demonstrate the Managed Card 
                              age verification on the base of our <i>sample web application</i>, 
                              please select the age of the customer here. You 
                              can decide between under 21 years or over 21 years.
			      
			      <p class="noIC">
				<strong>Either your browser
				  does not support Information
				  Cards or JavaScript is disabled. Please download an Identity
				  Selector or enable JavaScript.</strong>
			      </p>
                              <p align="left"><font face="Arial, Helvetica, sans-serif" size="3" color="#000000">
				  <form action="shop.jsp"
                              		method="post" 
                              		enctype="application/x-www-form-urlencoded"
                              		name="SendInfocard">
                              	    <object type="application/x-informationCard"
					    name="xmltoken">
                                      <param name="tokenType"
					     value="http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0" />
                                      <param name="requiredClaims"
					     value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress
						    http://elan.fokus.fraunhofer.de/claim/offullage" />
                                      <param name="optionalClaims"
					     value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender" />
                                    </object>
				    <button type="submit" class="ic">over 21</button>
				  </form>
				  <form action="shop.jsp"
                              		method="post" 
                              		enctype="application/x-www-form-urlencoded"
                              		name="SendInfocard">
                              	    <object type="application/x-informationCard"
					    name="xmltoken">
                                      <param name="tokenType"
					     value="urn:oasis:names:tc:SAML:1.0:assertion" />
                                      <param name="requiredClaims"
					     value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress" />
                                      <param name="optionalClaims"
					     value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender" />
                                    </object>
				    <button type="submit" class="ic">under 21</button>
				  </form>
                                  
                              </font></div>
                          </td>
                        </tr>
                      </table>
                    </div></div></div>
                <br>
                    
                <br>
				      <div class=blur1> 
          <div class=shadow1> 
                    <div class=content1> 
                      <table width="700" border="0" height="64">
                        <tr>
                          <td width="105" height="100"><img src="images/selfIssued.png" width="100" height="100"></td>
                          <td width="588" height="100"> 
                            <div class=text2> 
                              <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000">To 
                                demonstrate the functionality of SSL and Non-SSL 
                                connection in combinations with Self Issued Card, 
                                please use selection below to change between the 
                                kinds of connection.</font></p>
                              <p align="left">
	                          <a href="https://www.jinfocard.org/minishop2/">
		                    <img src="images/ssl.png" width="38" height="36" align="absmiddle">
		                    <b>SSL</b>
	                          </a>
	                          <img src="images/spacer.gif" width="50" height="3">
	                          <a href="http://www.jinfocard.org/minishop2/">
	                            <img src="images/ssl_non.png" width="38" height="36" align="absmiddle"> 
	                            <b>Non-SSL</b>
				  </a>
			      </p>
                            </div>
                          </td>
                        </tr>
                      </table>
                    </div></div></div>
                <br>
									
              </div>
            </div>
          </div>
        </div>
<br><br><br><br><br><br><br><br>
        </td>
    </tr>
    <tr> 
      <td class="table2" height="7"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000"><b><font size="1"><img src="images/spacer.gif" width="3" height="3">Menu</font></b></font></td>
    </tr>
    <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='index.jsp'" background="images/left.gif">Home
    </tr>
	<tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='biominishop.jsp'" background="images/left.gif">Services
    </tr>
    <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='partner.jsp'"  background="images/left.gif">Constributors</td>
    </tr>
    <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='identitylab.jsp'" height="13"  >Developers</td>
    </tr>
    
    <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='contact.jsp'" >Contact 
        Us </td>
    </tr>
    <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='imprint.jsp'" height="2" >Imprint</td>
    </tr>
    
    <tr> 
      <td height="741px" background="images/left.gif">&nbsp;</td>
    </tr>
  </table>
</div>
</body>
</html>
