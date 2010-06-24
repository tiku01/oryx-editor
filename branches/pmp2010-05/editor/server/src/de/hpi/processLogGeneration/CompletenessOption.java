package de.hpi.processLogGeneration;

public enum CompletenessOption {
	None{
		public String toString() {
			return "None";
		}
	}, Trace{
		public String toString() {
			return "Trace";
		}
	}, Ordering{
		public String toString() {
			return "Ordering";
		}
	};
	public static CompletenessOption fromString(String value) {
		if (value.equals("None")) return None;
		if (value.equals("Trace")) return Trace;
		if (value.equals("Ordering")) return Ordering;
		throw new IllegalArgumentException("CompletenessOption must be None, Trace or Ordering");
	}
}
