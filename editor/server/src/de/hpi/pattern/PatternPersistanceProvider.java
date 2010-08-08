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
	public Pattern saveNewPattern(String serializedPattern, String description);
	
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
	 * @param id
	 */
	public void deletePattern(int id);
	
	/**
	 * returns all patterns for the stencilset
	 * @return list of patterns
	 */
	public List<Pattern> getPatterns();
	
	/**
	 * Changes the description of the pattern matching the supplied id
	 * @param id Id of the pattern to be changed
	 * @param newDescription Description that substitutes the old description
	 * @return the changed pattern or null if the pattern is not found
	 */
	public Pattern changePatternDescription(int id, String newDescription);
}
