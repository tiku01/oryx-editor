package de.hpi.bpmn2xpdl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Package")
public class XPDLPackage extends XPDLThing {
	
	@Attribute("Language")
	protected String language;
	@Attribute("QueryLanguage")
	protected String queryLanguage;
	
	@Element("PackageHeader")
	protected XPDLPackageHeader packageHeader;
	@Element("RedefinableHeader")
	protected XPDLRedefinableHeader redefinableHeader;
	@Element("ConformanceClass")
	protected XPDLConformanceClass conformanceClass;
	@Element("Script")
	protected XPDLScript script;
	
	protected XPDLPool mainPool;
	
	@Element("Artifacts")
	protected XPDLArtifacts artifacts;
	@Element("Associations")
	protected XPDLAssociations associations;
	@Element("MessageFlows")
	protected XPDLMessageFlows messageFlows;
	@Element("Pools")
	protected XPDLPools pools;
	@Element("WorkflowProcesses")
	protected XPDLWorkflowProcesses workflowProcesses;
	
	public XPDLPackage() {
		setConformanceClass(new XPDLConformanceClass());
	}
	
	public XPDLArtifacts getArtifacts() {
		return artifacts;
	}
	
	public XPDLAssociations getAssociations() {
		return associations;
	}
	
	public XPDLConformanceClass getConformanceClass() {
		return conformanceClass;
	}

	public String getLanguage() {
		return language;
	}
	
	public XPDLMessageFlows getMessageFlows() {
		return messageFlows;
	}
	
	public XPDLPackageHeader getPackageHeader() {
		return packageHeader;
	}
	
	public XPDLPools getPools() {
		return pools;
	}
	
	public String getQueryLanguage() {
		return queryLanguage;
	}
	
	public XPDLRedefinableHeader getRedefinableHeader() {
		return redefinableHeader;
	}
	
	public XPDLScript getScript() {
		return script;
	}
	
	public XPDLWorkflowProcesses getWorkflowProcesses() {
		return workflowProcesses;
	}
	
	public void readJSONauthor(JSONObject modelElement) throws JSONException {
		initializeRedefinableHeader();
		
		JSONObject author = new JSONObject();
		author.put("author", modelElement.optString("author"));
		getRedefinableHeader().parse(author);
	}
	
	public void readJSONbounds(JSONObject modelElement) {
		JSONObject lowerRight = modelElement.optJSONObject("bounds").optJSONObject("lowerRight");
		
		createExtendedAttribute("boundsX", lowerRight.optString("x"));
		createExtendedAttribute("boundsY", lowerRight.optString("y"));
	}
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
		JSONArray childShapes = modelElement.optJSONArray("childShapes");
		
