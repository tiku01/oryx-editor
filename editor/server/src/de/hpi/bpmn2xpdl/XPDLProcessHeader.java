package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLProcessHeader extends XMLConvertable {

	protected String durationUnit;
	
	protected String created;
	protected String description;
	protected String priority;
	protected String limit;
	protected String validFrom;
	protected String validTo;
	
	protected XPDLTimeEstimation timesEstimation;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:ProcessHeader", XPDLProcessHeader.class);
		
		xstream.useAttributeFor(XPDLProcessHeader.class, "durationUnit");
		xstream.aliasField("DurationUnit", XPDLProcessHeader.class, "durationUnit");
		
		xstream.aliasField("xpdl2:Created", XPDLProcessHeader.class, "created");
		xstream.aliasField("xpdl2:Description", XPDLProcessHeader.class, "description");
		xstream.aliasField("xpdl2:Priority", XPDLProcessHeader.class, "priority");
		xstream.aliasField("xpdl2:Limit", XPDLProcessHeader.class, "limit");
		xstream.aliasField("xpdl2:ValidFrom", XPDLProcessHeader.class, "validFrom");
		xstream.aliasField("xpdl2:ValidTo", XPDLProcessHeader.class, "validTo");
		xstream.aliasField("xpdl2:TimeEstimation", XPDLProcessHeader.class, "timeEstimation");
	}

	public String getDurationUnit() {
		return durationUnit;
	}

	public String getCreated() {
		return created;
	}

	public String getDescription() {
		return description;
	}

	public String getPriority() {
		return priority;
	}

	public String getLimit() {
		return limit;
	}

	public XPDLTimeEstimation getTimesEstimation() {
		return timesEstimation;
	}

	public String getValidFrom() {
		return validFrom;
	}

	public String getValidTo() {
		return validTo;
	}

	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public void setTimesEstimation(XPDLTimeEstimation timesEstimation) {
		this.timesEstimation = timesEstimation;
	}

	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
	}

	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}
}
