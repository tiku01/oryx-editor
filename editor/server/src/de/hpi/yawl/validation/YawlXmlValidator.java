package de.hpi.yawl.validation;

import javax.xml.validation.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class YawlXmlValidator {
	
	private Validator validator;
	
	public YawlXmlValidator(){
		instantiate();
	}
	
	private void instantiate(){
		//1. Lookup a factory for the YAWL XML Schema language
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		
		//2. Compile the schema.
		try {
			URL schemaLocation = new URL("http://www.yawlfoundation.org/yawlschema/YAWL_Schema2.0.xsd");
			Schema schema = factory.newSchema(schemaLocation);
			
			//3. Get a validator from the schema
			this.validator = schema.newValidator();
			
		} catch (MalformedURLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (SAXException ex) {
			ex.printStackTrace();
		}
	}
	public Boolean validate(File file){
		try{
			//4. Parse the document you want to check
			Source source = new StreamSource(file);

			//5. Check the document
			this.validator.validate(source);
			System.out.println("Generated YAWL file is valid.\n");
			return true;
		} catch (SAXException ex) {
			System.out.println("Generated YAWL file is not valid because ");
			System.out.println(ex.getMessage());
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return false;
	}
}

