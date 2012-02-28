package de.hpi.netgraph2xml.netgraph;

import java.util.Collection;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;
@RootElement
public class Connection extends XMLConvertible{
    @Element
    NetworkLink network;
    @Element("gateway")
    Collection<Gateway> gateways;
    @Element("rules")
    Rules rules;
    public NetworkLink getNetwork() {
	return network;
    }
    public void setNetwork(NetworkLink network) {
	this.network = network;
    }
    public Collection<Gateway> getGateways() {
	return gateways;
    }
    public void setGateways(Collection<Gateway> gateways) {
	this.gateways = gateways;
    }
    public Rules getRules() {
	return rules;
    }
    public void setRules(Rules rules) {
	this.rules = rules;
    }
}
