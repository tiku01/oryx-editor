package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("products")
public class PictureProducts {

	@Element(name="product", targetType=PictureProduct.class)
	protected ArrayList<PictureProduct> children;

	public ArrayList<PictureProduct> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureProduct> list) {
		children = list;
	}
}