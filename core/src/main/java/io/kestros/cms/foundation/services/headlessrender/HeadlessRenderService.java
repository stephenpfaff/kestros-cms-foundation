package io.kestros.cms.foundation.services.headlessrender;

import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.io.IOException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

public interface HeadlessRenderService {

  void renderHeadlessResponse(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException, InvalidResourceTypeException;

}
