package de.hpi.netgraph2xml.netgraph;

import java.util.ArrayList;
import java.util.Collection;

import org.xmappr.Element;

import de.hpi.bpmn2xpdl.XMLConvertible;

public class Rules extends XMLConvertible{
    public Rules() {
	rules = new ArrayList<Rule>();
    }
    @Element("rule")
    Collection<Rule> rules;

    public Collection<Rule> getRules() {
        return rules;
    }

    public void setRules(Collection<Rule> rules) {
        this.rules = rules;
    }

}
