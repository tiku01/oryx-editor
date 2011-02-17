package de.hpi.bpt.mashup.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;
import org.oryxeditor.server.diagram.JSONBuilder;

import de.hpi.PTnet.PTNet;
import de.hpi.bp.AbstractedBPCreator;
import de.hpi.bp.AbstractedBehaviouralProfile;
import de.hpi.bp.BPCreatorNet;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.layouting.PetriNetLayouter;

public class ABPGenerator extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	/**
	 * location of the servlet which generates the SVG
	 */
	private static final String SVG_PATH = "/extensions/svg";  
	
	
	/**
	 * Generates a derived model based on the given model and the given grouping
	 * of nodes. Returns the newly generated model and the according SVG.
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) {
		if ((req.getParameter("model") != null) && (req.getParameter("groups") != null)) {
			try {
				req.setCharacterEncoding("UTF-8");
				
				// parse the given diagram
				Diagram diagram = DiagramBuilder.parseJson(req.getParameter("model"));
				// convert it to a PTNet
				PTNet net = Diagram2PTNet.convert(diagram);
				// get the shape groupings
				HashMap<String, List<Node>> groupMap = parseGroups(req.getParameter("groups"), net);
				// derive the BehaviouralProfile and generate the according new net
				AbstractedBPCreator creator = new AbstractedBPCreator(BPCreatorNet.getInstance());
				AbstractedBehaviouralProfile abp = creator.deriveBehaviouralProfile(net, groupMap, AbstractedBPCreator.PARALLEL);
				PTNet derived = abp.getNet();
				// layout the new net
				PetriNetLayouter layouter = new PetriNetLayouter(derived);
				layouter.layout();
				// convert the PTNet back to a diagram and serialize it to JSON
				Diagram newDiagram = PTNet2Diagram.convert(derived, diagram.getStencilset(), diagram.getStencil());
				JSONObject model = JSONBuilder.parseModel(newDiagram);
				JSONObject result = new JSONObject();
				result.put("model", model);
				// generate the SVG
				result.put("svg", getSVG(model.toString(), getSVGAddress(req)));
				// return the result
				res.getOutputStream().print(result.toString());
				res.setStatus(200);
			} catch (Exception e) {
				try {
					res.getOutputStream().print(e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				res.setStatus(500);
			}
		} else 
			res.setStatus(404);
	}
	
	/**
	 * Returns the absolute address of the SVG generator servlet.
	 * @param req
	 * @return
	 */
	private String getSVGAddress(HttpServletRequest req) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("http://");
		buffer.append(req.getLocalName());
		buffer.append(":").append(req.getLocalPort());
		buffer.append(SVG_PATH);
		return buffer.toString();
	}
	
	/**
	 * Sends a POST request with the given model as the parameter to
	 * the given address and expects an SVG as response.
	 * @param String of the model in JSON format
	 * @param address of the rendering Service
	 * @return SVG string representation of the model
	 * @throws IOException 
	 */
	private String getSVG(String model, String address) throws IOException {
		URL url = new URL(address);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
    
        // send model
        writer.write("model=" + model);
        writer.flush();
        
        // Get the response
        StringBuffer answer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            answer.append(line);
        }
        writer.close();
        reader.close();
        return answer.toString();
	}

	/**
	 * Returns the node of the net which has the given id.
	 * Returns null if no node was found.
	 * @param id of the node 
	 * @param the net
	 * @return node or null
	 */
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
	
	/**
	 * Parses the grouping given in the string (JSON) and returns
	 * the according groups of nodes from the given net mapped by their
	 * group name.
	 * @param content
	 * @param net
	 * @return
	 * @throws JSONException
	 */
	private HashMap<String, List<Node>> parseGroups(String content, PTNet net) throws JSONException {
		JSONArray groups = new JSONArray(content);
		HashMap<String, List<Node>> groupMap = new HashMap<String, List<Node>>();
		for (int i = 0; i < groups.length(); i++) {
			JSONObject group = groups.getJSONObject(i);
			List<Node> shapeNodes = new ArrayList<Node>();
			JSONArray shapes = group.getJSONArray("shapes");
			for (int j = 0; j < shapes.length(); j++) {
				Node n = getNodeForId(shapes.getString(j), net);
				if (n != null) shapeNodes.add(n);
			}
			groupMap.put(group.getString("name"), shapeNodes);
		}
		return groupMap;
	}
}
