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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

class SVGGenerator {
	String pathToDot = "C:\\Programme\\Graphviz2.26\\bin\\";
	String pathToStaticResources;
	String scriptCommand;
	ReadWriteAdapter rwa;
	
	public SVGGenerator(ReadWriteAdapter rwa) {
		this.rwa = rwa;
		this.pathToStaticResources = rwa.getToSavePath();
		this.pathToStaticResources = pathToStaticResources.substring(0, pathToStaticResources.lastIndexOf(File.separator));
		this.pathToStaticResources = pathToStaticResources.substring(0, pathToStaticResources.lastIndexOf(File.separator) + 1);

//		command to switch current directory to the graphvizrun-directory
		this.scriptCommand = "\"" + pathToStaticResources + "static" + File.separator + "cd_call.bat\"";
	}
		
	public String getDotPath() {
		return pathToDot;
	}
	
	private String getTranslation(String graphLabel, TranslatorInput translatorInput, String layoutAlgorithm) {
//		gets the dot/neato representation of an graph
		return Translator.translate(graphLabel, translatorInput, layoutAlgorithm);
	}
	
	private File createTranslationFile(String dotInput, String name){
//		creating file with dot/neato graph description for passing as parameter to graphviz
		File dotInputFile;
	      try {
	    	  dotInputFile = rwa.createFile(name);
	    	  OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(dotInputFile),"UTF-8");
	          out.write(dotInput);
	          out.close();
	          return dotInputFile;
	      } 
	      catch (java.io.IOException e) { 
			  e.printStackTrace();
			  return null;
	      }
	}
	
	public void generateSVG(String graphLabel, TranslatorInput translatorInput, String layoutAlgorithm, String name) {
		String dotInput = getTranslation(graphLabel, translatorInput, layoutAlgorithm);
		File dotInputFile = createTranslationFile(dotInput, name);
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
		command = command + pathToDot + "dot" + " -K" + layoutAlgorithm + " -Tsvg -O " + "\"" + destination + "\"";

		Runtime runtime = Runtime.getRuntime();
		
		try {
			Process p = runtime.exec(command);
			synchronized(p) {
				
				if (layoutAlgorithm.equals("dot")) {
			        p.waitFor();
				}
				if (layoutAlgorithm.equals("neato")){
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
	
	private String removeEscChars(String fileName) {
//		replacing escaped chars, e.g. for graphVizLabels
		String fileName_new = fileName.replace("\n", "").replace("\t","").replace("\r","");
		return fileName_new;
	}
	
	private String getLabel(String name) {
		return removeEscChars(name).replace(" ", "_");
	}
		
	private TranslatorInputNode createCommunicationNode(String communicationNodeId, String urlAttribute) {
		TranslatorInputNode communicationNode = new TranslatorInputNode(communicationNodeId);
		communicationNode.setAttribute("shape", "hexagon");
		communicationNode.setAttribute("label", "\"" + "   " + "\"");
		communicationNode.setAttribute("width", ".3");
		communicationNode.setAttribute("height", ".3");
		communicationNode.setAttribute("fixedsize", "true");
		communicationNode.setAttribute("URL", urlAttribute);
		communicationNode.setAttribute("target", "_blank");
		return communicationNode;
	}
	
	private TranslatorInputNode createParticipantNode(String participantNodeId) {
		TranslatorInputNode participantNode = new TranslatorInputNode(participantNodeId);
		participantNode.setAttribute("shape", "box");
		participantNode.setAttribute("label", participantNodeId);
		return participantNode;
	}
	
	private TranslatorInputNode createHumanAgentNode(String nodeId) {
		TranslatorInputNode humanAgentNode = new TranslatorInputNode(nodeId);
		humanAgentNode.setAttribute("shape", "box");
		humanAgentNode.setAttribute("imagescale", "true");
		humanAgentNode.setAttribute("labelloc", "b");
		humanAgentNode.setAttribute("margin", "1.11,0.01");
		humanAgentNode.setAttribute("image", "\"../static/human_agent.png\"");
		humanAgentNode.setAttribute("label", nodeId);
		return humanAgentNode;
	}
	
	private TranslatorInputNode createDataObjectNode(String nodeId) {
		TranslatorInputNode dataObjectNode = new TranslatorInputNode(nodeId);
		dataObjectNode.setAttribute("shape", "box");
		dataObjectNode.setAttribute("imagescale", "true");
		dataObjectNode.setAttribute("labelloc", "c");
		dataObjectNode.setAttribute("margin", "0.81,0.155");
		dataObjectNode.setAttribute("image", "\"../static/data_object.png\"");
		dataObjectNode.setAttribute("label", nodeId);
		return dataObjectNode;
	}
	
	private TranslatorInputNode createDataStoreNode(String nodeId) {
		TranslatorInputNode dataStoreNode = new TranslatorInputNode(nodeId);
		dataStoreNode.setAttribute("shape", "box");
		dataStoreNode.setAttribute("imagescale", "true");
		dataStoreNode.setAttribute("labelloc", "b");
		dataStoreNode.setAttribute("margin", "0.81,0.155");
		dataStoreNode.setAttribute("image", "\"../static/data_store.png\"");
		dataStoreNode.setAttribute("label", nodeId);		
		return dataStoreNode;
	}
	
	private TranslatorInputEdge createEdge(String sourceNodeId, String targetNodeId, String urlAttribute) {
		TranslatorInputEdge edge = new TranslatorInputEdge(sourceNodeId, targetNodeId);
		edge.setAttribute("URL", urlAttribute);
		edge.setAttribute("target", "_blank");
		return edge;
	}
	
	private TranslatorInput createConversationViewTranslatorInput(ExtractedConnectionList extractedConnectionList) {
		TranslatorInput input = new TranslatorInput("neato");
		ArrayList<String> done_participantIds = new ArrayList<String>();
		ArrayList<String> done_communicationIds = new ArrayList<String>();
		ArrayList<TranslatorInputNode> communicationNodes = new ArrayList<TranslatorInputNode>();
		HashMap<String, ArrayList<String>> done_participantIdsForCommunications = new HashMap<String, ArrayList<String>>();
				
		for (ArrayList<String> attributePair: extractedConnectionList.connectionAttributePairs()) {
//			Node for communication
			String communicationNodeId = "\"" + removeEscChars(attributePair.toString()) + "\"";
			if (!done_communicationIds.contains(communicationNodeId)) {
				String urlAttribute = "\"" + rwa.getOriginHTMLName(attributePair) + "\"";
				TranslatorInputNode communicationNode = createCommunicationNode(communicationNodeId, urlAttribute);
//				Store communicationNode in communicationNodes - communicationNodes have to be added after participantNodes,
//				because otherwise the URL attribute will not be set properly by GraphViz
				communicationNodes.add(communicationNode);
				done_communicationIds.add(communicationNodeId);
			}	
			
			if (!done_participantIdsForCommunications.containsKey(communicationNodeId)) {
				done_participantIdsForCommunications.put(communicationNodeId, new ArrayList<String>());
			}

			ArrayList<String> done_participantIdsForCommunication = done_participantIdsForCommunications.get(communicationNodeId);
			for (String participant: attributePair) {
//				Node for participant
				String participantNodeId = "\"" + removeEscChars(participant) + "\"";
				if (!done_participantIds.contains(participantNodeId)) {
					TranslatorInputNode participantNode = createParticipantNode(participantNodeId);
					input.addNode(participantNode);
					done_participantIds.add(participantNodeId);
				}
				if (!done_participantIdsForCommunication.contains(participantNodeId)) {
					done_participantIdsForCommunication.add(participantNodeId);
//					Edge between participant and communication
					input.addEdge(new TranslatorInputEdge(participantNodeId,communicationNodeId));	
				}
			}
			done_participantIdsForCommunications.put(communicationNodeId, done_participantIdsForCommunication);
		}			
//		add previously stored communicationNodes to the TranslatorInput
		for (TranslatorInputNode comNode: communicationNodes) {
			input.addNode(comNode);
		}
		return input;
	}
	
	private TranslatorInput createHandoversTranslatorInput(ExtractedConnectionList extractedLanePassings) {
		TranslatorInput input = new TranslatorInput("dot");
		ArrayList<String> done_Ids = new ArrayList<String>();
	
		for (ArrayList<String> attributePair: (extractedLanePassings.connectionAttributePairs())) {
					
//			attributePairs should have a length of 2, a target and a source
			String sourceId = attributePair.get(0);
			String targetId = attributePair.get(1);
			
//			Node for source
			String sourceNodeId = "\"" + removeEscChars(sourceId) + "\"";
			TranslatorInputNode sourceNode;
			
			if (!done_Ids.contains(sourceId)) {
				sourceNode = createHumanAgentNode(sourceNodeId);
				input.addNode(sourceNode);
				done_Ids.add(sourceId);
			}
				
//			Node for target
			String targetNodeId = "\"" + removeEscChars(targetId) + "\"";
			TranslatorInputNode targetNode;

			if (!done_Ids.contains(targetId)) {
				targetNode = createHumanAgentNode(targetNodeId);
				input.addNode(targetNode);
				done_Ids.add(targetId);
			}
		
//			edge between source and target		
			String urlAttribute = "\"" + rwa.getOriginHTMLName(attributePair) + "\"";
			TranslatorInputEdge edge = createEdge(sourceNodeId, targetNodeId, urlAttribute);
			input.addEdge(edge);									
		}
		return input;
	}
		
	private TranslatorInput createInformationAccessTranslatorInput(ExtractedConnectionList extractedDataMappings) {
		TranslatorInput input = new TranslatorInput("dot");
		ArrayList<String> done_Ids = new ArrayList<String>();

		for (ArrayList<String> attributePair: (extractedDataMappings.connectionAttributePairs())) {
//			attributePairs should have a length of 2, a target and a source
			String sourceId = attributePair.get(0);
			String targetId = attributePair.get(1);
				
//			Node for source
			String sourceNodeId = "\"" + removeEscChars(sourceId) + "\"";
			
			if (!done_Ids.contains(sourceId)) {
				TranslatorInputNode sourceNode;
				
				if (sourceId.contains("DataObject\\n")) {
					sourceNode = createDataObjectNode(sourceNodeId);
				}
				else if (sourceId.contains("DataStore\\n")) {
					sourceNode = createDataStoreNode(sourceNodeId);
				}
				else {
					sourceNode = createHumanAgentNode(sourceNodeId);
				}
				input.addNode(sourceNode);
				done_Ids.add(sourceId);
			}
				
//			Node for target
			String targetNodeId = "\"" + removeEscChars(targetId) + "\"";
			
			if (!done_Ids.contains(targetId)) {
				TranslatorInputNode targetNode;
				
				if (targetId.contains("DataObject\\n")) {
					targetNode = createDataObjectNode(targetNodeId);
				}
				else if (targetId.contains("DataStore\\n")) {
					targetNode = createDataStoreNode(targetNodeId);
				}
				else {
					targetNode = createHumanAgentNode(targetNodeId);
				}
				input.addNode(targetNode);
				done_Ids.add(targetId);
			}
				
//			edge between source and target 
			String urlAttribute = "\"" + rwa.getOriginHTMLName(attributePair) + "\"";
			TranslatorInputEdge edge = createEdge(sourceNodeId, targetNodeId, urlAttribute);
			input.addEdge(edge);											
		}
		return input;
	}
	
	public void generateHandoversView(ExtractedConnectionList extractedLanePassings, String name){
//		generate SVG from ExtractedConnectionList for Handovers View
		TranslatorInput translatorInput = createHandoversTranslatorInput(extractedLanePassings);
		generateSVG(getLabel(name), translatorInput,"dot", name);
	}
	
	public void generateConversationView(ExtractedConnectionList extractedCommunications, String name) {
//		generate SVG from ExtractedConnectionList for Conversation View
		TranslatorInput translatorInput = createConversationViewTranslatorInput(extractedCommunications);
		generateSVG(getLabel(name), translatorInput,"neato", name);
	}
	
	public void generateInformationAccessView(ExtractedConnectionList extractedDataMappings, String name) {
//		generate SVG from ExtractedConnectionList for Information Access View
		TranslatorInput translatorInput = createInformationAccessTranslatorInput(extractedDataMappings);
		generateSVG(getLabel(name), translatorInput,"dot", name);
	}
}
