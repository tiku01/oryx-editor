package de.hpi.bpt.mashup.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;

import de.hpi.PTnet.PTNet;
import de.hpi.bp.AbstractedBPCreator;
import de.hpi.bp.AbstractedBehaviouralProfile;
import de.hpi.bp.BPCreatorNet;
import de.hpi.petrinet.Node;

public class ABPGenerator extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) {
		if ((req.getParameter("model") != null) && (req.getParameter("groups") != null)) {
			try {
				req.setCharacterEncoding("UTF-8");
				
				Diagram diagram = DiagramBuilder.parseJson(req.getParameter("model"));
				PTNet net = Diagram2PTNet.convert(diagram);
				System.out.println("Diagram " + diagram.getResourceId() + " has " + diagram.getChildShapes().size() + " child shapes");
				System.out.println("PTNet " + net.getId() + " has " + net.getPlaces().size() + " places, " + 
						net.getTransitions().size() + " transitions and " + 
						net.getFlowRelationships().size() + " flows");
				HashMap<String, List<Node>> groupMap = parseGroups(req.getParameter("groups"), net);
				AbstractedBPCreator creator = new AbstractedBPCreator(BPCreatorNet.getInstance());
				AbstractedBehaviouralProfile abp = creator.deriveBehaviouralProfile(net, groupMap);
				PTNet derived = abp.getNet();
				System.out.println("derived net is workflow net: " + derived.isWorkflowNet());
				
			} catch (IOException e) {
				try {
					res.getOutputStream().print(e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (JSONException e) {
				try {
					res.getOutputStream().print(e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		// TODO: send error status
	}

	private Node getNodeForId(String id, PTNet net) {
		Node node = null;
		for (Node n:net.getNodes()) {
			if (n.getResourceId().equals(id)) {
				node = n;
				break;
			}
		}
		return node;
	}
	
	private HashMap<String, List<Node>> parseGroups(String content, PTNet net) throws JSONException {
		JSONArray groups = new JSONArray(content);
		HashMap<String, List<Node>> groupMap = new HashMap<String, List<Node>>();
		for (int i = 0; i < groups.length(); i++) {
			JSONObject group = groups.getJSONObject(i);
			List<Node> shapeNodes = new ArrayList<Node>();
			JSONArray shapes = group.getJSONArray("shapes");
			for (int j = 0; j < shapes.length(); j++) {
				Node n = getNodeForId(shapes.getString(i), net);
				if (n != null) shapeNodes.add(n);
			}
			groupMap.put(group.getString("name"), shapeNodes);
		}
		return groupMap;
	}
}
