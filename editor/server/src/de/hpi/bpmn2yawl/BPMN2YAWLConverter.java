package de.hpi.bpmn2yawl;

import de.hpi.yawl.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.Activity;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.EndErrorEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.IntermediateErrorEvent;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.IntermediateTimerEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.XOREventBasedGateway;

public class BPMN2YAWLConverter {

	private int nodeCount = 0;
	
	private HashMap<Decomposition, LinkedList<de.hpi.bpmn.Node>> loopingActivities = new HashMap<Decomposition, LinkedList<de.hpi.bpmn.Node>>();
	
	/**
	 * @param
	 */
	public BPMN2YAWLConverter() {
	}
	
	/**
	 */
	public String translate(BPMNDiagram diagram) {
		Container pool = diagram.getProcesses().get(0);	
		
		Model model = new Model("mymodel");
		// YAWL
		mapDecomposition(model, pool);		

		return model.writeToYAWL();
	}

	/**
	 * @param model
	 * @param graph
	 * @return
	 */
	private Decomposition mapDecomposition(Model model, Container graph) {
		de.hpi.yawl.Node exit = null;
		Decomposition dec = null;
		
		if (graph instanceof SubProcess) {
			dec = new Decomposition(((SubProcess)graph).getLabel().trim(), "false", "NetFactsType");
		} else
			dec = new Decomposition("OryxBPMNtoYAWL_Net", "true", "NetFactsType");
		
		model.addDecomposition(dec.getID(), dec);
		
		

		HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> nodeMap = new HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node>();
		LinkedList<Node> controlElements = new LinkedList<Node>();
		LinkedList<Activity> withEventHandlers = new LinkedList<Activity>();
		// Map process elements
		for (de.hpi.bpmn.Node node : graph.getChildNodes()) {
			de.hpi.yawl.Node ynode = mapProcessElement(model, dec, node, nodeMap);
			if (ynode == null)
				controlElements.add(node);
			else {
				if (ynode instanceof Condition && ((Condition)ynode).isOutputCondition())
					exit = ynode;
				else if (node instanceof Activity && ((Activity)node).getAttachedEvents().size() > 0) {
					withEventHandlers.add((Activity)node);
				}
			}
		}

		// Map control Elements
		for (Node node : controlElements)
			mapControlElement(model, dec, node, nodeMap);			
		
		if (exit == null)
			exit = dec.addOutputCondition("", "");
		// Map links
		linkYawlElements(exit, nodeMap, dec);

		// Event handlers
		for (Activity act : withEventHandlers)
			mapExceptions(model, dec, act, nodeMap);
		
		rewriteLoopingTasks(nodeMap);
		
		return dec;
	}

	/**
	 * Graph rewriting to deal with Looping Activities
	 * 
	 * @param nodeMap
	 */
	private void rewriteLoopingTasks(HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> nodeMap) {
		for (Decomposition d : loopingActivities.keySet()) {
			LinkedList<de.hpi.bpmn.Node> activities = loopingActivities.get(d);
			for (de.hpi.bpmn.Node act : activities) {
				Task task = (Task)nodeMap.get(act);
				
				if (task.getSplitType() == Task.SplitJoinType.AND) {
					// Factor out the split decorator to allow a self loop
					Task split = d.addTask(generateId(), "SplitTask", "XOR", "AND", "");

					for (FlowRelationship flow : task.getOutgoingEdges()){
						if (flow instanceof Edge){
							Edge edge = (Edge)flow;
							d.addNormalEdge(split, edge.getTarget(), false, "", 0);
						}
					}
					task.getOutgoingEdges().clear();
					
					d.addNormalEdge(task, split, false, "", 0);					
				}

				if (task.getJoinType() == Task.SplitJoinType.AND) {
					// Factor out the split decorator to allow a self loop
					Task join = d.addTask(generateId(), "JoinTask", "AND", "AND", "");

					for (FlowRelationship flow : task.getIncomingEdges()){
						if(flow instanceof Edge){
							Edge e = (Edge)flow;
							d.addNormalEdge(e.getSource(), join, false, "", 0);
						}
					}
						
					task.getIncomingEdges().clear();
					
					d.addNormalEdge(join, task, false, "", 0);					
				}
				
				// Self loop edge
				d.addNormalEdge(task, task, false, "", 1);
				task.setSplitType(Task.SplitJoinType.XOR);
				task.setJoinType(Task.SplitJoinType.XOR);
			}
		}
	}

