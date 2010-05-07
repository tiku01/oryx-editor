/***************************************
 * Copyright (c) 2008
 * Helen Kaltegaertner
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
 ****************************************/

package de.hpi.tbpm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.JSONBuilder;
import org.oryxeditor.server.diagram.StencilSet;
import org.oryxeditor.server.diagram.StencilType;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.data_object.DataObject;
import de.hpi.bpmn2_0.model.diagram.DataObjectShape;
import de.hpi.bpmn2_0.model.diagram.EventShape;
import de.hpi.bpmn2_0.model.diagram.GatewayShape;
import de.hpi.bpmn2_0.model.diagram.LaneCompartment;
import de.hpi.bpmn2_0.model.diagram.ProcessDiagram;
import de.hpi.bpmn2_0.model.diagram.activity.ActivityShape;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.event.IntermediateThrowEvent;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.gateway.ExclusiveGateway;
import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;
import de.hpi.diagram.OryxUUID;
import de.hpi.util.Bounds;

public class BPMNBuilder {

	private ArrayList<PolygonStructure> tasks;
	private ArrayList<PolygonStructure> gateways;
	private ArrayList<PolygonStructure> dataObjects;
	private ArrayList<CircleStructure> events;	
	
	private Definitions defs;
	private Process process;
	private ProcessDiagram diagram;
	private LaneCompartment lane;
	
	private String rootDir;
	private double ratio;
	private final int DEFAULT_WIDTH = 100;
	
	public BPMNBuilder() {
		
		this.ratio = 1;
		
		this.tasks = new ArrayList<PolygonStructure>();
		this.gateways = new ArrayList<PolygonStructure>();
		this.dataObjects = new ArrayList<PolygonStructure>();
		this.events = new ArrayList<CircleStructure>();
		
	}

	public BPMNBuilder(ArrayList<PolygonStructure> tasks,
			ArrayList<PolygonStructure> gateways,
			ArrayList<PolygonStructure> dataObjects,
			ArrayList<CircleStructure> events, String rootDir) {		
		
		this.rootDir = rootDir;
		
		// assuming the default size of a task is 100px (DEFAULT_WIDTH)
		// ratio is the resize factor the for further object creation
		if (tasks.size() > 0 ) {
			this.ratio = this.DEFAULT_WIDTH
				/ tasks.get(0).getPolygon().getBounds2D().getWidth();
		}
		else 
			this.ratio = 1;
			
		Comparator<Object> c = new XComparator();
		
		Object[] tmp = tasks.toArray();
		Arrays.sort(tmp, c);
		this.tasks = new ArrayList<PolygonStructure>();
		for ( int i = 0; i < tmp.length; i++)
			this.tasks.add( (PolygonStructure) tmp[i]);
		
		tmp = gateways.toArray();
		Arrays.sort(tmp, c);		
		this.gateways = new ArrayList<PolygonStructure>();
		for ( int i = 0; i < tmp.length; i++)
			this.gateways.add( (PolygonStructure) tmp[i]);
		
		tmp = dataObjects.toArray();
		Arrays.sort(tmp, c);		
		this.dataObjects = new ArrayList<PolygonStructure>();
		for ( int i = 0; i < tmp.length; i++)
			this.dataObjects.add( (PolygonStructure) tmp[i]);
		
		tmp = events.toArray();
		Arrays.sort(tmp, c);
		this.events = new ArrayList<CircleStructure>();
		for ( int i = 0; i < tmp.length; i++)
			this.events.add( (CircleStructure) tmp[i]);
		
	}

