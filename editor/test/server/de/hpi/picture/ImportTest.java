package de.hpi.picture;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Test;

import de.hpi.pictureSupport.PictureConverter;

public class ImportTest
{
	@Test
	public void Importglobbox() throws IOException, JSONException {		
		System.out.print(PictureConverter.importXML(PictureConverter.getXMLNamed("process1.xml")));
	}
}
