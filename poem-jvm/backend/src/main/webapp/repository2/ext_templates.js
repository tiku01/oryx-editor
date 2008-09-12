
Ext.namespace('Repository');

Repository.Templates = {
		login :  function() { tpl = new Ext.XTemplate(		'<div id="oryx_repository_header" onmouseover="this.className = \'mouseover\'" onmouseout="this.className = \'\'">'+
					        '<a href="http://oryx-editor.org" target="_blank">'+
						    '<img src="/backend/images/style/oryx.small.gif" id="oryx_repository_logo" alt="ORYX Logo" title="ORYX"/>'+
					    '</a>'+
					
						'<tpl if="isPublicUser">'+
							'<form action="/backend/poem/login" method="post" id="openid_login">'+
								'<div>'+
									'<span>'+
										'<img src="/backend/images/repository/hpi.png" onclick="Repository.render.openid_tpl.changeOpenId(\'https://openid.hpi.uni-potsdam.de/user/username\'+ 39, 8)"/>'+
										'<img src="/backend/images/repository/blogger.png" onclick="Repository.render.openid_tpl.changeOpenId(\'http://username.blogspot.com/\'+ 7, 8)"/>'+
										'<img src="/backend/images/repository/getopenid.png" onclick="Repository.render.openid_tpl.changeOpenId(\'http://getopenid.com/username\'+ 21, 8)"/>'+
									'</span>'+
									'<input type="text" name="openid_identifier" id="openid_login_openid" class="text gray" value="your.openid.net" onblur="if(this.value.replace(/^\s+/, \'\').replace(/\s+$/, \'\').length==0) {this.value=\'your.openid.net\'; this.className+=\' gray\';}" onfocus="this.className = this.className.replace(/ gray/ig, \'\'); if(this.value==\'your.openid.net\') this.value=\'\';" />'+
									'<input type="submit" class="button" value="login"/>'+
								'</div>'+
							'</form>'+
						'</tpl>'+
						
						'<tpl if="!isPublicUser">'+
							'<form action="/backend/logout.jsp" method="post" id="openid_login">'+
								'<div>'+
									'Hi, {currentUser}'+
									'<input type="submit" class="button" value="logout"/>'+
								'</div>'+
							'</form>'+
					
						'</tpl>'+
					
						'<div style="clear: both;"></div>'+
					'</div>', 
					{						
						changeOpenId: function(url, start, size){
							var o = document.getElementById('openid_login_openid');
							o.value = url;
							o.focus();
							
							if (window.ActiveXObject) {
								try {
									var tr = o.createTextRange();
									tr.collapse(true);
									tr.moveStart('character', start);
									tr.moveEnd('character', size);
									tr.select();
								} 
								catch (e) {
								}
							}
							else {
								o.setSelectionRange(start, start + size);
							}
						}
					});
		  //tpl.compile();
		  return tpl;
		}, // end of login
		login2 : function () {
			tpl = new Ext.XTemplate('<div id="oryx_repository_header"> <tpl if="isPublicUser"> PUBLIC </tpl><tpl if="!isPublicUser">  NOT PUBLIC </tpl></div>');
			//tpl.compile;
			return tpl;
		}
}