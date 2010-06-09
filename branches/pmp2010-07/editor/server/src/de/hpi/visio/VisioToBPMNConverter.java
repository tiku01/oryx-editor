package de.hpi.visio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

import javax.sql.rowset.spi.XmlReader;

import org.apache.fop.image.analyser.XMLReader;
import org.xmappr.Xmappr;

import de.hpi.visio.data.VisioDocument;
import de.hpi.visio.util.MappingConfigurationUtil;

public class VisioToBPMNConverter {
	
	private MappingConfigurationUtil mappingUtil;

	public VisioToBPMNConverter(String realPath) {
		mappingUtil = new MappingConfigurationUtil(realPath);
	}

	public static void importVisioData(String xml) {
		Reader reader = new StringReader(xml);
		Xmappr xmappr = new Xmappr(VisioDocument.class);
		System.out.println(xmappr.getXmlConfiguration(VisioDocument.class));
		VisioDocument document = (VisioDocument) xmappr.fromXML(reader);
		System.out.println(document);
	}
	
	// testing purpose
	public static void main(String[] args) {
		try {
			byte[] buffer = new byte[(int) new File("/Users/Thamsen/Desktop/test.vdx").length()];
		    BufferedInputStream f = new BufferedInputStream(new FileInputStream("/Users/Thamsen/Desktop/test2.xml"));
		    f.read(buffer);
			importVisioData(new String(buffer));
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Put a good vdx in place...");
		}
	}

}
