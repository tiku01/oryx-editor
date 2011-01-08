/**
 * This is an implementation of a matrix growing dynamically.
 * Copied from petrinet.layouting.PetriNetLayoutMatrix and
 * adapted to org.oryxeditor.server.diagram.Shape
 * @author Ole Eckermann, Kai Schlichting
 */

package de.hpi.olc;

import java.util.Vector;

import org.oryxeditor.server.diagram.Shape;

public class CpnLayoutMatrix {
	private final Vector<Vector<Shape>> data;
	public int sizeCols;
	public int sizeRows;
	
    public CpnLayoutMatrix() {
    	sizeCols = 0;
    	sizeRows = 0;
    	data = new Vector<Vector<Shape>>(sizeRows);
    }
    
    public void set(int row, int col, Shape val){
    	ensureSize(row+1, col+1);
    	
    	data.get(row).set(col, val);
    }
    
    public Shape get(int row, int col){
    	if(row >= sizeRows || col >= sizeCols)
    		return null;
    	
    	return data.get(row).get(col);
    }
    
    public boolean contains(Shape node){
    	for(Vector<Shape> col : data){
    		if(col.contains(node)) return true;
    	}
    	
    	return false;
    }
    
    protected void ensureSize(int sizeRows, int sizeCols){
    	ensureSizeRows(sizeRows);
    	ensureSizeCols(sizeCols);
    }
    
    protected void ensureSizeRows(int sizeRows){
    	if( this.sizeRows <= sizeRows ){
    		this.sizeRows = sizeRows;
    		while(data.size() <= sizeRows){
    			data.add(new Vector<Shape>());
    		}
    	}
    }
    
    protected void ensureSizeCols(int sizeCols){
    	if( this.sizeCols <= sizeCols ){
    		this.sizeCols = sizeCols;
    		for(Vector<Shape> col : data){
        		while(col.size() <= sizeCols){
        			col.add(null);
        		}
    		}
    	}
    }
}
