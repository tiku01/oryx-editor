package de.hpi.visio.data;

import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Master")
public class Master {
		
	@Attribute("ID")
	public String masterId;
	
	@Attribute("NameU")
	public String name;
	
	public String getName() {
		if (name != null && name.contains("."))
			name = normalizeName(name);
		return name;
	}

	public String getMasterId() {
		return masterId;
	}
	
	private String normalizeName(String incorrectName) {
		int end = incorrectName.indexOf(".");
		String correctedName = incorrectName.substring(0,end);
		return correctedName;
	}

}
