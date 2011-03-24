package de.hpi.picture;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Test;

import de.hpi.pictureSupport.helper.PictureConverter;

public class ImportTest
{
	@Test
	public void Importglobbox() throws IOException, JSONException {		
		System.out.print(PictureConverter.importXML(PictureConverter.getXMLNamed("process1.xml")));
	}
}
