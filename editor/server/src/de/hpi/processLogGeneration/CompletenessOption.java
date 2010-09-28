package de.hpi.processLogGeneration;

/**
 * Represents the selected completenss of a log.
 * 
 * @author Thomas Milde
 * */
public enum CompletenessOption {
	/**
	 * No completeness is required for the log. The log should be generated
	 * according to propabilities, if they are annotated
	 * */
	None{
		public String toString() {
			return "None";
		}
	},
	/**
	 * Trace-completeness: every possible trace should be represented in the log.
	 * */
	Trace{
		public String toString() {
			return "Trace";
		}
	},
	/**
	 * Ordering-completeness: if the model allows B to directly succeed A, there
	 * should be at least one generated log, where B directly succeeds A.
	 * */
	Ordering{
		public String toString() {
			return "Ordering";
		}
	};
	
	/**
	 * parses a completeness-option from a String.
	 * */
	public static CompletenessOption fromString(String value) {
		if (value.equals("None")) return None;
		if (value.equals("Trace")) return Trace;
		if (value.equals("Ordering")) return Ordering;
		throw new IllegalArgumentException("CompletenessOption must be None, Trace or Ordering");
	}
}
