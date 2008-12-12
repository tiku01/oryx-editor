<%@ page contentType="text/html; charset=iso-8859-1" language="java" 
	import="java.sql.*, de.fraunhofer.fokus.jic.bioshop.*, de.fraunhofer.fokus.jic.rp.*" errorPage="error.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- DW6 -->
<head>
<!-- Copyright 2005 Macromedia, Inc. All rights reserved. -->
<title>Home Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" href="images/mm_health_nutr.css" type="text/css" />
<script language="JavaScript" type="text/javascript">
//--------------- LOCALIZEABLE GLOBALS ---------------
var d=new Date();
var monthname=new Array("January","February","March","April","May","June","July","August","September","October","November","December");
//Ensure correct for language. English is "January 1, 2004"
var TODAY = monthname[d.getMonth()] + " " + d.getDate() + ", " + d.getFullYear();
//---------------   END LOCALIZEABLE   ---------------
</script>
<%
	String status = (String)session.getAttribute("USER_STATUS");
	boolean isLoggedIn;
	if (status == null || status.length() == 0) {
		session.setAttribute("USER_STATUS","LOGGED_OUT");
		isLoggedIn = false;
	} else if (status.equals("LOGGED_IN")) {
		isLoggedIn = true;
	} else {
		isLoggedIn = false;
	}
%>

