package io.kestros.cms.foundation.services.pagerenderservice;

import java.io.IOException;
import java.io.Serializable;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

/**
 * Service which renders content requests.
 */
public interface ContentRenderService extends Serializable {

  /**
   * Renders a response for the requested content.
   *
   * @param request Request.
   * @param response Response.
   * @throws IOException Failed to write to the response.
   */
  void render(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response)
      throws IOException;

}
