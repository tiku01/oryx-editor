/**
 * @author Ole Eckermann
 */

package de.hpi.olc;

import java.util.LinkedList;
import java.util.List;

import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;


public class CpnLayouter {
	Diagram net;
	CpnLayoutMatrix matrix;
	List<Shape> examinedNodes;
	
	public CpnLayouter(Diagram net){
		this.net = net;
	}
	
	public void layout(){
		matrix = new CpnLayoutMatrix();
		examinedNodes = new LinkedList<Shape>();
		
		buildLayoutMatrix();
		
		setBounds();
	}
	
	private void setBounds() {
		for(int row = 0; row < matrix.sizeRows; row ++){
			for(int col = 0; col < matrix.sizeCols; col ++){
				Shape node = matrix.get(row, col);
				if(node != null){
					int height = 0;
					int width = 0;
					int margin = 80; //distance from left and upper corner of oryx canvas
					int x = margin + 100*col; //center x
					int y = margin + 100*row; // center y
					if(node.getStencil().equals("Place")){
						height = 30;
						width = 30;
					} else if (node.getStencil().equals("Transition") && node.getProperty("title").length() > 0) {
						height = 40;
						width = 80;
					} else if (node.getStencil().equals("Transition") && node.getProperty("title").length() == 0) {
						height = 50;
						width = 10;
					}
					Bounds bounds = new Bounds(new Point(x-width/2.0, y-height/2.0), new Point(x+width/2.0, y+height/2.0));
					node.setBounds(bounds);
				}
			}
		}
	}

	public void buildLayoutMatrix(){
		takeStep(getStartNodes(), 0);
	}
	
	public void takeStep(List<Shape> nodes, int step){
		if(nodes.size() == 0) return;
		
		List<Shape> nextNodes = new LinkedList<Shape>();

		int i = 0;
		for(Shape node : nodes){
			// Set position in layouting matrix
			matrix.set(i, step, node);
			
			addNextNodes(nextNodes, node);
			
			i++;
		}
		
		step++;
		takeStep(nextNodes, step);
	}
	
	public void addNextNodes(List<Shape> nextNodes, Shape node){
		for(Shape arc : node.getOutgoings()){
			if(		// If next node isn't already in nextNodes (e.g. if node has multiple incoming arcs)
					!nextNodes.contains(arc.getTarget()) && 
					// If next node hasn't already been added to matrix
					!matrix.contains(arc.getTarget())
			){
				nextNodes.add(arc.getTarget());
			}
		}
	}

	public List<Shape> getStartNodes(){
		List<Shape> startNodes = new LinkedList<Shape>();
		
		for(Shape shape : net.getChildShapes()){
			if(shape.getIncomings().size() == 0 && !shape.getStencil().equals("Arc")){
				startNodes.add(shape);
			}
		}
		
		return startNodes;
	}
}
