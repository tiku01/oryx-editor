package de.hpi.pictureSupport;

//import java.util.HashMap;
//import java.util.Map;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.pictureSupport.Logger;

@RootElement("ns2:pictureExport")
public class PictureXML extends XMLConvertible {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Element(name="processes", targetType=PictureProcesses.class)
	protected PictureProcesses processes;
	
	@Element(name="resources", targetType=PictureResources.class)
	protected PictureResources resources;
	
	@Element(name="products", targetType=PictureProducts.class)
	protected PictureProducts products;
	
	@Element(name="organisationUnits", targetType=PictureOrganisationUnits.class)
	protected PictureOrganisationUnits organisationUnits;
	
	@Element(name="users", targetType=PictureUsers.class)
	protected PictureUsers users;
	
	@Element(name="methodDefinition", targetType=PictureMethodDefinition.class)
	protected PictureMethodDefinition methodDefinition;
	
	public PictureProcesses getProcesses() {
		return processes;
	}

	public void setProcesses(PictureProcesses processes) {
		this.processes = processes;
	}

	public PictureResources getResources() {
		return resources;
	}

	public void setResources(PictureResources resources) {
		this.resources = resources;
	}

	public PictureProducts getProducts() {
		return products;
	}

	public void setProducts(PictureProducts products) {
		this.products = products;
	}

	public PictureOrganisationUnits getOrganisationUnits() {
		return organisationUnits;
	}

	public void setOrganisationUnits(PictureOrganisationUnits organisationUnits) {
		this.organisationUnits = organisationUnits;
	}

	public PictureUsers getUsers() {
		return users;
	}

	public void setUsers(PictureUsers users) {
		this.users = users;
	}

	public PictureMethodDefinition getMethodDefinition() {
		return methodDefinition;
	}

	public void setMethodDefinition(PictureMethodDefinition methodDefinition) {
		this.methodDefinition = methodDefinition;
	}
	
	@Override
	public void writeJSON(JSONObject json){
		//nothing to do
	}
	
	public Vector<JSONObject> writeJSON() throws JSONException{
		Logger.i("writeDiagrams");
      
		
		Vector<JSONObject> jsonDiagrams = new Vector<JSONObject>();
		//JSONObject json = null;
		
		//Map<String,String> inheritedProperties = new HashMap<String,String>();
		//inheritedProperties.put("version",getVersion());
		
		/*for (PictureModel aModel : getModels().getModel()){
			//pass global information to models
			aModel.getInheritedProperties().putAll(inheritedProperties);
			
			
			Logger.i("write Model "+aModel.getName());
			
			json = new JSONObject();
			//set the appropriate language to translate correctly
			aModel.setLanguage(Unifier.getLanguage(aModel.getModeltype()));
			aModel.writeJSON(json);
			jsonDiagrams.add(json);
		}*/
		
		return jsonDiagrams;
	}
}
