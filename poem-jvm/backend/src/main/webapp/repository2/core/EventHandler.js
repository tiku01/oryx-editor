Ext.namespace('Repository');

function EventHandler() {
	this._callbacks = new Array();
}

EventHandler.prototype = {
	registerCallback : function(callback) {
		if (typeof(callback) == "function") {
			this._callbacks.push(callback);
		}
	},
	
	unregisterCallback : function(callback) {
		// if callback exists
		if (this._callbacks.indexOf(callback) > -1) {
			this._callbacks[this._callbacks.indexOf(callback)] = null; // remove it
			this._callbacks = this._callbacks.compact(); // remove null item from array
		}
	},
	
	invoke : function(arg) {
		this._callbacks.each(function(callback) { 
			try {
				callback(arg);
			} catch(e) {
				// if the call fails do nothing but call remaining callbacks
			}});
	},
	
	invoke : function(arg1, arg2) {
		this._callbacks.each(function(callback) {callback(arg1, arg2);})
	}
}