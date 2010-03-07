/*please put rpst.jar to your library pass
 * put ogdf.dll to C:/Windows/System32/
 * give "-Djava.library.path=C:/Windows/System32/" as VM Argument in Run Configurations*/

package org.oryxeditor.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.bpt.hpi.graph.Graph;
import de.bpt.hpi.ogdf.rpst.RPST;
import de.bpt.hpi.ogdf.spqr.TreeNode;
import de.hpi.epc.layouting.model.EPCDiagram;
import de.hpi.epc.layouting.model.EPCJSONParser;
import de.hpi.epc.layouting.model.EPCType;
import de.hpi.layouting.model.LayoutingElement;

public class EPC2RPSTTreeServlet extends HttpServlet {
	private RPST rpst;

	private Map<Integer, String> rpstId2JsonId = new HashMap<Integer, String>();
	private static final long serialVersionUID = -5592867075605609828L;

	private EPCDiagram epcDiagram;

	// create response - JSON
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JSONObject jsonRequest; // jsonModel
		JSONObject jsonResponse; // jsonRPST

		request.setCharacterEncoding("UTF-8");
		String requestString = request.getParameter("data");

		// JSON parser
		try {
			jsonRequest = new JSONObject(requestString);
			EPCJSONParser parser = new EPCJSONParser();
			this.epcDiagram = parser.loadEPCFromJSON(jsonRequest);
		// JSON import failed
		} catch (JSONException e1) {
			response.setStatus(500);
			response.getWriter().print("import of json failed:");
			e1.printStackTrace(response.getWriter());
			return;
		}

		if (this.epcDiagram == null) {
			response.setStatus(500);
			response.getWriter().print("import failed");
			return;
		} 
		// jsonResponse: EPC -> Graph -> RPST -> Json

