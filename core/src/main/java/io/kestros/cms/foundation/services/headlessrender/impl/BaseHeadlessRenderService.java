package io.kestros.cms.foundation.services.headlessrender.impl;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.adaptTo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.cms.foundation.content.sites.BaseSite;
import io.kestros.cms.foundation.services.headlessrender.HeadlessRenderService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.io.IOException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true,
           service = HeadlessRenderService.class,
           property = "service.ranking:Integer=1")
public class BaseHeadlessRenderService implements HeadlessRenderService {

  @Override
  public void renderHeadlessResponse(final SlingHttpServletRequest request,
      final SlingHttpServletResponse response) throws IOException, InvalidResourceTypeException {
    BaseContentPage page;
    try {
      page = adaptTo(request.getResource(), BaseContentPage.class);
    } catch (final InvalidResourceTypeException exception) {
      try {
        page = adaptTo(request.getResource(), BaseSite.class);
      } catch (final InvalidResourceTypeException exception1) {
        response.setStatus(400);
        throw new InvalidResourceTypeException(request.getResource().getPath(),
            BaseContentPage.class,
            "Unable to adapt to BaseContentPage or Site while building headless response.");
      }
    }
    response.setContentType("application/json");
    final ObjectMapper mapper = new ObjectMapper();
    final String json = mapper.writeValueAsString(page);
    response.getWriter().write(json);
  }
}
