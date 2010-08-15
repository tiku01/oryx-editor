package de.hpi.pattern;

import java.io.IOException;

public class PatternPersistanceException extends Exception{

	public PatternPersistanceException(Exception cause) {
		super(cause);
	}
	
	public PatternPersistanceException(String msg, Exception cause) {
		super(msg, cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1621913261191969131L;

}
