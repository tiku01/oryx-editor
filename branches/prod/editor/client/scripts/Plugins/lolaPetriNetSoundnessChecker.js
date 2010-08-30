/**
 * Copyright (c) 2010 Philipp Berger, Kai Schlichting
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
Ext.ns("Oryx.Plugins");

ORYX.Plugins.LolaPetriNetSoundnessChecker = ORYX.Plugins.AbstractPlugin.extend({

    hideOverlays: function(){
        // TODO set in constructor!!!
        if(!this.overlayIds) 
            return;
    
        Ext.each(this.overlayIds, function(overlayId){
            this.facade.raiseEvent({
                type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
                id: overlayId
            });
        }.bind(this));
    },
    
    getChildShapesByResourceIds: function(resourceIds){
        var shapes = [];
    
        Ext.each(resourceIds, function(resourceId){
            shapes.push(this.facade.getCanvas().getChildShapeByResourceId(resourceId));
        }.bind(this));
        
        return shapes;
    },
    
    /**
	 * Show overlay on given shape.
	 * 
	 * @methodOf ORYX.Plugins.AbstractPlugin.prototype
	 * @example showOverlay( myShape, { stroke: "green" },
	 *          ORYX.Editor.graft("http://www.w3.org/2000/svg", null, ['path', {
	 *          "title": "Click the element to execute it!", "stroke-width":
	 *          2.0, "stroke": "black", "d": "M0,-5 L5,0 L0,5 Z",
	 *          "line-captions": "round" }]) )
	 * @param {Oryx.XXX.Shape[]}
	 *            shapes One shape or array of shapes the overlay should be put
	 *            on
	 * @param {Oryx.XXX.Attributes}
	 *            attributes some attributes...
	 * @param {Oryx.svg.node}
	 *            svgNode The svg node which should be used as overlay
	 * @param {String}
	 *            [svgNode="NW"] The svg node position where the overlay should
	 *            be placed
	 */
    showOverlay: function(shapes, attributes, svgNode, svgNodePosition ){
        if(!this.overlayIds){
            this.overlayIds = [];
        }
        
        if( !(shapes instanceof Array) ){
            shapes = [shapes]
        }
        
        // Define Shapes
        shapes = shapes.map(function(shape){
            var el = shape;
            if( typeof shape == "string" ){
                el = this.facade.getCanvas().getChildShapeByResourceId( shape );
                el = el || this.facade.getCanvas().getChildById( shape, true );
            }
            return el;
        }.bind(this)).compact();
        
        // Define unified id
        var overlayId = this.type + ORYX.Editor.provideId();
        this.overlayIds.push(overlayId);
        
        this.facade.raiseEvent({
            type        : ORYX.CONFIG.EVENT_OVERLAY_SHOW,
            id          : overlayId,
            shapes      : shapes,
            attributes  : attributes,
            node        : svgNode,
            nodePosition: svgNodePosition || "NW"
        });
        
    },

    // Offers the plugin functionality
    construct: function(facade){
        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);
                
        this.facade.offer({
            'name': "Check soundness",// ORYX.I18N.BPMN2PNConverter.name,
            'functionality': this.showCheckerWindow.bind(this),
            'group': "Verification",
            'icon': ORYX.PATH + "images/soundness_checker/accept.png",
            'description': "Checks current Petri net for different soundness criteria with LoLA.",
            'index': 3,
            'minShape': 0,
            'maxShape': 0
        });
    },
    
    showCheckerWindow: function(){
        var plugin = this;
        
        var CheckNode = Ext.extend(Ext.tree.TreeNode, {
            constructor: function(config) {
                if(!config.icon && !this.icon)
                    config.icon = CheckNode.UNKNOWN_STATUS;

                CheckNode.superclass.constructor.apply(this, arguments);
                
                Ext.apply(this, config);
                
                if(this.clickHandler){
                    this.on('click', this.clickHandler.bind(this));
                }
            },

            setIcon: function(status) {
                this.ui.getIconEl().src = status;
            },
            getIcon: function(status) {
                return this.ui.getIconEl().src;
            },
            reset: function(){
                plugin.hideOverlays();
                this.hideMarking();
                // Reset syntax errors
                plugin.facade.raiseEvent({type: ORYX.Plugins.SyntaxChecker.RESET_ERRORS_EVENT});
            },
            hideMarking: function(){
                if(!plugin.marking)
                    return;
            
                for(place in plugin.marking){
                    var placeShape = plugin.facade.getCanvas().getChildShapeByResourceId(place);
                    if(placeShape)// place can be null if removed
                        placeShape.setProperty("oryx-numberoftokens", 0);
                }
                // Show changes
                plugin.facade.getCanvas().update();
                
                plugin.marking = undefined;
            },
            showMarking: function(marking){
                plugin.marking = marking;
            
                for(place in marking){
                    var placeShape = plugin.facade.getCanvas().getChildShapeByResourceId(place);
                    placeShape.setProperty("oryx-numberoftokens", marking[place]);
                }
                // Show changes
                plugin.facade.getCanvas().update();
            },
            showErrors: function(errors){
                // Remove all old error nodes
                Ext.each(this.childNodes, function(child){
                    if(child && child.itemCls === 'error')
                        child.remove();
                });
                
                // Show Unknown status on child nodes
                Ext.each(this.childNodes, function(childNode){
                    // Only change icon if it is in loading state (otherwise
					// structural soundness icon would be replaced)
                    if(childNode.getIcon().search(CheckNode.LOADING_STATUS) > -1){
                        childNode.setIcon(CheckNode.UNKNOWN_STATUS);
                    }
                });
                
                // Show errors
                Ext.each(errors, function(error){
                    this.insertBefore(new CheckNode({
                        icon: CheckNode.ERROR_STATUS,
                        text: error,
                        itemCls: 'error'
                    }), this.childNodes[0]);
                }.bind(this));
            },
            showOverlayWithStep: function(shapeIds){
                Ext.each(shapeIds, function(shapeId, index){
                    plugin.showOverlay(
                        plugin.facade.getCanvas().getChildShapeByResourceId(shapeId), 
                        {
                            fill: "#FB7E02"// orange
                        },
                        ORYX.Editor.graft("http://www.w3.org/2000/svg", null, ['text', {
                            "style": "font-size: 16px; font-weight: bold;"
                        }, (index + 1)+"."]),
                        "SE" // position in south east
                    );
                });
            },
            /*
             * incoming list of marking resourceId:#tokens
             */
            showOverlayMarking: function(shapeIdsToMark){
                Ext.each(shapeIdsToMark, function(shapeIdToMark, index){
                	var split = shapeIdToMark.split(":");
                    plugin.showOverlay(
                        plugin.facade.getCanvas().getChildShapeByResourceId(split[0].trim()), 
                        {
                            fill: "#FB7E02"// orange
                        },
                        ORYX.Editor.graft("http://www.w3.org/2000/svg", null, ['text', {
                            "style": "font-size: 16px; font-weight: bold;"
                        }, (split[1])]),
                        "SE" // position in south east
                    );
                });
            },
            showOverlay: function(shapes){
                if(shapes.length === 0)
                    return;

                if(! shapes[0] instanceof ORYX.Core.Node)
                    shapes = plugin.getChildShapesByResourceIds(shapes)
            
                plugin.showOverlay(
                    shapes, 
                    {
                        fill: "#FB7E02"// orange
                    }
                );
            }
        });
        CheckNode.UNKNOWN_STATUS = ORYX.PATH + 'images/soundness_checker/' + 'asterisk_yellow.png';
        CheckNode.ERROR_STATUS = ORYX.PATH + 'images/soundness_checker/' + 'exclamation.png';
        CheckNode.OK_STATUS = ORYX.PATH + 'images/soundness_checker/' + 'accept.png';
        CheckNode.LOADING_STATUS = ORYX.PATH + 'images/soundness_checker/' + 'loading.gif';
        
        var LivenessNode = Ext.extend(CheckNode, {
            constructor: function(config) {
//            config.qtip = '<b>AGEF Criteria</b>: Makes sure that any process instance that starts in the initial state will eventually reach the final state.';
            config.qtip = '<b>Weak Termination</b>: Makes sure that from any state, reachable from the initial state, the final state well eventually be reached.';
                // If any dead locks are detected, click to show one counter example.';            
                LivenessNode.superclass.constructor.apply(this, arguments);
            },
            clickHandler: function(node){
                node.reset();
                this.showOverlayMarking(this.marking);
            },
            update: function(res){
            	this.marking = res.counter?res.counter.split(","):[];
                this.setIcon(res.liveness? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
//                this.setText('The net is '+(res.liveness?  'not' : '')+' AGEF final place.');
                this.setText('There is '+(res.liveness?  'no' : 'a')+' marking from which one cannot reach the final state.');
            }
        });
        
        var BoundednessNode = Ext.extend(CheckNode, {
            constructor: function(config) {
                config.qtip = '<b>Boundedness Criteria</b>: There are not unbounded places in the net. If any unbounded places are detected, click to show one counter example.';
            
                BoundednessNode.superclass.constructor.apply(this, arguments);
            },
            clickHandler: function(node){
                node.reset();
                this.showOverlay(this.unboundedplaces);
            },
            update: function(res){
            	this.unboundedplaces  = res.unboundedplaces ;
            	if(res.boundedness){
            		this.unboundedplaces=[];
            	}
                
                this.setIcon(res.boundedness? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
                this.setText('There are ' + this.unboundedplaces.length +' unbounded places.');
            }
        });
        
        var DeadTransitionsNode = Ext.extend(CheckNode, {
            constructor: function(config) {
                config.qtip = '<b>No Dead Transitions Criteria</b>: Each transition can contribute to at least one process instance. Click to see all dead transitions.';
            
                DeadTransitionsNode.superclass.constructor.apply(this, arguments);
            },
            clickHandler: function(node){
                node.reset();
                
                this.showOverlay(this.deadTransitions);
            },
            update: function(res){
                this.deadTransitions = res.deadtransitions ;
              	if(res.quasiliveness ){
            		this.deadTransitions=[];
            	}
                
                this.setIcon(this.deadTransitions.length == 0 ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
                this.setText('There are ' + this.deadTransitions.length +' dead transitions.');
            }
        });
        
        var NotParticipatingTransitionsNode = Ext.extend(CheckNode, {
            constructor: function(config) {
                config.qtip = '<b>Transition Participation Criteria</b>: Each transition participates in at least one process instance that starts in the initial state and reaches the final state. Click to see all transitions not participating in any process instance.';
            
                NotParticipatingTransitionsNode.superclass.constructor.apply(this, arguments);
            },
            clickHandler: function(node){
                node.reset();
                
                this.showOverlay(this.notParticipatingTransitions);
            },
            update: function(res){
//            	res.transitioncover 
                this.notParticipatingTransitions = res.uncoveredtransitions;
                
                this.setIcon(this.notParticipatingTransitions.length == 0 ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
                this.setText('There are ' + this.notParticipatingTransitions.length +' transitions that cannot participate in a properly terminating firing sequence.');
            }
        });
        
        var service_tech_site_banner = new Object;
        service_tech_site_banner.html='<a href="http://www.service-technology.org/">'+
        '<img src="images/service_tech_site_banner.png" width="400" style="position: relative; left: 35px;"/></a>';
        
        this.checkerWindow = new Ext.Window({
            title: 'Soundness Checker powered by service-technology.org',
            autoScroll: true,
            width: '500',
            tbar: [
                {
                    text: 'Check', 
                    handler: function(){
                        this.checkerWindow.check();
                    }.bind(this)
                },
                {
                    text: 'Hide Errors', 
                    handler: function(){
                        this.checkerWindow.getTree().getRootNode().reset();
                    }.bind(this)
                },
                '->',
                {
                    text: 'Close', 
                    handler: function(){
                        this.checkerWindow.close();
                    }.bind(this)
                }
            ],
            getTree: function(){
                return this.items.get(0);
            },
            check: function(renderAll){
                this.prepareCheck(renderAll);
                this.checkSyntax(this.checkSoundness.bind(this), this.reRender.bind(this));
            },
            reRender: function(){
            	window.setTimeout(function(){
            		this.getResizeEl().beforeAction();
            		this.getResizeEl().sync(true);
            	}.bind(this), 70); 

            },
            prepareCheck: function(renderAll){// call with renderAll=true if
												// showing for the first time
                var root = this.getTree().getRootNode();
                
                root.reset();
                
                // Set loading status to all child nodes
                Ext.each(root.childNodes, function(childNode){
                    if(renderAll)// this expands all nodes so they're
									// rendered a first time
                        childNode.expand(true);
                    childNode.collapse(true); // collapse deeply
                    childNode.setIcon(CheckNode.LOADING_STATUS);
                });
            },
            checkSyntax: function(callback, finshedCallback){
                plugin.facade.raiseEvent({
                    type: ORYX.Plugins.SyntaxChecker.CHECK_FOR_ERRORS_EVENT,
                    onErrors: function(){
                        Ext.Msg.alert("Syntax Check", "Some syntax errors have been found, please correct them!")
                        this.turnLoadingIntoUnknownStatus();
                        finshedCallback();

                    }.bind(this),
                    onNoErrors: function(){
                        callback();
                        finshedCallback();
                    }
                });
            },
            // All child nodes with loading status get unknown status
            turnLoadingIntoUnknownStatus: function(){
                Ext.each(this.getTree().getRootNode().childNodes, function(childNode){
                    // Only change icon if it is in loading state (otherwise
					// structural soundness icon would be replaced)
                    if(childNode.getIcon().search(CheckNode.LOADING_STATUS) > -1){
                        childNode.setIcon(CheckNode.UNKNOWN_STATUS);
                    }
                });
            },
            checkSoundness: function(){
                var root = this.getTree().getRootNode();
                
                // Check for structural soundness (no server request needed and
				// return, if any has been found
                if(! root.findChild("id", "structuralSound").check()){
                    this.turnLoadingIntoUnknownStatus();
                    return;
                }
                var serialized_rdf = plugin.getRDFFromDOM();
    			if (!serialized_rdf.startsWith("<?xml")) {
    				serialized_rdf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + serialized_rdf;
    			}
                // Check other soundness criteria which needs server requests
                Ext.Ajax.request({
                    url: ORYX.CONFIG.ROOT_PATH + 'lola',
                    method: 'POST',
                    success: function(request){
                        var res = Ext.decode(request.responseText);
                        
                        root.showErrors(res.errors);
                        
                        if(res.errors.length === 0){
                            root.findChild("id", "sound").check(res);
                            root.findChild("id", "weakSound").check(res);
                            root.findChild("id", "relaxedSound").check(res);
                        }
                    }.bind(this),
                    failure: function(){

                    }.bind(this),
                    params: {
                        data: serialized_rdf
                    }
                });
                
            },
            items: [new Ext.tree.TreePanel({
                useArrows: true,
                autoScroll: true,
                rootVisible: false,
                animate: true,
                containerScroll: true,
                
                root: new CheckNode({
                    text: 'Checks',
                    id: 'source',
                    expanded: true
                }),
                listeners: {
                    render: function(treePanel){
                        var structuralSoundNode = new CheckNode({
                            text: 'Structural Sound (Workflow Net)',
                            id: 'structuralSound',
                            /* Returns false when any error has been found */
                            check: function(){
                                this.checkInitialNode.update();
                                this.checkFinalNode.update();
                                this.checkConnectedNode.update(this.checkInitialNode.initialNodes, this.checkFinalNode.finalNodes);
                                
                                if(this.checkInitialNode.hasErrors() || this.checkFinalNode.hasErrors() || this.checkConnectedNode.hasErrors()){
                                    this.setIcon(CheckNode.ERROR_STATUS);
                                    this.expand();
                                    return false;
                                } else {
                                    this.setIcon(CheckNode.OK_STATUS);
                                    return true;
                                }
                            },
                            checkInitialNode: new CheckNode({
                                qtip: 'There must be exactly one initial place, which is the only place without any incoming edges.',
                                update: function(){
                                    this.initialNodes = [];
                                    Ext.each(plugin.facade.getCanvas().getChildShapes(), function(shape){
                                        if(shape.getIncomingShapes().length == 0 && shape.getStencil().id().search(/Place/) > -1){
                                            this.initialNodes.push(shape);
                                        }
                                    }.bind(this));
                                    
                                    this.setText(this.initialNodes.length + ' initial places found.');
                                    
                                    this.setIcon(!this.hasErrors() ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
                                },
                                clickHandler: function(node){
                                    node.reset();
                                
                                    this.showOverlay(this.initialNodes);
                                },
                                hasErrors: function(){
                                    return this.initialNodes.length !== 1;
                                }
                            }),
                            checkFinalNode: new CheckNode({
                                qtip: 'There must be exactly one final place, which is the only place without any outgoing edges.',
                                update: function(){
                                    this.finalNodes = [];
                                    Ext.each(plugin.facade.getCanvas().getChildShapes(), function(shape){
                                        if(shape.getOutgoingShapes().length == 0 && shape.getStencil().id().search(/Place/) > -1){
                                            this.finalNodes.push(shape);
                                        }
                                    }.bind(this));
                                    
                                    this.setText(this.finalNodes.length + ' final places found.');
                                    
                                    this.setIcon(!this.hasErrors() ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
                                },
                                clickHandler: function(node){
                                    node.reset();
                                
                                    this.showOverlay(this.finalNodes);
                                },
                                hasErrors: function(){
                                    return this.finalNodes.length !== 1;
                                }
                            }),
                            checkConnectedNode: new CheckNode({
                                qtip: 'Each node in the process model is on the path from the initial node to the final node.',
                                update: function(initialNodes, finalNodes){
                                    // Step through without semantic knowledge
                                    if(initialNodes.length !== 1 || finalNodes.length !== 1){
                                        this.setText("There must be exactly one initial and final place to perform further checks!");
                                        this.setIcon(CheckNode.UNKNOWN_STATUS);
                                        return;
                                    }
                                    
                                    this.notParticipatingNodes = [];
                                    Ext.each(plugin.facade.getCanvas().getChildShapes(), function(shape){
                                        if(shape instanceof ORYX.Core.Node)
                                            this.notParticipatingNodes.push(shape);
                                    }.bind(this));
                                    
                                    this.passedNodes = [];
                                    
                                    this.findNotParticipatingNodes(initialNodes[0]);
                                    
                                    this.setText(this.notParticipatingNodes.length + ' nodes that aren\'t on any path from beginning to end found.');
                                    
                                    this.setIcon(!this.hasErrors() ? CheckNode.OK_STATUS : CheckNode.ERROR_STATUS);
                                },
                                clickHandler: function(node){
                                    node.reset();
                                
                                    this.showOverlay(this.notParticipatingNodes);
                                },
                                findNotParticipatingNodes: function(currentNode){
                                    this.passedNodes.push(currentNode);
                                    this.notParticipatingNodes.remove(currentNode);
                                    
                                    Ext.each(currentNode.getOutgoingShapes(), function(nextNode){
                                        if(!this.passedNodes.include(nextNode)){
                                            this.findNotParticipatingNodes(nextNode);
                                        };
                                    }.bind(this));
                                },
                                hasErrors: function(){
                                    return this.notParticipatingNodes.length !== 0;
                                }
                            })
                        });
                        structuralSoundNode.appendChild([
                            structuralSoundNode.checkInitialNode,
                            structuralSoundNode.checkFinalNode,
                            structuralSoundNode.checkConnectedNode
                        ]);
                    
                        var soundNode = new CheckNode({
                            text: 'Sound',
                            id: 'sound',
                            check: function(res){
                                if (res.soundness) {
                                    this.setIcon(CheckNode.OK_STATUS);
                                }
                                else {
                                    this.setIcon(CheckNode.ERROR_STATUS);
                                    this.expand();
                                }
                                
                                this.deadTransitionsNode.update(res);
                                this.boundednessNode.update(res);
                                this.livenessNode.update(res);
                            },
                            deadTransitionsNode: new DeadTransitionsNode({}),
                            boundednessNode: new BoundednessNode({}),
                            livenessNode: new LivenessNode({}),

                        });
                        soundNode.appendChild([
                            soundNode.deadTransitionsNode,
                            soundNode.boundednessNode,
                            soundNode.livenessNode
                        ]);
                        
                        var weakSoundNode = new CheckNode({
                            text: 'Weak Sound',
                            id: 'weakSound',
                            check: function(res){
                                if (res.weaksoundness) {
                                    this.setIcon(CheckNode.OK_STATUS);
                                }
                                else {
                                    this.setIcon(CheckNode.ERROR_STATUS);
                                    this.expand();
                                }

                                this.boundednessNode.update(res);
                                this.livenessNode.update(res);
                            },
                            boundednessNode: new BoundednessNode({}),
                            livenessNode: new LivenessNode({}),
                        });
                        weakSoundNode.appendChild([
                            weakSoundNode.boundednessNode,
                            weakSoundNode.livenessNode
                        ]);
                        
                        var relaxedSoundNode = new CheckNode({
                            text: 'Relaxed Sound',
                            id: 'relaxedSound',
                            check: function(res){
                                if (res.relaxedsoundness) {
                                    this.setIcon(CheckNode.OK_STATUS);
                                }
                                else {
                                    this.setIcon(CheckNode.ERROR_STATUS);
                                    this.expand();
                                }
                                this.deadTransitionsNode.update(res);
                                this.notParticipatingTransitionsNode.update(res);
                            },
                            deadTransitionsNode: new DeadTransitionsNode({}),

                            notParticipatingTransitionsNode: new NotParticipatingTransitionsNode({})
                        });
                        relaxedSoundNode.appendChild([
                            relaxedSoundNode.notParticipatingTransitionsNode,
							relaxedSoundNode.deadTransitionsNode

                        ]);
                        
                        treePanel.getRootNode().appendChild([structuralSoundNode, soundNode, weakSoundNode, relaxedSoundNode]);
                        
                    }
                }
            }),service_tech_site_banner],
            listeners: {
                close: function(window){
                    this.checkerWindow.getTree().getRootNode().reset();
                }.bind(this)
            }
        });


        this.checkerWindow.show();
        this.checkerWindow.check(true);
    }
});