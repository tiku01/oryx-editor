package de.hpi.netgraph2xml.netgraph;

import org.xmappr.*;

import de.hpi.bpmn2xpdl.XMLConvertible;
@RootElement
public class ComponentLink extends XMLConvertible{
    @Attribute
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
