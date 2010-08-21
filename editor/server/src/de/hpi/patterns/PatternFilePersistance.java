/**
 * Copyright (c) 2010
 * 
 * Kai Höwelmeyer
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
 * 
 **/
package de.hpi.patterns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.json.JSONArray;

/**
 * Stores pattern serialized in a simple file per stencilset. Not safe for multi-user environment, 
 * because Dirty Reads, Non-repeatable reads, lost updates and alike are all possible and not prevented.
 * This is a transitional implementation.
 * 
 * @author Kai Höwelmeyer
 *
 */
public class PatternFilePersistance implements PatternPersistanceProvider {
	/**
	 * Filename of file used for saving the pattern. Is determined by stencilset namespace.
	 */
	private File patternFile;
	
	/**
	 * All pattern that are read from the file
	 */
	private ArrayList<Pattern> patternList;
	
	/**
	 * Next id for newly saved pattern
	 */
	private int currentID;
	
	/**
	 * Logger
	 */
	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * Simple Constructor. Loads Pattern and sets their repository to this repository.
	 * @param ssNameSpace namespace of the current stencil set in the editor
	 * @param baseDir Directory in which all pattern files are stored
	 * @throws PatternPersistanceException
	 */
	public PatternFilePersistance(String ssNameSpace, String baseDir) throws PatternPersistanceException {
		this.patternFile = new File(baseDir + generatePatternFileName(ssNameSpace));
		
		this.loadPattern();
		this.initPattern();
	}

	/**
	 * Produces the filename corresponding to the current stencilset.
	 * @param ssNameSpace Current stencilset
	 * @return Filename for this stencilset
	 */
	private static String generatePatternFileName(String ssNameSpace) {
		String fileName = new Integer(ssNameSpace.hashCode()).toString();
		fileName = fileName.replace("-", "m"); //replaces leading "-" from negative integer
		fileName += ".patternStore";
		return fileName;
	}

	/**
	 * Sets this repository as repository in each pattern.
	 */
	private void initPattern() {
		ListIterator<Pattern> it = this.patternList.listIterator();
		while(it.hasNext()) {
			it.next().setRepos(this);
		}		
	}

	/**
	 * Loads the currentId as well as all pattern from the file using deserialization.
	 * @throws PatternPersistanceException if file could not be read or is not valid 
	 * serialization of pattern repository.
	 */
	@SuppressWarnings("unchecked")
	private void loadPattern() throws PatternPersistanceException {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(this.patternFile);
			ois = new ObjectInputStream(fis);
			this.currentID = ois.readInt();
			this.patternList = (ArrayList<Pattern>) ois.readObject();
		} catch (FileNotFoundException e) { //if directory could not be found
			this.currentID = 0;				// pattern repos is initialized anyway and will fail on add.
			this.patternList = new ArrayList<Pattern>();
		} catch (IOException e) {
			this.log.error("Error while reading file " + this.patternFile, e);
			throw new PatternPersistanceException("Could not read file.", e);
		} catch (ClassNotFoundException e) {
			this.log.error("Error while reading file " + this.patternFile + "\n" +
					"Class could not be found!", e);
			throw new PatternPersistanceException("Could not read file.", e);
		} finally {
			try {
				if (ois != null) ois.close();
				if (fis != null) fis.close();
			} catch (IOException e) {
				this.log.error("Error while closing file " + this.patternFile, e);
				throw new PatternPersistanceException("Could not close file.", e);
			}
		}
	}
	
	
	@Override
	public void removePattern(Pattern p) throws PatternPersistanceException {
		this.patternList.remove(p);
		commit();
	}
	
	@Override
	public Pattern getPattern(int id) {
		ListIterator<Pattern> it = this.patternList.listIterator();
		while(it.hasNext()) {
			Pattern p = it.next();
			if(p.getId() == id)
				return p;  //found the matching pattern
		}
		return null; //didn't find the pattern
	}
	
	@Override
	public Pattern addPattern(Pattern p) throws PatternPersistanceException {
		p.setId(this.generateNewId());
		p.setRepos(this);
		this.patternList.add(p);
		this.commit();
		return p;
	}

	/**
	 * Returns unique IDs for new pattern.
	 * @return Stencilset unique id
	 */
	private int generateNewId() {
		return this.currentID++;
	}

	@Override
	public Pattern updatePattern(Pattern p) throws PatternPersistanceException {
		
		ListIterator<Pattern> it = this.patternList.listIterator();

		while(it.hasNext()){
			Pattern currentPattern = it.next();
			if (currentPattern.equals(p)) {
				it.set(p);
				p.setRepos(this); //new pattern has repos == null
				break;
			}
		}
		this.commit();
		return p;
		
	}

	/**
	 * Writes the current pattern repository including the currentId and all patterns
	 * to the file via serialization.
	 * @throws PatternPersistanceException If the file could not be written to
	 */
	private void commit() throws PatternPersistanceException {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try {
			fos = new FileOutputStream(this.patternFile);
			oos = new ObjectOutputStream(fos);
			oos.writeInt(this.currentID);
			oos.writeObject(this.patternList);
		} catch (IOException e) {
			this.log.error("Error while writing file " + this.patternFile, e);
			throw new PatternPersistanceException("Could not write file.", e);
		} finally {
			try {
				if (oos != null) oos.close();
				if (fos != null) fos.close();
			} catch (IOException e) {
				this.log.error("Error while closing file " + this.patternFile, e);
				throw new PatternPersistanceException("Could not close file.", e);
			}
		}
		
	}
	
	@Override
	public List<Pattern> getAll() {
		return this.patternList;
	}

	@Override
	public String toJSONString() {
		JSONArray ja = new JSONArray();
		
		ListIterator<Pattern> it = this.patternList.listIterator();
		while(it.hasNext()) {
			ja.put(it.next().toJSONObject());
		}
		
		return ja.toString();
	}
}
