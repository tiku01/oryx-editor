package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Categories")
public class XPDLCategories extends XMLConvertable {

	@Element("Category")
	protected ArrayList<XPDLCategory> categories;

	public void add(XPDLCategory newCategory) {
		initializeCategories();
		
		getCategories().add(newCategory);
	}
	
	public ArrayList<XPDLCategory> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<XPDLCategory> categories) {
		this.categories = categories;
	}
	
	protected void initializeCategories() {
		if (getCategories() == null) {
			setCategories(new ArrayList<XPDLCategory>());
		}
	}
}
