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

package io.kestros.cms.sitebuilding.core.services;

import static io.kestros.cms.uiframeworks.api.DesignConstants.PN_THEME_PATH;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getFirstAncestorOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import io.kestros.cms.sitebuilding.api.models.BaseComponent;
import io.kestros.cms.sitebuilding.api.models.BaseContentPage;
import io.kestros.cms.sitebuilding.api.services.ThemeProviderService;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.hc.api.FormattingResultLog;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides {@link Theme} to {@link BaseComponent} and {@link BaseContentPage}. Looks up the Theme
 * base on the 'kes:Theme' property on the closest ancestor page with the property set.
 */
@Component(immediate = true,
           service = ThemeProviderService.class,
           property = "service.ranking:Integer=1")
public class BaseThemeProviderService implements ThemeProviderService {

  private static final Logger LOG = LoggerFactory.getLogger(BaseThemeProviderService.class);

  @Override
  public Theme getThemeForPage(final BaseContentPage page)
      throws ResourceNotFoundException, InvalidThemeException {
    String themePath = StringUtils.EMPTY;
    try {
      if (page != null) {
        themePath = page.getProperties().get(PN_THEME_PATH, StringUtils.EMPTY);
        return getResourceAsType(themePath, page.getResourceResolver(), Theme.class);
      }
    } catch (final ResourceNotFoundException exception) {
      try {
        return getThemeForPage(page.getParent());
      } catch (final NoParentResourceException exception1) {
        try {
          return getFirstAncestorOfType(page, BaseContentPage.class).getTheme();
        } catch (NoValidAncestorException e) {
          LOG.error(
              "Unable to inherit Theme for resource {}. No ancestor with a valid Theme could be "
              + "found.", page.getPath());
        }
      }
    } catch (final InvalidResourceTypeException e) {
      throw new InvalidThemeException(themePath,
          "Could not adapt to Theme. Resource must have jcr:primaryType 'kes:Theme'.");
    }
    throw new ResourceNotFoundException(themePath, "Theme reference resource missing or invalid.");
  }

  @Override
  @Nonnull
  public Theme getThemeForComponent(final BaseComponent component)
      throws InvalidThemeException, ResourceNotFoundException {
    if (component != null) {
      try {
        return getThemeForPage(component.getContainingPage());
      } catch (final NoValidAncestorException exception) {
        return getThemeFromFirstAncestor(component);
      }
    }
    throw new IllegalStateException();
  }

  @Nonnull
  private Theme getThemeFromFirstAncestor(final BaseResource resource)
      throws InvalidThemeException, ResourceNotFoundException {
    String themePath = StringUtils.EMPTY;
    BaseResource parentResource = resource;
    while (StringUtils.EMPTY.equals(themePath)) {
      themePath = parentResource.getProperty("kes:theme", StringUtils.EMPTY);
      try {
        parentResource = parentResource.getParent();
      } catch (final NoParentResourceException exception) {
        throw new InvalidThemeException(themePath,
            "No ancestor resource with configured Theme found.");
      }
    }
    try {
      return getResourceAsType(themePath, resource.getResourceResolver(), Theme.class);
    } catch (final InvalidResourceTypeException exception) {
      throw new InvalidThemeException(themePath, exception.getMessage());
    }
  }

  @Override
  public String getDisplayName() {
    return "Theme Provider Service";
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
