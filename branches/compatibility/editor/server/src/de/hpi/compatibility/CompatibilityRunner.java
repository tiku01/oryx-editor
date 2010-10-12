package de.hpi.compatibility;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tue.tm.is.graph.TwoTransitionSets;

import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.serialization.PTNetPNMLImporter;
import de.hpi.bp.BPCreatorNet;
import de.hpi.bp.BehaviouralProfile;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Transition;
import de.hpi.petrinet.serialization.XMLFileLoaderSaver;

public class CompatibilityRunner {

	private static String prefix = "models/taskpairs/";
	private static String outfilename = "results.txt";
	private static String[] pairs = {"_pair04","_pair06","_pair08","_pair10","_pair11","_pair12","_pair16","_pair17","_pair18","_pair19"};
//	private static String[] pairs = {"_pair16","_pair17","_pair18"};
//	private static String[] pairs = {"_pair18"};
	
	private static PTNetPNMLImporter pnmlImporter = new PTNetPNMLImporter();
	private static XMLFileLoaderSaver fileLoader = new XMLFileLoaderSaver();
	
	private static Map<String,BehaviouralProfile> profileCache = new HashMap<String,BehaviouralProfile>();

	private static List<CorrespondenceAnalysis> analysisPairs = new ArrayList<CorrespondenceAnalysis>();
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * Parse all nets and create analysis pairs
		 */
		for (int i = 0; i < pairs.length; i++){
			System.out.println("Parse: "  + (i+1) + " of " + pairs.length);
			parsePair(pairs[i]);
		}
		
		/*
		 * Do the actual compatibility check
		 */
		int i = 1;
		for (CorrespondenceAnalysis a : analysisPairs) {
			System.out.println("Check: "  + i + " of " + analysisPairs.size());
			a.checkCompatibility();
			i++;
		}
		
		/*
		 * Print results
		 */
		try {
			FileOutputStream file = new FileOutputStream(outfilename); 
			PrintStream filestream = new PrintStream(file); 
			int pair = 0;
			for (CorrespondenceAnalysis a : analysisPairs) {
				filestream.println("PAIR: " + pairs[pair]);
				a.printResults(filestream);
				filestream.println();
				pair++;
			}
			filestream.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void parsePair(String modelpair) {
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(prefix + modelpair);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String model1 = br.readLine();
			String model2 = br.readLine();
			
			model1 = model1.replaceAll(".pnml", "_sem.pnml");
			model2 = model2.replaceAll(".pnml", "_sem.pnml");
			
			model1 = model1.replaceAll("_E_sem.pnml", "_sem.pnml");
			model2 = model2.replaceAll("_E_sem.pnml", "_sem.pnml");
			
			if (!profileCache.containsKey(model1)) {
				parseNet(model1);
			}
			if (!profileCache.containsKey(model2)) {
				parseNet(model2);
			}
			
			CorrespondenceAnalysis analysis = new CorrespondenceAnalysis(
					profileCache.get(model1),
					profileCache.get(model2),
					getHumanSets(profileCache.get(model1).getNet(), profileCache.get(model2).getNet(), br)
				);
			
			analysisPairs.add(analysis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void parseNet(String netName) {
		try {
			PTNet net = (PTNet) pnmlImporter.loadPetriNet(fileLoader.loadDocumentFromFile(prefix + netName));
			NetNormalizer.getInstance().normalizeNet(net);
//			System.out.println(net);
			profileCache.put(netName, BPCreatorNet.getInstance().deriveBehaviouralProfile(net));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Transition getFirstTransitionByLabel(PTNet net, String label) {
		Transition result = null;
		for(Node t : net.getLabeledTransitions()) {
			if (((LabeledTransition)t).getLabel().equals(label))
				return (Transition)t;
		}
		return result;
	}
	
	public static Set<TwoTransitionSets> getHumanSets(PTNet net1, PTNet net2, BufferedReader br) throws IOException{
		
		Set<TwoTransitionSets> humanMappingSets = new HashSet<TwoTransitionSets>();

		String pairs;
		Map<Transition,TwoTransitionSets> v1ToSets = new HashMap<Transition,TwoTransitionSets>();
		Map<Transition,TwoTransitionSets> v2ToSets = new HashMap<Transition,TwoTransitionSets>();		

		while((pairs = br.readLine()) != null) {
			int separator = pairs.lastIndexOf(',');
			String left = pairs.substring(0, separator).trim();
			String right = pairs.substring(separator+1).trim();

			Transition leftNode = getFirstTransitionByLabel(net1, left);
			if (leftNode == null){
				System.err.println("ERROR: Node with label \"" + left + "\" not found in: " + net1.getId());
				System.exit(0);
			}
			Transition rightNode = getFirstTransitionByLabel(net2, right);
			if (rightNode == null){
				System.err.println("ERROR: Node with label \"" + right + "\" not found in: " + net2.getId());
				System.exit(0);
			}

			TwoTransitionSets foundLeft = v1ToSets.get(leftNode);			
			TwoTransitionSets foundRight = v2ToSets.get(rightNode);			
			if ((foundLeft == null) && (foundRight == null)){
				Set<Transition> leftSet = new HashSet<Transition>();
				leftSet.add(leftNode);
				Set<Transition> rightSet = new HashSet<Transition>();
				rightSet.add(rightNode);
				TwoTransitionSets newPair = new TwoTransitionSets(leftSet,rightSet);
				humanMappingSets.add(newPair);
				v1ToSets.put(leftNode, newPair);
				v2ToSets.put(rightNode, newPair);
			}else if ((foundLeft == null) && (foundRight != null)){
				foundRight.s1.add(leftNode);
				v1ToSets.put(leftNode, foundRight);				
			}else if ((foundLeft != null) && (foundRight == null)){
				foundLeft.s2.add(rightNode);
				v2ToSets.put(rightNode, foundRight);				
			}else if ((foundLeft != null) && (foundRight != null)){
				//Put everything in the left set, delete the right set
				humanMappingSets.remove(foundRight);
				foundLeft.s1.addAll(foundRight.s1);
				foundLeft.s1.add(leftNode);
				foundLeft.s2.addAll(foundRight.s2);
				foundLeft.s2.add(rightNode);
				v1ToSets.put(leftNode, foundLeft);
				v2ToSets.put(rightNode, foundLeft);
				for (Transition i: foundRight.s1){
					v1ToSets.put(i, foundLeft);
				}
				for (Transition i: foundRight.s2){
					v2ToSets.put(i, foundLeft);
				}
			}
		}
		return humanMappingSets;
	}

	
}
