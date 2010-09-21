/**
 * Copyright (c) 2010 Emilian Pascalau and Ahmed Awad

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
    	
    	   	
        if(oryxCreator1283772618640){
    			
            var respXML=oryxCreator1283772618640.variant;
    				
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
                		
                    var from=this.getShapeById(fId,processGraphXML,processGraphShape);
                    //                		alert(from);
                    //
                		
                    var tId=this.getSequenceFlowTo(ce);
                		
                    var to=this.getShapeById(tId,processGraphXML,processGraphShape);
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
    	
        var ssn 	= this.facade.getStencilSets().keys()[0];
        var stencil;
        if(element.nodeName=="Activity"){
    	
    							
            stencil = ORYX.Core.StencilSet.stencil(ssn + "Task");
    		
            //    	var option = {
            //				type:"http://b3mn.org/stencilset/bpmn1.1#Task",
            //				position:pos,
            //				namespace:parentShape.getStencil().namespace(),
            //				parent:parentShape
            //		};
            //newShape=this.facade.createShape(option);
    	
            newShape=new ORYX.Core.Node({
                'eventHandlerCallback':this.facade.raiseEvent
            },stencil);
    	
            //    	newShape.resourceId=element.getAttributeNode("id").nodeValue;
            var label=element.getAttributeNode("label").nodeValue;
            // alert(label);
    
            newShape.setProperty("oryx-name",label);
            this.facade.getCanvas().add(newShape);
        //this.facade.getCanvas().update();
        }
    	
        // deal with Events
        if(element.nodeName=="Event"){
        	
            var type2=element.getAttributeNode("type2").nodeValue;
            var type2LastChar=type2[type2.length-1];
            var eventType=type2.substring(0,type2.length-1);
            
//            alert("eventType "+eventType);

            
    		
            if(type2LastChar==1){
    		
                if(eventType=="MessageEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "StartMessageEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }
                else
                if(eventType=="TimerEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "StartTimerEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="ConditionalEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "StartConditionalEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="SignalEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "StartSignalEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="MultipleEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "StartMultipleEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                {
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "StartEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }
            }
    		
            if(type2LastChar==2){

               
                if(eventType=="MessageEventCatching"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateMessageEventCatching");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="MessageEventThrowing"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateMessageEventThrowing");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="TimerEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateTimerEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="ErrorEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateErrorEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="CancelEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateCancelEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="CompensationEventCatching"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateCompensationEventCatching");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="CompensationEventThrowing"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateCompensationEventThrowing");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="ConditionalEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateConditionalEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="SignalEventCatching"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateSignalEventCatching");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="SignalEventThrowing"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateSignalEventThrowing");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="MultipleEventCatching"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateMultipleEventCatching");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="MultipleEventThrowing"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateMultipleEventThrowing");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="LinkEventCatching"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateLinkEventCatching");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="LinkEventThrowing"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateLinkEventThrowing");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                {
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "IntermediateEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }

                
            }
    		
            if(type2LastChar==3){

              
                if(eventType=="MessageEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "EndMessageEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="ErrorEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "EndErrorEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="CancelEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "EndCancelEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="CompensationEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "EndCompensationEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="SignalEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "EndSignalEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="MultipleEvent"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "EndMultipleEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                if(eventType=="Terminate"){
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "EndTerminateEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);
                }else
                {
                    stencil = ORYX.Core.StencilSet.stencil(ssn + "EndEvent");
                    newShape=new ORYX.Core.Node({
                        'eventHandlerCallback':this.facade.raiseEvent
                    },stencil);                    
                }              
            	
            }  		
    		
            // var label=element.getAttributeNode("label").nodeValue;
            // newShape.setProperty("oryx-name",label);
            this.facade.getCanvas().add(newShape);
    		
        }
    	
        // deal with gateways
        if(element.nodeName=="Gateway"){
            var type2=element.getAttributeNode("type2").nodeValue;
    		
    		
    		
            if(type2.indexOf("XOR")>=0){
   		
                stencil = ORYX.Core.StencilSet.stencil(ssn + "Exclusive_Databased_Gateway");
                newShape=new ORYX.Core.Node({
                    'eventHandlerCallback':this.facade.raiseEvent
                },stencil);
            	
            }
    	

            if(type2.indexOf("OR")>=0 && type2.indexOf("XOR")==-1){
        		
            	
                stencil = ORYX.Core.StencilSet.stencil(ssn + "OR_Gateway");
                newShape=new ORYX.Core.Node({
                    'eventHandlerCallback':this.facade.raiseEvent
                },stencil);
            	
            }
    		
            if(type2.indexOf("AND")>=0 ){
        		          	
                stencil = ORYX.Core.StencilSet.stencil(ssn + "AND_Gateway");
                newShape=new ORYX.Core.Node({
                    'eventHandlerCallback':this.facade.raiseEvent
                },stencil);
            	
            }
    		
            // var label=element.getAttributeNode("label").nodeValue;
            // newShape.setProperty("oryx-name",label);
            this.facade.getCanvas().add(newShape);
    		
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
        var eto=sequenceFlow.getAttributeNode("to").nodeValue;
        ;
    	
        var pos=eto.indexOf("#");
        var type=eto.substring(0,pos);
        var shapeId=eto.substring(pos,eto.length);
    	
        var to={};
        to['type']=type;
        to['shapeId']=shapeId;
    	
        //    	return to;
        return shapeId;
    	
    },
    
    getShapeById:function(id,processGraphXML,processGraphShape){
        var pos=-1;
    	
        for(var k=0;k<processGraphXML.length;k++){
            var ce=processGraphXML[k];
            var ok=false;
            var ceId=ce.getAttributeNode("id").nodeValue;
            if (ceId==id){
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
    	
        var ssn 	= this.facade.getStencilSets().keys()[0];
        var stencil = ORYX.Core.StencilSet.stencil(ssn + "SequenceFlow");
   	
        newSequenceFlow=new ORYX.Core.Edge({
            'eventHandlerCallback':this.facade.raiseEvent
        },stencil);
        ;
    	
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
		
        //
        this.facade.getCanvas().add(newSequenceFlow);
        return newSequenceFlow;
    }
    
    
});