		if (childShapes != null) {
			for (int i = 0; i<childShapes.length(); i++) {
				JSONObject childShape = childShapes.getJSONObject(i);
				String stencil = childShape.getJSONObject("stencil").getString("id");
				
				if  (XPDLActivity.handlesStencil(stencil)) {
					createMainProcessChild(childShape);
				} else if (XPDLArtifact.handlesStencil(stencil)) {
					createArtifact(childShape);
				} else if (XPDLAssociation.handlesStencil(stencil)) {
					createAssociation(childShape);
				} else if (XPDLMessageFlow.handlesStencil(stencil)) {
					createMessageFlow(childShape);
				} else if (XPDLPool.handlesStencil(stencil)) {
					createPool(childShape);
				} else if (XPDLTransition.handlesStencil(stencil)) {
					createMainProcessChild(childShape);
				}
				readJSONchildShapes(childShape);
			}
		}
	}
	
	public void readJSONcreationdate(JSONObject modelElement) throws JSONException {
		initializePackageHeader();
	
		JSONObject date = new JSONObject();
		date.put("creationdate", modelElement.optString("creationdate"));
		getPackageHeader().parse(date);
	}
	
	public void readJSONdocumentation(JSONObject modelElement) throws JSONException {
		initializePackageHeader();
	
		JSONObject documentation = new JSONObject();
		documentation.put("documentation", modelElement.optString("documentation"));
		getPackageHeader().parse(documentation);
	}
	
	public void readJSONexpressionlanguage(JSONObject modelElement) throws JSONException {
		initializeScriptType();
		
		JSONObject expressionlanguage = new JSONObject();
		expressionlanguage.put("expressionlanguage", modelElement.optString("expressionlanguage"));
		getScript().parse(expressionlanguage);
	}
	
	public void readJSONlanguage(JSONObject modelElement) {
		setLanguage(modelElement.optString("language"));
	}
	
	public void readJSONmodificationdate(JSONObject modelElement) throws JSONException {
		initializePackageHeader();
		
		JSONObject date = new JSONObject();
		date.put("modificationdate", modelElement.optString("modificationdate"));
		getPackageHeader().parse(date);
	}
	
	public void readJSONpools(JSONObject modelElement) {
	}
	
	public void readJSONquerylanguage(JSONObject modelElement) {
		setQueryLanguage(modelElement.optString("querylanguage"));
	}
	
	public void readJSONssextensions(JSONObject modelElement) {
		createExtendedAttribute("ssextension", modelElement.optJSONArray("ssextenion").toString());
	}
	
	public void readJSONstencilset(JSONObject modelElement) {
		JSONObject stencilset = modelElement.optJSONObject("stencilset");
		
		createExtendedAttribute("stencilsetUrl", stencilset.optString("url"));
		createExtendedAttribute("stencilsetNamespace", stencilset.optString("namespace"));
	}
	
	public void readJSONversion(JSONObject modelElement) throws JSONException {
		initializeRedefinableHeader();
		
		JSONObject version = new JSONObject();
		version.put("version", modelElement.optString("version"));
		getRedefinableHeader().parse(version);
	}
	
	public void setArtifacts(XPDLArtifacts artifactsValue) {
		artifacts = artifactsValue;
	}
	
	public void setAssociations(XPDLAssociations associationsValue) {
		associations = associationsValue;
	}
	
	public void setConformanceClass(XPDLConformanceClass conformance) {
		conformanceClass = conformance;
	}
	
	public void setLanguage(String languageValue) {
		language = languageValue;
	}
	
	public void setMessageFlows(XPDLMessageFlows flows) {
		messageFlows = flows;
	}
	
	public void setPackageHeader(XPDLPackageHeader header) {
		packageHeader = header;
	}
	
	public void setPools(XPDLPools poolsValue) {
		pools = poolsValue;
	}
	
	public void setQueryLanguage(String languageValue) {
		queryLanguage = languageValue;
	}
	
	public void setRedefinableHeader(XPDLRedefinableHeader header) {
		redefinableHeader = header;
	}
	
	public void setWorkflowProcesses(XPDLWorkflowProcesses processes) {
		workflowProcesses = processes;
	}
	
	public void setScript(XPDLScript scriptValue) {
		script = scriptValue;
	}
	
	public void setWorklfowProcesses(XPDLWorkflowProcesses processes) {
		workflowProcesses = processes;
	}
	
	protected void createArtifact(JSONObject modelElement) {
		initializeArtifacts();
		
		XPDLArtifact nextArtifact = new XPDLArtifact();
		nextArtifact.parse(modelElement);
		getArtifacts().add(nextArtifact);
	}
	
	protected void createAssociation(JSONObject modelElement) {
		initializeAssociations();
		
		XPDLAssociation nextAssociation = new XPDLAssociation();
		nextAssociation.parse(modelElement);
		getAssociations().add(nextAssociation);
	}
	
	protected void createMessageFlow(JSONObject modelElement) {
		initializeMessageFlows();
		
		XPDLMessageFlow nextFlow = new XPDLMessageFlow();
		nextFlow.parse(modelElement);
		getMessageFlows().add(nextFlow);
	}
	
	protected XPDLPool createPool(JSONObject modelElement) {
		initializePools();
		initializeWorkflowProcesses();
		
		XPDLPool nextPool = new XPDLPool();
		XPDLWorkflowProcess accordingProcess = new XPDLWorkflowProcess();
		nextPool.setAccordingProcess(accordingProcess);
		
		nextPool.parse(modelElement);
		getPools().add(nextPool);
		
		accordingProcess.parse(modelElement);
		getWorkflowProcesses().add(accordingProcess);
		
		return nextPool;
	}
	
	protected void createMainProcessChild(JSONObject modelElement) throws JSONException {
		initializeMainPool();
		
		JSONArray childShapesArray = new JSONArray();
		childShapesArray.put(modelElement);
		
		JSONObject childShapes = new JSONObject();
		childShapes.put("childShapes", childShapesArray);
		
		XPDLWorkflowProcess mainProcess = getMainPool().getAccordingProcess();
		mainProcess.parse(childShapes);
	}
	
	protected XPDLPool getMainPool() {
		return mainPool;
	}
	
	protected void initializeArtifacts() {
		if (getArtifacts() == null) {
			setArtifacts(new XPDLArtifacts());
		}
	}
	
	protected void initializeAssociations() {
		if (getAssociations() == null) {
			setAssociations(new XPDLAssociations());
		}
	}
	
	protected void initializeMainPool() throws JSONException {
		if (getMainPool() == null) {
			JSONObject modelElement = new JSONObject(XPDL.implicitPool);
			XPDLPool newMainPool = createPool(modelElement);
			
			setMainPool(newMainPool);
		}
	}
	
	protected void initializeMessageFlows() {
		if (getMessageFlows() == null) {
			setMessageFlows(new XPDLMessageFlows());
		}
	}
	
	protected void initializePackageHeader() {
		if (getPackageHeader() == null) {
			setPackageHeader(new XPDLPackageHeader());
		}
	}
	
	protected void initializePools() {
		if (getPools() == null) {
			setPools(new XPDLPools());
		}
	}
	
	protected void initializeRedefinableHeader() {
		if (getRedefinableHeader() == null) {
			setRedefinableHeader(new XPDLRedefinableHeader());
		}
	}
	
	protected void initializeScriptType() {
		if (getScript() == null) {
			setScript(new XPDLScript());
		}
	}
	
	protected void initializeWorkflowProcesses() {
		if (getWorkflowProcesses() == null) {
			setWorklfowProcesses(new XPDLWorkflowProcesses());
		}
	}
	
	protected void setMainPool(XPDLPool poolValue) {
		mainPool = poolValue;
	}
}
