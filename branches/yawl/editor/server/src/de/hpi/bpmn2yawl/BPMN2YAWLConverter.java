package de.hpi.bpmn2yawl;

import de.hpi.yawl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.Activity;
import de.hpi.bpmn.Assignment;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.EndErrorEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.IntermediateErrorEvent;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.IntermediatePlainEvent;
import de.hpi.bpmn.IntermediateTimerEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.Property;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.XOREventBasedGateway;
import de.hpi.bpmn.SequenceFlow.ConditionType;

public class BPMN2YAWLConverter {

	private int nodeCount = 0;
	
	private HashMap<YDecomposition, LinkedList<Node>> loopingActivities = new HashMap<YDecomposition, LinkedList<Node>>();
	
	/**
	 * @param
	 */
	public BPMN2YAWLConverter() {
	}
	
	/**
	 */
	public String translate(BPMNDiagram diagram) {
		Container pool = diagram.getProcesses().get(0);	
		
		YModel model = new YModel("mymodel");
		// YAWL
		mapDecomposition(model, pool);		

		return model.writeToYAWL();
	}

	/**
	 * @param model
	 * @param graph
	 * @return
	 */
	private YDecomposition mapDecomposition(YModel model, Container graph) {
		de.hpi.yawl.YNode exit = null;
		YDecomposition dec = null;
		
		if (graph instanceof SubProcess) {
			dec = new YDecomposition(((SubProcess)graph).getLabel().trim(), "false", "NetFactsType");
		} else
			dec = new YDecomposition("OryxBPMNtoYAWL_Net", "true", "NetFactsType");
		
		model.addDecomposition(dec.getID(), dec);
		
		

		HashMap<Node, YNode> nodeMap = new HashMap<Node, YNode>();
		LinkedList<Node> controlElements = new LinkedList<Node>();
		LinkedList<Activity> withEventHandlers = new LinkedList<Activity>();
		// Map process elements
		for (Node node : graph.getChildNodes()) {
			YNode ynode = mapProcessElement(model, dec, node, nodeMap);
			if (ynode == null)
				controlElements.add(node);
			else {
				if (ynode instanceof YCondition && ((YCondition)ynode).isOutputCondition())
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
	private void rewriteLoopingTasks(HashMap<Node, YNode> nodeMap) {
		for (YDecomposition d : loopingActivities.keySet()) {
			LinkedList<Node> activities = loopingActivities.get(d);
			for (Node act : activities) {
				YTask task = (YTask)nodeMap.get(act);
				
				if (task.getSplitType() == YTask.SplitJoinType.AND) {
					// Factor out the split decorator to allow a self loop
					YTask split = d.addTask(generateId(), "SplitTask", "XOR", "AND", null);

					for (YFlowRelationship flow : task.getOutgoingEdges()){
						if (flow instanceof YEdge){
							YEdge edge = (YEdge)flow;
							d.addNormalEdge(split, edge.getTarget(), false, "", 0);
						}
					}
					task.getOutgoingEdges().clear();
					
					d.addNormalEdge(task, split, false, "", 0);					
				}

				if (task.getJoinType() == YTask.SplitJoinType.AND) {
					// Factor out the split decorator to allow a self loop
					YTask join = d.addTask(generateId(), "JoinTask", "AND", "AND", null);

					for (YFlowRelationship flow : task.getIncomingEdges()){
						if(flow instanceof YEdge){
							YEdge e = (YEdge)flow;
							d.addNormalEdge(e.getSource(), join, false, "", 0);
						}
					}
						
					task.getIncomingEdges().clear();
					
					d.addNormalEdge(join, task, false, "", 0);					
				}
				
				// Self loop edge
				d.addNormalEdge(task, task, false, "", 1);
				task.setSplitType(YTask.SplitJoinType.XOR);
				task.setJoinType(YTask.SplitJoinType.XOR);
			}
		}
	}

	/**
	 * @param model 
	 * @param dec
	 * @param act
	 * @param nodeMap
	 */
	private void mapExceptions(YModel model, YDecomposition dec, Activity act,
			HashMap<Node, YNode> nodeMap) {
		YTask compTask = (YTask) nodeMap.get(act);
		YTask sourceTask = compTask;
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
				YTask newSplit = dec.addTask(generateId(), "newSplitTask", "XOR", "AND", null);
				
				for (YFlowRelationship flow : compTask.getOutgoingEdges()){
					if (flow instanceof YEdge){
						YEdge edge = (YEdge)flow;
						dec.addNormalEdge(newSplit, edge.getTarget(), false, "", 0);
					}
				}
				compTask.getOutgoingEdges().clear();
				
				dec.addNormalEdge(compTask, newSplit, false, "", 0);
				sourceTask = newSplit;
				newSplit.setSplitType(compTask.getSplitType());
			}
			compTask.setSplitType(YTask.SplitJoinType.XOR);
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
			YNode predecesor = null;
			boolean needsLinking = false;
			if (compTask.getIncomingEdges().size() > 1) {
				predecesor = dec.addTask(generateId(), "Task", "XOR", "AND", null);
				needsLinking = true;
			} else {
				predecesor = (YNode)compTask.getIncomingEdges().get(0).getSource();
				
				if ((predecesor instanceof YCondition)) {
					YNode gw = dec.addTask(generateId(), "Task", "XOR", "AND", null);
					
					YEdge edge = (YEdge) predecesor.getOutgoingEdges().get(0);
					dec.removeEdge(edge);
					
					dec.addNormalEdge(predecesor, gw, false, "", 1);
					predecesor = gw;
					needsLinking = true;
				} else if ((predecesor.getOutgoingEdges().size() > 1 && ((YTask)predecesor).getSplitType() != YTask.SplitJoinType.AND)) {
					; // TODO: factor out a AND split
				}
			}
			
			for (IntermediateTimerEvent timer : timers) {
				YTask timerTask = (YTask)nodeMap.get(timer);
				compTask.getCancellationSet().add(timerTask);
				timerTask.getCancellationSet().add(compTask);
				for (IntermediateTimerEvent another : timers) {
					if (!timer.equals(another))
						timerTask.getCancellationSet().add((YTask)nodeMap.get(another));
				}
				
				dec.addNormalEdge(predecesor, timerTask, false, "", 1);
			}

			if (needsLinking) {
				dec.addNormalEdge(predecesor, compTask, false, "", 1);
			}
		}
	}

	private void mapTimerException(YModel model, YDecomposition dec,
			HashMap<Node, YNode> nodeMap, YTask compTask, YTask sourceTask,
			IntermediateEvent eventHandler, LinkedList<IntermediateTimerEvent> timers) {
		
		YTask timer = dec.addTask(generateId("timer"), "TimerTask", "XOR", "AND", null);
		YNode targetTask = nodeMap.get(eventHandler.getOutgoingSequenceFlows().get(0).getTarget());
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
	private void mapErrorException(YModel model, YDecomposition dec,
			HashMap<Node, YNode> actMap, YTask compTask, YTask sourceTask,
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
	private void mapControlElement(YModel model, YDecomposition dec, Node node,
			HashMap<Node, YNode> nodeMap) {		
		if (node instanceof Gateway)
			mapGateway(model, dec, node, nodeMap);
	}

	/**
	 * @param model
	 * @param dec
	 * @param node
	 * @param nodeMap
	 */
	private void mapGateway(YModel model, YDecomposition dec, Node node,
			HashMap<Node, YNode> nodeMap) {
		YTask task = null;
		boolean split = false, join = false;
		if (node.getOutgoingSequenceFlows().size() > 1 && node.getIncomingSequenceFlows().size() > 1) {
			// Both roles
			task = dec.addTask(generateId(), "Task", "XOR", "AND", null);

			split = true; join = true;
		} else if (node.getOutgoingSequenceFlows().size() > 1) {
			// SPLIT role
			Node predNode = (Node) node.getIncomingSequenceFlows().get(0).getSource();
			YNode predTask = nodeMap.get(predNode);
			
			if (predTask == null || (predNode.getOutgoingEdges() != null && predNode.getOutgoingEdges().size() > 1) ||
					predTask instanceof YCondition)
				task = dec.addTask(generateId(), "Task", "XOR", "AND", null);
			else
				task = (YTask)predTask;
			split = true;
		} else if (node.getIncomingSequenceFlows().size() > 1) {
			// JOIN role			
			Node succNode = (Node) node.getOutgoingSequenceFlows().get(0).getTarget();
			YNode succTask = nodeMap.get(succNode);
			
			//succTask.getIncidentEdges()
			if (succTask == null || (succNode.getIncomingEdges() != null && succNode.getIncomingEdges().size() > 1) ||
					succTask instanceof YCondition)
				task = dec.addTask(generateId(), "Task", "XOR", "AND", null);
			else
				task = (YTask)succTask;
			join = true;
		}
		
		if (node instanceof XORDataBasedGateway) {
			if (split)
				task.setSplitType(YTask.SplitJoinType.XOR);
			if (join)
				task.setJoinType(YTask.SplitJoinType.XOR);
		} else if (node instanceof ANDGateway) {
			if (split)
				task.setSplitType(YTask.SplitJoinType.AND);
			if (join)
				task.setJoinType(YTask.SplitJoinType.AND);
		} else if (node instanceof ORGateway) {
			if (split)
				task.setSplitType(YTask.SplitJoinType.OR);
			if (join)
				task.setJoinType(YTask.SplitJoinType.OR);
		}

		if(isLoopingActivityBySequenceFlow(node)){
			if (!loopingActivities.containsKey(dec))
				loopingActivities.put(dec, new LinkedList<Node>());
			loopingActivities.get(dec).add(node);
		}
		
		nodeMap.put(node, task);
	}
	
	private boolean isLoopingActivityBySequenceFlow(Node node){
		boolean isLoop = false;
		
		for(SequenceFlow firstSequenceFlow: node.getOutgoingSequenceFlows()){
			Node firstSuccessorNode = (Node) firstSequenceFlow.getTarget();
			for(SequenceFlow secondSequenceFlow: firstSuccessorNode.getOutgoingSequenceFlows()){
				Node secondSuccessorNode = (Node) secondSequenceFlow.getTarget();
				for(SequenceFlow thirdSequenceFlow: secondSuccessorNode.getOutgoingSequenceFlows()){
					Node thirdSuccessorNode = (Node) thirdSequenceFlow.getTarget();
					
					if(thirdSuccessorNode == node)
						isLoop = true;
				}
			}
		}
		
		return isLoop;
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
	private YNode mapProcessElement(YModel model, YDecomposition dec, Node node, HashMap<Node, YNode> nodeMap) {
		YNode ynode = null;
		
		if (node instanceof SubProcess)
			ynode = mapCompositeTask(model, dec, (Activity)node, nodeMap);
		else if (node instanceof Activity)
			ynode = mapTask(model, dec, (Activity)node, nodeMap);
		else if (node instanceof StartPlainEvent)
			ynode = mapInputCondition(model, dec, node, nodeMap);
		else if (node instanceof EndPlainEvent)
			ynode = mapOutputCondition(model, dec, node, nodeMap);
		else if (node instanceof XOREventBasedGateway)
			ynode = mapConditionFromEventBased(model, dec, node, nodeMap);
		else if (node instanceof IntermediateTimerEvent && node.getIncomingSequenceFlows().get(0).getSource() instanceof XOREventBasedGateway) {
			ynode = dec.addTask(generateId("timer"), "TaskMappedFromIntermediateTimerEvent", "XOR", "AND", null);
			nodeMap.put(node, ynode);
		} else if (node instanceof IntermediateMessageEvent && node.getIncomingSequenceFlows().get(0).getSource() instanceof XOREventBasedGateway) {
			ynode = dec.addTask(generateId("msg"), "TaskMappedFromIntermediateMessageEvent", "XOR", "AND", null);
			nodeMap.put(node, ynode);
		} else if (node instanceof IntermediateEvent && node.getIncomingSequenceFlows().get(0).getSource() instanceof Gateway) {
			ynode = dec.addTask(generateId("intermediate"), "TaskMappedFromIntermediateEvent", "XOR", "AND", null);
			nodeMap.put(node, ynode);
		} else if (node instanceof IntermediatePlainEvent && node.getOutgoingSequenceFlows().get(0).getTarget() instanceof Gateway) {
			ynode = dec.addCondition(generateId("plain"), "ConditionMappedFromIntermediatePlainEvent");
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
	private void linkYawlElements(YNode exitTask, HashMap<Node, YNode> nodeMap, YDecomposition dec) {		
		Map<YNode, Integer> counter = new HashMap<YNode, Integer>();
		
		for (Node node : nodeMap.keySet()) {
			if (node instanceof EndErrorEvent) {
				YNode sourceTask = nodeMap.get(node);				
				dec.addNormalEdge(sourceTask, exitTask, false, "", 1);
				continue;
			}
			for (SequenceFlow edge : node.getOutgoingSequenceFlows()) {
				String predicate = "";
				
				Node target = (Node) edge.getTarget();
				YNode sourceTask = nodeMap.get(node);
				YNode targetTask = nodeMap.get(target);
				
				if (sourceTask == null || targetTask == null)
					continue;
				
				if (!sourceTask.equals(targetTask) || (node instanceof Gateway && node == target)) {
					if (!counter.containsKey(sourceTask)) {
						counter.put(sourceTask, 0);
					}
					Integer order = counter.get(sourceTask) + 1;
					counter.put(sourceTask, order);
					
					if(edge.getConditionType() == ConditionType.EXPRESSION){
						predicate = edge.getConditionExpression();
					} else if(edge.getConditionType() == ConditionType.DEFAULT){
						predicate = "true()";
					}
					
					dec.addNormalEdge(sourceTask, targetTask, false, predicate, order);
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
	private YNode mapInputCondition(YModel model, YDecomposition dec, Node node,
			HashMap<Node, YNode> nodeMap) {
		
		YNode ynode = dec.addInputCondition(node.getId(), "inputCondition");
		nodeMap.put(node, ynode);
		
		return ynode;
	}

	/**
	 * @param model
	 * @param act
	 * @param actMap
	 * @return
	 */
	private YNode mapOutputCondition(YModel model, YDecomposition dec, Node node,
			HashMap<Node, YNode> nodeMap) {
		
		YNode ynode = dec.addOutputCondition(node.getId(), "outputCondition");
		nodeMap.put(node, ynode);
		
		return ynode;
	}

	/**
	 * @param model
	 * @param node
	 * @param nodeMap
	 * @return
	 */
	private YNode mapConditionFromEventBased(YModel model, YDecomposition dec, Node node,
			HashMap<Node, YNode> nodeMap) {
			
		YNode task = dec.addCondition(generateId("EXorGW"), "Condition");
		nodeMap.put(node, task);
		return task;
	}

	/**
	 * @param model
	 * @param act
	 * @param actMap
	 * @return
	 */
	private YNode mapException(YModel model, YDecomposition dec, Node node,
			HashMap<Node, YNode> nodeMap) {
		
		YNode task = dec.addTask(generateId("ErrorEvent"), "TaskMappedFromErrorEvent", "XOR", "AND", null);
		nodeMap.put(node, task);
		
		return task;
	}

	/**
	 * @param model
	 * @param act
	 * @param actMap
	 * @return
	 */
	private YNode mapTask(YModel model, YDecomposition dec, Activity activity,
			HashMap<Node, YNode> nodeMap) {
		YNode task = mapTask(model, dec, activity, false, null);		
		nodeMap.put(activity, task);
		return task;
	}

	/**
	 * @param model
	 * @param node
	 * @param nodeMap
	 * @return
	 */
	private YNode mapCompositeTask(YModel model, YDecomposition dec, Activity activity, HashMap<Node, YNode> nodeMap) {
		YTask task = null;
		YDecomposition subdec = null;
//		if (node.getEAnnotation("adhoc") != null)
//			subdec = mapSpecialDecomposition(model, (SubProcess)node);
//		else
		subdec = mapDecomposition(model, (SubProcess)activity);
		
		task = (YTask)mapTask(model, dec, activity, true, subdec);
		nodeMap.put(activity, task);
		
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
	private YNode mapTask(YModel model, YDecomposition dec, Activity activity, boolean isComposite, YDecomposition subDec) {
		ArrayList<YVariable> taskVariablesLocal = new ArrayList<YVariable>();
		ArrayList<YVariable> taskVariablesInput = new ArrayList<YVariable>();
		ArrayList<YVariable> taskVariablesOutput = new ArrayList<YVariable>();
		Boolean addTaskDecomposition = false;
		
		YTask task = dec.addTask(generateId(activity.getId()), activity.getLabel(), "XOR", "AND", null);
		
		if(activity.getProperties().size() > 0){
			addTaskDecomposition = true;
			
			for(Property property : activity.getProperties()){
				//add a local variable in the given decomposition
				YVariable mappedVariable = new YVariable();
				
				mappedVariable.setName(property.getName());
				mappedVariable.setType(property.getType().toLowerCase());
				mappedVariable.setNamespace("http://www.w3.org/2001/XMLSchema");
				mappedVariable.setInitialValue(property.getValue());
				
				taskVariablesLocal.add(mappedVariable);
				
				//set the variable mappings for the task
				String startQuery = "&lt;" + mappedVariable.getName() + "&gt;{/" + dec.getID() + "/" + mappedVariable.getName() + "/text()}&lt;/" + mappedVariable.getName() +"&gt;";			
				YVariableMapping localStartVarMap = new YVariableMapping(startQuery, mappedVariable);			
				task.getStartingMappings().add(localStartVarMap);
				
				String completeQuery = "&lt;" + mappedVariable.getName() + "&gt;{/" + task.getID() + "/" + mappedVariable.getName() + "/text()}&lt;/" + mappedVariable.getName() +"&gt;";
				YVariableMapping localCompleteVarMap = new YVariableMapping(completeQuery, mappedVariable);			
				task.getCompletedMappings().add(localCompleteVarMap);
			}			
		}
		
		if(activity.getAssignments().size() > 0){
			addTaskDecomposition = true;
			
			for(Assignment assignment : activity.getAssignments()){
				 
				if(assignment.getAssignTime() == Assignment.AssignTime.Start){
					Boolean propertyIsMapped = false;
					YVariable mappedVariable = null;
					
					//the mappings have to be accessed, because the task can still have no decomposition
					for(YVariable localVar : taskVariablesLocal){
						if(localVar.getName().equalsIgnoreCase(assignment.getTo())){
							propertyIsMapped = true;
							mappedVariable = localVar;
						}
					}
					
					if(!propertyIsMapped){
						for(YVariable inputVar : taskVariablesInput){
							if(inputVar.getName().equalsIgnoreCase(assignment.getTo())){
								propertyIsMapped = true;
								mappedVariable = inputVar;
							}
						}
					}
					
					if(!propertyIsMapped){
						//add a local variable in the given decomposition
						mappedVariable = new YVariable();
						
						mappedVariable.setName(assignment.getTo());
						mappedVariable.setType("string");
						mappedVariable.setNamespace("http://www.w3.org/2001/XMLSchema");
						
						taskVariablesLocal.add(mappedVariable);
					}
					
					//set the variable mappings for the task
					String startQuery = "&lt;" + mappedVariable.getName() + "&gt;{/" + dec.getID() + "/" + mappedVariable.getName() + "/text()}&lt;/" + mappedVariable.getName() +"&gt;";
					Boolean sameMappingExisting = false;
					for(YVariableMapping mapping : task.getStartingMappings()){
						if(mapping.getQuery().equalsIgnoreCase(startQuery)){
							sameMappingExisting = true;
						}
					}
					if(!sameMappingExisting){
						YVariableMapping localStartVarMap = new YVariableMapping(startQuery, mappedVariable);			
						task.getStartingMappings().add(localStartVarMap);
					}
				}
				
				if(assignment.getAssignTime() == Assignment.AssignTime.End){
					Boolean propertyIsMapped = false;
					YVariable mappedVariable = null;
					
					for(YVariable localVar : taskVariablesLocal){
						if(localVar.getName().equalsIgnoreCase(assignment.getTo())){
							propertyIsMapped = true;
							mappedVariable = localVar;
						}
					}
					
					if(!propertyIsMapped){
						for(YVariable outputVar : taskVariablesOutput){
							if(outputVar.getName().equalsIgnoreCase(assignment.getTo())){
								propertyIsMapped = true;
								mappedVariable = outputVar;
							}
						}
					}
					
					if(!propertyIsMapped){
						//add a local variable in the given decomposition
						mappedVariable = new YVariable();
						
						mappedVariable.setName(assignment.getTo());
						mappedVariable.setType("string");
						mappedVariable.setNamespace("http://www.w3.org/2001/XMLSchema");
						
						taskVariablesLocal.add(mappedVariable);
					}
					
					//set the variable mappings for the task
					String completeQuery = "&lt;" + mappedVariable.getName() + "&gt;{/" + task.getID() + "/" + mappedVariable.getName() + "/text()}&lt;/" + mappedVariable.getName() +"&gt;";
					
					Boolean sameMappingExisting = false;
					for(YVariableMapping mapping : task.getCompletedMappings()){
						if(mapping.getQuery().equalsIgnoreCase(completeQuery)){
							sameMappingExisting = true;
						}
					}
					if(!sameMappingExisting){
						YVariableMapping localCompleteVarMap = new YVariableMapping(completeQuery, mappedVariable);			
						task.getCompletedMappings().add(localCompleteVarMap);
					}
				}		
			}

		}
		
		if(addTaskDecomposition){
			
			dec.getLocalVariables().addAll(taskVariablesLocal);
			dec.getInputParams().addAll(taskVariablesInput);
			dec.getOutputParams().addAll(taskVariablesOutput);
			
			//add a new decomposition for the task to the model
			YDecomposition taskDecomposition;
			
			if(subDec != null){
				taskDecomposition = subDec;
			}else{
				taskDecomposition = new YDecomposition(task.getID(), "", "WebServiceGatewayFactsType");
			}
			
			for(YVariable mappedVariable: taskVariablesLocal){
				taskDecomposition.getInputParams().add(mappedVariable);
				taskDecomposition.getOutputParams().add(mappedVariable);
			}
			
			for(YVariable mappedVariable: taskVariablesInput){
				taskDecomposition.getInputParams().add(mappedVariable);
			}
			
			for(YVariable mappedVariable: taskVariablesOutput){
				taskDecomposition.getOutputParams().add(mappedVariable);
			}
			
			model.addDecomposition(taskDecomposition.getID(), taskDecomposition);
			
			isComposite = true;
			subDec = taskDecomposition;
		}
		
		if (isComposite)
			task.setDecomposition(subDec);
		
		// TODO: Multiple Instances
		if (activity.isMultipleInstance()) {
			YTask ytask = (YTask) task;
			ytask.setIsMultipleTask(true);
			
			
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
		
		if (activity.getLoopType() == Activity.LoopType.Standard) {
			if (!loopingActivities.containsKey(dec))
				loopingActivities.put(dec, new LinkedList<Node>());
			loopingActivities.get(dec).add(activity);
		}
		return task;
	}	
}
