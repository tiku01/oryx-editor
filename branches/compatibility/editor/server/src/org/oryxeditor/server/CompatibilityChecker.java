package org.oryxeditor.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import de.hpi.compatibility.Correlation;

public class CompatibilityChecker extends HttpServlet {
	private static final long serialVersionUID = -3215102566003538575L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text");
		String result;
		try {
			JSONArray data = new JSONArray(req.getParameter("data"));
			Correlation correlation = new Correlation(data);
			result = correlation.check();
		} catch (Exception e) {
			result = "error";
			e.printStackTrace();
		}
		PrintWriter responseText = res.getWriter();
		responseText.write(result);

	}
}
