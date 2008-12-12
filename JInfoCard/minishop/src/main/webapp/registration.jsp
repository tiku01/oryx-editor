<html>
<head>
<title>Register</title>
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

<body bgcolor="#ffffff" text="#000000">
<div style="position:absolute; top:-2px; left:0px;">
  <table class="table" width="1024px" border="0" height="857">
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
      <td  width="111" background="images/left.gif" height="100"> 
        <div align="left"><img src="images/spacer.gif" width="16" height="3"><img src="images/contact.gif" width="62" height="62" border="1"></div>
      </td>
      <td  bgcolor="#EFEFEF" rowspan="11" width="62">&nbsp;</td>
      <td  bgcolor="#EFEFEF" rowspan="11" width="834"> 
        <p align="left">&nbsp;</p>
        <p><div style="position:absolute; top:109px; left:186px; width:834px;">
        <div class=blur>
          <div class=shadow>
            <div class=content>
                <div class=text> 
                  <p> 
                   
                  <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000"><b>Registration</b></font></p>
                  <table class="text_body" border="0" cellspacing="0" cellpadding="2" width="650">
                    <tr> 
                      <td class="pageName">&nbsp;</td>
                    </tr>
                    <tr> 
                      <td class="subHeader">Please provide registration data</td>
                    </tr>
                    <tr> 
                      <td class="bodyText">
                        <table border="0">
                          <tr> 
                            <td width="299">
                              <form action="register.do.jsp" method="post">
                                <input type="hidden" name="_action_" value="uid.register"/>
                                <table width="299" border="1">
                                  <tr> 
                                    <td colspan="2" background="images/top.jpg"> 
                                      <div align="center"><strong>by the classical 
                                        way</strong> </div>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="114">First 
                                      name: </td>
                                    <td width="270"> 
                                      <div align="center"> 
                                        <input name="fname" type="text" id="fname" size="40" />
                                      </div>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td>Last 
                                      name:</td>
                                    <td> 
                                      <div align="center"> 
                                        <input name="lname" type="text" id="lname" size="40" />
                                      </div>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td>User 
                                      id: </td>
                                    <td> 
                                      <div align="center"> 
                                        <input name="uid" type="text" id="uid" size="40" />
                                      </div>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td>Email:</td>
                                    <td> 
                                      <div align="center"> 
                                        <input name="email" type="text" id="email" size="40" />
                                      </div>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td>Password:</td>
                                    <td> 
                                      <div align="center"> 
                                        <input name="pswd" type="password" id="pswd" size="40" />
                                      </div>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td>Password 
                                      (repeat): </td>
                                    <td> 
                                      <div align="center"> 
                                        <input name="pswd2" type="password" id="pswd2" size="40" />
                                      </div>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <th colspan="2" scope="row"> 
                                      <input type="reset" name="input" value="Reset" />
                                      &nbsp; 
                                      <input type="submit" name="Register" value="Register" />
                                    </th>
                                  </tr>
                                </table>
                              </form>
                            </td>
                            <td width="100" align="center" valign="middle">
                              <h1>OR</h1>
                            </td>
                            <td width="250" align="center" valign="middle">
                              <form action="register.do.jsp" method="post" enctype='application/x-www-form-urlencoded' name="SendInfocard" id="SendInfocard">
                                <table border="1">
                                  <tr> 
                                    <td background="images/top.jpg"> 
                                      <div align="center"><strong>using information 
                                        card</strong> </div>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td>
                                      <div align="center"> 
                                      <!-- 

                                        <input type="hidden" name="_action_2" value="card.register"/>
                                        
                                        <input name="send" type="image" value="CARDSPACE" src="images/under21.png" vspace="25px" hspace="25px"alt="Please choose an information card in order to login." align="absmiddle" width="126" height="89" />
                                         -->
                              <form action="register.do.jsp"
                              		method="post" 
                              		enctype="application/x-www-form-urlencoded"
                              		name="SendInfocard">
                              		User ID: <input name="uid" type="text" id="uid" size="30" /><br>
                              		
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
						    http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender
						    http://elan.fokus.fraunhofer.de/claim/offullage/" />
                                    </object>
				    <button type="submit" class="ic">over 21</button>
				  </form>

                                      </div>
                                    </td>
                                  </tr>
                                </table>
                              </form>
                            </td>
                          </tr>
                        </table>
                        <div class="text_body">In order to use an information card please 
                          download and install into your system certificate store 
                          following <a href="certs/relay.cer">server certificate</a>.</div>
                      </td>
                    </tr>
                  </table>
                  <p><b></b></p>
                  <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000"><b><img src="images/spacer.gif" width="7" height="3"></b></font></p>
                </div>
            </div>
          </div>
        </div>
		</div>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        </td>
    </tr>
    <tr> 
      <td class="table2" width="112" height="7"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000"><b><font size="1"><img src="images/spacer.gif" width="3" height="3">Menu</font></b></font></td>
    </tr>
    <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='index.jsp'" width="111" background="images/left.gif" height="18">Home
    </tr>
		<tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='biominishop.jsp'" background="images/left.gif">Services
    </tr>
        <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='partner.jsp'" width="111" background="images/left.gif" height="18">Constributors</td>
    </tr>
    <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='identitylab.jsp'" width="111" background="images/left.gif" height="18">Developers</td>
    </tr>
        <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'"width="111" onClick="location.href='contact.jsp'" background="images/left.gif" height="18">Contact 
        Us </td>
    </tr>
    <tr>
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'"width="111" onClick="location.href='imprint.jsp'" background="images/left.gif" height="18">Imprint</td>
    </tr>
        <tr> 
      <td height="641" background="images/left.gif">&nbsp;</td>
    </tr>
  </table>
</div>
</body>
</html>
