/**
 * 
 */
package de.fraunhofer.fokus.jic;

/**
 * @author cht
 *
 */
public class JICException extends Exception {
	private static final long serialVersionUID = -889928466203111502L;
	
	public JICException(Throwable t) { super(t); }
	public JICException(String s, Throwable t) { super(s, t); }
	public JICException(String s) { super(s); }
}
