/***************************************
 * Copyright (c) 2008
 * Helen Kaltegaertner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ****************************************/

package org.b3mn.poem.sketching;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class SVGPath {
	
	private Element path;
	private CSSStyleHandler styleHandler;
	private ArrayList<PathSection> corners;
	private boolean modified = false;
	
	public SVGPath(Element e, Document doc, CSSStyleHandler styleHandler){
		java.util.Locale.setDefault(java.util.Locale.US);
		
		this.styleHandler 	= styleHandler;
		this.path 			= e;
		this.corners 		= new ArrayList<PathSection>();
	}

	private void extractSections() {
		String d = this.path.getAttribute("d").replace(",", " ");
		Pattern pattern = Pattern.compile("(M|L|m|l)\\s*(-*((\\d+\\.\\d+)|\\d+)\\s*){2}");
		Matcher matcher = pattern.matcher(d);
		
		while (matcher.find()) {
			String type 		= matcher.group().substring(0,1);
			String numbers 		= matcher.group().substring(1).trim();
			String[] section 	= numbers.split("\\s+");
			
			if (section.length < 2){
				System.out.println(section.length + " " + matcher.group() + "type: " + type);
				continue;
			}
			double[] point = new double[2];
			point[0] = Double.parseDouble(section[0].trim());
			point[1] = Double.parseDouble(section[1].trim());
			this.corners.add(new PathSection(type, point));
		}	
	}
	
	public void transform() throws SketchyException{
		
		// for Data-based XOR Gateways the cross inside ist not a child but a child's child of the Gateway Node
		if (  ((Element)this.path.getParentNode()).getAttribute("title").contains("Gateway")
					|| ((Element)this.path.getParentNode().getParentNode()).getAttribute("title").contains("Gateway") ){
			
			transformGateway();
		}
		
		else if (!this.path.getAttribute("d").contains("C") && !this.path.getAttribute("d").contains("c")){
			this.extractSections();
			String d = String.format("M %.2f, %.2f ", this.corners.get(0).getX(), this.corners.get(0).getY());		
			for (int i = 1; i < this.corners.size(); i += 1){
				d += changeSection(this.corners.get(i-1), this.corners.get(i));
			}
			
			if (this.path.getAttribute("d").trim().endsWith("z") || this.path.getAttribute("d").trim().endsWith("Z"))
				d += "z";	
			
			this.path.setAttribute("d", d);
			this.changeStyle();
		}
		
	}

	private void transformGateway() throws SketchyException {
		if ( this.path.getAttribute("id").contains("frame") ){ 
			this.path.setAttribute("d", 
					"M 20, 1 L35, 15 Q 40, 20, 33, 25 L 25, 35" +
					"Q 20, 40, 15, 35 L 5, 25 Q 0, 20, 5, 15 L 25, 0");
//				this.path.setAttribute("d", 
//						"M15 0 L45 25 M45 15 L15 40 M20 40 L-5 15 M-5 25 L 25 0");
		}
		else if ( ((Element)this.path.getParentNode()).getAttribute("title").contains("Parallel") ){
			this.path.setAttribute("d", 
					"M 10 20 L32 20 M20 10 L21 32");
		}
		else if ( ((Element)this.path.getParentNode().getParentNode()).getAttribute("title").contains("Data-based") ){
			System.out.println("has d: " + (this.path.getAttribute("d") == "") );
			if(this.path.getAttribute("d") != ""){
				NodeList paths = ((Element)this.path.getParentNode()).getElementsByTagName("path");
				if (paths.getLength() != 2)
					throw new SketchyException("unexpected  SVG-representation for Data Based XOR Gateway");
				((Element) paths.item(0)).setAttribute("d", "M13 13 L 27 27 M 27 13 L 13 27");
				((Element) paths.item(1)).setAttribute("d", "");
			}
			
		}
		else if ( ((Element)this.path.getParentNode()).getAttribute("title").contains("Event-based") ){
			this.path.setAttribute("d", 
					"M15 0 L45 25 M45 15 L15 40 M20 40  L-5 15 M-5 25 L 25 0");
		}
		else if ( ((Element)this.path.getParentNode()).getAttribute("title").contains("Inclusive") ){
			this.path.setAttribute("d", 
					"M15 0 L45 25 M45 15 L15 40 M20 40 L-5 15 M-5 25 L 25 0");
		}
		this.changeStyle();
	}
	
	private String changeSection(PathSection from, PathSection to){	
				
		// in case of a relative target transform relative coordinates to absolutes
		if (to.getType().equals("l") || to.getType().equals("m")){
			to.setX(to.getX() + from.getX());
			to.setY(to.getY() + from.getY());
		}
		
		double length = Math.hypot(from.getX()-to.getX(), from.getY()-to.getY()) - 10;
		
		String d = "";
		if (to.getType().equals("M") || to.getType().equals("m"))
			return String.format("M %.2f, %.2f", to.getX(), to.getY());
		
		if (length < 40)
			return String.format("L %.2f, %.2f", to.getX(), to.getY());
		
		else {
			this.modified = true;
			// vertical line
			if (Math.abs(from.getX() - to.getX()) < 5)
				return verticalLine(from, to, length);

			// horizontal line
			if (Math.abs(from.getY() - to.getY()) < 5)
				return this.horizontalLine(from, to, length);
			
			// neither horizontal nor vertical
			d += this.diagonalLine(from, to, length);
		}
		
		// last section
		if (this.corners.indexOf(to) == this.corners.size()-1 /*&& length % 30 > 10*/)
			d += String.format("L %.2f, %.2f ", to.getX(), to.getY());	
			
		return d;
	}

	private String diagonalLine(PathSection from, PathSection to, double length) {
		String d = "";
		double angle 	= Math.atan((from.getY() - to.getY())/ (from.getX() - to.getX()));
		double dx 		= from.getX() < to.getX() ? 30 * Math.cos(angle) : -30* Math.cos(angle);
		double dy 		= from.getX() < to.getX() ? 30 * Math.sin(angle) : -30* Math.sin(angle);
		double refX 	= from.getX() + dx / 2;
		double destX 	= from.getX() + dx;
		double refY 	= from.getY() + dy / 2;
		double destY 	= from.getY() + dy;
		
		for (int i = 30; i < length; i += 30) {

			d += String.format("Q %.2f, %.2f, %.2f, %.2f", 
					refX - Math.random(), refY + Math.random(), destX + 1.5*Math.random(), destY - 1.5*Math.random());
			refX 	+= dx;
			destX 	+= dx;
			refY 	+= dy;
			destY 	+= dy;
		}
		d += String.format("L %.2f, %.2f ", to.getX()- 0.2*dx, to.getY() - 0.2*dy);	
		return d;
	}

	private String horizontalLine(PathSection from, PathSection to, double length) {
		String d = "";
		int direction = from.getX() < to.getX() ? 1 : -1;
		double destY = from.getY();
		double refY = from.getY();
		for (int x = 30; x < length; x += 30) {
			// less variance for destination than for the reference
			destY += (destY < from.getY()) ? Math.random() : -Math.random();
			refY += (refY < from.getY()) ? Math.random() * 2 : -Math.random() * 2;
			d += String.format("Q %.2f, %.2f, %.2f, %.2f", (x - 15)* direction + from.getX(), refY, (x * direction)+ from.getX(), destY);
		}
		d += String.format("Q %.2f, %.2f, %.2f, %.2f", to.getX() - 5* direction, to.getY(), to.getX() - 3 * direction, to.getY());
		return d;
	}

	private String verticalLine(PathSection from, PathSection to, double length) {
		String d = "";
		int direction = from.getY() < to.getY() ? 1 : -1;
		double destX = from.getX();
		double refX = from.getX();
		for (int y = 30; y < length; y += 30) {
			// less variance for destination than for the reference
			destX += (destX < from.getX()) ? Math.random() : -Math.random();
			refX += (refX < from.getX()) ? Math.random() * 2 : -Math.random() * 2;
			d += String.format("Q %.2f, %.2f, %.2f, %.2f", refX, (y - 15) * direction + from.getY(), destX, y* direction + from.getY());
		}
		d += String.format("Q %.2f, %.2f, %.2f, %.2f", to.getX(), to.getY()- 5 * direction, to.getX(), to.getY() - 3 * direction);
		return d;
	}
	
	private void changeStyle() {
		if (this.modified)
			this.path.setAttribute("fill", "none");
		this.path.setAttribute("stroke-width", "2");
		// set dasharray to longer strokes
		if (this.path.hasAttribute("stroke-dasharray")){
			this.path.setAttribute("stroke-dasharray", "25, 15");
		}
		
		if (this.path.hasAttribute("marker-end")){
			this.path.setAttribute("marker-end", "url(#oryx_arrow)");
		}
		
		if (this.path.getAttribute("display").equals(""))
			this.path.removeAttribute("display");
	}
	
	public Element getPath() {
		return path;
	}

	public void setPath(Element path) {
		this.path = path;
	}

	public CSSStyleHandler getStyleHandler() {
		return styleHandler;
	}

	public void setStyleHandler(CSSStyleHandler styleHandler) {
		this.styleHandler = styleHandler;
	}

	public ArrayList<PathSection> getCorners() {
		return corners;
	}

	public void setCorners(ArrayList<PathSection> corners) {
		this.corners = corners;
	}
}
