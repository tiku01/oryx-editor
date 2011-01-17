/**
 * Copyright (c) 2011
 * Ole Eckermann
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

/**
 * Transforms an object life-cycle (Petri net)
 * to a workflow model (colored Petri net)
 */
Ext.ns("Oryx.Plugins");

ORYX.Plugins.OLC2CPN = ORYX.Plugins.AbstractPlugin.extend({

	// Constructor
    construct: function(facade){
		arguments.callee.$.construct.apply(this, arguments);
        this.facade.offer({
            'name': "Transform to workflow model",
            'functionality': this.transform.bind(this),
            'group': ORYX.I18N.SyntaxChecker.group,
            'icon': ORYX.PATH + "images/cpn/cpn_export.png",
            'description': "Creates a new colored Petri net based on this object life-cycle",
            'index': 1,
            'minShape': 0,
            'maxShape': 0
        });
	},

	transform: function() {		
        var serialized_rdf = this.getRDFFromDOM();
		if (!serialized_rdf.startsWith("<?xml")) {
			serialized_rdf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + serialized_rdf;
		}
		
        Ext.Ajax.request({
            url: ORYX.CONFIG.ROOT_PATH + 'olc2cpn',
            method: 'POST',
            success: function(request){
            alert('done');
        },
        failure: function(){
        },
        params: {
            data: serialized_rdf
        }
        });
	}
	
});


