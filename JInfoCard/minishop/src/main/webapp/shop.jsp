<jsp:useBean id="uris" class="de.fraunhofer.fokus.jic.identity.ClaimUris" scope="page"></jsp:useBean>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<title>Welcome</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="style.css">
<link rel="icon" href="images/favicon.png" type="image/x-icon">
</head>

<body bgcolor="#ffffff" text="#000000">
<div style="position:absolute; top:-2px; left:0px;; width: 1259px; height: 753px"> 
  <table class="table" width="1024px" border="0" height="857">
    <tr> 
      <td height="67" background="images/top.jpg" colspan="3"> 
        <div class="subtext" style="width: 955px; height: 16px">JInfoCard</div>
		<div style="position:absolute; left:622;top:3px;; width: 373px; height: 67px"> 
		<img src="images/rightLogo.png" width="400" height="67"></div>
        <p>&nbsp;</p>
        <p><font face="Arial, Helvetica, sans-serif" size="2" color="#000000"><i><font face="Verdana, Arial, Helvetica, sans-serif" size="1"><b> 
          </b></font></i></font></p>
      </td>
    </tr>
    <tr> 
      <td  width="111" background="images/left.gif" height="100"> 
        <div align="left"><img src="images/spacer.gif" width="16" height="1"><img src="images/fokus.gif" width="62" height="62" border="1"></div>
      </td>
      <td  bgcolor="#EFEFEF" rowspan="11" width="62">&nbsp;</td>
      <td  bgcolor="#EFEFEF" rowspan="11" width="834"> 
        <p align="left">&nbsp;</p>
        <p> 
        <div class=blur> 
          <div class=shadow> 
            <div class=content> 
              <div class=text> 
    <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000"><b>Welcome, ${userdata.firstName[0]} ${userdata.lastName[0]}</b></font></p>
                <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000">
                You have successfully used an Information Card to login and authenticate yourself.
                </font></p>
                  <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000">You submitted the following data:</font></p>
                  <font face="Arial, Helvetica, sans-serif" size="3" color="#000000">
                  <ul>
                  <li>First Name: ${userdata.firstName[0]}</li>
                  <li>Last Name: ${userdata.lastName[0]}</li>
                  <!-- <li>Email: ${userdata.email[0]}</li> -->
                  <li>Email: ${userdata[uris.emailAddress][0] }</li>
                  <li>PPID: ${userdata["http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier"][0]}</li>
                  <li>Friendly ID: ${friendlyid}</li>
                  <li><b>Of full age: ${offullage}</b></li>
                  </ul>
                  </font>
              </div>
            </div>
          </div>
        </div>
		<br>
        <div class=blur> 
          <div class=shadow> 
            <div class=content> 
              <div class=text> 
                <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000">
                  <b>You're logged in</b>
                </font></p>
                
                <p><c:if test="${offullage}"></c:if></p>
                <p align="left"><img src="images/logo.jpg" width="255" height="72" border="1"></p>
                <p>&nbsp;</p>
                <p>&nbsp;</p>
                <p>&nbsp;</p>
                <p><br>
                </p>
              </div>
            </div>
          </div>
        </div>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
        <p>&nbsp;</p>
      </td>
    </tr>
    <tr> 
      <td class="table2" width="111" height="7"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000"><b><font size="1"><img src="images/spacer.gif" width="3" height="3">Menu</font></b></font></td>
    </tr>
    <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='index.jsp'" background="images/left.gif">Home</td>
    </tr>
    <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='biominishop.jsp'" background="images/left.gif">Services</td>
    </tr>
    <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='partner.jsp'"  background="images/left.gif">Constributors</td>
    </tr>
    <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='identitylab.jsp'" height="13">Developers</td>
    </tr>
    <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='contact.jsp'" >Contact Us</td>
    </tr>
    <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='imprint.jsp'" height="2" >Imprint</td>
    </tr>
    <tr> 
      <td height="740" background="images/left.gif">&nbsp;</td>
    </tr>
  </table>
</div>
</body>
</html>
