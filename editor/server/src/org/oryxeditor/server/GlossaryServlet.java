package org.oryxeditor.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.json.*;

/**
 * 
 * @author Nicolas Peters
 *
 */
public class GlossaryServlet extends HttpServlet {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Return an HTML representation of the glossary
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		System.out.println("Glossary");
		try {
			String glosFileName = this.getServletContext().getRealPath("/glossary.json");
			
			File glosFile = new File(glosFileName);
			String glosJSONString = FileUtils.readFileToString(glosFile, "UTF-8");
			
			JSONArray glossary = new JSONArray(glosJSONString);
			
			StringBuffer result = new StringBuffer();
			
			result.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
			result.append("<html>");
				result.append("<head>");
					result.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
					result.append("<title>Glossary - AOK</title>");
					result.append("<link media=\"all\" type=\"text/css\" href=\"css/layout.css\" rel=\"stylesheet\"/>");
					result.append("<link media=\"all\" type=\"text/css\" href=\"css/typo.css\" rel=\"stylesheet\"/>");
				result.append("</head>");
				result.append("<body>");
					result.append("<div id=\"header\"></div>");
				
					result.append("<div id=\"main\">");
					
						result.append("<h1>Glossar - AOK</h1>");
						
						for(int i = 0; i < glossary.length(); i++) {
							JSONObject entry = glossary.getJSONObject(i);
							result.append("<h4>" + entry.getString("title") + "</h4>");
							result.append("<p>" + entry.getString("description") + "</p>");
							result.append("<p><strong>Prozesse:</strong><br/>");
							result.append(entry.getString("occurrence") + "</p>");
							
						}
	
						result.append("<p> </p>");
						result.append("<h3>Einen neuen Eintrag hinzufÃ¼gen:</h3><p>");
						
						result.append("<form action=\"/oryx/glossary\" method=\"post\">");
							result.append("<strong>Name</strong><br/>");
							result.append("<textarea class=\"twikiInputField\" name=\"name\" cols=\"50\" rows=\"2\"></textarea><br/>");
							result.append("<strong>Beschreibung</strong><br/>");
							result.append("<textarea class=\"twikiInputField\" name=\"description\" cols=\"50\" rows=\"10\"></textarea>");
							result.append("<br/><input class=\"twikiSubmit\" type=\"submit\" name=\"submit\" value=\"Speichern\" />");
						result.append("</form></p>");
					
					result.append("</div>");
				result.append("</body>");
				
			result.append("<html>");
			
			res.setContentType("text/html");
			res.setCharacterEncoding("UTF-8");
			res.getWriter().write(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Glossary End");
	}
	
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		System.out.println("Glossary");
		try {
			String name = req.getParameter("name");
			String description = req.getParameter("description");
			String occurrence = "";
			
			String glosFileName = this.getServletContext().getRealPath("/glossary.json");
			
			File glosFile = new File(glosFileName);
			String glosJSONString = FileUtils.readFileToString(glosFile, "UTF-8");
			
			JSONArray glossary = new JSONArray(glosJSONString);
			
			JSONObject newEntry = new JSONObject();
			newEntry.put("title", name);
			newEntry.put("description", description);
			newEntry.put("occurrence", occurrence);
			glossary.put(newEntry);
			
			glosJSONString = glossary.toString();
			
			FileUtils.writeStringToFile(glosFile, glosJSONString, "UTF-8");
			
			doGet(req, res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Glossary End");
	}
}
