package de.hpi.visio.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Masters")
public class Masters {
	
	@Element("Master")
	public List<Master> masters;
	
	public Map<String, String> getMasterIdToNameMapping() {
		HashMap<String, String> mapping = new HashMap<String, String>();
		for (Master master : masters) {
			if (master.getName() != null && !master.getName().equals("")) {
				if (master.getMasterId() != null && !master.getMasterId().equals(""))
					mapping.put(master.getMasterId(), master.getName());
			}
		}
		return mapping;
	}

}
