/***************************************
 * Copyright (c) 2008
 * Philipp Berger 2009
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
****************************************/

package org.oryxeditor.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProcessWaveEditorHandler extends EditorHandler {
	final static String defaultSS= "/stencilsets/bpmn/bpmn.json";
	private static final long serialVersionUID = 1L;
	private static final Map<String,String> map = new HashMap<String, String>();
	static{
		map.put("bpmn2.0", "bpmn2.0");
		map.put("epc", "epc");
		map.put("petrinet", "petrinet");
		map.put("uml2.2", "uml2.2");
	}
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/*
		 * stencilsets/bpmn1.1/bpmn1.1.json
		 */
		

		String sset=request.getParameter("stencilset");
		String json = request.getParameter("json");
		String extString=request.getParameter("exts");
		String content = 
	        "<script type='text/javascript'>" +
	        "if(!ORYX) var ORYX = {};" +
	        "if(!ORYX.CONFIG) ORYX.CONFIG = {};\n" +
	        "ORYX.CONFIG.SSET='" + sset +"';\n" +
	        "ORYX.CONFIG.SSEXTS=" + extString + ";\n"+
        	"window.onOryxResourcesLoaded = function() {\n" +
        	"var json=" +json+";\n" +
        	"if(json.stencilset){\n" +
        	"json.stencilset.url='" +
        	sset+
        	"';\n" +
        	"}"+
            "new ORYX.Editor(json);\n"+
	      	  "}" +
          	"</script>";
		response.setContentType("application/xhtml+xml");
		
		response.getWriter().println(this.getOryxModel("Oryx-Editor", 
				content, this.getLanguageCode(request), 
				this.getCountryCode(request), profileName(sset)));
		response.setStatus(200);
		
	}
	private ArrayList<String> profileName(String stencilset) {
		//FIXME resue mapping from backend or transfer mapping to editor site
		ArrayList<String> list = new ArrayList<String>();
		Pattern p = Pattern.compile("/([^/]+).json");
		Matcher matcher = p.matcher(stencilset);
		if(matcher.find()){
			String name = map.get(matcher.group(1));
			if(name!=null){
				list.add(name);
			}
		}

	if(list.size()<1)
		list.add("default");
		return list;
	}
	
	
    
    
}
