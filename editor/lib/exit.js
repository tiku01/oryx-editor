/**
 * Exit notification
 */
(function() {
	var cookie = (function(ID) {

		// understand how cookies work: http://www.quirksmode.org/js/cookies.html
		var expires = function(interval){
			return new Date((new Date).getTime() + (interval)).toUTCString();
		}

		var path = function(){
			return "/";
		}

		// persist clipboard for 24h
		var set = function(content, time) {
			if ("undefined" == typeof(time)) {
					time = 1000*60*60*24;
			}

			document.cookie = ID+"="+encodeURIComponent(content) + ";" +
			"expires=" + expires(time) + ";" +
			"path=" + path();

			return get();
		}

		var unset = function() {
			document.cookie = 
				ID+"=null;" +
				"expires=" + expires(-1) + ";" +
				"path=" + path()

			return (null == get());
		}

		var get = function(){
			var ck = document.cookie;
			var content = null;
			var match = ck.match(new RegExp(ID + "=([^;]+)"));
			if (match) {
				content = decodeURIComponent(match[1]);
			}
			return content;
		}

		return {
			get: get,
			set: set,
			unset: unset,

			getJson: function() {
				return Ext.decode(this.get())
			},
			setJson: function(content) {
				return this.set(Ext.encode(content));
			}
		}

	})("org.oryx-project.oryx.exit");

	if (!(cookie.get() || "").match(/^exit-(\d{0,2})/)) {
		Ext.Msg.show({
			title:'Oryx service will be discontinued!',
			msg: 'In an effort to establish the <b><a href="http://bpt.hpi.uni-potsdam.de/BPMAcademicInitiative" target="_blank">BPM Academic Initiative</a></b> as the central hub for teaching and research in business process management and to avoid redundant work, the Oryx online service will be discontinued from September 30, 2011.<br/>'+
			     'As of September 1 you may not create or update any models, but view them.<br/>'+
			     'Interested parties from academia and from research projects are invited to join the BPM Academic Initiative free of charge. More information can be found at <a href="http://bpt.hpi.uni-potsdam.de/BPMAcademicInitiative">http://bpt.hpi.uni-potsdam.de/BPMAcademicInitiative</a>',
			width: 500,
			model: true,
			buttons: Ext.Msg.OK,
			animEl: 'elId',
			icon: Ext.MessageBox.INFO
		});
	
		cookie.set("exit-1", 1000*60*60*24) // will be shown at most once a day
	}
})();