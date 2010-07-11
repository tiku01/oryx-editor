package de.hpi.visio;

public class VisioXmlPreparator {
	
	public String prepareXML(String xml) {
		xml = removeXmlDeclaration(xml);
		xml = removeAttributesFromRootElement(xml);
		xml = cleanAllTextElements(xml);
		xml = removeNamespacePrefix(xml);
		return xml;
	}

	private String removeXmlDeclaration(String xml) {
		xml = xml.substring(xml.indexOf(">") + 1);
		if (xml.startsWith("\n"))
			xml = xml.substring("\n".length());
		return xml;
	}
	
	
	// so that there will be a mapping with the declared xmappr-root 
	private String removeAttributesFromRootElement(String xml) {
		xml = xml.substring(xml.indexOf(">") + 1);
		xml = "<VisioDocument>" + xml;
		return xml;
	}
	
	// It's possible that the <Text>-elements in Visio have own children,
	// but xMappr needs plainText-<Text>-elements to convert to String member.
	private String cleanAllTextElements(String xml) {
		String result = "";
		// -1 means there is no matching substring
		while (xml.indexOf("<Text>") != -1) {
			int textStart = xml.indexOf("<Text>");
			int textEnd = xml.indexOf("</Text>");
			result += xml.substring(0,textStart);
			String cleanedTextElement = getCleanedTextElement(xml.substring(textStart + "<Text>".length(), textEnd));
			result += "<Text>" + cleanedTextElement + "</Text>";
			xml = xml.substring(textEnd + "</Text>".length(), xml.length());
		}
		return result + xml;
	}
	
	private String removeNamespacePrefix(String xml) {
		if (xml.contains(":v14") || xml.contains("v14:")) {
			xml = xml.replaceAll(":v14", "");
			xml = xml.replaceAll("v14:", "");
		}
		return xml;
	}

	private String getCleanedTextElement(String text) {
		while (text.contains("<") || text.contains("/>")) {
			int elementStart = text.indexOf("<");
			int elementEnd = text.indexOf(">");
			text = text.substring(0, elementStart) + text.substring(Math.min(elementEnd + 1, text.length())); // minimum to stay in array length
			if (text.contains("\\"))
				text = text.substring(2, text.length()); // removes the \n per <..>-element...
		}
		return text;
	}
	
}
