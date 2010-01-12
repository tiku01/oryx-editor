package de.hpi.bpmn2yawl;

import java.util.HashMap;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Pool;
import de.hpi.yawl.resourcing.*;

public class BPMN2YAWLResourceMapper {

	
	public BPMN2YAWLResourceMapper() {

	}
	
	public String translate(BPMNDiagram diagram) {
		
		HashMap<Node, ResourcingType> nodeMap = new HashMap<Node, ResourcingType>();
		OrgData orgData = new OrgData();

		for (Node node : diagram.getChildNodes()){
			if(!(node instanceof Pool))
				continue;
			
			Pool pool = (Pool)node;
			
			if(pool.getChildNodes().size() == 0)
				continue;
			
			mapToOrgGroup(orgData, pool, nodeMap);
			
			for (Node subNode : pool.getChildNodes()){
				if (subNode instanceof Lane){
					Lane lane = (Lane) subNode;
					handleLane(orgData, lane, nodeMap);
				}
			}
		}
		return orgData.writeToYAWL();
	}

	/**
	 * @param orgData
	 * @param lane
	 * @param nodeMap
	 */
	private void mapLaneToParticipant(OrgData orgData, Lane lane,
			HashMap<Node, ResourcingType> nodeMap) {
		Participant participant = new Participant();
		participant.setLastname(lane.getLabel());
		orgData.getParticipants().add(participant);
		nodeMap.put(lane, participant);
	}

	/**
	 * @param orgData
	 * @param node
	 * @param nodeMap
	 */
	private void mapToOrgGroup(OrgData orgData, Node node,
			HashMap<Node, ResourcingType> nodeMap) {
		OrgGroup orgGroup = new OrgGroup();
		orgGroup.setName(node.getLabel());
		orgGroup.setGroupType("GROUP");
		if (node instanceof Lane){
			Lane lane = (Lane)node;
			orgGroup.setBelongsToID((OrgGroup)nodeMap.get(lane.getPool()));
		}
		orgData.getOrgGroups().add(orgGroup);
		nodeMap.put(node, orgGroup);
	}

	/**
	 * @param orgData
	 * @param lane
	 * @param nodeMap
	 */
	private void mapLaneToPosition(OrgData orgData, Lane lane, 
			HashMap<Node, ResourcingType> nodeMap) {
		Position position = new Position();
		position.setName(lane.getLabel());
		position.setOrgGroupBelongingTo((OrgGroup)nodeMap.get(lane.getPool()));
		orgData.getPositions().add(position);
		nodeMap.put(lane, position);
	}

	/**
	 * @param orgData
	 * @param lane
	 * @param nodeMap 
	 */
	private void mapLaneToRole(OrgData orgData, Lane lane, HashMap<Node, ResourcingType> nodeMap) {
		Role role = new Role();
		role.setName(lane.getLabel());
		orgData.getRoles().add(role);
		nodeMap.put(lane, role);
	}
	
	private void handleLane(OrgData orgData, Lane lane, HashMap<Node, ResourcingType> nodeMap)
	{
		boolean shouldCheckResourceType = checkForNestedLane(orgData, lane, nodeMap);

		if (shouldCheckResourceType){
			if(lane.getResourcingType().equalsIgnoreCase("participant"))
				mapLaneToParticipant(orgData, lane, nodeMap);
			else if(lane.getResourcingType().equalsIgnoreCase("role"))
				mapLaneToRole(orgData, lane, nodeMap);
			else if(lane.getResourcingType().equalsIgnoreCase("position"))
				mapLaneToPosition(orgData, lane, nodeMap);
		}
	}

	/**
	 * @param orgData
	 * @param lane
	 * @param nodeMap
	 * @return
	 */
	private boolean checkForNestedLane(OrgData orgData, Lane lane, HashMap<Node, ResourcingType> nodeMap)
	{
		boolean isNotNested = true;
		
		for (Node laneNode : lane.getChildNodes()){
			if (laneNode instanceof Lane){
				if (isNotNested){
					mapToOrgGroup(orgData, lane, nodeMap);
					isNotNested = false;
				}
				handleLane(orgData, (Lane)laneNode, nodeMap);
			}
		}
		return isNotNested;
	}

}