	/**
	 * @param model 
	 * @param dec
	 * @param act
	 * @param nodeMap
	 */
	private void mapExceptions(Model model, Decomposition dec, Activity act,
			HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> nodeMap) {
		Task compTask = (Task) nodeMap.get(act);
		Task sourceTask = compTask;
		boolean splitAttached = false;
		LinkedList<IntermediateTimerEvent> timers = new LinkedList<IntermediateTimerEvent>();
		
		for (IntermediateEvent eventHandler : act.getAttachedEvents()) {
			if (eventHandler instanceof IntermediateTimerEvent)
				timers.add((IntermediateTimerEvent)eventHandler);
			else
				splitAttached = true;
		}
		
		if (splitAttached) {
			if (compTask.getOutgoingEdges().size() > 1) {
				// There is a split attached to the composite task
				// so, factor it out !
				Task newSplit = dec.addTask(generateId(), "newSplitTask", "XOR", "AND", "");
				
				for (FlowRelationship flow : compTask.getOutgoingEdges()){
					if (flow instanceof Edge){
						Edge edge = (Edge)flow;
						dec.addNormalEdge(newSplit, edge.getTarget(), false, "", 0);
					}
				}
				compTask.getOutgoingEdges().clear();
				
				dec.addNormalEdge(compTask, newSplit, false, "", 0);
				sourceTask = newSplit;
				newSplit.setSplitType(compTask.getSplitType());
			}
			compTask.setSplitType(Task.SplitJoinType.XOR);
		}
		
		// link eventHandler outgoing flow
		for (IntermediateEvent eventHandler : act.getAttachedEvents()) {
			if (eventHandler instanceof IntermediateErrorEvent) {
				mapErrorException(model, dec, nodeMap, compTask, sourceTask,
						(IntermediateErrorEvent) eventHandler);
			} else if (eventHandler instanceof IntermediateTimerEvent) {
				mapTimerException(model, dec, nodeMap, compTask, sourceTask,
						eventHandler, timers);				
			}
		}
		
		if (timers.size() > 0) {
			de.hpi.yawl.Node predecesor = null;
			boolean needsLinking = false;
			if (compTask.getIncomingEdges().size() > 1) {
				predecesor = dec.addTask(generateId(), "Task", "XOR", "AND", "");
				needsLinking = true;
			} else {
				predecesor = (de.hpi.yawl.Node)compTask.getIncomingEdges().get(0).getSource();
				
				if ((predecesor instanceof Condition)) {
					de.hpi.yawl.Node gw = dec.addTask(generateId(), "Task", "XOR", "AND", "");
					
					Edge edge = (Edge) predecesor.getOutgoingEdges().get(0);
					dec.removeEdge(edge);
					
					dec.addNormalEdge(predecesor, gw, false, "", 1);
					predecesor = gw;
					needsLinking = true;
				} else if ((predecesor.getOutgoingEdges().size() > 1 && ((Task)predecesor).getSplitType() != Task.SplitJoinType.AND)) {
					; // TODO: factor out a AND split
				}
			}
			
			for (IntermediateTimerEvent timer : timers) {
				Task timerTask = (Task)nodeMap.get(timer);
				compTask.getCancellationSet().add(timerTask);
				timerTask.getCancellationSet().add(compTask);
				for (IntermediateTimerEvent another : timers) {
					if (!timer.equals(another))
						timerTask.getCancellationSet().add((Task)nodeMap.get(another));
				}
				
				dec.addNormalEdge(predecesor, timerTask, false, "", 1);
			}

			if (needsLinking) {
				dec.addNormalEdge(predecesor, compTask, false, "", 1);
			}
		}
	}

