package de.hpi.yawl;

public class YMIDataInput {

	private String expression = "";
	private String splittingExpression = "";
	private YVariable formalInputParam = null;
	
	public YMIDataInput() {
		super();
	}

	public YMIDataInput(String expression, String splittingExpression,
			YVariable formalInputParam) {
		super();
		this.expression = expression;
		this.splittingExpression = splittingExpression;
		this.formalInputParam = formalInputParam;
	}

	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public String getSplittingExpression() {
		return splittingExpression;
	}
	
	public void setSplittingExpression(String splittingExpression) {
		this.splittingExpression = splittingExpression;
	}
	
	public YVariable getFormalInputParam() {
		return formalInputParam;
	}
	
	public void setFormalInputParam(YVariable formalInputParam) {
		this.formalInputParam = formalInputParam;
	}
	
	public String writeToYAWL(){
		String s = "";
		
		s += "\t\t\t\t<miDataInput>\n";
		
		s += String.format("\t\t\t\t\t<expression query=\"%s\" />\n", getExpression());
        s += String.format("\t\t\t\t\t<splittingExpression query=\"%s\" />\n", getSplittingExpression());
        s += String.format("\t\t\t\t\t<formalInputParam>%s</formalInputParam>\n", getFormalInputParam().getName());
        
        s += "\t\t\t\t</miDataInput>\n";
        
        return s;
	}
}
