package de.hpi.pictureSupport.helper;

import java.util.HashMap;
import java.util.Map;

public class BlockTypeMapping {

	private Map<String, String> mapping = new HashMap<String, String>();
	
	public BlockTypeMapping() {
		mapping.put("Dokument/Information entgegennehmen", "incomingdocument");
		mapping.put("Bearbeitungsunterbrechung", "pauseediting");
		mapping.put("Dokument/Information versenden", "outgoingdocument");
		mapping.put("Dokument sichten", "reviewdocument");
		mapping.put("Formelle Prüfung", "checkdocumentformally");
		mapping.put("Beratung durchführen", "doconsulting");
		mapping.put("Dokument/Information erstellen", "createdocument");
		mapping.put("Daten in EDV übernehmen", "edvinput");
		mapping.put("Drucken", "print");
		mapping.put("Kopieren", "copy");
	}
	
	public String getInternalStringFor(String key) {
		return mapping.get(key);
	}
}