	private void mapTimerException(Model model, Decomposition dec,
			HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> nodeMap, Task compTask, Task sourceTask,
			IntermediateEvent eventHandler, LinkedList<IntermediateTimerEvent> timers) {
		
		// TODO: I thought that an timer event should have had a Label (this is not the case).
		Task timer = dec.addTask(generateId("timer"), "TimerTask", "XOR", "AND", "");
		de.hpi.yawl.Node targetTask = nodeMap.get(eventHandler.getOutgoingSequenceFlows().get(0).getTarget());
		dec.addNormalEdge(timer, targetTask, false, "", 1);
		nodeMap.put(eventHandler, timer);
	}

	/**
	 * @param model
	 * @param dec
	 * @param actMap
	 * @param compTask
	 * @param sourceTask
	 * @param eventHandler
	 */
	private void mapErrorException(Model model, Decomposition dec,
			HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> actMap, Task compTask, Task sourceTask,
			IntermediateErrorEvent eventHandler) {
		
		// TODO: Error Exception handling
		//de.hpi.yawl.Node targetTask = actMap.get(eventHandler.getOutgoingEdges().get(0).getTarget());

		// PREDICATE & Mapping
		//String varName =  "EventName";//eventHandler.getName().replaceAll(" ", "_");
		//String predicate = String.format("/%s/%s_%s_exception/text()", dec.getID(), compTask.getID(), varName);
		//String tag = String.format("%s_%s_exception", compTask.getID(), varName);
		//String query = String.format("<%s>%s</%s>", tag, predicate, tag);

		//Edge newEdge = dec.addNormalEdge(sourceTask, targetTask, false, predicate, 1);

//		Variable local = factory.createVariable();
//		local.setName(compTask.getId() + "_" + varName + "_exception");
//		local.setType("boolean");
//		local.setNamespace("http://www.w3.org/2001/XMLSchema");
//		local.setInitialValue("false");
//		dec.getLocalVariables().add(local);
//
//		VarMapping mapping = factory.createVarMapping();
//		mapping.setQuery(query);
//		mapping.setMapsTo(local);
//		compTask.getCompletedMappings().add(mapping);
//		
//		// Add control flow variables ... to composite task decomposition
//		Variable localVariable = factory.createVariable();
//		localVariable.setName("_"+varName+"_exception");
//		localVariable.setType("boolean");
//		localVariable.setNamespace("http://www.w3.org/2001/XMLSchema");
//		compTask.getDecomposesTo().getLocalVariables().add(localVariable);
//		
//		Variable outputParam = factory.createVariable();
//		outputParam.setName("_"+varName+"_exception");
//		outputParam.setType("boolean");
//		outputParam.setNamespace("http://www.w3.org/2001/XMLSchema");
//		compTask.getDecomposesTo().getOutputParams().add(outputParam);
//		
//		for (Task exceptionTask : compTask.getDecomposesTo().getProcessControlElements()) {
//			if (exceptionTask.getName() != null && exceptionTask.getName().equals(eventHandler.getName())) {
//				String anotherQuery = String.format("<%s>true</%s>", localVariable.getName(), localVariable.getName());
//				VarMapping anotherMapping = factory.createVarMapping();
//				anotherMapping.setQuery(anotherQuery);
//				anotherMapping.setMapsTo(localVariable);
//				exceptionTask.getCompletedMappings().add(anotherMapping);
//				// Decomposition
//				Decomposition exceptionDec = factory.createDecomposition();
//				//exceptionDec.setId(exceptionTask.getId());
//				exceptionDec.setId(EcoreUtil.generateUUID());
//				exceptionDec.setIsRootNet(false);
//				exceptionDec.setXsiType("WebServiceGatewayFactsType");
//				exceptionTask.setDecomposesTo(exceptionDec);
//				model.getDecompositions().add(exceptionDec);
//				break;
//			}
//		}
	}


	/**
	 * @param model
	 * @param dec 
	 * @param node
	 * @param nodeMap
	 * @return
	 */
	private void mapControlElement(Model model, Decomposition dec, de.hpi.bpmn.Node node,
			HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> nodeMap) {		
		if (node instanceof Gateway)
			mapGateway(model, dec, node, nodeMap);
	}

