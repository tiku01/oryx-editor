package de.hpi.netgraph2xml.netgraph;

import org.xmappr.Attribute;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;
@RootElement
public class NetworkLink extends XMLConvertible{
    @Attribute
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
