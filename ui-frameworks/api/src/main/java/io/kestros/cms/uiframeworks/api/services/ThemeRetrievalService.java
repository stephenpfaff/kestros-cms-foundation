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

package io.kestros.cms.uiframeworks.api.services;

import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.exceptions.ThemeRetrievalException;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import java.util.List;

/**
 * Retrieves {@link Theme} objects.
 */
public interface ThemeRetrievalService extends ManagedService {

  /**
   * Retrieves a specified Theme.
   *
   * @param themeName Theme name.
   * @param uiFramework UiFramework the Theme belongs to.
   * @return A specified Theme.
   * @throws ThemeRetrievalException Failed to retrieve the specified Theme.
   */
  Theme getTheme(String themeName, UiFramework uiFramework) throws ThemeRetrievalException;

  /**
   * Retrieves a specified Theme.
   *
   * @param themePath Theme path.
   * @return A specified Theme.
   * @throws ThemeRetrievalException Failed to retrieve the specified Theme.
   */
  Theme getTheme(String themePath) throws ThemeRetrievalException;

  /**
   * Retrieves all themes for a UiFramework.
   *
   * @param uiFramework UiFramework.
   * @return All themes for a UiFramework.
   */
  List<Theme> getThemes(UiFramework uiFramework);

  /**
   * Retrieves a Virtual Theme. (if versioned UiFramework does not have have themes).
   *
   * @param virtualThemePath Path the desired theme.
   * @return Specified Virtual Theme.
   * @throws InvalidThemeException Invalid Theme.
   * @throws ThemeRetrievalException Failed to retrieve virtual theme.
   */
  Theme getVirtualTheme(String virtualThemePath)
      throws InvalidThemeException, ThemeRetrievalException;

  /**
   * Retrieves all Virtual themes for a specified UiFramework (if any).
   *
   * @param uiFramework UiFramework.
   * @return All Virtual themes for a specified UiFramework (if any).
   */
  List<Theme> getInheritedVirtualThemes(UiFramework uiFramework);
}
