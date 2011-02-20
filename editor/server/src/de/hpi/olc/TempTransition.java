package de.hpi.olc;

import java.util.HashMap;

import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Transition;

public class TempTransition {
	private String label;
	private static final String SEPARATOR = ",";
	// Joins are decomposed and outgoing places of the same transition have
	// equal names (states). Hence, each transition has only one input place
	// and one output place
	private HashMap<String, String> transformations = new HashMap<String, String>();

	public TempTransition(LabeledTransition t) {
		this.label = t.getLabel();
		String input = t.getIncomingFlowRelationships().get(0).getSource().getId();
		String output = t.getOutgoingFlowRelationships().get(0).getTarget().getId();
		transformations.put(input, output);
	}
	
	public TempTransition(Transition t, String label) {
		this.label = label;
		String input = t.getIncomingFlowRelationships().get(0).getSource().getId();
		String output = t.getOutgoingFlowRelationships().get(0).getTarget().getId();
		transformations.put(input, output);
	}

	public String getLabel() {
		return this.label;
	}

	public void addTransformation(String input, String output) {
		transformations.put(input, output);
	}

	/**
	 * Extracts precondition(state) and post-condition (state) for the given
	 * transition from olc precondition = precedingPlace.name postcondition =
	 * succeedingPlace.name
	 */
	public void addTransformation(LabeledTransition t) {
		String input = t.getIncomingFlowRelationships().get(0).getSource().getId();
		String output = t.getOutgoingFlowRelationships().get(0).getTarget().getId();
		// Handle merge of XOR transitions
		if (transformations.containsKey(input)) {
			String out = transformations.get(input);
			output = out + SEPARATOR + output;
		}
		transformations.put(input, output);
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
				arcCondition += " orelse i=";
			}
			arcCondition += input;
		}

		arcCondition += "\nthen 1`i\nelse empty";
		return arcCondition;
	}

	/**
	 * Generates the outgoing arc expression for transitions in the Transition
	 * Block
	 * 
	 * @return Arc Expression
	 */
	public String getOutgoingArcExpression() {
		String expression = "";
		if (transformations.size() == 1) {
			String out = transformations.values().iterator().next();
			// Handle XORs
			if(out.contains(SEPARATOR)) {
				expression += "case l of\n";
				String[] cases = out.split(SEPARATOR);
				for(int i = 0; i < cases.length; i++) {
					if(i != 0) expression += "| ";
					expression += i + "=> " + cases[i] + "\n";
				}
			}
			// Handle standard transition
			else {
				expression += out;
			}
		} else {
			// Handle transitions with multiple occurrences.
			expression += "case i of\n";
			boolean first = true;
			for (String input : transformations.keySet()) {
				if (first)
					first = false;
				else
					expression += "| ";
				expression += input + "=> " + transformations.get(input) + "\n";
			}
		}
		return expression;
	}
}
