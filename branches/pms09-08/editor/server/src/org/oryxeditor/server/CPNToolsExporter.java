package org.oryxeditor.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import de.hpi.cpn.*;
import de.hpi.cpn.model.CPNFillattr;
import de.hpi.cpn.model.CPNTransformer;

public class CPNToolsExporter extends HttpServlet
{
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
	
		
		PrintWriter out = null;		
		out = response.getWriter();
		
		String json = request.getParameter("data");
		
		CPNTransformer transformer = new CPNTransformer();
		
		try
		{
			String cpnfileString = transformer.transformtoCPN(json);
			
			out.write(cpnfileString);
			
		} catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.write("Schade nicht geklappt.");
		}
		
		
	}

}
