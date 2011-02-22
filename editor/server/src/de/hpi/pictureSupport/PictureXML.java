package de.hpi.pictureSupport;

import org.xmappr.Element;
import org.xmappr.Namespaces;
import org.xmappr.RootElement;

@Namespaces("ns2=http://www.picture-gmbh.de/prozessplattform/2009")
@RootElement("ns2:pictureExport")
public class PictureXML extends XMLConvertible {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Element(targetType=PictureProcesses.class)
	protected PictureProcesses processes;
	
	@Element(targetType=PictureResources.class)
	protected PictureResources resources;
	
	@Element(targetType=PictureProducts.class)
	protected PictureProducts products;
	
	@Element(targetType=PictureOrganisationUnits.class)
	protected PictureOrganisationUnits organisationUnits;
	
	@Element(targetType=PictureUsers.class)
	protected PictureUsers users;
	
	@Element(targetType=PictureMethodDefinition.class)
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
}
