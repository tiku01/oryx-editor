package de.hpi.visio.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Provides all properties for visio import: Uses the VisioBPMNConfiguration.xml  
 */
public class ImportConfigurationUtil {
	
	private static final String CONFIGURATION_XML_FILE = "VisioImportConfiguration.xml";

	private String path;
	private Properties properties;
	
	private String stencilSetPath;
	private String stencilSetMappingConfigPath;
	private String stencilSetConfigPath;
	private String heuristicPath;
	private String stencilSetHeuristicsPath;

	
	public ImportConfigurationUtil(String realPath, String type) {
		path = realPath;
		initializeTheProperties();
		heuristicPath = "heuristics.";
		if (type.equals("bpmn")) {
			stencilSetPath = "stencilsets.bpmn.";
			stencilSetMappingConfigPath = "stencilsets.bpmn.config.";
			stencilSetConfigPath = "oryx.config.bpmn.";
			stencilSetHeuristicsPath = "heuristics.bpmn.";
		} else if (type.equals("epc")){
			stencilSetPath = "stencilsets.epc.";
			stencilSetMappingConfigPath = "stencilsets.epc.config.";
			stencilSetConfigPath = "oryx.config.epc.";
			stencilSetHeuristicsPath = "heuristics.epc.";
		} else {
			throw new RuntimeException("Failure in visio import: Stencilset type isn't supported yet.");
		}
	}
	
	private void initializeTheProperties() {
		Properties newProperties;
		FileInputStream in;
		try {
			in = new FileInputStream(path + CONFIGURATION_XML_FILE);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			throw new IllegalStateException("Not able to load the mapping properties for Visio importing.",e1);			
		}
		if (in != null) {
			try {
				newProperties = new Properties();
				newProperties.loadFromXML(in);
				properties = newProperties;
			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (properties == null)
			throw new IllegalStateException("Not able to create the Visio Stencil Set to BPMN mapping properties.");
	}

	public String getStencilIdForName(String name) {
		String mappingsString = properties.getProperty(stencilSetPath + "mappings");
		if (mappingsString == null || "".equals(mappingsString)) {
			throw new RuntimeException("Failure to load .vdx-visio-model: " +
					"Check VisioImportConfiguration.xml and define mappings for this stencil set.");
		}
		String[] stencilSets = mappingsString.split(",");
		for (String stencilSet : stencilSets) {
			String stencilIdForName = properties.getProperty(stencilSetPath + stencilSet + ".stencil." + name);
			if (stencilIdForName != null)
				return stencilIdForName;
		}
		return null;
	}
	
	public String getMappingConfig(String key) {
		return properties.getProperty(stencilSetMappingConfigPath + key);
	}
	
	public String getStencilSetConfig(String key) {
		return properties.getProperty(stencilSetConfigPath + key);
	}
	
	public String getHeuristic(String key) {
		return properties.getProperty(heuristicPath + key);
	}
	
	public String getStencilSetHeuristic(String key) {
		return properties.getProperty(stencilSetHeuristicsPath + key);
	}
	
	
	
}
