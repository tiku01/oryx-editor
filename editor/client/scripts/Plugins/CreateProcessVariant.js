/**
 * Copyright (c) 2010 Emilian Pascalau and Ahmed Awad

 * 
 * WARNING THIS IS ONLY TO PROVE CONCEPT!!!! NOT TO BE USED IN PRODUCTION
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
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.CreateProcessVariant = ORYX.Plugins.AbstractPlugin.extend({

    facade: undefined,
    
    // processGraph:undefined,
    
    construct: function(facade){
		
        this.facade = facade;
        
		this.active 		= false;
		this.raisedEventIds = [];
		
				
		this.createModel();
		
    },
    
	createModel:function(){
    	
    		function test(){}
    	
    	
    		if(creator1234){
    			

    			
    			// alert("creator 1234 exists");
    			var respXML=creator1234.variant;
    				
    			var root=respXML.firstChild;
                var pg=root.getElementsByTagName("ProcessGraph");
                var processGraphElements=pg.item(0).childNodes;
                
                var posx=-30;
                var processGraphXML=new Array();
                var processGraphShape=new Array();
                
                for(var i=0;i<processGraphElements.length;i++){
                
                	var ce=processGraphElements[i];
                	if(ce.nodeName=="Activity" || ce.nodeName=="Gateway" || ce.nodeName=="Event" ){
                	 posx=posx+150;	                		
                	
                	 var shape=this.drawNode(ce,posx);
                	 processGraphXML.push(ce);
                	 processGraphShape.push(shape);
                	} 
                	
                	//it is assumed that all sequenceFlows are at the end
                	if(ce.nodeName=="SequenceFlow"){
                		var fId=this.getSequenceFlowFrom(ce);
                		
                		var from=this.getShapeById(fId,processGraphShape);
//                		alert(from);
//                		
                		
                		var tId=this.getSequenceFlowTo(ce);
                		
                		var to=this.getShapeById(tId,processGraphShape);
//                		alert(to);
                		
                		shape=this.drawSequenceFlow(from,to);
                		
                		processGraphXML.push(ce);
                		processGraphShape.push(shape);               		
                		
                	}
                	
                }
                
               // alert(processGraphXML.length);
               // alert(processGraphShape.length);               
                
    

            // this.drawShape();
                
 		}
    	
    },
    
    drawNode:function(element, posX){

    	var parentShape=this.facade.getCanvas();
    	var newShape;
    	
    	
    	var pos={};
    	pos['x']=posX;
    	pos['y']=0;
    	
    	if(element.nodeName=="Activity"){
    	
    	var option = {
				type:"http://b3mn.org/stencilset/bpmn1.1#Task",
				position:pos,
				namespace:parentShape.getStencil().namespace(),
				parent:parentShape				
		};
    	newShape=this.facade.createShape(option);
    	newShape.resourceId=element.getAttributeNode("id").nodeValue;
    	var label=element.getAttributeNode("label").nodeValue;
    	// alert(label);
    
    	newShape.setProperty("oryx-name",label);
    	
		this.facade.getCanvas().update();
    	}
    	
    	// deal with Events
    	if(element.nodeName=="Event"){
        	
    		var type2=element.getAttributeNode("type2").nodeValue;
    		var type2LastChar=type2[type2.length-1];
    		
    		
    		if(type2LastChar==1){
    		
        	var option = {
    				type:"http://b3mn.org/stencilset/bpmn1.1#StartEvent",
    				position:pos,
    				namespace:parentShape.getStencil().namespace(),
    				parent:parentShape				
    		};
        	newShape=this.facade.createShape(option);
    		}
    		
    		if(type2LastChar==2){
        		
            	var option = {
        				type:"http://b3mn.org/stencilset/bpmn1.1#IntermediateEvent",
        				position:pos,
        				namespace:parentShape.getStencil().namespace(),
        				parent:parentShape				
        		};
            	newShape=this.facade.createShape(option);
        		}
    		
    		if(type2LastChar==3){
        		
            	var option = {
        				type:"http://b3mn.org/stencilset/bpmn1.1#EndEvent",
        				position:pos,
        				namespace:parentShape.getStencil().namespace(),
        				parent:parentShape				
        		};
            	newShape=this.facade.createShape(option);
        		}
    		
        	newShape.resourceId=element.getAttributeNode("id").nodeValue;
        	// var label=element.getAttributeNode("label").nodeValue;
        	// newShape.setProperty("oryx-name",label);
        	
    		this.facade.getCanvas().update();
        	}
    	
    	// deal with gateways
    	if(element.nodeName=="Gateway"){
    		var type2=element.getAttributeNode("type2").nodeValue;
    		
    		
    		
    		if(type2.indexOf("XOR")>=0){
        		
            	var option = {
        				type:"http://b3mn.org/stencilset/bpmn1.1#Exclusive_Databased_Gateway",
        				position:pos,
        				namespace:parentShape.getStencil().namespace(),
        				parent:parentShape				
        		};
            	newShape=this.facade.createShape(option);
        		}
    	

    		if(type2.indexOf("OR")>=0 && type2.indexOf("XOR")==-1){
        		
            	var option = {
        				type:"http://b3mn.org/stencilset/bpmn1.1#OR_Gateway",
        				position:pos,
        				namespace:parentShape.getStencil().namespace(),
        				parent:parentShape				
        		};
            	newShape=this.facade.createShape(option);
        		}
    		
    		if(type2.indexOf("AND")>=0 ){
        		
            	var option = {
        				type:"http://b3mn.org/stencilset/bpmn1.1#AND_Gateway",
        				position:pos,
        				namespace:parentShape.getStencil().namespace(),
        				parent:parentShape				
        		};
            	newShape=this.facade.createShape(option);
        		}
    		
    		newShape.resourceId=element.getAttributeNode("id").nodeValue;
        	// var label=element.getAttributeNode("label").nodeValue;
        	// newShape.setProperty("oryx-name",label);
        	
    		this.facade.getCanvas().update();
    		
    	}	
    	
   	
    	return newShape;
    	
    },
    
    getSequenceFlowFrom:function(sequenceFlow){
    	var efrom=sequenceFlow.getAttributeNode("from").nodeValue;
    	
    	var pos=efrom.indexOf("#");
    	var type=efrom.substring(0,pos);
        var shapeId=efrom.substring(pos,efrom.length);
    	
    	var from={};
    	from['type']=type;
    	from['shapeId']=shapeId;    	
    	
//    	return from;
    	return shapeId;
    },
    
    getSequenceFlowTo:function(sequenceFlow){
    	var eto=sequenceFlow.getAttributeNode("to").nodeValue;;
    	
    	var pos=eto.indexOf("#");
    	var type=eto.substring(0,pos);
        var shapeId=eto.substring(pos,eto.length);
    	
    	var to={};
    	to['type']=type;
    	to['shapeId']=shapeId;    	
    	
//    	return to;
    	return shapeId;
    	
    },
    
    getShapeById:function(id,processGraphShape){
    	var pos=-1;
    	
    	for(var k=0;k<processGraphShape.length;k++){
    		var ce=processGraphShape[k];
    		var ok=false;
    		if (ce.resourceId==id){
    			pos=k;
    			ok=true;
    		}
    		if(ok) break;
    		
    	}
    	return processGraphShape[pos];
    },
    
    //from and to are shapes
    drawSequenceFlow:function(from,to){
    	
//    	alert(from);
//    	alert(to);
    	
    	var newSequenceFlow;
    	var parentShape=this.facade.getCanvas();
    	
    	var pos={};
    	pos['x']=0;
    	pos['y']=0;
    	
    	var option = {
				type:"http://b3mn.org/stencilset/bpmn1.1#SequenceFlow",
				//position:pos,
				namespace:parentShape.getStencil().namespace(),
				parent:parentShape				
		};
    	newSequenceFlow=this.facade.createShape(option);
    	
    	// Set the docker
    	newSequenceFlow.dockers.first().setDockedShape( from );
    	newSequenceFlow.dockers.first().setReferencePoint({x: from.bounds.width() / 2.0, y: from.bounds.height() / 2.0});
    	
    	newSequenceFlow.dockers.last().setDockedShape( to );
    	newSequenceFlow.dockers.last().setReferencePoint({x: to.bounds.width() / 2.0, y: to.bounds.height() / 2.0});
		
    	this.facade.getCanvas().update();
    	return newSequenceFlow;
    }
    
    
});



