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

/**
 * Represents a problem when saving to the backend storage of a PatterPersistanceProvider.
 * @author Kai Höwelmeyer
 *
 */
public class PatternPersistanceException extends Exception{
	
	/**
	 * Generated serialization id.
	 */
	private static final long serialVersionUID = -1621913261191969131L;

	/**
	 * Constructor allowing to specify a root cause.
	 * This helps a client to inspect the cause of a problem in-depth.
	 * @param rootCause which exception let to this exception?
	 */
	public PatternPersistanceException(Exception rootCause) {
		super(rootCause);
	}
	
	/**
	 * Constructor allows to specify a message additionally to the root cause
	 * @param msg Description of the erroneous situation
	 * @param rootCause what exception let to this exception?
	 */
	public PatternPersistanceException(String msg, Exception rootCause) {
		super(msg, rootCause);
	}

}
