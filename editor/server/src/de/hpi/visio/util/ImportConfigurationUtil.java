package de.hpi.visio.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Provides an entry to the VisioBPMNConfiguration.xml and is used to map Visio 2003 Stencilset 
 * values to BPMN properties
 */
public class ImportConfigurationUtil {
	
	private static final String CONFIGURATION_XML_FILE = "VisioBPMNConfiguration.xml";
	private static final String HEURISTICS_PATH = "heuristics.";
	private static final String ORYX_CONFIG_BPMN = "oryx.config.BPMN.";
	
	private Properties properties;
	private String path;
	
	/*
	 * Important to do: The path shouldn't be passed through the VisioToBPMNConverter
	 * all down from the servlet. There have to be a better solution!!
	 * In a better solution properties would be final while die initializeTheProperties
	 * would be static {...} code running once, so that the properties aren't reread
	 * for each call. Therefore methods and members should also be static.
	 */
	
	public ImportConfigurationUtil(String realPath) {
		path = realPath;
		initializeTheProperties();
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

	/**
	 * Uses VisioBPMNConfiguration.xml: Uses all defined mappings, that are included in the property: stencilsets.mappings
	 * @param name in a Microsoft Visio .vdx: A shape's attribute nameU
	 * @return a Stencil of BPMN or null, if there is no value defined for the given nameU
	 */
	public String getStencilIdForName(String name) {
		String[] stencilSets = properties.getProperty("stencilsets.mappings").split(",");
		for (String stencilSet : stencilSets) {
			String stencilIdForName = properties.getProperty("stencilsets." + stencilSet + ".stencil." + name);
			if (stencilIdForName != null)
				return stencilIdForName;
		}
		return null;
	}
	
	/**
	 * Get additional information for mappings
	 * @param key
	 * @return
	 */
	public String getStencilSetConfig(String key) {
		return properties.getProperty("stencilsets.config." + key);
	}
	
	/**
	 * @param name in a Microsoft Visio .vdx: A shape's attribute nameU
	 * @return a Stencil of BPMN or null, if there is no value defined for the given nameU
	 */
	public String getHeuristicValue(String key) {
		String heuristicValue = properties.getProperty(HEURISTICS_PATH + key);
		return heuristicValue;
	}
	
	/**
	 * Get configuration values for resizing and setting of shapes that will be displayed correctly at oryx.
	 * @param key
	 * @return
	 */
	public String getOryxBPMNConfig(String key) {
		String configValue = properties.getProperty(ORYX_CONFIG_BPMN + key);
		return configValue;
	}
	
}
