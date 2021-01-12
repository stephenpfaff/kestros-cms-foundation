/*
 *      Copyright (C) 2020  Kestros, Inc.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.kestros.cms.sitebuilding.core.servlets;

import io.kestros.cms.sitebuilding.api.services.PageRenderService;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet used for associating jcr:content resources to a kes:Page request.
 */
@Component(service = {Servlet.class},
           property = {"sling.servlet.resourceTypes=kes:Page",
               "sling.servlet.resourceTypes=kes:Site", "sling.servlet.extensions=html",
               "sling.servlet.methods=" + HttpConstants.METHOD_TRACE,
               "sling.servlet.methods=" + HttpConstants.METHOD_GET,
               "sling.servlet.methods=" + HttpConstants.METHOD_OPTIONS,
               "sling.servlet.methods=" + HttpConstants.METHOD_HEAD})
public class PageRenderServlet extends SlingSafeMethodsServlet {

  private static final Logger LOG = LoggerFactory.getLogger(PageRenderServlet.class);
  private static final long serialVersionUID = -1646968733338928122L;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PageRenderService pageRenderService;

  @Override
  protected void doGet(@Nonnull final SlingHttpServletRequest request,
      @Nonnull final SlingHttpServletResponse response) throws IOException {
    if (getPageRenderService() != null) {
      getPageRenderService().render(request, response);
    } else {
      LOG.error("Unable to render page {}, no PageRenderService detected.",
          request.getRequestPathInfo());
      response.setStatus(400);
    }
  }

  PageRenderService getPageRenderService() {
    return pageRenderService;
  }
}