package de.hpi.picture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import de.hpi.pictureSupport.PictureConverter;

public class ImportTest
{
	@Test
	public void Importglobbox() throws IOException
	{		
		System.out.print(PictureConverter.importXML(this.getXMLNamed("process1.xml")));
	}
	
	private String getXMLNamed(String filename) throws IOException
	{
		try
		{
			File f = new File(filename);

			FileReader fReader = new FileReader(f);
			BufferedReader bReader = new BufferedReader(fReader);
			String xml = "";
			while (bReader.ready())
			{
				xml += bReader.readLine();
			}
			
			return xml;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new IOException();
		}
		
		
	}

}
