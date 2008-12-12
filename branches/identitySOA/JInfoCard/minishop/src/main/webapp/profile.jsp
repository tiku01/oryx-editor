<html>
<head>
<title>Profile</title>
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
                   
                  <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000"><b>Profile</b></font></p>
                  <table class="text_body" border="0" cellspacing="0" cellpadding="2" width="417">
                    <tr> 
                      <td width="190" class="subHeader"><b>Your profile data</b> 
                      </td>
                      <td width="191" class="subHeader">&nbsp;</td>
                      <td width="263" class="subHeader"><b>Manage your InfoCard</b> 
                      </td>
                      <td width="14" class="subHeader">&nbsp;</td>
                    </tr>
                    <tr> 
                      <td colspan="4" class="bodyText"> 
                        <table width="653" border="0">
                          <tr> 
                            <td width="371" rowspan="2"> 
                              <form id="register" name="form1" method="post" action="profile.do.cs.jsp">
                                <input type="hidden" name="_action_" value="do.update"/>
                                <table width="370" border="1">
                                  <tr> 
                                    <td background="images/top.jpg">user id:</td>
                                    <td background="images/top.jpg">&nbsp;</td>
                                  </tr>
                                  <tr> 
                                    <td width="114">First name: </td>
                                    <td width="240"> 
                                      <div align="center"> 
                                        <input name="fname" type="text" id="fname" size="40"/>
                                      </div>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td>Last name: </td>
                                    <td> 
                                      <div align="center"> 
                                        <input name="lname" type="text" id="lname" size="40" />
                                      </div>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td>Email:</td>
                                    <td> 
                                      <div align="center"> 
                                        <input name="email" type="text" id="email" size="40"/>
                                      </div>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td>&nbsp;</td>
                                    <td>To change password, please enter below</td>
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
                                    <td>Password (repeat): </td>
                                    <td> 
                                      <div align="center"> 
                                        <input name="pswd2" type="password" id="pswd2" size="40" />
                                      </div>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td colspan="2"> 
                                      <div align="center"> 
                                        <input name="update" type="submit" id="Update" value="Update" onChange="alert('Your data was changed');"/>
                                      </div>
                                    </td>
                                  </tr>
                                </table>
                              </form>
                            </td>
                            <td width="290" height="286"> 
                              <form action="profile.do.cs.jsp" enctype="application/x-www-form-urlencoded" method="post">
                                <table width="290" border="1">
                                  <tr> 
                                    <td bgcolor="#CCCCCC" background="images/top.jpg">Attaching 
                                      Information Card </td>
                                  </tr>
                                  <tr> 
                                    <td> 
                                      <div align="center"><br>
                                        <input type="hidden" name="_action_2" value="do.link"/>
                                        
                                          
                                        <input name="send" value="CARDSPACE" type="image" src="images/under21.png" vspace="6px" hspace="20px" alt="Please choose an information card." width="126" height="89"/>
                                        <br>
                                      </div>
                                    </td>
                                  </tr>
                                </table>
                              </form>
                              <p>With click on Information Card icon, you can 
                                link your Info Card to your account.<br>
                                To detach an Information Card, please use the 
                                buttons below.<br>
                              </p>
                            </td>
                          </tr>
                          <tr> 
                            <td width="290" height="2">&nbsp;</td>
                          </tr>
                          <tr> 
                            <td colspan="2">&nbsp;</td>
                          </tr>
                        </table>
                       
                        <table border="1">
                          
                          <tr bgcolor="#CCCCCC" background="images/top.jpg"> 
                            <td colspan="5" align="center"><b>The list of the 
                              attached information cards.</b></td>
                          </tr>
                          <tr> 
                            <th>Card ID</th>
                            <th>first name</th>
                            <th>last name</th>
                            <th>email address</th>
                            <th>detach</th>
                          </tr>
                          <script language="javascript">
function ConfirmChoice()
{
answer = confirm("Do you really want to detach your Information Card?")
if (answer !=0)
{
return right;
}
}
	  
     </script>
                      
                            
                              <td> 
                                
                              <input type="image" src="images/InfoCard_small.png" value="detach it" name="do.delete.attachment.ppid" onClick=" ConfirmChoice(); return false;" width="40" height="28">
                                <input type="hidden" name="ppid" value=" "/>
                                <input type="hidden" name="_action_2" value="do.delete.attachment.ppid"/>
                              </td>
                            
                          </tr>
                       </table>
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
