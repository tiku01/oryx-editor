package de.hpi.yawl;

public class YMIDataOutput {

	private String formalOutputExpression = "";
	private String outputJoiningExpression = "";
	private YVariable resultAppliedToLocalVariable = null;
	
	public YMIDataOutput() {
		super();
	}

	public YMIDataOutput(String formalOutputExpression, String outputJoiningExpression,
			YVariable resultAppliedToLocalVariable) {
		super();
		this.formalOutputExpression = formalOutputExpression;
		this.outputJoiningExpression = outputJoiningExpression;
		this.resultAppliedToLocalVariable = resultAppliedToLocalVariable;
	}

	public String getFormalOutputExpression() {
		return formalOutputExpression;
	}
	
	public void setFormalOutputExpression(String formalOutputExpression) {
		this.formalOutputExpression = formalOutputExpression;
	}
	
	public String getOutputJoiningExpression() {
		return outputJoiningExpression;
	}
	
	public void setOutputJoiningExpression(String outputJoiningExpression) {
		this.outputJoiningExpression = outputJoiningExpression;
	}
	
	public YVariable getResultAppliedToLocalVariable() {
		return resultAppliedToLocalVariable;
	}
	
	public void setResultAppliedToLocalVariable(YVariable resultAppliedToLocalVariable) {
		this.resultAppliedToLocalVariable = resultAppliedToLocalVariable;
	}
	
	public String writeToYAWL(){
		String s = "";
		
		s += "\t\t\t\t<miDataOutput>\n";
		
        s += String.format("\t\t\t\t\t<formalOutputExpression query=\"%s\" />\n",getFormalOutputExpression());
        s += String.format("\t\t\t\t\t<outputJoiningExpression query=\"%s\" />\n",getOutputJoiningExpression());
        s += String.format("\t\t\t\t\t<resultAppliedToLocalVariable>%s</resultAppliedToLocalVariable>\n", getResultAppliedToLocalVariable().getName());
        
        s += "\t\t\t\t</miDataOutput>\n";
        
        return s;
	}
	
}