<style type="text/css">
<!--
body {
	background-color: #FFFFFF;
}
.Stil9 {
	color: #FFFFFF;
	font-size: 16px;
	font-weight: bold;
	font-family: Verdana, Arial, Helvetica, sans-serif;
}
a:hover {
	color: #B04302;
}
a:visited {
	color: #FFFFFF;
}
a:active {
	color: #FFFFFF;
}
.Stil16 {
	font-size: 12px
}
.Stil18 {
	font-size: 10px;
	color: #000000;
	font-family: Arial, Helvetica, sans-serif;
}
.Stil20 {color: #000000}
-->
</style>
<%
	String uid = (String)session.getAttribute("USER_ID");
	System.out.println("got uid from session: " + uid);
	IBioMiniShopUserManager mgr = BioMiniShopUserManager.getInstance();
	BioMiniShopUserData _data = mgr.getUserData(uid);
	if (_data == null) {
		session.setAttribute("ERR_CODE", "401");
		session.setAttribute("ERR_TEXT", "user doesn't exists");
		session.setAttribute("ERR_LINK_TITLE", "home");
		session.setAttribute("ERR_LINK","home.jsp");
		response.sendRedirect("error.jsp");
	}
%>
</head>
<body>
<table width="75%" border="0" cellspacing="0" cellpadding="0">
  


  <tr bgcolor="#99CC66">
    <td height="90" colspan="6" bgcolor="#ffffff" id="dateformat2"><img src="images/top.jpg" alt="MiniShop" width="940" height="100" /></td>
  </tr>
  <tr bgcolor="#99CC66">
  	<td height="20" colspan="4" bgcolor="#337900" class="navText" id="dateformat">&nbsp;&nbsp;
    <script language="JavaScript" type="text/javascript">
      document.write(TODAY);	</script>	</td>
<td width="261" bgcolor="#337900">
<% if (isLoggedIn && session.getAttribute("cardid") != null) { %>
		<span class="Stil9">Logged in [ <%= session.getAttribute("USER_ID") %> ] with this Card ID [ <%= session.getAttribute("cardid") %> ]
		<% } else if (isLoggedIn) { %>
		</span><span class="Stil9">Logged in [ <%= session.getAttribute("USER_ID") %> ]</span>
    <% } else { %>
		<span class="Stil9">Logged out (!!!)</span>
	<% } %>	 </td>
  <td width="63" bgcolor="#FFFFFF"><img src="images/mm_spacer.gif" width="23" height="1" /></td>
  </tr>
  

 <tr>
    <td width="165" rowspan="2" valign="top" bgcolor="#337900">
	<table border="0" cellspacing="0" cellpadding="0" width="165" id="navigation">
        <tr>
          <td bordercolor="#337900"><img src="images/mm_spacerII.gif" width="24" height="46" /></td>
        </tr>
        <tr>
          <td><span class="Stil18">Menu</span></td>
        </tr>
        <tr>
          <td><a href="home.jsp" class="navText">Home</a></td>
        </tr>
        <tr>
          <td width="165"><a href="about.jsp" class="navText">About</a></td>
        </tr>
        <tr>
          <td width="165"><a href="constributors.jsp" class="navText">Contributors</a></td>
        </tr>
        <tr>
          <td><a href="docu.jsp" class="navText">Documentation</a></td>
        </tr>
        <tr>
          <td><a href="download.jsp" class="navText">Download</a></td>
        </tr>
        
        <tr>
          <td><span class="Stil18">Sample Shop</span></td>
        </tr>
        
		<% if (isLoggedIn) { %>
		<tr>
          <td width="165"><a href="profile_cs.jsp" class="navText">My Profile</a></td>
        </tr>
		<% } %>
        <tr>
          <td width="165">
		  	<% if (isLoggedIn) { %>
				<a href="logout.do.jsp" class="navText">Logout</a>
			    <% } else { %>
		  		<a href="login_cs.jsp" class="navText">Login</a>
			    <% }%>		  </td>
        </tr>
		
		<tr>
		  <td class="Stil18">Menu</td>
	    </tr>
		<tr>
		  <td><a href="help.jsp" class="navText"> Help</a></td>
	    </tr>
		<tr>
		  <td><a href="links.jsp" class="navText"> Links</a></td>
	    </tr>
		<tr>
		  <td><a href="contact.jsp" class="navText">Contact</a></td>
	    </tr>
      </table>
 	 
 	<p><br />
 	  &nbsp;<br />
 	  &nbsp;<br />
&nbsp;    </p>
 	<p align="left">&nbsp;</p>
 	<p>&nbsp;</p>
 	<p>&nbsp;</p>
 	<p>&nbsp;</p>
 	<p>&nbsp;</p>
 	<p>&nbsp;</p></td>
    <td width="10" rowspan="2" valign="top" bgcolor="#C0FF97"><img src="images/mm_spacer.gif" width="10" height="1" /></td>
    <td height="19" colspan="3" bgcolor="#C0FF97"><div align="left"><span class="subHeader"><span class="smallText"><img src="images/mm_spacerII.gif" width="22" height="1" />Demonstrating InformationCard interoperability on heterogeneous platforms using Java</span></span></div></td>
    <td height="19" bgcolor="#FFFFFF">&nbsp;</td>
 </tr>
 <tr>
   <td width="61"><img src="images/mm_spacer.gif" width="1" height="1" /><img src="images/mm_spacer.gif" width="60" height="1" /></td>
   <td colspan="2" valign="top"><table border="0" cellspacing="0" cellpadding="0" width="638">
     
     <tr>
       <td width="638" class="bodyText"><p align="justify">&nbsp;</p>
           <table border="0" cellspacing="0" cellpadding="0" width="658">
             <tr>
                <td width="658" class="subHeader"><span class="subHeader">Your 
                  account details</span></td>
             </tr>
             <tr>
                <td class="bodyText"> 
                  <table border="0" cellspacing="0" cellpadding="5" width="611">
                    <tr> 
                      <td width="607" class="pageName" height="25">&nbsp;</td>
                    </tr>
                    <tr> 
                      <td class="bodyText"> 
                        <table width="652" border="0" cellpadding="0">
                          <tr> 
                            <td colspan="2"> 
                              <form id="register" name="form1" method="post" action="profile.do.cs.jsp">
                                <input type="hidden" name="_action_" value="do.update"/>
                                <table width="400" border="0" cellpadding="5">
                                  <tr> 
                                    <td bgcolor="#336600"><font color="#FFFFFF">User 
                                      id:</font></td>
                                    <td bgcolor="#336600"><font color="#FFFFFF"><%= uid %></font></td>
                                  </tr>
                                  <tr bgcolor="#CCFF99"> 
                                    <td width="114">First name: </td>
                                    <td width="270"> 
                                      <div align="center"> 
                                        <input name="fname" type="text" id="fname" size="40" value="<%= _data.getFirstName() %>"/>
                                      </div>
                                    </td>
                                  </tr>
                                  <tr bgcolor="#CCFF99"> 
                                    <td>Last name: </td>
                                    <td> 
                                      <div align="center"> 
                                        <input name="lname" type="text" id="lname" size="40" value="<%= _data.getLastName() %>" />
                                      </div>
                                    </td>
                                  </tr>
                                  <tr bgcolor="#CCFF99"> 
                                    <td>Email:</td>
                                    <td> 
                                      <div align="center"> 
                                        <input name="email" type="text" id="email" size="40" value="<%= _data.getEmailAddress() %>"/>
                                      </div>
                                    </td>
                                  </tr>
                                  <tr bgcolor="#CCFF99"> 
                                    <td>Password:</td>
                                    <td> 
                                      <div align="center"> 
                                        <input name="pswd" type="password" id="pswd" size="40" />
                                      </div>
                                    </td>
                                  </tr>
                                  <tr bgcolor="#CCFF99"> 
                                    <td>Password (repeat): </td>
                                    <td> 
                                      <div align="center"> 
                                        <input name="pswd2" type="password" id="pswd2" size="40" />
                                      </div>
                                    </td>
                                  </tr>
                                  <tr bgcolor="#CCFF99"> 
                                    <td colspan="2"> 
                                      <div align="center"> 
                                        <input name="update" type="submit" id="Update" value="Update" onclick="alert('Your data will be changed');" />
                                      </div>
                                    </td>
                                  </tr>
                                </table>
                              </form>
                            </td>
                          </tr>
                          <tr> 
                            <td colspan="2">&nbsp;</td>
                          </tr>
                          <tr>
                            <td colspan="2">The activation key was sent to you 
                              via email. Please enter below.</td>
                          </tr>
                          <tr> 
                            <td colspan="2">&nbsp; </td>
                          </tr>
                          <tr> 
                            <td bgcolor="#336600" width="400" cellpadding="5"> 
                              <font color="#FFFFFF"><img src="images/mm_spacer.gif" width="5" height="1">Please 
                              insert the activation key:</font></td>
                            <td width="246">&nbsp;</td>
                          </tr>
                          <tr> 
                            <td bgcolor="#CCFF99" width="400" cellpadding="5" height="47"><br>
                              <form action="useractivation.do.jsp" method="post">
                                <img src="images/mm_spacer.gif" width="2" height="1"> 
                                <input name="do.update.key" type="text" id="textfield" value="activation key" />
                                <label> 
                                <input type="submit" name="13" id="13" value="Ok" />
                                <br>
                                </label> 
                              </form>
                            </td>
                            <td width="246" height="47">&nbsp;</td>
                          </tr>
                        </table>
                        <p>&nbsp;</p>
                      </td>
                    </tr>
                  </table>
                  <p>&nbsp;</p>
                 <p class="Stil20">&nbsp;</p>
                <p class="Stil20">&nbsp;</p>
                <p class="Stil20">&nbsp;</p>
                <p class="Stil20">&nbsp;</p>
                <p class="Stil20">&nbsp;</p>
                <p class="Stil20">&nbsp;</p>
                <p class="Stil20">&nbsp;</p>
                <p class="Stil20">&nbsp;</p></td>
             </tr>
           </table>           
           <p align="justify">&nbsp;</p>
           <p align="justify"><br />
            </p></td>
     </tr>
   </table>   
     <img src="images/mm_spacer.gif" width="684" height="1" />
     <p class="subHeader"><br />
   </p>     </td>
  </tr>
 
  <tr>
    <td colspan="2">&nbsp;</td>
    <td width="61">&nbsp;</td>
    <td width="423">&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
  	<td colspan="6"><hr/></td>
  </tr>
  <tr>
  	<td colspan="6" align="center">&nbsp;</td>
  </tr>
</table>
</body>
</html>
