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

package io.kestros.cms.foundation.servlets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.foundation.services.headlessrender.HeadlessRenderService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Servlet for rendering a headless JSON response for a given page or site.
 */
@Component(service = {Servlet.class},
           property = {"sling.servlet.resourceTypes=kes:Page",
               "sling.servlet.resourceTypes=kes:Site", "sling.servlet.resourceTypes=kes:Site",
               "sling.servlet.selectors=headless", "sling.servlet.extensions=json",
               "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class HeadlessPageServlet extends SlingAllMethodsServlet {

  private static final long serialVersionUID = 9009795847660986763L;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference
  private transient HeadlessRenderService headlessRenderService;

  @Override
  protected void doGet(@Nonnull final SlingHttpServletRequest request,
      @Nonnull final SlingHttpServletResponse response) throws IOException {
    if (getHeadlessRenderService() != null) {
      try {
        getHeadlessRenderService().renderHeadlessResponse(request, response);
      } catch (final InvalidResourceTypeException e) {
        response.setStatus(400);
      }
    } else {
      response.setStatus(400);
    }
  }

  HeadlessRenderService getHeadlessRenderService() {
    return headlessRenderService;
  }

}
