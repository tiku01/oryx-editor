package de.hpi.cpn.mapperhelper;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DOMValidateDTD {
  public static void main(String args[]) {  
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(new org.xml.sax.ErrorHandler() {
        //Ignore the fatal errors
        public void fatalError(SAXParseException exception)throws SAXException { }
        //Validation errors 
        public void error(SAXParseException e)throws SAXParseException {
          System.out.println("Error at " +e.getLineNumber() + " line.");
          System.out.println(e.getMessage());
          System.exit(0);
        }
        //Show warnings
        public void warning(SAXParseException err)throws SAXParseException{
          System.out.println(err.getMessage());
          System.exit(0);
        }
      });
      Document xmlDocument = builder.parse(new FileInputStream("cpnxml.xml"));
      DOMSource source = new DOMSource(xmlDocument);
      StreamResult result = new StreamResult(System.out);
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "cpndtd.dtd");
      transformer.transform(source, result);
    }
    catch (Exception e) 
    {
      System.out.println(e.getMessage());
    }
  }
}