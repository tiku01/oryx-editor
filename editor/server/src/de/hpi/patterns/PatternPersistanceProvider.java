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

/**
 * Provides a consistent interface for classes that persist pattern.
 * Each Class that wishes to provide means for the persistence of Patterns shall
 * implement this interface.
 * 
 * @author Kai Höwelmeyer
 *
 */
public interface PatternPersistanceProvider extends JSONString{
	
	/**
	 * Add the supplied serialized pattern.
	 * 
	 * @param serializedPattern
	 * @return the pattern
	 * @throws PatternPersistanceException 
	 */
	public Pattern addPattern(Pattern p) throws PatternPersistanceException;
	
	/**
	 * Updates the supplied pattern in the backend storage with the provided values. Missing values 
	 * will be assumed empty.
	 * @param p a pattern featuring the new values to be set
	 * @return updated pattern as stored in backend storage
	 * @throws PatternPersistanceException if saving was not possible
	 */
	public Pattern updatePattern(Pattern p) throws PatternPersistanceException;
	
	/**
	 * Return the saved pattern with the supplied id.
	 * 
	 * @param id
	 * @return the saved pattern if exists null otherwise
	 * 
	 */
	public Pattern getPattern(int id); //throw Exception???
	
	/**
	 * Deletes the specified pattern.
	 * @param p Pattern to be deleted.
	 * @throws PatternPersistanceException 
	 */
	public void removePattern(Pattern p) throws PatternPersistanceException;
	
	/**
	 * Returns all patterns for the stencilset
	 * @return List of patterns
	 */
	public List<Pattern> getAll();
	
	/**
	 * Produces a JSON representation of all patterns
	 * @return String JSON array of pattern in JSON format
	 */
	@Override
	public String toJSONString();
}
