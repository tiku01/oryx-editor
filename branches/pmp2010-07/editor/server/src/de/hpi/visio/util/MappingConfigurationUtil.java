package de.hpi.visio.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Provides an entry to the VisioBPMNConfiguration.xml and is used to map Visio 2003 Stencilset 
 * values to BPMN properties
 */
public class MappingConfigurationUtil {
	
	private static final String CONFIGURATION_XML_FILE = "VisioBPMNConfiguration.xml";
	private static final String BPT_BPMN_11_Path = "stencilsets.BPTBPMN11.";
	
	private Properties properties;
	private String path;
	
	/*
	 * Important to do: The path shouldn't be passed through the VisioToBPMNConverter
	 * all down from the servlet. There have to be a better solution!!
	 * In a better solution properties would be final while die initializeTheProperties
	 * would be static {...} code running once, so that the properties aren't reread
	 * for each call. Therefore methods and members should also be static.
	 */
	
	public MappingConfigurationUtil(String realPath) {
		path = realPath;
		initializeTheProperties();
	}
	
	private void initializeTheProperties() {
		Properties newProperties = new Properties();
		ClassLoader cl = MappingConfigurationUtil.class.getClassLoader();
		InputStream in = cl.getResourceAsStream(path + CONFIGURATION_XML_FILE);
		if (in != null) {
			try {
				newProperties.loadFromXML(in);
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
		properties = newProperties;
		if (properties == null)
			throw new IllegalStateException("Not able to create the Visio Stencil Set to BPMN mapping properties.");
	}

	/**
	 * @param nameU in a Microsoft Visio .vdx: A shape's attribute nameU
	 * @return a Stencil of BPMN or null, if there is no value defined for the given nameU
	 */
	public String getStencilIdForNameU(String nameU) {
		String type = properties.getProperty(BPT_BPMN_11_Path + nameU);
		return type;
	}
	
}
