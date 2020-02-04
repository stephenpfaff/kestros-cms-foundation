package io.kestros.cms.foundation.services.pagerendermethod;

import java.io.IOException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

public interface PageRenderMethod {

  void doRender(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException;

  Boolean useRenderMethod(SlingHttpServletRequest request);

}