	/**
	 * @param model
	 * @param dec
	 * @param node
	 * @param nodeMap
	 */
	private void mapGateway(Model model, Decomposition dec, de.hpi.bpmn.Node node,
			HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> nodeMap) {
		Task task = null;
		boolean split = false, join = false;
		if (node.getOutgoingSequenceFlows().size() > 1 && node.getIncomingSequenceFlows().size() > 1) {
			// Both roles
			task = dec.addTask(generateId(), "Task", "XOR", "AND", "");

			split = true; join = true;
		} else if (node.getOutgoingSequenceFlows().size() > 1) {
			// SPLIT role
			de.hpi.bpmn.Node predNode = (de.hpi.bpmn.Node) node.getIncomingSequenceFlows().get(0).getSource();
			de.hpi.yawl.Node predTask = nodeMap.get(predNode);
			
			if (predTask == null || (predTask.getOutgoingEdges() != null && predTask.getOutgoingEdges().size() > 1) ||
					predTask instanceof Condition)
				task = dec.addTask(generateId(), "Task", "XOR", "AND", "");
			else
				task = (Task)predTask;
			split = true;
		} else if (node.getIncomingSequenceFlows().size() > 1) {
			// JOIN role			
			de.hpi.bpmn.Node succNode = (de.hpi.bpmn.Node) node.getOutgoingSequenceFlows().get(0).getTarget();
			de.hpi.yawl.Node succTask = nodeMap.get(succNode);
			
			//succTask.getIncidentEdges()
			if (succTask == null || (succTask.getIncomingEdges() != null && succTask.getIncomingEdges().size() > 1) ||
					succTask instanceof Condition)
				task = dec.addTask(generateId(), "Task", "XOR", "AND", "");
			else
				task = (Task)succTask;
			join = true;
		}
		
		if (node instanceof XORDataBasedGateway) {
			if (split)
				task.setSplitType(Task.SplitJoinType.XOR);
			if (join)
				task.setJoinType(Task.SplitJoinType.XOR);
		} else if (node instanceof ANDGateway) {
			if (split)
				task.setSplitType(Task.SplitJoinType.AND);
			if (join)
				task.setJoinType(Task.SplitJoinType.AND);
		} else if (node instanceof ORGateway) {
			if (split)
				task.setSplitType(Task.SplitJoinType.OR);
			if (join)
				task.setJoinType(Task.SplitJoinType.OR);
		}

		nodeMap.put(node, task);
	}

	private String generateId() {
		return generateId("gw");
	}
	
	private String generateId(String prefix) {
		return prefix + (nodeCount++);
	}

	/**
	 * @param model
	 * @param node
	 * @param nodeMap
	 * @return
	 */
	private de.hpi.yawl.Node mapProcessElement(Model model, Decomposition dec, de.hpi.bpmn.Node node, HashMap<Node, de.hpi.yawl.Node> nodeMap) {
		de.hpi.yawl.Node ynode = null;
		
		if (node instanceof SubProcess)
			ynode = mapCompositeTask(model, dec, node, nodeMap);
		else if (node instanceof Activity)
			ynode = mapTask(model, dec, node, nodeMap);
		else if (node instanceof StartPlainEvent)
			ynode = mapInputCondition(model, dec, node, nodeMap);
		else if (node instanceof EndPlainEvent)
			ynode = mapOutputCondition(model, dec, node, nodeMap);
		else if (node instanceof XOREventBasedGateway)
			ynode = mapConditionFromEventBased(model, dec, node, nodeMap);
		else if (node instanceof IntermediateTimerEvent && node.getIncomingSequenceFlows().get(0).getSource() instanceof XOREventBasedGateway) {
			ynode = dec.addTask(generateId("timer"), "TaskMappedFromIntermediateTimerEvent", "XOR", "AND", "");
			nodeMap.put(node, ynode);
		} else if (node instanceof IntermediateMessageEvent && node.getIncomingSequenceFlows().get(0).getSource() instanceof XOREventBasedGateway) {
			ynode = dec.addTask(generateId("msg"), "TaskMappedFromIntermediateMessageEvent", "XOR", "AND", "");
			nodeMap.put(node, ynode);
		} else if (node instanceof EndErrorEvent)
			ynode = mapException(model, dec, node, nodeMap);
		return ynode;
	}

