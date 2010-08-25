
if(!Repository) var Repository = {};
 
Repository.Templates = {
		login :  new Ext.XTemplate(		'<div id="oryx_repository_header" onmouseover="this.className = \'mouseover\'" onmouseout="this.className = \'\'">'+
					        '<a href="http://oryx-project.org" target="_blank">'+
						    '<img src="/backend/images/style/oryx.small.gif" id="oryx_repository_logo" alt="ORYX Logo" title="ORYX"/>'+
					    '</a>'+
					
						'<tpl if="isPublicUser">'+
							'<form action="login" method="post" id="openid_login">'+
								'<div>'+
//									'<span>'+
//										'<img src="/backend/images/repository/hpi.png" onclick="Repository.Templates.login.changeOpenId(\'https://openid.hpi.uni-potsdam.de/user/username\' , 39, 8)"/>'+
//										'<img src="/backend/images/repository/blogger.png" onclick="Repository.Templates.login.changeOpenId(\'http://username.blogspot.com/\' , 7, 8)"/>'+
//										'<img src="/backend/images/repository/getopenid.png" onclick="Repository.Templates.login.changeOpenId(\'http://getopenid.com/username\', 21, 8)"/>'+
//									'</span>'+
//									'<input type="text" name="openid_identifier" id="openid_login_openid" class="text gray" value={Repository.I18N.Repository.openIdSample} onblur="if(this.value.replace(/^\s+/, \'\').replace(/\s+$/, \'\').length==0) {this.value=Repository.I18N.Repository.openIdSample; this.className+=\' gray\';}" onfocus="this.className = this.className.replace(/ gray/ig, \'\'); if(this.value==Repository.I18N.Repository.openIdSample) this.value=\'\';" />'+
									'<input type="button" name="btn" class="button"  onClick="Repository.Templates.login.loginWindow()" value={Repository.I18N.Repository.login} />'+
								'</div>'+
							'</form>'+
						'</tpl>'+
						
						'<tpl if="!isPublicUser">'+
							'<form action="login?logout=true" method="post" id="openid_login">'+
								'<div>'+
									'<div style="display:inline;" class="login_name">{Repository.I18N.Repository.sayHello}, {currentUser}</div>'+
									'<input type="submit" class="button" value={Repository.I18N.Repository.logout} />'+
								'</div>'+
							'</form>'+
					
						'</tpl>'+
						'<tpl if="!isPublicUser">'+
							'<div>'+
								'<div style="display:inline;" class="login_name">{Repository.I18N.Repository.sayHello}, {currentUser}</div>'+
								'<fb:like href="'+
								'http://oryx-project.org/research'+
								'" layout="button_count" font="lucida grande"></fb:like>'+
							'</div>'+
					'</tpl>'+
						'<div style="clear: both;"></div>'+
					'</div>', 
					{	loginWindow: function(){
						 var win = new Ext.Window({
					         width:500
					        ,id:'autoload-win'
					        ,height:300
					        ,autoScroll:true
						 	,html: '<form action="login" method="post" id="openid_login">'+
								'<div>'+
								'<div style="width:80%;	margin:0px auto;">'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/hpiLarge.png" onclick="Repository.Templates.login.changeOpenId(\'https://openid.hpi.uni-potsdam.de/user/username\' , 39, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/bloggerLarge.png" onclick="Repository.Templates.login.changeOpenId(\'http://blogname.blogspot.com/\' , 7, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/getopenidLarge.png" onclick="Repository.Templates.login.changeOpenId(\'http://getopenid.com/username\', 21, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/myspace.png" onclick="Repository.Templates.login.changeOpenId(\'http://www.myspace.com/username\', 23, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/google.png" onclick="Repository.Templates.login.changeOpenId(\'http://www.google.com/profiles/username\', 31, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/wordpress.png" onclick="Repository.Templates.login.changeOpenId(\'http://username.wordpress.com\', 7, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/livejournal.png" onclick="Repository.Templates.login.changeOpenId(\'http://username.livejournal.com\', 7, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/orange.png" onclick="Repository.Templates.login.changeOpenId(\'http://openid.orange.fr/username\', 24, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/claimid.png" onclick="Repository.Templates.login.changeOpenId(\'http://claimid.com/username\', 19, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/aol.png" onclick="Repository.Templates.login.changeOpenId(\'http://openid.aol.com/username\', 22, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/flickr.png" onclick="Repository.Templates.login.changeOpenId(\'http://www.flickr.com\', 21, 0)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/hyves.png" onclick="Repository.Templates.login.changeOpenId(\'http://hyves.nl\', 15, 0)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/myidnet.png" onclick="Repository.Templates.login.changeOpenId(\'http://username.myid.net\', 7, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/verisign.png" onclick="Repository.Templates.login.changeOpenId(\'http://username.pip.verisignlabs.com/\', 7, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/yahoo.png" onclick="Repository.Templates.login.changeOpenId(\'http://yahoo.com\', 16, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/yiid.png" onclick="Repository.Templates.login.changeOpenId(\'http://username.yiid.com\', 7, 8)"/>'+
									'<img style="float:left;height:34px;width:120px" src="/backend/images/repository/myopenidLarge.png" onclick="Repository.Templates.login.changeOpenId(\'http://username.myopenid.com/\', 7, 8)"/>'+
								'</div>'+
								'<div>'+
									'<input style="width:80%;	margin:0px auto;" type="text" name="openid_identifier" id="openid_login_openid" class="text gray" value="'+Repository.I18N.Repository.openIdSample+'" onblur="if(this.value.replace(/^\s+/, \'\').replace(/\s+$/, \'\').length==0) {this.value=Repository.I18N.Repository.openIdSample; this.className+=\' gray\';}" onfocus="this.className = this.className.replace(/ gray/ig, \'\'); if(this.value==Repository.I18N.Repository.openIdSample) this.value=\'\';" />'+
									'<input type="submit" class="button"  value="'+Repository.I18N.Repository.login+'"/>'+
									'<a href="'+Repository.FacebookLoginURI+'"><img height="34px" src="/backend/images/repository/facebook.png"/></a>'+

								'</div>'+


								
							'</div>'+
						'</form>'
					        ,title:	Repository.I18N.Repository.login
//					        ,tbar:[{
//					             text:'Reload'
//					            ,handler:function() {
//					                win.load(win.autoLoad.url + '?' + (new Date).getTime());
//					            }
//					        }]
//					        ,listeners:{show:function() {
//					            this.loadMask = new Ext.LoadMask(this.body, {
//					                msg:'Loading. Please wait...'
//					            });
//					        }}
					    });
					    win.show();
					},			
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
					}), // end of login
		login2 : function () {
			tpl = new Ext.XTemplate('<div id="oryx_repository_header"> <tpl if="isPublicUser"> PUBLIC </tpl><tpl if="!isPublicUser">  NOT PUBLIC </tpl></div>');
			//tpl.compile;
			return tpl;
		}
}