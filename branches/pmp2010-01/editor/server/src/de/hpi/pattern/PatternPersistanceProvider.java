package de.hpi.pattern;

import java.util.Collection;
import java.util.List;

public interface PatternPersistanceProvider {
	
	/**
	 * Save the supplied serialized pattern.
	 * 
	 * @param serializedPattern
	 * @return the id of the pattern
	 */
	public Pattern setPattern(Pattern p);
	
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
	 */
	public void removePattern(Pattern p);
	
	/**
	 * returns all patterns for the stencilset
	 * @return list of patterns
	 */
	public List<Pattern> getAll();
}
