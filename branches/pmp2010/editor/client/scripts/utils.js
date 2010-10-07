
/**
 * @namespace Oryx name space for different utility methods
 * @name ORYX.Utils
*/
ORYX.Utils = {
    /**
     * General helper method for parsing a param out of current location url
     * @example
     * // Current url in Browser => "http://oryx.org?param=value"
     * ORYX.Utils.getParamFromUrl("param") // => "value" 
     * @param {Object} name
     */
    getParamFromUrl: function(name){
        name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
        var regexS = "[\\?&]" + name + "=([^&#]*)";
        var regex = new RegExp(regexS);
        var results = regex.exec(window.location.href);
        if (results == null) {
            return null;
        }
        else {
            return results[1];
        }
    },
	
	adjustGradient: function(gradient, reference){
		
		if (ORYX.CONFIG.DISABLE_GRADIENT && gradient){
		
			var col = reference.getAttributeNS(null, "stop-color") || "#ffffff";
			
			$A(gradient.getElementsByTagName("stop")).each(function(stop){
				if (stop == reference){ return; }
				stop.setAttributeNS(null, "stop-color", col);
			})
		}
	},
    
	contains: function(array, object){
		for(var i = 0; i < array.length; i++) {
		    if(this.equals(array[i],object)){
		      return true;
		    }
		}
	  return false;
	},
	
	equals: function(object1,object2)
	{
	    for(p in object1)
	    {
	        switch(typeof(object1[p]))
	        {
	                case 'object':
	                		break;
	                case 'function':
	                        if (typeof(object2[p])=='undefined' || (object1[p].toString() != object2[p].toString())) { return false; }; break;
	                default:
	                        if (object1[p] != object2[p]) { return false; }
	        }
	    }

	    return true;
	},
	
	deepEquals: function(object1,object2)
	{
	    for(p in object1)
	    {
	        switch(typeof(object1[p]))
	        {
	                case 'object':
	                		if (!this.equals(object1[p],object2[p])) { return false }; break;
	                case 'function':
	                        if (typeof(object2[p])=='undefined' || (object1[p].toString() != object2[p].toString())) { return false; }; break;
	                default:
	                        if (object1[p] != object2[p]) { return false; }
	        }
	    }

	    for(p in object2)
	    {
	        if(typeof(object1[p])=='undefined') {return false;}
	    }

	    return true;
	}
}


