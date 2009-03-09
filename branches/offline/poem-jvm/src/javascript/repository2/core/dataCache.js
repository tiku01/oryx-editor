/**
 * Copyright (c) 2008
 * Bj�rn Wagner, Sven Wagner-Boysen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

// set namespace

if(!Repository) var Repository = {};
if(!Repository.Core) Repository.Core = {};

Repository.Core.DataCache = {
		
	oryxUrl : '/oryx',
	stencilsetUrl : '/stencilsets',
	
	GearsStoreName: "modelcache",
	GearsManagedStoreName: "oryx",
	GearsDatabaseName:"modeldb",
	//relative path to manifest.json, currently in root dir
	MANIFEST_FILENAME:"/../../../manifest.json",
	//defines downloaded representation for each model
	SavedDataUris:["/meta", "/access", "/tags"],
	
	construct : function() {
		//Stores, if Usage of Google Gears is enabled
		this._online=navigator.onLine,		
		this._gearsLocalServer= false,
		this._gearsLocalStore= false,
		this._gearsManagedStore= false,
		this._gearsDatabase= false,
		this._gearsDatabaseOpen=false,
		this._currentOfflineUser=false,
		this._offlineModels= [ ],
		this._dummyData=new Hash();
		
		// Stores data returned from /config handler
		this._configData = null;
		
		// Stores data returned from /user handler
		this._userData = null;
		
		// stores meta data of available modeltypes
		this._model_types = [];	
		// Stores cache type as key and the corresponding hash as value
		this._data = new Hash();
	
		
		this._addHandler 		= new EventHandler();
		this._updateHandler 	= new EventHandler();
		this._removeHandler 	= new EventHandler();
		this._userUpdateHandler = new EventHandler();
		
		this._busyHandler 		= { start: new EventHandler(), end:new EventHandler() };
		
		
		
	},
	
	
	getAddHandler 		: function() {return this._addHandler;},
	getUpdateHandler 	: function() {return this._updateHandler;},
	getRemoveHandler 	: function() {return this._removeHandler;},
	getBusyHandler 		: function() {return this._busyHandler;},
	getUserUpdateHandler : function() {return this._userUpdateHandler;},

	
	getDataAsync : function(fetchDataUri, ids, callback) {
		
		this._busyHandler.start.invoke();
		
		var modelIds = $A(ids); // Ensure that ids is an array
		var cacheMisses = []; // Stores ids of models that aren't cached
		if (this._data.get(fetchDataUri)) {
			// Check if all models 
			modelIds.each(function(modelId) {
				var data = this._data.get(fetchDataUri).get(modelId); // Read value from data hash
				// data isn't cached
				if (data == undefined) {
					cacheMisses.push(modelId);
				}				
			}.bind(this));
		} else {
			cacheMisses = modelIds.clone(); // load all if nothing was loaded before
			this._data.set(fetchDataUri, new Hash());
		}
		// All model data is cached, no server request necessary
		if (cacheMisses.length == 0) {
			result = new Hash();
			modelIds.each(function(id) {
				result.set(id, this._data.get(fetchDataUri).get(id));
			}.bind(this));
			
			if( callback )
				callback(result);
			
			this._busyHandler.end.invoke();
			return;
		}
		// Build query object
		var query = {};
		query.modelIds 		= modelIds;
		query.fetchDataUri 	= fetchDataUri;
		query.callback 		= callback;
		query.cacheMisses 	= cacheMisses;
		
		cacheMisses.each(function(modelId) {
			// Remove leading slash from model uri
			var requestUrl = modelId.substring(1) +  fetchDataUri 
			Ext.Ajax.request({url : requestUrl,  success : this.defaultReturnHandler.bind(this, query, modelId), failure:function(){/*console.log(arguments)*/}});
		}.bind(this));
		
		if( cacheMisses.length <= 0){
			this._busyHandler.end.invoke();
		}
	},
	
	updateObject : function(fetchDataUri, id, data, forceNotUpdate) {
		if (!this._data.get(fetchDataUri)) {
			this._data.set(fetchDataUri, new Hash())
		}
		this._data.get(fetchDataUri).set(id, data);
		if( !forceNotUpdate )
			this._updateHandler.invoke(id);
	},
	
	defaultReturnHandler : function(queryData, modelId, response, options) { 

		// Decode JSON
		var respText 		= response.responseText;
		var returnedData 	= respText.length > 0 ? Ext.util.JSON.decode(respText) : null;

		queryData.cacheMisses = queryData.cacheMisses.without( modelId );
		this.updateObject(queryData.fetchDataUri, modelId, returnedData, true); // Force update event only when at last request 

		// Everything returned from server
		if (queryData.cacheMisses.length == 0) {
			var queriedData = new Hash()
			
			queryData.modelIds.each(function (id){
				queriedData.set(id, this._data.get(queryData.fetchDataUri).get(id) ); // Write data to output hash
			}.bind(this));
			
			if( queryData.callback )
				queryData.callback(queriedData, response); 
			
			
			this._busyHandler.end.invoke();
		
		}
	},
	
	setData:  function( modelIds, uriSuffix, params, successHandler, raiseUserUpdateEvent ){
		this._sendRequest( modelIds, uriSuffix, 'post', params, successHandler, raiseUserUpdateEvent)
	}, 
	
	deleteData:  function( modelIds, uriSuffix, params, successHandler,  raiseUserUpdateEvent){
		this._sendRequest( modelIds, uriSuffix, 'delete', params, successHandler, raiseUserUpdateEvent)
	},	
	
	_sendRequest: function( modelIds, uriSuffix, method, params, successHandler, raiseUserUpdateEvent ){
		
		this._busyHandler.start.invoke();
		
		if( !(modelIds instanceof Array) ){
			modelIds = [ modelIds ]
		}

		uriSuffix = (uriSuffix.startsWith("/") ? uriSuffix : "/" + uriSuffix)	
		
		// Build query object
		var query = {};
		query.modelIds 		= modelIds;
		query.fetchDataUri 	= uriSuffix;
		query.callback 		= function(){
			
			this._updateHandler.invoke( modelIds );
			
			if( successHandler )
				successHandler.apply( successHandler , arguments)
				
			if( raiseUserUpdateEvent )
				this.updateUserData()
			
		}.bind(this);
						
		query.cacheMisses 	= modelIds;
		
		modelIds.each(function(modelId) {
			var requestUrl = modelId.substring(1) + uriSuffix;
			
			if( method.toLowerCase() == "get" || method.toLowerCase() == "delete" ){
				requestUrl += params ? "?" + $H(params).toQueryString() : "";
			}
			
			Ext.Ajax.request({
				    url		: requestUrl,
				    method	: method,
				    params	: params,
				    success	: this.defaultReturnHandler.bind(this, query, modelId),
					failure	: function(){
						Ext.Msg.alert('Oryx','Server communication failed!')
					}
				});
		
		}.bind(this));

		if( modelIds.length <= 0){
			this._busyHandler.end.invoke();
		}
	},
	
	getModelTypes : function() {
		// lazy loading
		if (!this._modelTypes) {
			
			this._busyHandler.start.invoke();
			
			new Ajax.Request( Repository.Config.STENCILSET_URI, 
			 {
				method: "get",
				asynchronous : false,
				onSuccess: function(transport) {
					this._modelTypes = transport.responseText.evalJSON();
					this._modelTypes.each(function(type) {
						type.iconUrl = this.oryxUrl + this.stencilsetUrl + type.icon_url;
						type.url = this.stencilsetUrl + type.uri
						
						/*Cache stencilsets in the cache if gears is enabled*/
						this._cacheGearsData(type.iconUrl);
						this._cacheGearsData(type.url);
					}.bind(this));
					
					
					this._busyHandler.end.invoke();
			
				}.bind(this),
				onFailure: function() {
						alert("Fehler modelTypes");
						this._busyHandler.end.invoke();
					}.bind(this)
			});
		}
		return this._modelTypes;
	},
	
	/* The following functions handle the requests to the /config server handler
	 * 
	 */
	
	
	_ensureConfigData : function() {
		// lazy loading
		if (!this._configData) {
			
			this._busyHandler.start.invoke();
			//cache Configdata
			this._cacheGearsData('config');
			new Ajax.Request("config", 
			 {
				method: "get",
				asynchronous : false,
				onSuccess: function(transport) {
					this._configData = transport.responseText.evalJSON();
					this._busyHandler.end.invoke();
					
					
				}.bind(this),
				onFailure: function() {
						alert("Error loading config data.")
						this._busyHandler.end.invoke();
					}.bind(this)
			});
		}
	},
	
	getAvailableLanguages : function() {
		this._ensureConfigData();
		return this._configData.availableLanguages;
	},
	
	getAvailableSorts : function() {
		this._ensureConfigData();
		return this._configData.availableSorts;
	},

	getAvailableExports : function() {
		this._ensureConfigData();
		return this._configData.availableExports;
	},
		
	/* The following functions handle the requests to the /user server handler
	 * 
	 */

	updateUserData: function(useCache){
		
		if( !useCache ){
			this._userData = null;
			this._ensureConfigData();
		}
		
		this._userUpdateHandler.invoke();
				
	},
		
	_ensureUserData : function() {
		// lazy loading
		if (!this._userData) {
			//UserData not available let Gears cache it for you, critical
			this._cacheGearsData("user");
			this._busyHandler.start.invoke();
			new Ajax.Request("user", 
			 {
				method: "get",
				asynchronous : false,
				onSuccess: function(transport) {
					this._userData = transport.responseText.evalJSON();
					this._busyHandler.end.invoke();
				}.bind(this),
				onFailure: function() {
						alert("Error loading user data.")
						this._busyHandler.end.invoke();
					}.bind(this)
			});
		}
	},
	
	getUserTags : function() {
		this._ensureUserData();
		return this._userData.userTags;
	},
	
	getFriends : function() {
		this._ensureUserData();
		return this._userData.friends;
	},
	
	getLanguage : function() {
		this._ensureUserData();
		return this._userData.currentLanguage;
	},
	
	setLanguage : function(languagecode, countrycode) {
		
		
		this._busyHandler.start.invoke();
			
		new Ajax.Request("user", 
				 {
					method: "post",
					asynchronous : false,
					parameters : { 
						"languagecode" : languagecode,
						"countrycode" : countrycode
					},
					onSuccess: function(transport) {
						
						this._busyHandler.end.invoke();
						window.location.reload(); // reload repository to 
					}.bind(this),
					onFailure: function() {
						alert("Changing langauge failed!")
						this._busyHandler.end.invoke();
					}.bind(this)
				});
	},
	
	
	doRequest: function( url, successHandler, params,  method, asynchronous ){
		
		if(!url){
			return
		}
		
		// Set Busy
		this._busyHandler.start.invoke();
		
		// Define successcallback
		var callback = function(){
		
			this._busyHandler.end.invoke();
			
			if( successHandler )
				successHandler.apply( successHandler , arguments)
			
		}.bind(this);
		
		// Check URL
		method = method ? method : "get";
		if( method.toLowerCase() == "get" || method.toLowerCase() == "delete" ){
			url += params ? "?" + $H(params).toQueryString() : "";
			params = null;
		}				

		if( !asynchronous ){
			new Ajax.Request(url, 
				 {
					method			: "get",
					asynchronous 	: false,
					onSuccess		: callback,
					onFailure		: function(){
						Ext.Msg.alert('Oryx','Server communication failed!')
					},
					parameters 		: params
				});
		} else {
			// Send request	
			Ext.Ajax.request({
				    url		: url,
				    method	: method,
				    params	: params,
				    success	: callback,
					failure	: function(){
						Ext.Msg.alert('Oryx','Server communication failed!')
					}
				});			
		}

		

	},
	// Get in Gears
	enableGears: function(){
		/*Initialize Stores and Database, additional create a public user*/
		this._initLocalModelStore();
		this._initLocalModelDatabase();
		//StandardUser
		//this.createOfflineUser('public');
		this._createManagedStore();
	},
	_initLocalModelStore: function(){ 
		/* Creates LocalServer and LocalStore(used for URL caching)*/
		this._gearsLocalServer = google.gears.factory.create("beta.localserver");
		this._gearsLocalStore = this._gearsLocalServer.createStore(this.GearsStoreName);
		 // Store initialized
	},
	_initLocalModelDatabase: function(){
		/* Create Database and standard tables*/
		this._gearsDatabase = google.gears.factory.create('beta.database', '1.0');
		this._gearsDatabaseOpen=false;
		this._openDB();
		this._gearsDatabase.execute('create table if not exists users' +
           ' (User text, UserData text, ConfigData text)');
		this._gearsDatabase.execute('create table if not exists types' +
           ' (Data text)');
		this._gearsDatabase.execute('create table if not exists EditorModels' +
           ' (ID text, Name text, Model text , editorUrl text, Type text, Owner text)');		
		//´Table for each URI
		this.SavedDataUris.each(function(uri){
			this._gearsDatabase.execute('create table if not exists ' + uri.substring(1) + 
			' (ModelID text, Data text, Owner text)');
		}.bind(this));
		this._closeDB();
		// Database initialzied Tables ensured
		 
	},
	_createManagedStore: function () {
		/* Create the managed resource store, used for caching all files defined in the manifest*/
		if(!this._gearsLocalServer){return}
		this._gearsManagedStore =this._gearsLocalServer.createManagedStore(this.GearsManagedStoreName);
		this._gearsManagedStore.manifestUrl = this.MANIFEST_FILENAME;
		 },	
	restoreDefault: function(){
			/*Debug function, remove both stores and reload data*/
			if(!this._gearsLocalServer)
				return
			this._gearsLocalServer.removeManagedStore(this.GearsManagedStoreName);
			this._createManagedStore();
			this._gearsManagedStore.checkForUpdate();
			this._gearsLocalServer.removeStore(this.GearsStoreName)
			this._initLocalModelStore();
			this.getDefaultDataOffline();
	},	
	
	//Offline Models
	getOfflineModels: function(){
		/*Returns all offline models from the database for the current user */
		var owner= this._currentOfflineUser;
		if(!this._gearsDatabase || !owner){return this._offlineModels}
		this._openDB();
		var tmpUri=this.SavedDataUris[0];
		var rs = this._gearsDatabase.execute('select * from '+tmpUri.substring(1)+" WHERE Owner=? OR Owner=?",[owner,"public"]);
		var tmpOfflineModels=new Array();;
		while (rs.isValidRow()) {
			tmpOfflineModels.push("/model/"+rs.field(0));
			rs.next();
		}
		this._offlineModels= tmpOfflineModels;
		this._closeDB();
		return this._offlineModels;
	},
	saveModelsOffline: function(modelIDs){
		/* Add an array of Models to the LocalDatabase and save default URI*/
		
		
		// Check if Model is yet offline capable, if not add it to the offlineModels
		modelIDs.each(function(modID){
			var yetOffline=false;
			this._offlineModels.each(function(offID){
				if(modID==offID){yetOffline=true;}
			}.bind(this))
			if(!yetOffline){
				this._offlineModels.push(modID)
			}
		}.bind(this));
		
		// Load Data for each URI
		this.SavedDataUris.each(function(uri){
			this.getDataAsync(uri, this._offlineModels,function(result){this._getDataOffline(uri)}.bind(this));
				
		}.bind(this));
		
		
	},
	getOfflineChangedModels: function(){
		/* Return all changes saved in the database (uri, name, type)*/
		var changedModels=new Array();
		var owner= this._currentOfflineUser;
		if(!this._gearsDatabase || !owner){
			return changedModels};
		 
		this._openDB();
		var rs = this._gearsDatabase.execute("select * from EditorModels WHERE (Owner=? OR Owner=?)",[owner,"public"]);
			
			while (rs.isValidRow()) {
				//Load in the Cache if not yet defined
				changedModels.push(["/model/" + rs.field(0), rs.field(1), rs.field(4)]);
				rs.next();
			}
		this._closeDB();
		return changedModels
	},
	deleteOfflineModels: function(IDs){
		// Delete a list of Models out of the DB
		if(!this._gearsDatabase){return}
		 
		var modelIDs=new Array();;
		IDs.each(function(ID){modelIDs.push(ID);}.bind(this))
		this._openDB();
		this.SavedDataUris.each(function(uri){
			modelIDs.each(function(modID){
				
				var found=false;
				// Check if Model exists in Offline Array, is actually Offline
				var rs = this._gearsDatabase.execute("select * from "+uri.substring(1)+' where ModelID='+modID.substring(7));
				if (rs.isValidRow()) {
					this._gearsDatabase.execute('delete from ' + uri.substring(1) + ' where ModelID=' + modID.substring(7));
					var tmp_data = this._data.get(uri).get(modID);
					if (tmp_data.thumbnailUri && this._gearsLocalStore.isCaptured(tmp_data.thumbnailUri)) {
							this._gearsLocalStore.remove(modID.slice(1) + "/local");
							this._gearsLocalStore.remove(tmp_data.thumbnailUri);
							this._gearsLocalStore.remove(tmp_data.pngUri);
					};
				}
			}.bind(this))
		}.bind(this));
		this._closeDB();
		//Ensure Offline Models
		this._offlineModels=this.getOfflineModels();
		
	},	
	loadOfflineChangedModels: function(){
		/*load all offline changed of the current user and adds dummy data to the cache*/
		var dummies= new Array();
		var owner= this._currentOfflineUser;
		if(!this._gearsDatabase || !owner){
			return dummies};
		 
		var currentOfflineModels=this.getOfflineModels()
		this._openDB();
		var rs = this._gearsDatabase.execute("select * from EditorModels WHERE Owner=? OR Owner=?",[owner,"public"]);
		while (rs.isValidRow()) {
			var modID=rs.field(0);
			var data=(rs.field(2)).evalJSON(true)
			var title=rs.field(1)
			if(data.title){
				title=data.title
			}
			var summary=Repository.I18N.OfflinePlugin.unsavedModel
			if(data.summary){
				summary=data.summary
			}
			var type=rs.field(4)
			if(data.type){
				type=data.type
			}
			var editorUrl=rs.field(3);
			var owner=rs.field(5);
			try {				
				dummies.push("/model/"+modID);
				//Load in the Cache if not yet defined	
				
				//Access data
				this._addModeldata(modID, "/access", ('{ "'+ owner+'": "owner"}').evalJSON(true))
				//Meta data
				this._addModeldata(modID,"/meta",('{"summary": "'+summary+'", "author": "'+owner+'", "creationDate": "1999-09-19 19:59:22.629", "title": "'+title+'", "thumbnailUri": "../images/dummy_klein.png", "pngUri": "../images/dummy_groß.png", "lastUpdate": "2009-01-27 15:57:22.629", "type": "'+type+'"}').evalJSON(true)); 
				//Tag data
				this._addModeldata(modID,"/tags",('{"publicTags": [], "userTags": []}').evalJSON(true));
				//Editor Url
					this._cacheGearsData(editorUrl, function(url, loaded){
							//old version found
							if (!this._gearsLocalStore.isCaptured("./model/" + modID + "/local")) {
								this._gearsLocalStore.copy(editorUrl, "./model/" + modID + "/local");
							}
							
							
					}.bind(this))				
				}
				catch(e){Ext.Msg.alert("Offline Error","Failed on loading offline changed models: "+e)};
  			rs.next();
		}
		this._closeDB();
		 
		return dummies
	},	
	
	//Url Cache
	_cacheGearsData: function(url, callback){
		//Cache userdata last Userdata/Configdata
		// optional callback methode executed after capturing
		if(this._gearsLocalStore){
			/*if(this._online && this._gearsLocalStore.isCaptured(url)){
				this._gearsLocalStore.remove(url)
			}*/
			this._gearsLocalStore.capture(url, callback);
		}
	},
	getDefaultDataOffline: function(){
		/*saves a repository start site and the new model sites*/
			this._cacheGearsData(window.location+"")
			this.getModelTypes();
			this._modelTypes.each(function(type){
				var url = './new' + '?stencilset=' + type.url
				this._cacheGearsData(url);
			}.bind(this));
	},
	_getDataOffline: function(uri){
		//Set URI Data for all Offline Models offline
		var author= this._currentOfflineUser;
		if(!this._gearsDatabase || !author){return};
		this._openDB();
		this._offlineModels.each(function(modelId){
				// Check if Modeldata cached
			if (!this._data.get(uri) || !this._data.get(uri).get(modelId)) {return;};
				
			var tmp_id = modelId.substring(7);
			var tmp_data = this._data.get(uri).get(modelId);
			
			//Check if any Entry exists
			var rs = this._gearsDatabase.execute("select * from "+uri.substring(1)+' WHERE ModelID=? AND (Owner=? OR Owner=?)',[tmp_id, author, "public"]);
			if (rs.isValidRow()) {
				this._gearsDatabase.execute("update "+ uri.substring(1)+" set Data="+"'"+Object.toJSON(tmp_data)+"'"+" WHERE ModelID=? AND (Owner=? OR Owner=?)",[tmp_id, author, "public"]);
			}
			else {
				this._gearsDatabase.execute("insert into " + uri.substring(1) + " (ModelID, Data, Owner) values (?,?,?)",[tmp_id,Object.toJSON(tmp_data),author]);
			}
			if (uri == "/meta") {
				// Load in meta defined modelspecific files
				this._cacheGearsData(tmp_data.thumbnailUri);
				this._cacheGearsData(modelId.slice(1) + "/self", function(url, success, captureId){
					try{
						if(success);
							this._gearsLocalStore.rename(url,modelId.slice(1) + "/local")
					}catch(e){
						Ext.Msg.alert("Offline Error","Failed to rename: "+url)
						}
					}.bind(this));
				this._cacheGearsData(tmp_data.pngUri);
				
			}
		}.bind(this));
		this._closeDB();
		 	
	},
	getOfflineData: function(){
		// Read the Data from all Offline Models in the Cache
		if(!this._gearsDatabase){return};
		this._openDB();	
		this.SavedDataUris.each(function(uri){
			//Read out all table entries
			var rs = this._gearsDatabase.execute('select * from '+uri.substring(1));
			
			while (rs.isValidRow()) {
				//this._addModeldata(rs.field(0), uri, rs.field(1).evalJSON(true))
				//Load in the Cache if not yet defined	
				if (!this._data.get(uri) ){
					this._data.set(uri, new Hash());
				};
				if(!this._data.get(uri).get("/model/"+rs.field(0))){
					this._offlineModels.push("/model/"+rs.field(0));
					this._data.get(uri).set("/model/"+rs.field(0), rs.field(1).evalJSON(true));
				};
	  			rs.next();
			}
			
		}.bind(this));
		this._closeDB();
		 
	},	
	
	
	updateChange: function(modID, successHandler){
		/*updates a change to the server*/
		if(!this._gearsDatabase || !this._online){return};
		this._openDB();
		var rs = this._gearsDatabase.execute('select * from EditorModels where ID=? ', [modID]);
		// iterate over all offline changes
		while(rs.isValidRow()) {
						var data=rs.field(2)
						data=data.evalJSON(true)	
						
						new Ajax.Request(rs.field(3), {
		                method: 'POST',
		                asynchronous: false,
		                parameters: data,
						onSuccess: (function(transport) {
							/*if (modID.substring(0, 6) == "offnew") {
								var loc = transport.getResponseHeader("location").toString();
								modID = (loc.split("/"))[(loc.split("/").length - 2)]
							}*/
								 this._openDB();
								// delete change from database
								this._gearsDatabase.execute('delete from EditorModels  where ID=?', [modID]);
								 
								// delete capture
								if(this._gearsLocalStore.isCaptured("model/" + modID + "/local")){
									this._gearsLocalStore.remove( "model/"+modID + "/local");
								}
								if(this._gearsLocalStore.isCaptured("model/" + modID + "/self")){
									this._gearsLocalStore.remove( "model/"+modID + "/self");
								}
								this._data=new Hash();
								//reload offline data
								this.saveModelsOffline(this.getOfflineModels())//.push("/model/"+modID));
								successHandler();
								}.bind(this)),
						onFailure: (function(transport) {
								Ext.Msg.alert("Oryx", ORYX.I18N.Save.failed);
								}.bind(this)),
						on403: (function(transport) {
							// raise loading disable event.
		               		 Ext.Msg.alert("Oryx", "no rights");
					}.bind(this))
				});
				rs.next();
			}
			this._closeDB();
		 
	},
	discardChange: function(modID, successHandler){
		/*deletes a change from the database*/
		var owner=this._currentOfflineUser;
		if(!this._gearsDatabase || !this._online || !owner){return};
		this._openDB();
		var rs = this._gearsDatabase.execute('select * from EditorModels where ID=?', [modID]);
		if(rs.isValidRow){
			this._gearsDatabase.execute('delete from EditorModels where ID=?', [modID]);
			if(this._gearsLocalStore){
						// delete capture
						if(!this._gearsLocalStore.isCaptured("model/" + modID + "/local")){
							this._gearsLocalStore.remove( modID + "/local");
						}
						this._data=new Hash();
						//reload offline data
						this.saveModelsOffline(this.getOfflineModels());
						}
			successHandler();
		}
		this._closeDB();		
		 
	},



	_setOfflineTypes: function(Json){
		//Save a JSON in the Types Table
		if(!this._gearsDatabase){return}
		 
		this._openDB();
		var rs = this._gearsDatabase.execute("select * from types ");
		
		//Falls vorhanden wir der Eintrage erneuert
		if (rs.isValidRow()) {
			this._gearsDatabase.execute("update types set Data="+"'"+Json+"'"+" where Data="+"'"+rs.field(0)+"'");
		}
		else {
			this._gearsDatabase.execute("insert into types (Data) values (?)",[Json]);
		}
		this._closeDB();
		 
	},
	getOfflineTypes: function(){
		//Read out the Database types, eval the JSON and add to the modelTypes array
		if(!this._gearsDatabase){return}
		 
		this._openDB();
		var rs = this._gearsDatabase.execute("select * from types ");
			//read out database
			
			if (rs.isValidRow()) {		
				this.modelTypes = rs.field(0).evalJSON();
			}
		this._closeDB();
		 
	},


	_addModeldata:function(modID, uri, moddata){
				//Load in the Cache if not yet defined	
				if (!this._data.get(uri) ){
					this._data.set(uri, new Hash());
				};
				if(!this._data.get(uri).get("/model/"+modID)){
					this._data.get(uri).set("/model/"+modID, moddata);
				}else{
					this._data.get(uri).set("/model/"+modID, moddata);
				}
		
	},
	addListenerGearsManagedStore:function(callback){
		 	//Add a EventListenes to the LocalStore and force update
		 	if(!this._gearsManagedStore){  
			    var localServer = google.gears.factory.create('beta.localserver');
				localServer.createManagedStore("oryx");}
		 	this._gearsManagedStore.onprogress=callback;
			//Error messages			
			this._gearsManagedStore.onerror=function(error){ if(error!="Error: The browser is offline")Ext.Msg.alert("Oryx", error);};
			this._gearsManagedStore.checkForUpdate();
		 },
	clearDatabase:function(){
		/*remove database and create standard tables*/
		if(!this._gearsDatabase)
			return
		this._openDB();
		this._gearsDatabase.remove();
		this._gearsDatabaseOpen=false;
		this._initLocalModelDatabase();
	},
	clearCache:function(){
		/*clear all intern caches*/
		this._offlineModels= [ ],
		// Stores data returned from /config handler
		this._configData = null;
		
		// Stores data returned from /user handler
		this._userData = null;
		
		// stores meta data of available modeltypes
		this._modelTypes = null;	
		// Stores cache type as key and the corresponding hash as value
		this._data = new Hash();
	},
	
	//Offline User Control functions
	getCurrentOfflineUser: function(){
		return this._currentOfflineUser
	},
	getAllOfflineUser: function(){
		/*reads all user out of the DB*/
		result= new Array();
		if(!this._gearsDatabase){return result};
		this._openDB();
		var rs = this._gearsDatabase.execute('select * from users WHERE User!=?',["public"]);
		while(rs.isValidRow()){
			result.push(rs.field(0))
			rs.next();
		}
		this._closeDB();
		return result;
	},
	setOfflineUser: function(name){
		/*set an user as offline user if user is offline capable*/
		if(!this._gearsDatabase){return false};
		this._openDB();
		var rs = this._gearsDatabase.execute('select * from users where User=?', [name]);
		if(rs.isValidRow){
			this._currentOfflineUser=rs.field(0);
			 
			return true;
		}		
		this._closeDB();
		return false;
	},
	createOfflineUser: function(name){
		/*creates an offline user and caches all relevant data*/
		if(!this._gearsDatabase || !this._online){return}
		this._ensureUserData();
		this._ensureConfigData();
		 
		//Check if any Entry exists
		this._openDB();
			var rs = this._gearsDatabase.execute('select * from users WHERE User=?',[name]);
			if (rs.isValidRow()) {
				this._gearsDatabase.execute("update users set UserData=?, ConfigData=? WHERE User=?",[Object.toJSON(this._userData),Object.toJSON(this._configData),name]);
			}
			else {
				this._gearsDatabase.execute("insert into users (User, UserData, ConfigData) values (?,?,?)",[name,Object.toJSON(this._userData),Object.toJSON(this._configData)]);
			}
		 this._closeDB();

	},
	deleteOfflineUser: function(name){
		/* remove user from the DB and clear relevant caches*/
		if(!this._gearsDatabase || !this._online){return};
		this._openDB();
		var rs = this._gearsDatabase.execute('select * from users where User=?', [name]);
		if(rs.isValidRow){
			this._gearsDatabase.execute('delete from users where User=?', [name]);
			//TODO Delete all models and updates from User
			this._offlineModels= [ ],
			this._dummyData=new Hash();
			
		}
		this._closeDB();		
		 
		this._currentOfflineUser=false;
	},

	loadOfflineUserData: function(){
		/* save relevant user data from the DB to the caches*/
		var user=this._currentOfflineUser;
		if(!this._gearsDatabase || !this._online || !user){return}
		 
		//Check if any Entry exists
		this._openDB();
			var rs = this._gearsDatabase.execute('select * from users WHERE User=?',[user]);
			if (rs.isValidRow()) {
				this._userData= rs.field(1).evalJSON(true);
				this._configData= rs.field(2).evalJSON(true);
			}
		 this._closeDB();
	},
	_openDB: function(){
		/*open DB if not yet opened*/
		if (!this._gearsDatabaseOpen) {
			this._gearsDatabase.open(this.GearsDatabaseName);
			this._gearsDatabaseOpen = true;
		}
	},
	_closeDB: function(){
		if (!this._gearsDatabaseOpen) {
			this._gearsDatabase.close();
			this._gearsDatabaseOpen = false;
		}
	}


	
	
};

Repository.Core.DataCache = Clazz.extend(Repository.Core.DataCache);

