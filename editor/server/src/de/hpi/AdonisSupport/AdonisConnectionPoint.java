package de.hpi.AdonisSupport;

import org.xmappr.Attribute;

//<!ELEMENT FROM EMPTY>
//<!ATTLIST FROM
//  class    CDATA #REQUIRED
//  instance CDATA #REQUIRED
//>

//<!ELEMENT TO EMPTY>
//<!ATTLIST TO
//class    CDATA #REQUIRED
//instance CDATA #REQUIRED
//>

public class AdonisConnectionPoint {

	@Attribute(name="instance")
	protected String instanceName;
	
	@Attribute(name="class")
	protected String stencilClass;

	private AdonisStencil instance;
	private String instanceResourceId;
	private AdonisConnector connector; 
	
	public void setConnector(AdonisConnector connector) {
		this.connector = connector;
	}

	public AdonisConnector getConnector() {
		return connector;
	}

	public void setInstanceResourceId(String instanceResourceId) {
		this.instanceResourceId = instanceResourceId;
	}

	public String getInstanceResourceId() {
		return instanceResourceId;
	}

	public String getInstanceName(){
		return instanceName;
	}
	
	public void setInstanceName(String value){
		instanceName = value;
	}
	
	public String getStencilClass(){
		return stencilClass;
	}
	
	public void setStencilClass(String value){
		stencilClass = value;
	}

	public void setInstance(AdonisStencil adonisInstance) {
		instance = adonisInstance;
	}
	
	public AdonisStencil getInstance(){
		return instance;
	}
	
	public void distributeValues(){
		Log.d("ConnectionPoint setName()"+instance.getName());
		setInstanceName(instance.getName());
		setStencilClass(instance.getAdonisStencilClass("en"));
	}

	

}
