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
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.io.IOException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

/**
 * Service which determines how headless json responses will be rendered.
 */
public interface HeadlessRenderService extends ManagedService {

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
