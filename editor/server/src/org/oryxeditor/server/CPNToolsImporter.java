package org.oryxeditor.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hpi.cpn.model.CPNTransformer;

public class CPNToolsImporter extends HttpServlet 
{
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out = null;		
		out = response.getWriter();
		
		String cpnToImport = request.getParameter("data");
		String[] pagesToImport = request.getParameter("pagesToImport").split(";;");
		
		CPNTransformer transformer = new CPNTransformer();
		
		// die Diagramme sind mit ";;;" voneinander getrennt
		String resultJSONDiagrams = transformer.fromXML(cpnToImport, pagesToImport);
		
		if (resultJSONDiagrams.equals("problems"))
		{
			out.write("error");
		}
		else
		{
			out.write(resultJSONDiagrams);
		}		
	}
	
}
