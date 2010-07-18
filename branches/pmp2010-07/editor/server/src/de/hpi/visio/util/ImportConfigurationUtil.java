package de.hpi.visio.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Provides all properties for visio import: Uses the
 * VisioImportConfiguration.xml
 * 
 * @author Thamsen
 */
public class ImportConfigurationUtil {

	private static final String CONFIGURATION_XML_FILE = "VisioImportConfiguration.xml";

	private String contextPath; // path to get the configuration xml
	private Properties properties;

	private String stencilSetPath;
	private String stencilSetMappingConfigPath;
	private String stencilSetConfigPath;
	private String heuristicPath;
	private String stencilSetHeuristicsPath;

	public ImportConfigurationUtil(String contextPath, String type) {
		this.contextPath = contextPath;
		initializeTheProperties();

		// import configuration paths
		heuristicPath = "heuristics.";
		// stencil set specifics
		stencilSetPath = "stencilsets." + type + ".";
		stencilSetMappingConfigPath = "stencilsets." + type + ".config.";
		stencilSetConfigPath = "oryx.config." + type + ".";
		stencilSetHeuristicsPath = "heuristics." + type + ".";
	}

	private void initializeTheProperties() {
		Properties newProperties;
		FileInputStream in;
		try {
			in = new FileInputStream(contextPath + CONFIGURATION_XML_FILE);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Not able to load the mapping properties for Visio impor.", e1);
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
					throw new RuntimeException("Not able to create the Visio Stencil Set to BPMN mapping properties.",
							e);
				}
			}
		}
	}

	public String getStencilIdForName(String name) {
		String mappingsString = properties.getProperty(stencilSetPath + "mappings");
		if (mappingsString == null || "".equals(mappingsString)) {
			throw new RuntimeException("No mappings defined for the given stencilset: "
					+ "Check VisioImportConfiguration.xml or define mappings for this stencil set.");
		}
		String[] stencilSets = mappingsString.split(",");
		for (String stencilSet : stencilSets) {
			String stencilIdForName = properties.getProperty(stencilSetPath + stencilSet + ".stencil." + name);
			if (stencilIdForName != null)
				return stencilIdForName;
		}
		return null;
	}

	/**
	 * Additional mapping configuration for shape merging (shape is only a
	 * marker for another shape (e.g., +-marker element to make a task to a
	 * collapsed subprocess) and to set additional properties (e.g.,
	 * istransaction or is MI-task)
	 */
	public String getMappingConfig(String key) {
		return properties.getProperty(stencilSetMappingConfigPath + key);
	}

	/**
	 * Additional configuration for the stencil set at oryx: Minimal sizes or
	 * fixed sizes for some elements of a stencilset can be defined
	 */
	public String getStencilSetConfig(String key) {
		return properties.getProperty(stencilSetConfigPath + key);
	}

	/**
	 * Heuristic values for given keys. Values can be changed in the
	 * configuration xml and are defined for free-text-interpretation, default
	 * page sizes, size-unit-exchange-factor from visio to oryx and for
	 * edge-assignment.
	 */
	public String getHeuristic(String key) {
		return properties.getProperty(heuristicPath + key);
	}

	/**
	 * Stencil set specific heuristic values that can be defined and used in
	 * interpretation (e.g., What size must a task have to be interpreted as
	 * subprocess)
	 */
	public String getStencilSetHeuristic(String key) {
		return properties.getProperty(stencilSetHeuristicsPath + key);
	}

}
