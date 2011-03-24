package de.hpi.pictureSupport.diagram;

import java.util.ArrayList;
import java.util.UUID;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.JSONBuilder;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilSet;
import org.oryxeditor.server.diagram.StencilType;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.pictureSupport.PictureBuildingBlockOccurrence;
import de.hpi.pictureSupport.PictureResultingProduct;
import de.hpi.pictureSupport.PictureSubprocess;
import de.hpi.pictureSupport.container.PictureBuildingBlockRepository;
import de.hpi.pictureSupport.container.PictureProcessAttributes;
import de.hpi.pictureSupport.container.PictureProcessFlow;

/**
 * The Class PictureProcessModel.
 */
@RootElement("processModel")
public class PictureProcessModel {

	/** The id. */
	@Attribute
	private int id;
	
	/** The name. */
	@Element
	private String name;
	
	/** The description. */
	@Element
	private String description;
	
	/** The organization unit id. */
	@Element
	private int organisationUnitID;
	
	/** The resulting product. */
	@Element(targetType=PictureResultingProduct.class)
	private PictureResultingProduct resultingProduct;
	
	/** The creator id. */
	@Element
	private int creatorID;
	
	/** The last editor id. */
	@Element
	private int lastEditorID;
	
	/** The number of cases. */
	@Element
	private int numberOfCases;
	
	/** The process attributes. */
	@Element(targetType=PictureProcessAttributes.class)
	private PictureProcessAttributes processAttributes;
	
	/** The process flow. */
	@Element(targetType=PictureProcessFlow.class)
	private PictureProcessFlow processFlow;
	
	/** The building block repository. */
	@Element(targetType=PictureBuildingBlockRepository.class)
	private PictureBuildingBlockRepository buildingBlockRepository;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the organisation unit id.
	 *
	 * @return the organisation unit id
	 */
	public int getOrganisationUnitID() {
		return organisationUnitID;
	}

	/**
	 * Sets the organisation unit id.
	 *
	 * @param organisationUnitID the new organisation unit id
	 */
	public void setOrganisationUnitID(int organisationUnitID) {
		this.organisationUnitID = organisationUnitID;
	}

	/**
	 * Gets the resulting product.
	 *
	 * @return the resulting product
	 */
	public PictureResultingProduct getResultingProduct() {
		return resultingProduct;
	}

	/**
	 * Sets the resulting product.
	 *
	 * @param resultingProduct the new resulting product
	 */
	public void setResultingProduct(PictureResultingProduct resultingProduct) {
		this.resultingProduct = resultingProduct;
	}

	/**
	 * Gets the creator id.
	 *
	 * @return the creator id
	 */
	public int getCreatorID() {
		return creatorID;
	}

	/**
	 * Sets the creator id.
	 *
	 * @param creatorID the new creator id
	 */
	public void setCreatorID(int creatorID) {
		this.creatorID = creatorID;
	}

	/**
	 * Gets the last editor id.
	 *
	 * @return the last editor id
	 */
	public int getLastEditorID() {
		return lastEditorID;
	}

	/**
	 * Sets the last editor id.
	 *
	 * @param lastEditorID the new last editor id
	 */
	public void setLastEditorID(int lastEditorID) {
		this.lastEditorID = lastEditorID;
	}

	/**
	 * Gets the number of cases.
	 *
	 * @return the number of cases
	 */
	public int getNumberOfCases() {
		return numberOfCases;
	}

	/**
	 * Sets the number of cases.
	 *
	 * @param numberOfCases the new number of cases
	 */
	public void setNumberOfCases(int numberOfCases) {
		this.numberOfCases = numberOfCases;
	}

	/**
	 * Gets the process attributes.
	 *
	 * @return the process attributes
	 */
	public PictureProcessAttributes getProcessAttributes() {
		return processAttributes;
	}

	/**
	 * Sets the process attributes.
	 *
	 * @param processAttributes the new process attributes
	 */
	public void setProcessAttributes(PictureProcessAttributes processAttributes) {
		this.processAttributes = processAttributes;
	}

