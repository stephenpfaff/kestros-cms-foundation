package io.kestros.cms.foundation.services.pagerendermethod;

import java.io.IOException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

/**
 * Strategy for rendering pages.
 */
public interface PageRenderMethod {

  /**
   * Renders the requested page.
   *
   * @param request Request.
   * @param response Response.
   * @throws IOException Failed to write the response.
   */
  void doRender(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException;

  /**
   * Whether to use this PageRenderMethod to render the requested page.
   *
   * @param request Request.
   * @return Whether to use this PageRenderMethod to render the requested page.
   */
  Boolean useRenderMethod(SlingHttpServletRequest request);

}
