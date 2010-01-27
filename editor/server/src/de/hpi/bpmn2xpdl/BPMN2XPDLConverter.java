package de.hpi.bpmn2xpdl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.DomElement;
import org.xmappr.Xmappr;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class BPMN2XPDLConverter {
	protected XMLConvertable convertObject;

	public XMLConvertable getConvertObject() {
		return convertObject;
	}

	public String exportXPDL(String json) throws JSONException {		
		XPDLPackage newPackage = new XPDLPackage();
		newPackage.parse(new JSONObject(json));
		
		StringWriter writer = new StringWriter();
		
		Xmappr xmappr = new Xmappr(XPDLPackage.class);
		xmappr.setPrettyPrint(true);
		xmappr.toXML(newPackage, writer);
		
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + writer.toString();
	}
	
	public String importXPDL(String xml) {
		StringReader reader = new StringReader(xml);
		
		Xmappr xmappr = new Xmappr(XPDLPackage.class);
		XPDLPackage newPackage = (XPDLPackage) xmappr.fromXML(reader);
		System.out.println(newPackage.getUnknownAttributes());
		/////////////////////////////////////////////////////////////
		DomElement test = null;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(newPackage.getUnknownChildren().get(0));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BASE64Encoder base64enc = new BASE64Encoder();
		String b64enc = base64enc.encode(byteStream.toByteArray());
		
		BASE64Decoder base64dec = new BASE64Decoder();
		try {			
			ByteArrayInputStream byteStreamIn = new ByteArrayInputStream(base64dec.decodeBuffer(b64enc));
			ObjectInputStream objectStreamIn = new ObjectInputStream(byteStreamIn);
			test = (DomElement) objectStreamIn.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		newPackage.getUnknownChildren().add(test);
		StringWriter writer = new StringWriter();
		xmappr = new Xmappr(XPDLPackage.class);
		xmappr.setPrettyPrint(true);
		xmappr.toXML(newPackage, writer);
		
		System.out.println(writer.toString());
		//////////////////////////////////////////////////////////////////////
		
		return "";
	}

	public void setConvertObject(XMLConvertable toConvert) {
		convertObject = toConvert;
	}
}
