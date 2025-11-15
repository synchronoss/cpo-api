package org.synchronoss.cpo.exporter;

/*-
 * [-------------------------------------------------------------------------
 * core
 * --------------------------------------------------------------------------
 * Copyright (C) 2003 - 2025 David E. Berry
 * --------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * --------------------------------------------------------------------------]
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;

/**
 * Servlet that will output the current state of a meta descriptor in the form of xml. This xml can
 * be opened in CpoUtil for viewing or any other means appropriate
 *
 * @author Michael Bellomo
 * @since 7/16/12
 */
public class XmlExporterServlet extends HttpServlet {
  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private static final String HTML_CONTENT_TYPE = "text/html";
  private static final String XML_CONTENT_TYPE = "text/xml";
  public static final String PARAM_META_DESCRIPTOR_NAME = "metaDescriptorName";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doPost(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    PrintWriter pw = response.getWriter();
    try {
      String metaDescriptorName = request.getParameter(PARAM_META_DESCRIPTOR_NAME);

      if (metaDescriptorName == null) {
        throw new CpoException("Missing MetaDescriptor name");
      }

      CpoMetaDescriptor metaDescriptor = CpoMetaDescriptor.getInstance(metaDescriptorName);
      if (metaDescriptor == null) {
        throw new CpoException("No meta descriptor found: " + metaDescriptorName);
      }

      response.setContentType(XML_CONTENT_TYPE);
      metaDescriptor.export(pw);
    } catch (CpoException e) {
      response.setContentType(HTML_CONTENT_TYPE);
      pw.println("<html><head><title>ERROR</title></head><p>Error generating xml: <pre>");
      ExceptionHelper.getLocalizedMessage(e);
      pw.println("</pre></p></html>");
    } finally {
      pw.flush();
      pw.close();
    }
  }
}
