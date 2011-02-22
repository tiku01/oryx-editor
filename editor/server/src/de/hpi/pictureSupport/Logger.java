package de.hpi.pictureSupport;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.SimpleLayout;

/**
 * The Class Logger.
 */
public class Logger {
	
	/** The logger. */
	private static org.apache.log4j.Logger logger = null;
	
	/** The log level. */
	private static Level logLevel = Level.ALL;
	
	/** The counter. */
	private static Integer[] counter = {
		/*ERROR*/	0,
		/*WARNING*/	0,
		/*INFO*/	0,
		/*DEBUG*/	0,
		/*VERBOSE*/	0};
	static {
		logger = org.apache.log4j.Logger.getRootLogger();
		logger.addAppender(new ConsoleAppender(new SimpleLayout()));
		logger.debug("Logger initialized");
		logger.setLevel(logLevel);
	}
	
	/**
	 * Reset.
	 */
	public static void reset(){
		counter = new Integer[]{
				/*ERROR*/	0,
				/*WARNING*/	0,
				/*INFO*/	0,
				/*DEBUG*/	0,
				/*VERBOSE*/	0};
	}
	
	/**
	 * Prints the summary.
	 */
	public static void printSummary(){
		logger.info("ERRORS:   "+counter[0]+"\nWARNINGS: "+counter[1]+"\nINFO:     "+counter[2]+"\nDEBUG:    "+counter[3]+"\nVERBOSE:  "+counter[4]);
	}
	
	/**
	 * I.
	 *
	 * @param message the message
	 */
	public static void i(String message){
		logger.info(message);
		counter[4]++;
	}
	
	/**
	 * I.
	 *
	 * @param message the message
	 * @param e the e
	 */
	public static void i(String message, Throwable e){
		logger.info(message,e);
		counter[4]++;
	}
	
	/**
	 * D.
	 *
	 * @param message the message
	 */
	public static void d(String message){
		logger.debug(message);
		counter[3]++;
	}
	
	/**
	 * D.
	 *
	 * @param message the message
	 * @param e the e
	 */
	public static void d(String message, Throwable e){
		logger.debug(message,e);
		counter[3]++;
	}
	
	/**
	 * W.
	 *
	 * @param message the message
	 */
	public static void w(String message){
		logger.warn(message);
		counter[1]++;
	}
	
	/**
	 * W.
	 *
	 * @param message the message
	 * @param e the e
	 */
	public static void w(String message, Throwable e){
		logger.warn(message,e);
		counter[1]++;
	}
	
	/**
	 * E.
	 *
	 * @param message the message
	 */
	public static void e(String message){
		logger.error(message);
		counter[0]++;
	}
	
	/**
	 * E.
	 *
	 * @param message the message
	 * @param e the e
	 */
	public static void e(String message, Throwable e){
		logger.error(message,e);
		counter[0]++;
	}
}
