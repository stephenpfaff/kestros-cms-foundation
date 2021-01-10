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

package io.kestros.cms.foundation.services;

import io.kestros.commons.osgiserviceutils.services.ManagedService;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

/**
 * Renders responses for {@link io.kestros.cms.foundation.content.sites.BaseSite} and {@link
 * io.kestros.cms.foundation.content.pages.BaseContentPage} requests.
 */
public interface PageRenderService extends ManagedService {

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
