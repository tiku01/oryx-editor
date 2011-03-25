package de.hpi.pictureSupport.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * The BlockTypeMapping Class for mapping PICTURE names to Oryx-PICTURE names.
 * <key>	PICTURE name (specified in XML)
 * <value>	Oryx-PICTURE name (specified node IDs in picture.json)
 */
public class BlockTypeMapping {

	private static Map<String, String> mapping = new HashMap<String, String>();
	
	/**
	 * Prepare the mapping of the names.
	 */
	public static void prepareMapping() {
		mapping.put("Dokument/Information entgegennehmen", "incomingdocument");
		mapping.put("Bearbeitungsunterbrechung", "pauseediting");
		mapping.put("Dokument/Information versenden", "outgoingdocument");
		mapping.put("Dokument sichten", "reviewdocument");
		mapping.put("Formelle Pruefung", "checkdocumentformally");
		mapping.put("Beratung durchfuehren", "doconsulting");
		mapping.put("Dokument/Information erstellen", "createnewdocument");
		mapping.put("Daten in EDV uebernehmen", "edvinput");
		mapping.put("Drucken", "print");
		mapping.put("Kopieren", "copy");
	}
	
	/**
	 * Gets the internal Oryx-PICTURE name for a given PICTURE name.
	 *
	 * @param key the key
	 * @return the internal string for
	 */
	public static String getInternalStringFor(String key) {
		return mapping.get(key);
	}
}
