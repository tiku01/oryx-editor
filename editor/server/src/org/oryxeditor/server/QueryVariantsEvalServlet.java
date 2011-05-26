/**
 * Copyright (c) 2009, 2010 Emilian Pascalau and Ahmed Awad
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.oryxeditor.server;


import java.io.IOException;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.bpmnq.AbstractQueryProcessor;

import com.bpmnq.ORYXModelXMLParser;
import com.bpmnq.OryxMemoryQueryProcessor;
import com.bpmnq.PartialProcessModel;
import com.bpmnq.ProcessGraph;
import com.bpmnq.Utilities;


public class QueryVariantsEvalServlet extends HttpServlet {
    private static final long serialVersionUID = -7946509291423453168L;
    private static final boolean useDataBaseConnection = false;
    private Logger log = Logger.getLogger(this.getClass());
 
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	System.out.println("QUERY VARIANT INVOKED");
    	resp.setContentType("text/xml");
        resp.setCharacterEncoding("UTF-8");
        long startTime,endTime;
        startTime = System.currentTimeMillis();
        String rdf = req.getParameter("data");
        System.out.println("rdf is "+rdf);
        // We have to process the incoming model rdf link
        if (rdf.contains("bpmnqvar#"))
        	rdf = rdf.replace("/oryx/editor;bpmnqvar#", "/backend/poem");
        else if (rdf.contains("/oryx/editor;default?stencilset=/stencilsets/bpmnqvar/bpmnqvar.json#"))
        	rdf = rdf.replace("/oryx/editor;default?stencilset=/stencilsets/bpmnqvar/bpmnqvar.json#", "/backend/poem");
        else // this is fhe default# case
        	rdf = rdf.replace("/oryx/editor;default#", "/backend/poem");
        rdf=rdf+"/rdf";
        
        rdf = rdf.replace("//rdf", "/rdf");
        
        System.out.println("model link >>>> "+rdf);
        
        //rdf = rdf.replace("self", "rdf");
        
        
        
        
//        InputStream rdfStream = new ByteArrayInputStream(rdf.getBytes("UTF-8"));
//        log.debug("reading in rdfStream as UTF-8.");
//        log.trace("read following RDF: " + rdf);
        
        // initialize BPMNQ processor
        try {
            Utilities util = Utilities.getInstance();
            if (useDataBaseConnection && !Utilities.isConnectionOpen()) {
                Utilities.openConnection();
                System.out.println(" +++++++++++++++ DB Connection has been opened ++++++++++++");
            }

        } catch (Exception ex) {
            // rethrow as ServletException
            throw new ServletException("Cannot communicate with BPMN-Q database", ex);
        }
        System.out.println("Starting PPM processing");
        ORYXModelXMLParser xp=new ORYXModelXMLParser();

//   	 	xp.createModel(rdfStream);
        xp.createModel(rdf);
   	 	PartialProcessModel ppm = xp.getPartialProcessModel();
   	 	AbstractQueryProcessor qProcessor;
   	 	PrintWriter respWriter = resp.getWriter();
   	 	
   	 	qProcessor = new OryxMemoryQueryProcessor(null);
   	 	qProcessor.stopAtFirstMatch = false;
   	    qProcessor.includeEnclosingAndSplits = false;
   	    qProcessor.allowGenericShapeToEvaluateToNone = false;
   	    ProcessGraph result = ppm.evaluatePPM(qProcessor);
//   	 respWriter.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
   	    respWriter.println("<result>");
   	    result.exportXMLDetailed(respWriter);
   	    respWriter.println("</result>");
   	    
   	    PrintWriter writer = new PrintWriter(System.out);
   	    writer.println("<result>");
   	    result.exportXMLDetailed(writer);
   	    writer.println("</result>");
   	    writer.flush();
   	    System.out.println("Terminating PPM processing");
   	                   
        try {
            Utilities.closeConnection();
        } catch (SQLException e) {
            log("Closing DB connection failed " + e.getMessage(), e);
        }
        endTime = System.currentTimeMillis();
        log.info("Total processing time "+ (endTime - startTime) + " ms");
    }

    
    
}