	public String buildDiagram() {
		
		this.defs = new Definitions();
		this.process = new Process();
		this.process.setId(OryxUUID.generate());
		this.defs.getRootElement().add(this.process);
		
		this.diagram = new ProcessDiagram();
		this.diagram.setId(OryxUUID.generate());
		this.defs.getDiagram().add(this.diagram);
		
		this.lane = new LaneCompartment();
		this.lane.setIsVisible(false);
		this.lane.setId(OryxUUID.generate());
		this.diagram.getLaneCompartment().add(this.lane);	
		
		this.buildEvents();
		this.buildTasks();
//		this.buildGateways();
//		this.buildDataObjects();
		
		BPMN2DiagramConverter converter = new BPMN2DiagramConverter("oryx/");
		List<Diagram> dia = converter.getDiagramFromBpmn20(this.defs);
		String json = "";
		try {
			json = JSONBuilder.parseModeltoString(dia.get(0));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * the most left events will be start plain events
	 * the most right ones will be end plain  events
	 * all events in between will be intermediate plain events
	 * 
	 */
	public void buildEvents(){		
		if (this.events.size() == 0)
			return;
		int variance = (int) (100 * this.ratio);
		int referenceStart = this.events.get(0).getX() + variance;
		int referenceEnd = this.events.get( this.events.size() - 1 ).getX() - variance;
		String currentType = "start";
		
		int firstIntermediate = 0;
		int firstEnd = this.events.size() - 1;
		// determine startevents
		// all events that are less than 100px away from the first event will be start events
		
		for ( int i = 0; i < this.events.size(); i++ ){
			
			Event e;
			// last start event not found yet
			if ( currentType.equals("start") ) {
				
				if ( this.events.get(i).getX() < referenceStart ){
					e = new StartEvent();
				}
				else {
					// first intermediate event is found 
					e = new IntermediateThrowEvent();
					currentType = "intermediate";
					
				}
			}
			else if (currentType.equals("intermediate")){
				if ( this.events.get(i).getX() < referenceEnd ){
					e = new IntermediateThrowEvent();
				}
				else {
					// first end event is found 
					e = new EndEvent();
					currentType = "end";
				}
			}
			else {
				e = new EndEvent();
			}
			e.setId(OryxUUID.generate());
			this.process.getFlowElement().add(e);
			
			// TODO maybe change bounds (center or top left??)
			EventShape es = new EventShape();
			es.setEventRef(e);
			es.setX(this.events.get(i).getX() * this.ratio);
			es.setY(this.events.get(i).getY() * this.ratio);
			es.setWidth(this.events.get(i).getWidth() * this.ratio);
			es.setHeight(this.events.get(i).getHeight() * this.ratio);
			this.lane.getBpmnShape().add(es);
				
		}
	
	}
	
	public void buildTasks(){
		if (this.tasks.size() == 0)
			return;
		for ( int i = 0; i < this.tasks.size(); i++ ){
			Task t = new Task();
			t.setId(OryxUUID.generate());
			this.process.getFlowElement().add(t);
			
			ActivityShape a = new ActivityShape();
			a.setActivityRef(t);
			a.setX(this.tasks.get(i).getX() * this.ratio);
			a.setY(this.tasks.get(i).getY() * this.ratio);
			a.setWidth(this.tasks.get(i).getWidth() * this.ratio);
			a.setHeight(this.tasks.get(i).getHeight() * this.ratio);
			this.lane.getBpmnShape().add(a);
		}
	}
	
	public void buildDataObjects(){
		if (this.dataObjects.size() == 0)
			return;
		for ( int i = 0; i < this.dataObjects.size(); i++ ){
			DataObject d = new DataObject();
			d.setId(OryxUUID.generate());
			this.process.getFlowElement().add(d);
			
			DataObjectShape ds = new DataObjectShape();
			ds.setDataObjectRef(d);
			ds.setX(this.dataObjects.get(i).getX() * this.ratio);
			ds.setY(this.dataObjects.get(i).getY() * this.ratio);
			ds.setWidth(this.dataObjects.get(i).getWidth() * this.ratio);
			ds.setHeight(this.dataObjects.get(i).getHeight() * this.ratio);
			this.lane.getBpmnShape().add(ds);
		}
	}
	
	public void buildGateways(){
		if (this.gateways.size() == 0)
			return;
		for ( int i = 0; i < this.gateways.size(); i++ ){
			ExclusiveGateway g = new ExclusiveGateway();
			g.setId(OryxUUID.generate());
			this.process.getFlowElement().add(g);
			
			GatewayShape gs = new GatewayShape();		
			gs.setGatewayRef(g);
			gs.setX(this.gateways.get(i).getX() * this.ratio);
			gs.setY(this.gateways.get(i).getY() * this.ratio);
			gs.setWidth(this.gateways.get(i).getWidth() * this.ratio);
			gs.setHeight(this.gateways.get(i).getHeight() * this.ratio);
			this.lane.getBpmnShape().add(gs);
		}
	}
	
	
	
}
