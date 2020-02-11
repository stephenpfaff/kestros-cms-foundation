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
  private HeadlessRenderService headlessRenderService;

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
