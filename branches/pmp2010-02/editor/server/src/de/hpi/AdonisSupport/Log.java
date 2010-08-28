package de.hpi.AdonisSupport;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class Log {
	private static Logger logger = null;
	private static Integer[] counter = {/*ERROR*/0,/*WARNING*/0,/*INFO*/0,/*DEBUG*/0,/*VERBOSE*/0};
	static {
		logger = Logger.getRootLogger();
		logger .addAppender(new ConsoleAppender(new SimpleLayout()));
		logger .debug("Logger initialized");
	}
	
	public static void reset(){
		counter = new Integer[]{/*ERROR*/0,/*WARNING*/0,/*INFO*/0,/*DEBUG*/0,/*VERBOSE*/0};
	}
	
	public static void printSummary(){
		logger.info("ERRORS:   "+counter[0]+"\nWARNINGS: "+counter[1]+"\nINFO:     "+counter[2]+"\nDEBUG:    "+counter[3]+"\nVERBOSE:  "+counter[4]);
	}
	
	public static void v(String message){
		logger.info(message);
		counter[4]++;
	}
	
	public static void v(String message, Throwable e){
		logger.info(message,e);
		counter[4]++;
	}
	
	public static void d(String message){
		logger.debug(message);
		counter[3]++;
	}
	
	public static void d(String message, Throwable e){
		logger.debug(message,e);
		counter[3]++;
	}
	
	public static void w(String message){
		logger.warn(message);
		counter[1]++;
	}
	
	public static void w(String message, Throwable e){
		logger.warn(message,e);
		counter[1]++;
	}
	
	public static void e(String message){
		logger.error(message);
		counter[0]++;
	}
	
	public static void e(String message, Throwable e){
		logger.error(message,e);
		counter[0]++;
	}
}
