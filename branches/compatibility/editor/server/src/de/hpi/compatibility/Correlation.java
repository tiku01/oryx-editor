package de.hpi.compatibility;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.tue.tm.is.graph.TwoTransitionSets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.serialization.PTNetRDFImporter;
import de.hpi.petrinet.Transition;

public class Correlation {
	private PTNet firstModel;
	private PTNet secondModel;
	private Set<TwoTransitionSets> correspondences;

	public Correlation() {
		firstModel = null;
		secondModel = null;
		correspondences = new HashSet<TwoTransitionSets>();
	}

	public Correlation(JSONArray correlation) throws JSONException,
			ParserConfigurationException, SAXException, IOException {
		// Create petri net models from urls given in first correspondence
		JSONObject firstCorrespondence = correlation.getJSONObject(0);
		String urlFirstModel = firstCorrespondence.getJSONArray("models").getJSONObject(0).getString("url");
		firstModel = getPTnet(urlFirstModel);
		String urlSecondModel = firstCorrespondence.getJSONArray("models").getJSONObject(1).getString("url");
		secondModel = getPTnet(urlSecondModel);

		// Create all correspondences given
		correspondences = new HashSet<TwoTransitionSets>();
		for (int i = 0; i < correlation.length(); i++)
			correspondences.add(buildTwoTransitionSets(
					correlation.getJSONObject(i), firstModel, secondModel));
	}

	public String check() {
		CompatibilityCheck check = new CompatibilityCheck(firstModel, secondModel, correspondences);
		return check.run();
	}

	private static PTNet getPTnet(String url) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(url + "/rdf");
		PTNetRDFImporter rdfImporter = new PTNetRDFImporter(document);
		return rdfImporter.loadPTNet();
	}

	private static TwoTransitionSets buildTwoTransitionSets(JSONObject correspondence, PTNet pn1, PTNet pn2) throws JSONException {
		JSONArray jsonModels = correspondence.getJSONArray("models");
		JSONObject firstJsonModel = jsonModels.getJSONObject(0);
		JSONObject secondJsonModel = jsonModels.getJSONObject(1);

		Set<Transition> s1 = buildTransitionSet(pn1, firstJsonModel);
		Set<Transition> s2 = buildTransitionSet(pn2, secondJsonModel);

		return new TwoTransitionSets(s1, s2);
	}

	private static Set<Transition> buildTransitionSet(PTNet pn, JSONObject jsonModel) throws JSONException {
		Set<Transition> transitionSet = new HashSet<Transition>();
		JSONArray nodesJsonModel = jsonModel.getJSONArray("nodes");
		for (int i = 0; i < nodesJsonModel.length(); i++) {
			String resourceId = nodesJsonModel.getJSONObject(i).getString("resourceId");
			transitionSet.add(pn.getTransitionByResourceId(resourceId));
		}

		return transitionSet;
	}
}
