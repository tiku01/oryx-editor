package nl.tue.tm.is.graph;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.petrinet.Transition;

public class TwoTransitionSets {
	public Set<Transition> s1;
	public Set<Transition> s2;

	public TwoTransitionSets(Set<Transition> s1, Set<Transition> s2) {
		this.s1 = s1;
		this.s2 = s2;
	}

	public String toString(){
		return "("+s1+","+s2+")";
	}
	
	public JSONObject toJson() throws JSONException {
		JSONObject twoTransitionSets = new JSONObject();
		JSONArray models = new JSONArray();
		JSONObject model1 = new JSONObject();
		JSONObject model2 = new JSONObject();
		JSONArray nodesModel1 = new JSONArray();
		JSONArray nodesModel2 = new JSONArray();
		for(Transition t : s1) {
			nodesModel1.put(t.getResourceId());
		}
		for(Transition t : s2) {
			nodesModel2.put(t.getResourceId());
		}
		model1.put("nodes", nodesModel1);
		model2.put("nodes", nodesModel2);
		
		models.put(model1);
		models.put(model2);
		twoTransitionSets.put("models", models);
		return twoTransitionSets;
	}

	public boolean equals(Object pair2){
		return pair2 instanceof TwoTransitionSets?(s1.equals(((TwoTransitionSets)pair2).s1) && s2.equals(((TwoTransitionSets)pair2).s2)):false;
	}

	public int hashCode(){
		return s1.hashCode() + s2.hashCode();
	}
	
	public boolean isComplex() {
		return (s1.size() > 1) || (s2.size() > 1);
	}

}
