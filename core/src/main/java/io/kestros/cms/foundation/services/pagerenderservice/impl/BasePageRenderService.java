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

package io.kestros.cms.foundation.services.pagerenderservice.impl;

import static io.kestros.commons.osgiserviceutils.utils.OsgiServiceUtils.getAllOsgiServicesOfType;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.foundation.services.pagerendermethod.PageRenderMethod;
import io.kestros.cms.foundation.services.pagerenderservice.PageRenderService;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks to all registered {@link PageRenderMethod} instances to determine how to render a page or
 * site request.
 */
@Component(immediate = true,
           service = PageRenderService.class,
           property = "service.ranking:Integer=200")
public class BasePageRenderService implements PageRenderService {

  private static final Logger LOG = LoggerFactory.getLogger(BasePageRenderService.class);

  private static final long serialVersionUID = 7897119235609576690L;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  private transient ComponentContext componentContext;

  /**
   * Activates the service.
   *
   * @param ctx ComponentContext.
   */
  @Activate
  public void activate(final ComponentContext ctx) {
    componentContext = ctx;
  }

  @Override
  public void render(@Nonnull final SlingHttpServletRequest request,
      @Nonnull final SlingHttpServletResponse response) throws IOException {
    for (final PageRenderMethod pageRenderMethod : getPageRenderMethods()) {
      LOG.trace("Checking if {} is proper pageRenderMethod",
          pageRenderMethod.getClass().getSimpleName());
      if (pageRenderMethod.useRenderMethod(request)) {
        pageRenderMethod.doRender(request, response);
        break;
      }
    }
  }

  /**
   * List of all registered {@link PageRenderMethod} instances.
   *
   * @return List of all registered {@link PageRenderMethod} instances.
   */
  public List<PageRenderMethod> getPageRenderMethods() {
    return getAllOsgiServicesOfType(componentContext, PageRenderMethod.class);
  }
}
