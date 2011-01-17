package de.hpi.olc;

import java.util.HashMap;

import de.hpi.petrinet.LabeledTransition;

public class TempTransition {
	private String label;
	// Joins are decomposed and outgoing places of the same transition have
	// equal names (states)
	// Therefore, each transition has only one input and one output

	private HashMap<String, String> transformations = new HashMap<String, String>();

	public TempTransition() {
	}

	public TempTransition(LabeledTransition t) {
		this.label = t.getLabel();
		String input = t.getIncomingFlowRelationships().get(0).getSource().getId();
		String output = t.getOutgoingFlowRelationships().get(0).getTarget().getId();
		transformations.put(input, output);
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void addTransformation(String input, String output) {
		transformations.put(input, output);
	}

	// TODO: Ensure valid expressions for exclusive decisions
	public void addTransformation(LabeledTransition t) {
		String input = t.getIncomingFlowRelationships().get(0).getSource().getId();
		String output = t.getOutgoingFlowRelationships().get(0).getTarget().getId();
		if (transformations.containsKey(input)) {
			String out = transformations.get(input);
			output = out + " | " + output;
		}
		transformations.put(input, output);
	}

	public String getLabel() {
		return this.label;
	}

	/**
	 * Generates the arc condition for a transition in the block of transitions
	 * 
	 * @param transition
	 *            : the transition in the Object Life Cycle
	 * @return Condition for the arc from "Or" to transition's incoming place
	 */
	public String getArcCondition() {
		String arcCondition = "if";
		boolean first = true;
		for (String input : transformations.keySet()) {
			if (first) {
				arcCondition += " i=";
				first = false;
			} else {
				arcCondition += " | i=";
			}
			arcCondition += input;
		}

		arcCondition += "\nthen 1`i\nelse empty";
		return arcCondition;
	}

	/**
	 * Generates the code of the transition in the block of transitions
	 * 
	 * @param transition
	 *            : the transition in the Object Life Cycle
	 * @return Code for the transition in the workflow model
	 */
	public String getCodeForTransition() {
		String code = "";
		if (transformations.size() == 1) {
			code += transformations.values().iterator().next();
		} else {
			code += "case i of\n";
			boolean first = true;
			for (String input : transformations.keySet()) {
				if (first)
					first = false;
				else
					code += "| ";
				code += input + "=> " + transformations.get(input) + "\n";
			}
		}
		return code;
	}
}
