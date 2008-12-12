<%@page isErrorPage="true" %>
<html>
<head>
<title>Help</title>
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
        <div align="left"><img src="images/spacer.gif" width="16" height="3"><img src="images/Partner.gif" width="72" height="51" border="1"></div>
      </td>
      <td  bgcolor="#EFEFEF" rowspan="11" width="62">&nbsp;</td>
      <td  bgcolor="#EFEFEF" rowspan="11" width="834"> 
        <p align="left">&nbsp;</p>
        <p><div style="position:absolute; top:109px; left:186px; width:834px;">
        <div class=blur>
          <div class=shadow>
            <div class=content>
                <div class=text> <font face="Arial, Helvetica, sans-serif" color="#000000" size="3"> 
                  </font> 
                  <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000"><b> 
                    ERROR <%= request.getAttribute("ERR_CODE") %></b></font></p>
                  <p><span class="Stil21"><font face="Arial, Helvetica, sans-serif" size="3" color="#000000"><b>An 
                    error has occured: <%= request.getAttribute("error") %></b></font></span></p>
                  <p><span class="Stil21"><font face="Arial, Helvetica, sans-serif" size="3" color="#000000"><b><img src="images/404.png" width="159" height="151"></b></font></span></p>
                  <p><font face="Arial, Helvetica, sans-serif" size="3">.</font></p>
                     
                  <p align="right">&nbsp;</p>
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
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'" onClick="location.href='partner.jsp'" width="111" background="images/left.gif" height="18">Developers</td>
    </tr>
        <tr> 
      <td class="off1" onmouseover="this.className='on1'" onmouseout="this.className='off1'"width="111" onClick="location.href='contact.jsp'" background="images/left.gif" height="18">Contact 
        Us</td>
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
