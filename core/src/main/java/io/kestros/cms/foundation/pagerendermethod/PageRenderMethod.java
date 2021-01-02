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

package io.kestros.cms.foundation.pagerendermethod;

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
