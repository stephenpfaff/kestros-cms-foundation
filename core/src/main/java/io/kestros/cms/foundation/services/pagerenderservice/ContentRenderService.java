package io.kestros.cms.foundation.services.pagerenderservice;

import java.io.IOException;
import java.io.Serializable;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

public interface ContentRenderService extends Serializable {

  void render(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response)
      throws IOException;

}
