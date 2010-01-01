package de.hpi.yawl;

import java.util.Date;

public class YTimer implements FileWritingForYAWL {

	public enum Trigger {
		OnExecuting, OnEnabled
	}
	
	private Trigger trigger;
	private String duration = "";
	private Date expiry;
	
	public YTimer(Trigger trigger, String duration, Date expiry) {
		super();
		setTrigger(trigger);
		setDuration(duration);
		setExpiry(expiry);
	}
	
	public Trigger getTrigger() {
		return trigger;
	}
	
	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}
	
	public String getDuration() {
		return duration;
	}
	
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public Date getExpiry() {
		return expiry;
	}
	
	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}
	
	public String writeToYAWL(){
		String s = "";
		s += "\t\t\t\t\t<timer>\n";
		s += String.format("\t\t\t\t\t\t<trigger>%s</trigger>\n", getTrigger().toString());
		if(duration.isEmpty())
			s += String.format("\t\t\t\t\t\t<expiry>%s</expiry>\n", getExpiry().getTime());
		else
			s += String.format("\t\t\t\t\t\t<duration>%s</duration>\n", getDuration());
		
		s += "\t\t\t\t\t</timer>\n";
		
		return s;
	}
}
