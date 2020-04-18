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

package io.kestros.cms.foundation.content;

import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.cms.foundation.content.sites.BaseSite;
import io.kestros.commons.structuredslingmodels.BaseSlingRequest;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component request context.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class ComponentRequestContext extends BaseSlingRequest {

  private static final Logger LOG = LoggerFactory.getLogger(ComponentRequestContext.class);

  /**
   * Requested page.
   *
   * @return requested page.
   */
  public BaseContentPage getCurrentPage() {
    try {
      return SlingModelUtils.getResourceAsType(getRequest().getRequestURI().split(".html")[0],
          getResourceResolver(), BaseContentPage.class);
    } catch (InvalidResourceTypeException e) {
      try {
        return SlingModelUtils.getResourceAsType(getRequest().getRequestURI().split(".html")[0],
            getResourceResolver(), BaseSite.class);
      } catch (InvalidResourceTypeException invalidResourceTypeException) {
        LOG.warn("Unable to adapt current page resource to BaseContentPage or BaseSite for "
                 + "NavigationContext.");
      } catch (ResourceNotFoundException resourceNotFoundException) {
        LOG.warn("Unable to find current page resource for NavigationContext.");
      }
    } catch (ResourceNotFoundException e) {
      LOG.warn("Unable to find current page resource for NavigationContext.");
    }
    return null;
  }

}
