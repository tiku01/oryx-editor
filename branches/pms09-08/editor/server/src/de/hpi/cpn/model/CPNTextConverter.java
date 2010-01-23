package de.hpi.cpn.model;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class CPNTextConverter implements Converter
{
	        public boolean canConvert(Class clazz) {
	                return clazz.equals(CPNText.class);
	        }

	        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context)
	        {
	                CPNText cpnText = (CPNText) value;
	                writer.addAttribute("tool", cpnText.getTool());
	                writer.addAttribute("version", cpnText.getVersion());
	                writer.setValue(cpnText.getText());
	        }

	        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) 
	        {	
	        	CPNText cpnText = new CPNText();
	        	
	        	cpnText.setText(reader.getValue());
	        		        	
	        	return cpnText;	    	       
	        }
}