	/**
	 * @param exitTask
	 * @param nodeMap
	 * @param dec 
	 */
	private void linkYawlElements(de.hpi.yawl.Node exitTask, HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> nodeMap, Decomposition dec) {		
		Map<de.hpi.yawl.Node, Integer> counter = new HashMap<de.hpi.yawl.Node, Integer>();
		
		for (de.hpi.bpmn.Node node : nodeMap.keySet()) {
			if (node instanceof EndErrorEvent) {
				de.hpi.yawl.Node sourceTask = nodeMap.get(node);				
				dec.addNormalEdge(sourceTask, exitTask, false, "", 1);
				continue;
			}
			for (SequenceFlow edge : node.getOutgoingSequenceFlows()) {
				de.hpi.bpmn.Node target = (de.hpi.bpmn.Node) edge.getTarget();
				de.hpi.yawl.Node sourceTask = nodeMap.get(node);
				de.hpi.yawl.Node targetTask = nodeMap.get(target);
				
				if (sourceTask == null || targetTask == null)
					continue;
				
				if (!sourceTask.equals(targetTask) || (node instanceof Gateway && node == target)) {
					if (!counter.containsKey(sourceTask)) {
						counter.put(sourceTask, 0);
					}
					Integer order = counter.get(sourceTask) + 1;
					counter.put(sourceTask, order);
					
					dec.addNormalEdge(sourceTask, targetTask, false, "", order);
				}
			}
		}
	}

	/**
	 * @param model
	 * @param act
	 * @param actMap
	 * @return
	 */
	private de.hpi.yawl.Node mapInputCondition(Model model, Decomposition dec, de.hpi.bpmn.Node node,
			HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> nodeMap) {
		
		de.hpi.yawl.Node ynode = dec.addInputCondition(node.getId(), "inputCondition");
		nodeMap.put(node, ynode);
		
		return ynode;
	}

	/**
	 * @param model
	 * @param act
	 * @param actMap
	 * @return
	 */
	private de.hpi.yawl.Node mapOutputCondition(Model model, Decomposition dec, de.hpi.bpmn.Node node,
			HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> nodeMap) {
		
		de.hpi.yawl.Node ynode = dec.addOutputCondition(node.getId(), "outputCondition");
		nodeMap.put(node, ynode);
		
		return ynode;
	}

	/**
	 * @param model
	 * @param node
	 * @param nodeMap
	 * @return
	 */
	private de.hpi.yawl.Node mapConditionFromEventBased(Model model, Decomposition dec, de.hpi.bpmn.Node node,
			HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> nodeMap) {		
		de.hpi.yawl.Node task = dec.addCondition(generateId("EXorGW"), "Condition");
		nodeMap.put(node, task);
		return task;
	}

	/**
	 * @param model
	 * @param act
	 * @param actMap
	 * @return
	 */
	private de.hpi.yawl.Node mapException(Model model, Decomposition dec, de.hpi.bpmn.Node node,
			HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> nodeMap) {
		
		de.hpi.yawl.Node task = dec.addTask(generateId("ErrorEvent"), "TaskMappedFromErrorEvent", "XOR", "AND", "");
		nodeMap.put(node, task);
		
		return task;
	}

	/**
	 * @param model
	 * @param act
	 * @param actMap
	 * @return
	 */
	private de.hpi.yawl.Node mapTask(Model model, Decomposition dec, de.hpi.bpmn.Node node,
			HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> nodeMap) {
		de.hpi.yawl.Node task = mapTask(model, dec, node, false, null);		
		nodeMap.put(node, task);
		return task;
	}

