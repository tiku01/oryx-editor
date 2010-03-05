package de.hpi.bpmn;

/**
 * @author Armin Zamani Farahani
 *
 */
public class Property {

	private String name;
	private String type;
	private String value = "";
	private Boolean correlation = false;
	
	/**
	 * constructor of class 
	 */
	public Property(String newName, String newType, String newValue, Boolean hasCorrelation){
		setName(newName);
		setType(newType);
		setValue(newValue);
		setCorrelation(hasCorrelation);
	}
	
	/**
	 * the name setter
	 * @param newName
	 */
	public void setName(String newName){
		name = newName;
	}
	
	/**
	 * the name getter
	 * @return name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * the type setter
	 * @param newType
	 */
	public void setType(String newType){
		type = newType;
	}
	
	/**
	 * the type getter
	 * @return type
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * the value setter
	 * @param newValue
	 */
	public void setValue(String newValue){
		value = newValue;
	}
	
	/**
	 * the value getter
	 * @return value
	 */
	public String getValue(){
		return value;
	}
	
	/**
	 * the correlation setter
	 * @param isCorrelation
	 */
	public void setCorrelation(Boolean isCorrelation){
		correlation = isCorrelation;
	}
	
	/**
	 * isCorrelation (getter)
	 * @return isCorrelation
	 */
	public Boolean isCorrelation(){
		return correlation;
	}
	
	
}
