package de.hpi.ViewGenerator;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

class SVGGenerator {



	String pathToDot = "C:\\Programme\\Graphviz2.26\\bin\\";
	String pathToStaticResources = "C:\\Programme\\Apache Software Foundation\\Tomcat 6.0\\webapps\\oryx\\viewgenerator\\";
	String scriptCommand = "\"" + pathToStaticResources + "static\\cd_call.bat\"";
	
	String dotInput;
	File dotInputFile;
	String layoutAlg;
	String toSavePath;
	String name;
	
	public SVGGenerator(String path, String translatorSettings, TranslatorInput translatorInput, String layoutAlgorithm, String svgName) {
		toSavePath = path;
		name = svgName;
		dotInput = getTranslation(translatorSettings, translatorInput, layoutAlgorithm);
		dotInputFile = createTranslationFile(dotInput);
		layoutAlg = layoutAlgorithm;
	}
	
	public String getDotPath() {
		return pathToDot;
	}
	
	private String getTranslation(String translatorSettings, TranslatorInput translatorInput, String layoutAlgorithm) {
		Translator translator = new Translator();
		return translator.translate(translatorSettings, translatorInput, layoutAlgorithm);
	}
	
	private File createTranslationFile(String dotInput){
		File dotInputFile;
	      try {
	    	  dotInputFile = new File(toSavePath + name);
	    	  
	          FileWriter fout = new FileWriter(dotInputFile);
	          fout.write(dotInput);
	          fout.close();
	          return dotInputFile;
	      } 
	      catch (java.io.IOException e) { 
			  e.printStackTrace();
			  return null;
	      }
	}
	
	public void generateSVG() {
		String destination = dotInputFile.getAbsolutePath();
		String command; 
		
//		graphviz expects given imagepaths, if not absolute, to be relative to the directory from where the call to it is started
//		therefore we switch to the directory where the svgs will be located so that the relative path in the svg (graphviz just copies the given imagepath)
//		and the relative path for the dot-call both look in the same directory - otherwise it would be possible for dot to locate the images
//		without the images being available to the svgviewer/browser or the images would be available, but dot does not include the path in svgs,
//		because it was not able to locate them
			
//		java runtime.exec doesn't look in global path-variables/shell environment variables
//		moving cd command to a shell script, calling dot-command from here for now
		
//		changing directory to /viewgenerator
		command = scriptCommand + "  && ";
		
//			running graphviz
		command = command + pathToDot + "dot" + " -K" + layoutAlg + " -Tsvg -O " + "\"" + destination + "\"";

		Runtime runtime = Runtime.getRuntime();
		
		try {
			Process p = runtime.exec(command);
			if (!layoutAlg.equals("neato")) {
//				neato is able to handle up to 16000+ nodes, however the returning value (exit status of the call) is buggy when node number exceeds a certain value
//				therefore cannot rely on the command to give the proper exit status - neato is the algorithm for structured graphs (not directed)
//				additional handling should be added if neato is not updated because dot wont exit although it has nothing to do anymore
		        p.waitFor();
			}
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
		}
		catch (java.lang.InterruptedException e) {
			e.printStackTrace();
		}
	}
}