	/**
	 * @param model
	 * @param node
	 * @param nodeMap
	 * @return
	 */
	private de.hpi.yawl.Node mapCompositeTask(Model model, Decomposition dec, Node node, HashMap<de.hpi.bpmn.Node, de.hpi.yawl.Node> nodeMap) {
		Task task = null;
		Decomposition subdec = null;
//		if (node.getEAnnotation("adhoc") != null)
//			subdec = mapSpecialDecomposition(model, (SubProcess)node);
//		else
		subdec = mapDecomposition(model, (SubProcess)node);
		
		task = (Task)mapTask(model, dec, node, true, subdec.getID());
		nodeMap.put(node, task);
		
		return task;
	}
//
//	/**
//	 * @param model
//	 * @param graph
//	 * @return
//	 */
//	private Decomposition mapSpecialDecomposition(Model model, Graph graph) {
//		Decomposition dec = factory.createDecomposition();
//		dec.setId(graph.getName() == null ? EcoreUtil.generateUUID() : graph.getName().replaceAll(" ", "_"));
//		//dec.setId(EcoreUtil.generateUUID());
//		dec.setIsRootNet(graph instanceof Pool);
//		dec.setXsiType("NetFactsType");
//		model.getDecompositions().add(dec);
//
//		Task inputCondition = factory.createTask();
//		inputCondition.setTaskType(TaskType.INPUT_CONDITION);
//		//inputCondition.setId("Node" + nodeCount++);
//		inputCondition.setId(EcoreUtil.generateUUID());
//		dec.getProcessControlElements().add(inputCondition);
//		
//		Task outputCondition = factory.createTask();
//		outputCondition.setTaskType(TaskType.OUTPUT_CONDITION);
//		//outputCondition.setId("Node" + nodeCount++);
//		outputCondition.setId(EcoreUtil.generateUUID());
//		dec.getProcessControlElements().add(outputCondition);
//
//		Task condition = factory.createTask();
//		condition.setTaskType(TaskType.CONDITION);
//		//condition.setId("Node" + nodeCount++);
//		condition.setId(EcoreUtil.generateUUID());
//		dec.getProcessControlElements().add(condition);
//
//		Task entry = factory.createTask();
//		//entry.setId("Node" + nodeCount++);
//		entry.setId(EcoreUtil.generateUUID());
//		entry.setSplitType(SplitType.AND_SPLIT);
//		dec.getProcessControlElements().add(entry);
//
//		Task exit = factory.createTask();
//		//exit.setId("Node" + nodeCount++);
//		exit.setId(EcoreUtil.generateUUID());
//		exit.setJoinType(JoinType.AND_JOIN);
//		dec.getProcessControlElements().add(exit);
//
//		Edge edge = factory.createEdge();
//		edge.setSource(inputCondition); edge.setTarget(entry);
//		inputCondition.getOutgoingSequenceFlows().add(edge); entry.getIncomingSequenceFlows().add(edge);
//		dec.getEdges().add(edge);
//		
//		edge = factory.createEdge();
//		edge.setSource(exit); edge.setTarget(outputCondition);
//		exit.getOutgoingSequenceFlows().add(edge); outputCondition.getIncomingSequenceFlows().add(edge);
//		dec.getEdges().add(edge);
//
//		edge = factory.createEdge();
//		edge.setSource(entry); edge.setTarget(condition);
//		entry.getOutgoingSequenceFlows().add(edge); condition.getIncomingSequenceFlows().add(edge);
//		dec.getEdges().add(edge);
//
//		edge = factory.createEdge();
//		edge.setSource(condition); edge.setTarget(exit);
//		condition.getOutgoingSequenceFlows().add(edge); exit.getIncomingSequenceFlows().add(edge);
//		dec.getEdges().add(edge);
//		
//		// Map process elements
//		for (Vertex vertex : graph.getVertices()) {
//			Activity act = (Activity)vertex;
//			
//			if (vertex.getOutgoingSequenceFlows().size() > 0 || vertex.getIncomingSequenceFlows().size() > 0)
//				return null;
//			
//			Task task = factory.createTask();
//			task.setName(act.getName());
//			//task.setId(act.getName() == null ? "Node" + (nodeCount++) : act.getName().replaceAll(" ", "_"));
//			task.setId(EcoreUtil.generateUUID());
//			task.setJoinType(JoinType.AND_JOIN); task.setSplitType(SplitType.AND_SPLIT);
//			dec.getProcessControlElements().add(task);
//			
//			// entry -> task
//			edge = factory.createEdge();
//			edge.setSource(entry); edge.setTarget(task);
//			entry.getOutgoingSequenceFlows().add(edge); task.getIncomingSequenceFlows().add(edge);
//			dec.getEdges().add(edge);
//			// condition -> task
//			edge = factory.createEdge();
//			edge.setSource(condition); edge.setTarget(task);
//			condition.getOutgoingSequenceFlows().add(edge); task.getIncomingSequenceFlows().add(edge);
//			dec.getEdges().add(edge);
//			// task -> exit
//			edge = factory.createEdge();
//			edge.setSource(task); edge.setTarget(exit);
//			task.getOutgoingSequenceFlows().add(edge); exit.getIncomingSequenceFlows().add(edge);
//			dec.getEdges().add(edge);
//			// task -> condition
//			edge = factory.createEdge();
//			edge.setSource(task); edge.setTarget(condition);
//			task.getOutgoingSequenceFlows().add(edge); condition.getIncomingSequenceFlows().add(edge);
//			dec.getEdges().add(edge);
//		}
//		return dec;
//	}
//	
	/**
	 * @param node
	 * @param isComposite
	 * @return
	 */
	private de.hpi.yawl.Node mapTask(Model model, Decomposition dec, Node node, boolean isComposite, String subdecId) {
		de.hpi.yawl.Node task = null;
		
		if (isComposite)
			task = dec.addTask(node.getLabel(), node.getLabel(), "XOR", "AND", subdecId);
		else
			task = dec.addTask(node.getLabel(), node.getLabel(), "XOR", "AND", "");
		
		// TODO: Multiple Instances
		if (((Activity)node).getLoopType() == Activity.LoopType.Multiinstance) {
			//de.hpi.yawl.Task ytask = (de.hpi.yawl.Task) task;
		}
		
//		if (node.getAssociations().size() > 0 && node.getAssociations().get(0).eContainer() instanceof TextAnnotation) {
//			TextAnnotation multi = (TextAnnotation)node.getAssociations().get(0).eContainer();
//			StringTokenizer tokenizer = new StringTokenizer(multi.getName(), ",");
//			if (tokenizer.countTokens() == 4) {
//				MultiInstanceParam param = factory.createMultiInstanceParam();
//				param.setMinimum(Integer.valueOf(tokenizer.nextToken()));
//				param.setMaximum(Integer.valueOf(tokenizer.nextToken()));
//				param.setThreshold(Integer.valueOf(tokenizer.nextToken()));
//				param.setCreationMode(tokenizer.nextToken().equals("static") ? CreationMode.STATIC : CreationMode.DYNAMIC);
//				task.setMultiInstanceParam(param);
//				
//				Variable local = factory.createVariable();
//				local.setName(task.getId() + "_input");
//				local.setType("string");
//				local.setNamespace("http://www.w3.org/2001/XMLSchema");
//				dec.getInputParams().add(local);
//				
//				// Decomposition
//				Decomposition subdec = factory.createDecomposition();
//				//exceptionDec.setId(exceptionTask.getId());
//				subdec.setId(EcoreUtil.generateUUID());
//				subdec.setIsRootNet(false);
//				subdec.setXsiType("WebServiceGatewayFactsType");
//				task.setDecomposesTo(subdec);
//				model.getDecompositions().add(subdec);
//				
//				Variable inputParam = factory.createVariable();
//				inputParam.setName(task.getId() + "_input");
//				inputParam.setType("string");
//				inputParam.setNamespace("http://www.w3.org/2001/XMLSchema");
//				task.getDecomposesTo().getInputParams().add(inputParam);
//				
//				MIDataInput miDataInput = factory.createMIDataInput();
//				miDataInput.setExpression("/" + dec.getId() + "/" + local.getName());
//				miDataInput.setSplittingExpression("splitting query goes here");
//				miDataInput.setFormalInputParam(local.getName());
//				task.setMiDataInput(miDataInput);
//				
//				MIDataOutput miDataOutput = factory.createMIDataOutput();
//				miDataOutput.setFormalOutputExpression("/" + dec.getId() + "/" + local.getName());
//				miDataOutput.setOutputJoiningExpression("aggregation query goes");
//				miDataOutput.setResultAppliedToLocalVariable(local.getName());
//				task.setMiDataOutput(miDataOutput);
//			}
//		}
		
		if (((Activity)node).getLoopType() == Activity.LoopType.Standard) {
			if (!loopingActivities.containsKey(dec))
				loopingActivities.put(dec, new LinkedList<Node>());
			loopingActivities.get(dec).add(node);
		}
		return task;
	}	
}
