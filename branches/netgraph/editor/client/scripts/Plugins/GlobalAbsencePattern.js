/**
 * Copyright (c) 2010 Ahmed Awad

 * 
 * WARNING THIS IS ONLY TO PROVE A CONCEPT!!!! NOT TO BE USED IN PRODUCTION
 * ENVIRONMENT!!!!
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
 /* This plugin is meant to create an instance of the Global-scope presence pattern according to my thesis*/

if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.GlobalAbsencePattern = ORYX.Plugins.AbstractPlugin.extend({

    facade: undefined,
    
    construct: function(facade){
		
        this.facade = facade;
        
        this.active 		= false;
        this.raisedEventIds = [];
		
		this.facade.offer({
            'name': ORYX.I18N.GlobalAbsencePattern.name,
            'functionality': this.showOverlay.bind(this),
            'group': ORYX.I18N.GlobalAbsencePattern.group,
            'dropDownGroupIcon' : ORYX.PATH +  "images/controlFlow.png",
            'icon': ORYX.PATH + "images/GlobalAbsencePattern.png",
            'description': ORYX.I18N.GlobalAbsencePattern.desc,
            'index': 0,
            'toggle': true,
			'minShape': 0,
            'maxShape': 0
        });
   },
    showOverlay: function(button, pressed){

		if (!pressed) {
			
			this.raisedEventIds = [];
			this.active 		= !this.active;
			//alert("Not Pressed");
			return true;
		} 
		
		var options = {
			command : 'undef'
		}
	
		this.createGlobalAbsencePattern(options);
		button.toggle();
    },
    createGlobalAbsencePattern:function(options){
    	//alert("Create Global Presence Pattern");
    	try
    	{
       		var posx=-30;
       		var from = this.drawStartEvent(posx);
       		posx = posx+150;
       		var to = this.drawEndEvent(posx);
       		this.drawPath(from, to);
       	}
       	catch(error)
       	{
       		Ext.Msg.alert(ORYX.I18N.Oryx.title, error);
       	}
       	
    },
    drawStartEvent:function(posX){
    	var parentShape=this.facade.getCanvas();
        var newShape;
    	
    	var pos={};
        pos['x']=posX;
        pos['y']=0;
        
        var ssn 	= this.facade.getStencilSets().keys()[0];
        //alert("ssn is "+ssn);
        var stencil;
        
        stencil = ORYX.Core.StencilSet.stencil(ssn + "StartEvent");
        newShape=new ORYX.Core.Node({
                'eventHandlerCallback':this.facade.raiseEvent
            },stencil);
        this.facade.getCanvas().add(newShape);
        return newShape;
            
    },
    drawEndEvent:function(posX){
    	var parentShape=this.facade.getCanvas();
        var newShape;
    	
    	
        var pos={};
        pos['x']=posX;
        pos['y']=0;
        var ssn 	= this.facade.getStencilSets().keys()[0];
        var stencil;
        stencil = ORYX.Core.StencilSet.stencil(ssn + "EndEvent");
        newShape=new ORYX.Core.Node({
                'eventHandlerCallback':this.facade.raiseEvent
            },stencil);
        
        this.facade.getCanvas().add(newShape);
        return newShape;
    },
    drawPath:function(from,to){
    	
        //    	alert(from);
        //    	alert(to);
    	
        var newSequenceFlow;
        var parentShape=this.facade.getCanvas();
    	
        var ssn 	= this.facade.getStencilSets().keys()[0];
        var stencil = ORYX.Core.StencilSet.stencil(ssn + "Path");
   	
        newSequenceFlow=new ORYX.Core.Edge({
            'eventHandlerCallback':this.facade.raiseEvent
        },stencil);
        
    	
        // Set the docker
        newSequenceFlow.dockers.first().setDockedShape( from );
        newSequenceFlow.dockers.first().setReferencePoint({
            x: from.bounds.width() / 2.0,
            y: from.bounds.height() / 2.0
        });
    	
        newSequenceFlow.dockers.last().setDockedShape( to );
        newSequenceFlow.dockers.last().setReferencePoint({
            x: to.bounds.width() / 2.0,
            y: to.bounds.height() / 2.0
        });
		//newSequenceFlow.setProperty("oryx-temporalproperty","leadsto");
//		alert(newSequenceFlow.getProperty("oryx-temporalproperty"));
		newSequenceFlow.setProperty("oryx-temporalproperty","Leads to");
		newSequenceFlow.setProperty("oryx-exclude","Some Activity");
        //
        this.facade.getCanvas().add(newSequenceFlow);
        return newSequenceFlow;
    }
    
    
});



