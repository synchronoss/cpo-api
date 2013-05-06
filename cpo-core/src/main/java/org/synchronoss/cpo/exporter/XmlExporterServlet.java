/*
 * Copyright (C) 2003-2012 David E. Berry
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.cpo.exporter;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

/**
 * Servlet that will output the current state of a meta descriptor in the form of xml.
 * This xml can be opened in CpoUtil for viewing or any other means appropriate
 *
 * @author Michael Bellomo
 * @since 7/16/12
 */
public class XmlExporterServlet extends HttpServlet {

  private static final String HTML_CONTENT_TYPE = "text/html";
  private static final String XML_CONTENT_TYPE = "text/xml";
  public static final String PARAM_META_DESCRIPTOR_NAME = "metaDescriptorName";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    PrintWriter pw = response.getWriter();
    try {
      String metaDescriptorName = request.getParameter(PARAM_META_DESCRIPTOR_NAME);
      CpoMetaDescriptor metaDescriptor = CpoMetaDescriptor.getInstance(metaDescriptorName);
      if (metaDescriptor == null) {
        throw new CpoException("No meta descriptor found: " + metaDescriptorName);
      }
      response.setContentType(XML_CONTENT_TYPE);
      metaDescriptor.export(pw);
    } catch (Exception e) {
      response.setContentType(HTML_CONTENT_TYPE);
      pw.println("<html><head><title>ERROR</title></head><p>Error generating xml: <pre>");
      e.printStackTrace(pw);
      pw.println("</pre></p></html>");
    } finally {
      pw.flush();
      pw.close();
    }
  }
}
