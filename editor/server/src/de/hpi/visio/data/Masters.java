package de.hpi.visio.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * Class for xmappr - xml to java mapping. - Shapes are linked to a master shape
 * and this master shape always has the original nameU value (stencil type) for
 * the linking shape.
 * 
 * @author Thamsen
 */
@RootElement("Masters")
public class Masters {

	@Element("Master")
	public List<Master> masters;

	/**
	 * @return a map to resolve shape's master id to the given master's
	 *         normalized nameU.
	 */
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
