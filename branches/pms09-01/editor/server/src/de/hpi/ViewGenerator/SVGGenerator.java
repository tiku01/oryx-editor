/***************************************
 * Copyright (c) 2010 
 * Martin Krüger
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
****************************************/

package de.hpi.ViewGenerator;

import java.io.File;
import java.io.FileWriter;

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
			synchronized(p) {
				
				if (layoutAlg.equals("dot")) {
			        p.waitFor();
				}
				if (layoutAlg.equals("neato")){
//					neato is able to handle up to 16000+ nodes, however when node number exceeds a certain value it is buggy in the way that the svg is generated but the process does not exit
//					therefore one cannot rely on the process to exit on its own - neato is the algorithm for structured graphs (not directed)
//					additional handling should be added if neato is not updated, set 5 seconds as maximum waittime
					p.wait(5000);
				}
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
