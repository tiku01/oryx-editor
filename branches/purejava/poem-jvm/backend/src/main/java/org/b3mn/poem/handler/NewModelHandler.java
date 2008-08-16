package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.json.JSONException;

public class NewModelHandler extends HandlerBase {

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		String stencilset = "/oryx/stencilsets/bpmn/bpmn.json";
		if (request.getParameter("stencilset") != null) {
			stencilset = request.getParameter("stencilset");
		}

		String content = "<div id=\"oryx-canvas123\" class=\"-oryx-canvas\">"
			+ "<span class=\"oryx-mode\">writeable</span>"
			+ "<span class=\"oryx-mode\">fullscreen</span>"
			+ "<a href=\"http://" + request.getServerName() + ':' + String.valueOf(request.getServerPort()) + stencilset + "\" rel=\"oryx-stencilset\"></a>\n"
			+ "</div>\n";
		response.getWriter().print(this.getOryxModel("New Process Model", content));

		response.setStatus(200);
		response.setContentType("application/xhtml+xml");
	}
}
