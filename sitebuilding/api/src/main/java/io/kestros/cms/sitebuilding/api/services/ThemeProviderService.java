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

package io.kestros.cms.sitebuilding.api.services;

import io.kestros.cms.sitebuilding.api.models.BaseComponent;
import io.kestros.cms.sitebuilding.api.models.BaseContentPage;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.exceptions.ThemeRetrievalException;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.services.ThemeRetrievalService;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;

/**
 * Provides Themes for {@link BaseContentPage} and {@link BaseComponent} instances.
 */
public interface ThemeProviderService extends ManagedService {

  /**
   * Retrieves the {@link Theme} for a page.
   *
   * @param page Page to retrieve the Theme for.
   * @return The {@link Theme} for a page.
   * @throws ResourceNotFoundException Expected Theme Resource was not found.
   * @throws InvalidThemeException Theme Resource was found, but could not be adatped to Theme.
   * @throws ThemeRetrievalException Failure while retrieving Theme.
   */
  Theme getThemeForPage(BaseContentPage page)
      throws ResourceNotFoundException, InvalidThemeException, ThemeRetrievalException;

  /**
   * Retrieves the {@link Theme} for a Component.
   *
   * @param component Component to retrieve the Theme for.
   * @return The {@link Theme} for a component.
   * @throws ResourceNotFoundException Expected Theme Resource was not found.
   * @throws InvalidThemeException Theme Resource was found, but could not be adatped to Theme.
   * @throws ThemeRetrievalException Failure while retrieving Theme.
   */
  Theme getThemeForComponent(BaseComponent component)
      throws ResourceNotFoundException, InvalidThemeException, ThemeRetrievalException;

  /**
   * Theme Retrieval Service.
   *
   * @return Theme Retrieval Service.
   */
  ThemeRetrievalService getVirtualThemeProviderService();
}
