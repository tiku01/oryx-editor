package de.hpi.pictureSupport;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.Namespaces;
import org.xmappr.RootElement;

/**
 * The Class PictureXML that contains the XML structure of PICTURE.
 */
@Namespaces("ns2=http://www.picture-gmbh.de/prozessplattform/2009")
@RootElement("ns2:pictureExport")
public class PictureXML extends XMLConvertible {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The processes. */
	@Element(targetType=PictureProcesses.class)
	protected PictureProcesses processes;
	
	/** The resources. */
	@Element(targetType=PictureResources.class)
	protected PictureResources resources;
	
	/** The products. */
	@Element(targetType=PictureProducts.class)
	protected PictureProducts products;
	
	/** The organisation units. */
	@Element(targetType=PictureOrganisationUnits.class)
	protected PictureOrganisationUnits organisationUnits;
	
	/** The users. */
	@Element(targetType=PictureUsers.class)
	protected PictureUsers users;
	
	/** The method definition. */
	@Element(targetType=PictureMethodDefinition.class)
	protected PictureMethodDefinition methodDefinition;
	
	/**
	 * Gets the processes.
	 *
	 * @return the processes
	 */
	public PictureProcesses getProcesses() {
		return processes;
	}

	/**
	 * Sets the processes.
	 *
	 * @param processes the new processes
	 */
	public void setProcesses(PictureProcesses processes) {
		this.processes = processes;
	}

	/**
	 * Gets the resources.
	 *
	 * @return the resources
	 */
	public PictureResources getResources() {
		return resources;
	}

	/**
	 * Sets the resources.
	 *
	 * @param resources the new resources
	 */
	public void setResources(PictureResources resources) {
		this.resources = resources;
	}

	/**
	 * Gets the products.
	 *
	 * @return the products
	 */
	public PictureProducts getProducts() {
		return products;
	}

	/**
	 * Sets the products.
	 *
	 * @param products the new products
	 */
	public void setProducts(PictureProducts products) {
		this.products = products;
	}

	/**
	 * Gets the organisation units.
	 *
	 * @return the organisation units
	 */
	public PictureOrganisationUnits getOrganisationUnits() {
		return organisationUnits;
	}

	/**
	 * Sets the organisation units.
	 *
	 * @param organisationUnits the new organisation units
	 */
	public void setOrganisationUnits(PictureOrganisationUnits organisationUnits) {
		this.organisationUnits = organisationUnits;
	}

	/**
	 * Gets the users.
	 *
	 * @return the users
	 */
	public PictureUsers getUsers() {
		return users;
	}

	/**
	 * Sets the users.
	 *
	 * @param users the new users
	 */
	public void setUsers(PictureUsers users) {
		this.users = users;
	}

	/**
	 * Gets the method definition.
	 *
	 * @return the method definition
	 */
	public PictureMethodDefinition getMethodDefinition() {
		return methodDefinition;
	}

	/**
	 * Sets the method definition.
	 *
	 * @param methodDefinition the new method definition
	 */
	public void setMethodDefinition(PictureMethodDefinition methodDefinition) {
		this.methodDefinition = methodDefinition;
	}
	
	@Override
	public void writeJSON(JSONObject json){
		//nothing to do
	}
	
	/**
	 * entry point for generating diagrams for Oryx
	 * @return a collection of diagrams in JSON
	 * @throws JSONException
	 */
	public Vector<JSONObject> writeJSON() throws JSONException{
		Logger.i("writeDiagrams");
      
		
		Vector<JSONObject> jsonDiagrams = new Vector<JSONObject>();
		JSONObject json = null;
		
		// every process is now told to write its JSON
		for (PictureProcess aProcess : getProcesses().getChildren()){
			aProcess.writeJSON(json);
			jsonDiagrams.add(json);
		}
		
		return jsonDiagrams;
	}
}
