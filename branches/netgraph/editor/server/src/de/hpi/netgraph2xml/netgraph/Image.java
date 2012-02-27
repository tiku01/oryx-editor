package de.hpi.netgraph2xml.netgraph;

import java.util.Collection;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;
@RootElement
public class Image extends XMLConvertible{
    @Element
    OS os;
    @Element("user")
    Collection<User> users;
    @Element("program")
    Collection<Program> programs;
    @Element("file")
    Collection<File> files;
    @Element("forwarding")
    Collection<Forwarding> forwardings;
    public OS getOs() {
        return os;
    }
    public void setOs(OS os) {
        this.os = os;
    }
    public Collection<User> getUsers() {
        return users;
    }
    public void setUsers(Collection<User> users) {
        this.users = users;
    }
    public Collection<Program> getPrograms() {
        return programs;
    }
    public void setPrograms(Collection<Program> programs) {
        this.programs = programs;
    }
    public Collection<File> getFiles() {
        return files;
    }
    public void setFiles(Collection<File> files) {
        this.files = files;
    }
    public Collection<Forwarding> getForwardings() {
        return forwardings;
    }
    public void setForwardings(Collection<Forwarding> forwardings) {
        this.forwardings = forwardings;
    }
    
}
