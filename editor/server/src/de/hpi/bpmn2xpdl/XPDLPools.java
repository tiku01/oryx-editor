package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Pools")
public class XPDLPools extends XMLConvertable {

	@Element("Pool")
	protected ArrayList<XPDLPool> pools;

	public void add(XPDLPool newPool) {
		initializePools();
		
		getPools().add(newPool);
	}
	
	public ArrayList<XPDLPool> getPools() {
		return pools;
	}

	public void setPools(ArrayList<XPDLPool> pool) {
		this.pools = pool;
	}
	
	protected void initializePools() {
		if (getPools() == null) {
			setPools(new ArrayList<XPDLPool>());
		}
	}
}
