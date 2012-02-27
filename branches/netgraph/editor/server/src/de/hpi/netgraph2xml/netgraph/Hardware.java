package de.hpi.netgraph2xml.netgraph;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;
//TODO no representation in stencilset

@RootElement
public class Hardware extends XMLConvertible{
    @Element
    Integer ram;
    @Element
    Integer cpu_cores;
    
    public Integer getRam() {
        return ram;
    }
    public void setRam(Integer ram) {
        this.ram = ram;
    }
    public Integer getCpu_cores() {
        return cpu_cores;
    }
    public void setCpu_cores(Integer cpu_cores) {
        this.cpu_cores = cpu_cores;
    }

}