		try {
			Graph graph = buildGraph(epcDiagram);
			rpst = new RPST(graph);
			// JSONArray jsonResponseArray = createJsonResponse();
			// jsonResponse = jsonRequest.put("rpst", jsonResponseArray);

			jsonResponse = (new JSONObject()).put("rpst", createJsonResponse(rpst));
			
			//System.out.println(jsonResponse);
			// JSON export failed
		} catch (Exception e) {
			response.setStatus(500);
			response.getWriter().print("layout failed:");
			e.printStackTrace(response.getWriter());
			return;
		}
		// JSON export successful
		response.setStatus(200);
		response.getWriter().print(jsonResponse);
		return;

	}

	// creates Graph from EPC
	private Graph buildGraph(EPCDiagram epcDiagramm) {
		Map<String, Integer> vertexDictionary = new HashMap<String, Integer>();
		Graph graph = new Graph();

		// create vertices
		for (LayoutingElement e : epcDiagramm
				.getElementsOfType(EPCType.Function)) {
			Integer id = graph.addVertex(e.getId()); // RPSTid

			vertexDictionary.put(e.getId(), id);
			rpstId2JsonId.put(id, e.getId());

		}

		for (LayoutingElement e : epcDiagramm.getElementsOfType(EPCType.Event)) {
			Integer id = graph.addVertex(e.getId());
			vertexDictionary.put(e.getId(), id);
			rpstId2JsonId.put(id, e.getId());

		}

		for (LayoutingElement e : epcDiagramm
				.getElementsOfType(EPCType.ProcessInterface)) {
			Integer id = graph.addVertex(e.getId());
			vertexDictionary.put(e.getId(), id);
			rpstId2JsonId.put(id, e.getId());
		}

		for (LayoutingElement e : epcDiagramm
				.getElementsOfType(EPCType.OrConnector)) {
			Integer id = graph.addVertex(e.getId());
			vertexDictionary.put(e.getId(), id);
			rpstId2JsonId.put(id, e.getId());

		}

		for (LayoutingElement e : epcDiagramm
				.getElementsOfType(EPCType.AndConnector)) {
			Integer id = graph.addVertex(e.getId());
			vertexDictionary.put(e.getId(), id);
			rpstId2JsonId.put(id, e.getId());

		}

		for (LayoutingElement e : epcDiagramm
				.getElementsOfType(EPCType.XorConnector)) {
			Integer id = graph.addVertex(e.getId());
			vertexDictionary.put(e.getId(), id);
			rpstId2JsonId.put(id, e.getId());

		}

		// create edges
		for (LayoutingElement e : epcDiagramm
				.getElementsOfType(EPCType.ControlFlow)) {
			String source = e.getIncomingLinks().get(0).getId();
			String target = e.getOutgoingLinks().get(0).getId();
			graph.addEdge(vertexDictionary.get(source), vertexDictionary
					.get(target));

		}
		return graph;
	}

	// JSONNodesArray
	public JSONArray createJsonResponse(RPST rpst) {
		JSONArray jsonResponse = new JSONArray();

		JSONObject treeDepth = new JSONObject();
		TreeNode root = rpst.getRootNode();
		try {
			treeDepth = treeDepth.put("treeDepth", getDepth(rpst));

			// parse Tree
			jsonResponse.put(rpstNode2JSON(rpst, root));
			jsonResponse.put(treeDepth);
		} catch (JSONException e) {
			System.out.println("jsonError" + e);
		}
		return jsonResponse;
	}

	// Node -> JSON
	public JSONObject rpstNode2JSON(RPST rpst, TreeNode node) {
		JSONObject json = new JSONObject();
		String jsonTitleIncoming = "nodeEntry";
		String jsonTitleOutgoing = "nodeExit";
		String jsonTitleDepth = "depth";
		String jsonTitleType = "type";

		String jsonValueIncoming = rpstId2JsonId.get(rpst.getEntry(node))
				.toString();
		String jsonValueOutgoing = rpstId2JsonId.get(rpst.getExit(node))
				.toString();
		String jsonValueDepth = new Integer(this.getDepth(rpst, node)).toString();
		String jsonValueType = node.getNodeType().toString();
		try {
			json.put(jsonTitleIncoming, jsonValueIncoming);
			json.put(jsonTitleOutgoing, jsonValueOutgoing);
			json.put(jsonTitleDepth, jsonValueDepth);
			json.put(jsonTitleType, jsonValueType);
			/*if (node != rpst.getRootNode()) {
				json.put("parentEntry", rpstId2JsonId.get(rpst.getEntry(node
						.getParentNode())));
				json.put("parentExit", rpstId2JsonId.get(rpst.getExit(node
						.getParentNode())));
			}
			*/
			// if exists, get children
			if (node.getChildNodes().size() != 0) {
				json.put("children", childNodes2JSON(rpst, node));
			}

		} catch (JSONException e) {

			System.out.println("Addition of JSONnode failed: " + e);
		}

		return json;

	}

	// adds ChildNodes of a parent Node to JSONArray
	public JSONArray childNodes2JSON(RPST rpst, TreeNode parent) {
		JSONArray jsonChildren = new JSONArray();

		for (TreeNode childNode : parent.getChildNodes()) {
			jsonChildren.put(rpstNode2JSON(rpst, childNode));

		}
		return jsonChildren;

	}

	// returns depth of the Tree
	public int getDepth(RPST rpst) {
		int maxDepth = 0;
		for (TreeNode node : rpst.getNodes()) {
			if (getDepth(rpst, node) > maxDepth) {
				maxDepth = getDepth(rpst, node);
			}
		}
		return maxDepth;
	}

	// returns depth of the node
	public int getDepth(RPST rpst, TreeNode node) {
		if (node == rpst.getRootNode()) {
			return 0;
		}
		return getDepth(rpst, node.getParentNode()) + 1;
	}

	public RPST getRpst() {
		return rpst;
	}

	public void setRpst(RPST rpst) {
		this.rpst = rpst;
	}

	public Map<Integer, String> getRpstId2JsonId() {
		return rpstId2JsonId;
	}

	public void setRpstId2JsonId(Map<Integer, String> rpstId2JsonId) {
		this.rpstId2JsonId = rpstId2JsonId;
	}
	
	
}
