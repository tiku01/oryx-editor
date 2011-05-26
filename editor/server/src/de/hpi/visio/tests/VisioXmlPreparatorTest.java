package de.hpi.visio.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.hpi.visio.VisioXmlPreparator;

/**
 * Test for the preparation (cleaning) of given visio .vdx-xml content. Tests
 * that the xml declaration is removed and also all attributes of the root
 * element. Furthermore the v14 (office version 14 - 2010) namespace and that
 * all <text></text> elements are clean - containing no child-elements.
 * 
 * @author Thamsen
 */
public class VisioXmlPreparatorTest {

	@Test
	public void testXmlPreparation() {
		VisioXmlPreparator preparator = new VisioXmlPreparator();
		String result = preparator.prepareXML(testXml());
		assertEquals(testShouldResultXml(), result);
	}

	public String testXml() {
		return "<?xml version='1.0' encoding='utf-8' ?>"
				+ "<VisioDocument key='C99A290682AF14E9753E65EA524CFBB45B975AD089FF447FF8A1E355AE96BE433DE5E05FCB0A77FB185E8A1887D2BDA28BA2B3EB9664FEE04E6C0CA1346DDE11'"
				+ " start='190' metric='0' DocLangID='1033' buildnum='4756' version='14.0' xml:space='preserve' xmlns='http://schemas.microsoft.com/visio/2003/core'"
				+ " xmlns:vx='http://schemas.microsoft.com/visio/2006/extension' xmlns:v14='http://schemas.microsoft.com/office/visio/2010/extension'>..."
				+ "<v14:Text> <cp IX='0'/> DO NOT REMOVE LABELS  <cp IX='0'/> <cp IX='0'/> <cp IX='0'/>  <v14:/Text:v14>"
				+ "<v14:Text> <cp IX='0'/> DO NOT REMOVE LABELS  <cp IX='0'/> <cp IX='0'/> <cp IX='0'/>  <v14:/Text:v14>";
	}

	public String testShouldResultXml() {
		return "<VisioDocument>"
				+ "...<Text>  DO NOT REMOVE LABELS      </Text><Text>  DO NOT REMOVE LABELS      </Text>";

	}

}
