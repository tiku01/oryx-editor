package de.hpi.netgraph2xml.netgraph;

import java.util.Collection;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;
@RootElement
public class Routing extends XMLConvertible{
    @Element("network")
    Collection<NetworkLink> networks;

    public Collection<NetworkLink> getNetworks() {
        return networks;
    }

    public void setNetworks(Collection<NetworkLink> networks) {
        this.networks = networks;
    }

}
