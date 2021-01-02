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

package io.kestros.cms.foundation.services.impl;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.adaptTo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.cms.foundation.content.sites.BaseSite;
import io.kestros.cms.foundation.services.HeadlessRenderService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.io.IOException;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;

/**
 * Service which determines how headless json responses will be rendered.
 */
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

  @Override
  public String getDisplayName() {
    return "Headless Render Service";
  }

  @Override
  public void activate(ComponentContext componentContext) {

  }

  @Override
  public void deactivate(ComponentContext componentContext) {

  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {

  }
}
