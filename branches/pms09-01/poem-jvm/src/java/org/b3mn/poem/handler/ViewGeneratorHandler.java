package org.b3mn.poem.handler;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.ViewGenerator.ViewGenerator;
import org.b3mn.poem.util.HandlerWithoutModelContext;


@HandlerWithoutModelContext(uri="/viewgenerator")
public class ViewGeneratorHandler extends HandlerBase {

	private static final long serialVersionUID = -2308798783469734955L;
	private String baseURL = "viewgenerator/";
	private String oryxRootDirectory = "C:\\Programme\\Apache Software Foundation\\Tomcat 6.0\\webapps\\oryx\\";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException { 
		doGetOrPost(req, resp); 	
	} 
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGetOrPost(req, resp); 
	} 
	
	private boolean isCorrectlySet(HttpServletRequest req, String paramName) {
		String value = req.getParameter(paramName); 
		if ((value == null) || ("".equals(value))) {
			return false;
		}
		return true;
	}
	
	private void doGetOrPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String baseParamName = "d";
		int count = 0;
		String value = req.getParameter(baseParamName + count); 
		PrintWriter out = resp.getWriter(); 
		resp.setContentType("text/plain"); 
		if (value == null) { 
			out.println("There was no input diagrams parameter d0 given.");
		} 
		else if ("".equals(value)) {
			// The request parameter 'param' was present in the query string but has no value 
			// e.g. http://hostname.com?param=&a=b  
			out.println("The input diagrams parameter was empty, no diagrams were selected as input.");
		}
		else {
			ArrayList<String> diagramIds = new ArrayList<String>();
			diagramIds.add(value);
			count +=1;
			
			while (this.isCorrectlySet(req, baseParamName+count)) {
				value = req.getParameter(baseParamName + count);
				diagramIds.add(value);
				count +=1;
			}
			
			ViewGenerator viewGenerator = new ViewGenerator(oryxRootDirectory, baseURL.replace("/", "\\"));
			viewGenerator.generate(diagramIds);
			
//			set for local testing, should be changed
			resp.sendRedirect(baseURL + viewGenerator.getOverviewHTMLName());

//			BufferedReader br = new BufferedReader(new FileReader(overviewHTMLFile));
//			String line;
//			  
//			while((line = br.readLine()) != null) {
//				out.println(line);
//			}
			out.close(); 			 
		}
	}

	

//	public void doGet(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
//		String title = "Sehr einfaches Servlet";
//		String text = "Hello World!";         
//		PrintWriter out = response.getWriter();
//		out.println(  "<html>"  );
//		out.println(  "<head>"  );
//		out.println(  "<title>" + title + "</title>"  );
//		out.println(  "</head>"  );
//		out.println(  "<body bgcolor=\"white\">  ");
//		out.println(  text  );
//		out.println(  "<table width=\"100%\">  ");
//		
//		for (int i=0;i<3;i++) {
//			               
//			out.println("<tr>");
//			        
//			for(int j=1;j<10;j++) {
//				out.println( "<td>" );
//			    out.println(  i+j  );
//			    out.println(  "</td>"  );
//			}
//			       
//			out.println("  </tr>  ");
//			        
//		}        
//		out.println(  "</table>"  );
//		out.println(  "</body>"  );
//		out.println(  "</html>"  );
//	}
	
	}




