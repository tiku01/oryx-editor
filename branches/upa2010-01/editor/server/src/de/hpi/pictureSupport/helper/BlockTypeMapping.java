package de.hpi.pictureSupport.helper;

import java.util.HashMap;
import java.util.Map;

public class BlockTypeMapping {

	private static Map<String, String> mapping = new HashMap<String, String>();
	
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
	
	public static String getInternalStringFor(String key) {
		return mapping.get(key);
	}
}
