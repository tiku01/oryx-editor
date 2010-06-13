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
	private static final String BPT_BPMN_11_PATH = "stencilsets.BPTBPMN11.";
	private static final String BPT_BPMN_11_STENCILS_PATH = BPT_BPMN_11_PATH + "stencil.";
	private static final String BPT_BPMN_11_CONFIG_PATH = BPT_BPMN_11_PATH + "config.";
	private static final String HEURISTICS_PATH = "heuristics.";
	
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
	 * @param name in a Microsoft Visio .vdx: A shape's attribute nameU
	 * @return a Stencil of BPMN or null, if there is no value defined for the given nameU
	 */
	public String getStencilIdForName(String name) {
		String stencilId = properties.getProperty(BPT_BPMN_11_STENCILS_PATH + name);
		return stencilId;
	}
	
	public String getStencilSetConfig(String key) {
		String stencilId = properties.getProperty(BPT_BPMN_11_CONFIG_PATH + key);
		return stencilId;
	}
	
	/**
	 * @param name in a Microsoft Visio .vdx: A shape's attribute nameU
	 * @return a Stencil of BPMN or null, if there is no value defined for the given nameU
	 */
	public String getValueForHeuristic(String name) {
		String stencilId = properties.getProperty(HEURISTICS_PATH + name);
		return stencilId;
	}
	
}
