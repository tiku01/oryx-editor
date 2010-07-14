package de.hpi.AdonisSupport;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class Log {
	private static Logger logger = null;
	static {
		logger = Logger.getRootLogger();
		logger .addAppender(new ConsoleAppender(new SimpleLayout()));
		logger .debug("Logger initialized");
	}
	public static void v(String message){
		logger.info(message);
	}
	
	public static void v(String message, Throwable e){
		logger.info(message,e);
	}
	
	public static void d(String message){
		logger.debug(message);
	}
	
	public static void d(String message, Throwable e){
		logger.debug(message,e);
	}
	
	public static void w(String message){
		logger.warn(message);
	}
	
	public static void w(String message, Throwable e){
		logger.warn(message,e);
	}
	
	public static void e(String message){
		logger.error(message);
	}
	
	public static void e(String message, Throwable e){
		logger.error(message,e);
	}
}