	/**
	 * Gets the process flow.
	 *
	 * @return the process flow
	 */
	public PictureProcessFlow getProcessFlow() {
		return processFlow;
	}

	/**
	 * Sets the process flow.
	 *
	 * @param processFlow the new process flow
	 */
	public void setProcessFlow(PictureProcessFlow processFlow) {
		this.processFlow = processFlow;
	}

	/**
	 * Gets the building block repository.
	 *
	 * @return the building block repository
	 */
	public PictureBuildingBlockRepository getBuildingBlockRepository() {
		return buildingBlockRepository;
	}

	/**
	 * Sets the building block repository.
	 *
	 * @param buildingBlockRepository the new building block repository
	 */
	public void setBuildingBlockRepository(
			PictureBuildingBlockRepository buildingBlockRepository) {
		this.buildingBlockRepository = buildingBlockRepository;
	}

	
	/**
	 * Gets a new picture diagram with basic information.
	 *
	 * @return the new picture diagram
	 */
	public Diagram getNewPictureDiagram() {
		String resourceId = "oryx-canvas123";		
		StencilType type = new StencilType("Diagram");		
		String stencilSetNs = "http://b3mn.org/stencilset/picture#";	
		String url ="/oryx/stencilsets/picture/picture.json";
		
		StencilSet stencilSet = new StencilSet(url, stencilSetNs);		
		Diagram diagram = new Diagram(resourceId, type, stencilSet);
		return diagram;
	}
	
	
	/**
	 * Create the JSON representations for all diagrams described in the XML.
	 * @return the vector of diagrams
	 * @throws JSONException the JSON exception
	 */
	public Vector<JSONObject> writeJSON() throws JSONException{
		Vector<JSONObject> jsonDiagrams = new Vector<JSONObject>();

		// for every variant arising in the XML, a JSON of a diagram needs to be processed
		for (PictureSubprocess aSubprocess : getProcessFlow().getChildren()) {
			for (PictureVariant aVariant : aSubprocess.getVariants().getChildren()) {
				Diagram diagram = getNewPictureDiagram();
				ArrayList<Shape> childShapes = new ArrayList<Shape>();
				
				// create the process lane and all its inner children
				Shape processLane = createProcessLaneFor(diagram, aVariant);
				childShapes.add(processLane);
				
				// put the shapes into the diagram
				diagram.setChildShapes(childShapes);
				
				// TODO calculate bounds according to children
				Bounds bounds = new Bounds(new Point(1485.0,1050.0),new Point(0.0,0.0));
				diagram.setBounds(bounds);
				
				// put JSON representation of diagram into list of imported diagrams
				JSONObject json = JSONBuilder.parseModel(diagram);
				jsonDiagrams.add(json);
			}
		}
		
		return jsonDiagrams;
	}

	/**
	 * Creates the process lane for a diagram.
	 *
	 * @param diagram the diagram the process lane shall be processed for
	 * @param variant the variant that represents the diagram inside the XML
	 * @param repository 
	 * @return the process lane shape
	 */
	private Shape createProcessLaneFor(Diagram diagram, PictureVariant variant) {
		Shape process = new Shape(String.valueOf(UUID.randomUUID()), new StencilType("process"));

		// put properties into the process
		process.putProperty("basic-title", variant.getName());
		
		// add all child shapes and enrich information of the diagram
		ArrayList<Shape> processChildren = new ArrayList<Shape>();
		for (PictureBuildingBlockOccurrence aBlock : variant.getBuildingBlockSequence().getChildren()) {
			Shape blockOccurrence = aBlock.createBlockFor(process,processChildren);
			processChildren.add(blockOccurrence);
		}
		process.setChildShapes(processChildren);
		
		// calculate the process' bounds based on children
		Point upperLeft = new Point(0.0, 0.0);
		Point lowerRight = new Point(160.0, 130.0);
		if (!processChildren.isEmpty()) {
			lowerRight = processChildren.get(processChildren.size()-1).getBounds().getLowerRight();			
		}
		process.setBounds(new Bounds(lowerRight, upperLeft));
		
		return process;
	}
}
