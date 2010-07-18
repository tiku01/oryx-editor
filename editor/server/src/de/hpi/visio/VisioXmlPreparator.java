package de.hpi.visio;

/**
 * Prepares visio vdx-files (xml) for import to java through the xmappr-library:
 * - removes the namespaces of Visio 2010 
 * - removes the xml-declaration 
 * - removes all attributes from the root-element 
 * - removes all child-elements in <Text></Text>-Elements
 * 
 * @author Thamsen
 */
public class VisioXmlPreparator {

	private StringBuilder stringBuilder;

	/**
	 * Returns a cleaned version of the given visio-xml, so that it can be
	 * imported correctly through the xmappr-library. (See class comment for
	 * additional information).
	 */
	public String prepareXML(String xml) {
		xml = removeVisio2010Namespaces(xml);
		// uses stringbuilder to prevend reallocation that comes with string
		// catenations
		stringBuilder = new StringBuilder(xml.length());
		int startIndex = getStartIndexWithoutXmlDeclaration(xml);
		int afterRootElementIndex = removeAttributesFromRootElement(xml, startIndex);
		cleanAllTextElements(xml, afterRootElementIndex);
		return stringBuilder.toString();
	}

	// v14 as office number - namespace - is used in visio 2010 only and can't
	// be parsed by xmappr
	private String removeVisio2010Namespaces(String xml) {
		return xml.replaceAll(":v14|v14:", "");
	}

	// xmappr needs the root-element of xml to be first
	private int getStartIndexWithoutXmlDeclaration(String xml) {
		int startIndex = xml.indexOf(">") + 1;
		if (xml.startsWith("\n"))
			startIndex += "\n".length();
		return startIndex;
	}

	// so that there will be a mapping with the declared xmappr-root
	// reason: if the root-element has a lot of attributes xmappr tends to
	// not recognize it
	private int removeAttributesFromRootElement(String xml, int startIndex) {
		int afterRootElement = xml.indexOf(">", startIndex) + 1;
		stringBuilder.append("<VisioDocument>");
		return afterRootElement;
	}

	// It's possible that the <Text>-elements in Visio have own children,
	// but xMappr needs plainText-<Text>-elements to convert to String member.
	private void cleanAllTextElements(String xml, int afterRootElementIndex) {
		int beginOfSequence = afterRootElementIndex;
		while (xml.indexOf("<Text>", beginOfSequence) != -1) {
			int textStart = xml.indexOf("<Text>", beginOfSequence);
			int textEnd = xml.indexOf("</Text>", beginOfSequence);
			stringBuilder.append(xml.substring(beginOfSequence, textStart));
			beginOfSequence = textEnd + "</Text>".length();

			// removes child element of text-elements
			String appendString = getCleanedTextElementContent(xml.substring(textStart + "<Text>".length(), Math.min(
					textEnd, xml.length())));
			appendString = "<Text>" + appendString + "</Text>";
			stringBuilder.append(appendString);
		}
		stringBuilder.append(xml.substring(Math.min(beginOfSequence, xml.length())).trim());
	}

	private String getCleanedTextElementContent(String text) {
		while (text.contains("<") || text.contains("/>")) {
			int elementStart = text.indexOf("<");
			int elementEnd = text.indexOf(">");
			text = text.substring(0, elementStart) + text.substring(Math.min(elementEnd + 1, text.length()));
			// minimum to stay in array length
			if (text.contains("\\"))
				// removes the \n per <..>-element...
				text = text.substring(2, text.length());
		}
		return text;
	}

}
