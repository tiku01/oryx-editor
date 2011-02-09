package org.oryxeditor.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hpi.pictureSupport.*;

public class PictureImporter extends HttpServlet 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			PrintWriter out = null;		
			out = response.getWriter();
			
			String pictureToImport = request.getParameter("data");
			String[] pagesToImport = request.getParameter("pagesToImport").split(";;");
		
			String resultJSONDiagrams = PictureConverter.importPagesNamed(pictureToImport, pagesToImport);
			
			if (resultJSONDiagrams.startsWith("error:"))
			{
				out.write(resultJSONDiagrams);
			}
			else
			{
				out.write(resultJSONDiagrams);
			}			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			PrintWriter out = null;
			out = response.getWriter();
			out.write("error:" + e.getMessage());
		}		
	}	
}