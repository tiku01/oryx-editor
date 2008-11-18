/**
 * Copyright (c) 2008
 * Willi Tscheschner
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

if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

/**
 * Supports EPCs by offering a syntax check and export and import ability..
 * 
 * 
 */
ORYX.Plugins.CollapsePool = Clazz.extend({

	facade: undefined,

	/**
	 * Offers the plugin functionality:
	 * 
	 */
	construct: function(facade) {

		this.facade = facade;
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_CANVAS_SHAPEADDED, this.shapeAdded.bind(this) );
		
	},

	/**
	 * Added the click button to Pools
	 * 
	 * @param {Object} option
	 */
	shapeAdded: function(option){

		if( option.shape instanceof ORYX.Core.Node && option.shape.getStencil().id().include('Pool')){
			
			this.show( option.shape , "");
			
		}
		
	},

	/**
	 *  Ececute the clicking
	 */	
	executeClick: function(shape, event) {
		
		alert('Clicked on ' + shape.toString())

	},
	
	/**
	 * Shows the Link for a particular shape with a specific url
	 * 
	 * @param {Object} shape
	 * @param {Object} url
	 */
	show: function( shape ){

		
		// Generate the svg-representation of a link
		var link  = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
					[ 'a',
						{'target': '_blank'},
						['rect', 
							{ "stroke-width": 1.0, "stroke":"black", "fill": "white", width: 10, height: 10, y:-10 }
						],
						['path', 
							{ "stroke-width": 1.0, "stroke":"black", "fill": "none", "d":  "M05 -10  v10  M0 -5  h10", "line-captions": "round"}
						]
					]);

		// Set the link with the special namespace	
		link.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", '#DoClick');	
		link.addEventListener( 'click', function(e){ this.executeClick(shape, e); Event.stop(e); return false}.bind(this), false)	
		
		// Shows the link in the overlay					
		this.facade.raiseEvent({
					type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
					id: 			"collapsepool.urlref_" + shape.id,
					dontCloneNode:	true,
					shapes: 		[shape],
					node:			link,
					nodePosition:	"S"
				});	
				
							
	},	

	/**
	 * Hides the Link for a particular shape
	 * 
	 * @param {Object} shape
	 */
	hide: function( shape ){

		this.facade.raiseEvent({
					type: 			ORYX.CONFIG.EVENT_OVERLAY_HIDE,
					id: 			"collapsepool.urlref_" + shape.id
				});	
							
	}		
});