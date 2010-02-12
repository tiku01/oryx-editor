/***************************************
 * Copyright (c) 2010 
 * Martin Krüger
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
package org.b3mn.poem.handler;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3mn.poem.Identity;
import org.b3mn.poem.util.ExportHandler;
@ExportHandler(uri="/xml", formatName="XML File", iconUrl="/backend/images/silk/page_code.png")
public class XMLFileExporter extends HandlerBase {
 final static String begin="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
   "<oryxmodel>" +
   "<description>";
 final static String part1="</description>" +
   "<type>";
 final static String part2="</type>" +
   "<json-representation><![CDATA[";
 final static String part3="]]></json-representation>" +
   "<svg-representation><![CDATA[";
 final static String end="]]></svg-representation>" +
   "</oryxmodel>";
 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object)  {
  
  res.setContentType("text/xml");
    res.setStatus(200);
    try {
     URL serverUrl = new URL( req.getScheme(),
                    req.getServerName(),
                    req.getServerPort(),
                    "" );
     res.setHeader("Content-Disposition", "inline; filename="+object.read().getTitle()+".xml" );
     PrintWriter out = res.getWriter();
     out.write(begin);
     out.write(object.read().getSummary());
     out.write(part1);
     out.write(object.read().getType());
     out.write(part2);
     out.write(object.read().getJson(serverUrl.toString()));
     out.write(part3);
     out.write(object.read().getSvg());
     out.write(end);
    } catch (IOException ie) {
   ie.printStackTrace();
  }
    }
}