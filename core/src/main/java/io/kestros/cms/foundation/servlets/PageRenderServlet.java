package io.kestros.cms.foundation.servlets;

import io.kestros.cms.foundation.services.pagerenderservice.PageRenderService;
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
//TODO clean up the annotation
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