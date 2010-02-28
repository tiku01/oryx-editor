/***************************************
 * Copyright (c) 2010 
 * Martin Krüger
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

package de.hpi.ViewGenerator;
import java.util.ArrayList;
import java.util.HashSet;

class ConnectorList {
//	holds the information of multiple Connectors
	private ArrayList<String> stencilList;
	private ArrayList<DataToSave> dataToSaveList;
	private HashSet<String> parentStencils;

	public ConnectorList() {
		this.stencilList = new ArrayList<String>();
		this.dataToSaveList = new ArrayList<DataToSave>();
		this.parentStencils = new HashSet<String>();
	}
	
	public void addConnector(Connector connector) {
		stencilList.add(connector.getStencilId());
		dataToSaveList.add(connector.getDataToSave());
		parentStencils.addAll(connector.getPossibleParentStencils());
	}

	private int findIndexOfConnectorWithStencil(String stencil) {
		return stencilList.indexOf(stencil);
	}
	
	public int size() {
		return stencilList.size();
	}
	
	public boolean containsConnectorWithStencil(String stencil) {
		return stencilList.contains(stencil);
	}
	
	public DataToSave dataToSaveForConnectorWithStencil(String stencil) {
		int index = findIndexOfConnectorWithStencil(stencil);
		try {
			return dataToSaveList.get(index);
		}
		catch (ArrayIndexOutOfBoundsException e){
			return null;
		}
	}
	
	public ArrayList<String> stencilLevelsToSaveForConnectorWithStencil(String stencil) {
		int index = findIndexOfConnectorWithStencil(stencil);
		return dataToSaveList.get(index).stencilLevelsToSave();
	}
	
	public ArrayList<String> attributesToSaveForConnectorWithStencil(String stencil) {
		int index = findIndexOfConnectorWithStencil(stencil);
		return dataToSaveList.get(index).attributesToSave();
	}
		
	public boolean isPossibleParentStencil(String stencilId) {
		return parentStencils.contains(stencilId);
	}
	
	
	public boolean shouldBeSaved(String stencil, String stencilToSave) {
		return stencilLevelsToSaveForConnectorWithStencil(stencil).contains(stencilToSave);
	}

}
