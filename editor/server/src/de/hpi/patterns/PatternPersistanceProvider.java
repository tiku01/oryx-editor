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

import java.util.List;

import org.json.JSONString;

public interface PatternPersistanceProvider extends JSONString{
	
	/**
	 * Add the supplied serialized pattern.
	 * 
	 * @param serializedPattern
	 * @return the pattern
	 * @throws PatternPersistanceException 
	 */
	public Pattern addPattern(Pattern p) throws PatternPersistanceException;
	
	public Pattern replacePattern(Pattern p) throws PatternPersistanceException; //TODO javadoc
	
	/**
	 * return the save pattern with the given id
	 * 
	 * @param id
	 * @return the saved pattern if exists
	 * 
	 */
	public Pattern getPattern(int id);
	
	/**
	 * deletes the specified pattern
	 * @param p
	 * @throws PatternPersistanceException 
	 */
	public void removePattern(Pattern p) throws PatternPersistanceException;
	
	/**
	 * returns all patterns for the stencilset
	 * @return list of patterns
	 */
	public List<Pattern> getAll();
}
