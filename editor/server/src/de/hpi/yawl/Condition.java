package de.hpi.yawl;

public class Condition extends Node {
	
	public enum ConditionType {
		NONE, IN, OUT
	}
	
	private ConditionType type = ConditionType.NONE;
	
	public Condition(String ID, String name, ConditionType type){
		super(ID, name);
		
		setType(type);
	}
	
	public void setType(ConditionType conditionType){
		this.type = conditionType;
	}
	
	public boolean isInputCondition() {
		return this.type == ConditionType.IN;
	}

	public boolean isOutputCondition() {
		return this.type == ConditionType.OUT;
	}
	
	/**
	 * Export to YAWL file.
	 * @param i Writing phase: 0 = inputCondition, 2 = outputCondition, 1 = rest.
	 * @return String The string to export for this YAWLDecompositon.
	 */
	public String writeToYAWL(int phase) {
		String s = "";
		if ((phase == 0 && this.type == ConditionType.IN) || (phase == 1 && this.type == ConditionType.NONE) ||
				(phase == 2 && this.type == ConditionType.OUT)) {
			if (this.type == ConditionType.OUT) {
				s +="\t\t\t\t<outputCondition id=\"Node" + getID() + "\"/>\n";
			} else {
				if (this.type == ConditionType.IN) {
					s +="\t\t\t\t<inputCondition\n";
				} else {
					s +="\t\t\t\t<condition\n";
				}
				s +="\t\t\t\t\tid=\"Node" + getID() + "\"\n";
				s +="\t\t\t\t>\n";

				if (this.type == ConditionType.NONE) {
					s +="\t\t\t\t\t<name>" + getName() + "</name>\n";
				}

				Iterator it = getOutEdgesIterator();
				while (it.hasNext()) {
					Edge edge = (Edge) it.next();
					s += edge.writeToYAWL(Task.SplitJoinType.NONE, Edge.EdgeType.NORMAL);
				}

				if (this.type == ConditionType.IN) {
					s +="\t\t\t\t</inputCondition>\n";
				} else {
					s +="\t\t\t\t</condition>\n";
				}
			}
		}
		return s;
	}
}
