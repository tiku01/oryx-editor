/**
 * Copyright (c) 2009
 * Ingo Kitzmann, Christoph Koenig
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

if (!ORYX) 
	ORYX = new Object();
if (!ORYX.Plugins) 
	ORYX.Plugins = new Object();

ORYX.Plugins.BpmnLayouter = ORYX.Plugins.AbstractPlugin.extend({
	facade: undefined,
	construct: function(facade){
		this.facade = facade;
		this.facade.offer({
			'name' : "Layout-BPMN",
			'description' : "Layout BPMN Model",
			'functionality' : this.layout.bind(this),
			'group' : "Layout",
			'icon' : ORYX.PATH + "images/auto_layout.png",
			'index' : 1,
			'minShape' : 0,
			'maxShape' : 0
		});
	},
	layout: function(){
		new Ajax.Request(ORYX.CONFIG.BPMN_LAYOUTER, {
			method : 'POST',
			asynchronous : false,
			parameters : {
				data: this.facade.getERDF()
			},
			onSuccess: function(request){

				Ext.Msg.alert("Oryx", "New Layout arrived:!\n" + request.responseText);
				var resp = request.responseText.evalJSON();

				if(resp instanceof Array && resp.size() > 0){
					console.log(resp);
					resp.each(function(elem){
						var shape = this.facade.getCanvas().getChildShapeByResourceId(elem.id);
						var bound = elem.bounds.split(" ");
						shape.bounds.set(bound[0],bound[1],bound[2],bound[3]);
						
						if(elem.dockers != null){
							/* clear all except of the first and last dockers */
							/*var dockers = shape.getDockers().slice(1,-1);
							dockers.each(function(docker){
								shape.removeDocker(docker);
							});
							
							var dockersCoordinates = elem.dockers.split(" ");*/
							/* set first and last docker */
							/*var firstDocker = shape.getDockers()[0];
							var firstPoint = {
									x: dockersCoordinates.shift(),
									y: dockersCoordinates.shift()
								};
							firstDocker.setReferencePoint(firstPoint);	
							firstDocker.update();
							
							var lastDocker = shape.getDockers()[1];
							var lastPoint = {
									y: dockersCoordinates.pop(),
									x: dockersCoordinates.pop()
								};
							lastDocker.setReferencePoint(lastPoint);	
							lastDocker.update();
							
							lastDocker.fail();
							*/
							/* add new dockers except of the first and last */
							/*dockersCoordinates = dockersCoordinates.slice(2,-2);
							for(var i = 0; i < dockersCoordinates.length; i = i + 2){
								var point = {
									x: parseFloat(dockersCoordinates[i]),
									y: parseFloat(dockersCoordinates[i+1])
								};
								shape.addDocker(point);
							}*/
							shape.deserialize([{
								prefix: 'oryx',
								name: 'dockers',
								value: elem.dockers
							}]);
						}
						shape.update();
					}.bind(this));
				this.facade.getCanvas().update();
				}
				
			}.bind(this)
		})
	}
});
