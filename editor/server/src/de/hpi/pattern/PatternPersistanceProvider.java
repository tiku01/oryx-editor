package de.hpi.pattern;

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
