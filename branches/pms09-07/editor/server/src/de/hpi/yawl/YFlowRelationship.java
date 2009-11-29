package de.hpi.yawl;

/**
 * Copyright (c) 2009 Armin Zamani Farahani
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public abstract class YFlowRelationship {
	
	protected YNode source;
	protected YNode target;
	protected String id;
	
	public YNode getSource() {
		return source;
	}

	public void setSource(YNode value) {
		if (source != null)
			source.getOutgoingEdges().remove(this);
		source = value;
		if (source != null)
			source.getOutgoingEdges().add(this);
	}

	public YNode getTarget() {
		return target;
	}

	public void setTarget(YNode value) {
		if (target != null)
			target.getIncomingEdges().remove(this);
		target = value;
		if (target != null)
			target.getIncomingEdges().add(this);
	}
	
	//perhaps removable method
	public String getId() {
		if(id != null){
			return id;
		} else if(id == null && 
				this.getSource().getID() != null && 
				this.getTarget().getID() != null){
			return this.getSource().getID() + this.getTarget().getID();
		} else {
			return null;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

}
