package de.hpi.netgraph2xml.netgraph;

import java.util.Collection;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;

@RootElement
public class OS extends XMLConvertible{
    @Element
    String cpe_name;
    @Element("vulnerability")
    Collection<Vulnerability> vulnerabilties;
    public String getCpe_name() {
        return cpe_name;
    }
    public void setCpe_name(String cpe_name) {
        this.cpe_name = cpe_name;
    }
    public Collection<Vulnerability> getVulnerabilties() {
        return vulnerabilties;
    }
    public void setVulnerabilties(Collection<Vulnerability> vulnerabilties) {
        this.vulnerabilties = vulnerabilties;
    }
    
}
