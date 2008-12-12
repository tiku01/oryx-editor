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
    
    

<div style="position:absolute; top:-2px; left:0px;">
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
<p> 
       <div class=blur> 
          <div class=shadow> 
            <div class=content> 
              <div class=text> 
                <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000"><b>Introduction</b></font></p>
                <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000">
                As part of the commitment to Interoperability,
                <a href="http://www.microsoft.com/interop/">Microsoft Corporation</a> and 
                <a href="http://www.fokus.fraunhofer.de/">Fraunhofer FOKUS</a>
                provide a  series of offerings that foster improved interoperability
                for online identity management. Microsoft has been a leading industry
                voice in the creation of an identity metasystem, an ecosystem designed
                to enable the exchange of personal identity information on the Internet
                so all parties may understand whom they are working with online. Three
                core elements make up the identity metasystem: the people who are
                presenting their identity, the Web site or online service requesting
                proof of identity, and the identity providers who assert some information
                about those people.
                <br><br>
                The purpose of JInfocard project is to improve interoperability for each
                of the three identity metasystem components and represent the next step
                in Microsoft's commitment to deliver interoperability by design. This open
                source project  that will help Java developers support information cards,
                the primary mechanism for representing user identities in the identity
                metasystem. The JInfoCard Framework implements software for specifying the
                Web site's security policy and accepting information cards in Java for
                a variety of Java-based web application servers.
                </font></p>
              </div>
            </div>
          </div>
        </div><br>
		  <div class=blur> 
          <div class=shadow> 
            <div class=content> 
              <div class=text> 
                <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000">
                  <b>JInfoCard Functionality</b>
                </font></p>
                <p><img src="images/main.jpg" width="800" height="417" border="1"></p>
              </div>
            </div>
          </div>
        </div><br>
		
        <div class=blur> 
          <div class=shadow> 
            <div class=content> 
              <div class=text> 
                <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000"><b>Animation</b></font><br>
                </p>        
                <div class=blur1> 
          <div class=shadow1> 
                    <div class=content1> 
                      <table width="700" border="0" height="64">
                        <tr>
                          <td width="105" height="123"><img src="images/flash.png" width="100" height="100"></td>
                          <td width="589" height="123"> 
                            <div class=text2> 
                              <p><font face="Arial, Helvetica, sans-serif" size="3" color="#000000">For 
                                a better understanding what happend in the background, 
                                we made a animation to show the individual steps. 
                                Please click on the button below to watch the 
                                animation. </font></p>
                              <p align="left"><a href="javascript:newwindow()"><img src="images/flash_ani.png" width="37" height="36" align="absmiddle" alt="JInfoCardAni" border="0"></a> 
                                <font face="Arial, Helvetica, sans-serif" size="3" color="#000000"><b> 
                                <a href="javascript:newwindow()">Jinformation 
                                Card Animation</a> </b></font></p>
                              </div>
                          </td>
                        </tr>
                      </table>
                     
                      
                    </div></div></div> <br>
					
              </div>
            </div>
          </div>
        </div>

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
      <td height="730x" background="images/left.gif">&nbsp;</td>
    </tr>
  </table>
</div>
</body>
</html>
