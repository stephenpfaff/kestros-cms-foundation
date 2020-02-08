package io.kestros.cms.foundation.services.headlessrender;

import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.io.IOException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

/**
 * Service which determines how headless json responses will be rendered.
 */
public interface HeadlessRenderService {

  /**
   * Renders a headless response for the given request.
   *
   * @param request Request.
   * @param response Response.
   * @throws IOException Failed to write JSON response.
   * @throws InvalidResourceTypeException Requested resource could not be adapted to any model
   *     type.
   */
  void renderHeadlessResponse(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException, InvalidResourceTypeException;

}
