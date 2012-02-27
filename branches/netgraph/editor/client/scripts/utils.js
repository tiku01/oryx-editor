
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
	}
}
/* 
 *  Javascript (good) hack fixing Chrome and Chromium bug that prevent using insertAdjacentHTML with namespaces
 * 
 *  Copyright (c) 2011 Florent FAYOLLE
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */

// check if Chrome/Chromium is used
if(/Chrome/.test(navigator.userAgent)){
        // test if the bug is really present
        var div = document.createElement("div");
        div.insertAdjacentHTML("BeforeEnd", "<p foo:bar='hello'>world</p>");
        
        // the bug is when the div Element, after the call of insertAdjacentHTML method, still has no chrildren
        if(div.children.length === 0){
                // save the native function of insertAdjacentHTML
                var proxy_insertAdjacentHTML = HTMLElement.prototype.insertAdjacentHTML;
                // function that replace all modified attributes to their real name
                function __clean_attr(node){
                        var name;
                        for(var i = 0; i < node.attributes.length; i++){
                                name = node.attributes[i].nodeName;
                                if( node.attributes[i].nodeName.indexOf("__colon__") >= 0){
                                        node.setAttribute(name.replace(/__colon__/g, ":"), node.getAttribute(name));
                                        node.removeAttribute(name);
                                }
                        }
                }
                // the new function insertAdjacentHTML will replace all attributes of that form : namespace:attribute="value"
                // to that form : namespace__colon__attribute="value"
                HTMLElement.prototype.insertAdjacentHTML = function(where, html){
                        var new_html = html.replace( /([\S]+):([\S]+)=/g ,"$1__colon__$2=");
                        // we call the native insertAdjacentHTML that will parse the HTML string to DOM
                        proxy_insertAdjacentHTML.call(this, where, new_html);
                        var nodes = this.getElementsByTagName("*");
                        for(var i = 0; i < nodes.length; i++)
                                __clean_attr(nodes[i]);
                }
        }
}
