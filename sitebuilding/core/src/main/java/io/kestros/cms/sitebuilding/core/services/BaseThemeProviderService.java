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

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getFirstAncestorOfType;

import io.kestros.cms.performanceservices.api.services.PerformanceService;
import io.kestros.cms.performanceservices.api.services.PerformanceTrackerService;
import io.kestros.cms.sitebuilding.api.models.BaseComponent;
import io.kestros.cms.sitebuilding.api.models.BaseContentPage;
import io.kestros.cms.sitebuilding.api.services.ThemeProviderService;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.exceptions.ThemeRetrievalException;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.services.ThemeRetrievalService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.hc.api.FormattingResultLog;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides {@link Theme} to {@link BaseComponent} and {@link BaseContentPage}. Looks up the Theme
 * base on the 'kes:Theme' property on the closest ancestor page with the property set.
 */
@Component(immediate = true,
           service = ThemeProviderService.class)
public class BaseThemeProviderService implements ThemeProviderService, PerformanceService {

  private static final Logger LOG = LoggerFactory.getLogger(BaseThemeProviderService.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  public ThemeRetrievalService themeRetrievalService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PerformanceTrackerService performanceTrackerService;

  @Override
  public ThemeRetrievalService getVirtualThemeProviderService() {
    return this.themeRetrievalService;
  }

  @Override
  public Theme getThemeForPage(final BaseContentPage page)
      throws ThemeRetrievalException, InvalidThemeException, ResourceNotFoundException {
    String tracker = startPerformanceTracking();
    String themePath = StringUtils.EMPTY;
    //    try {
    if (page != null) {
      themePath = page.getProperties().get("kes:theme", StringUtils.EMPTY);
      try {
        endPerformanceTracking(tracker);
        return getVirtualThemeProviderService().getTheme(themePath);
      } catch (ThemeRetrievalException exception) {
        try {
          endPerformanceTracking(tracker);
          return getThemeForPage(page.getParent());
        } catch (final NoParentResourceException exception1) {
          try {
            endPerformanceTracking(tracker);
            return getFirstAncestorOfType(page, BaseContentPage.class).getTheme();
          } catch (NoValidAncestorException e) {
            LOG.error(
                "Unable to inherit Theme for resource {}. No ancestor with a valid Theme could be"
                + "found.", page.getPath());
          } catch (ResourceNotFoundException e) {
            e.printStackTrace();
          } catch (InvalidThemeException e) {
            e.printStackTrace();
          } catch (ThemeRetrievalException e) {
            e.printStackTrace();
          }
        }
      }
    }
    //    }
    //    catch (final ResourceNotFoundException exception) {
    //
    //    }

    if (getVirtualThemeProviderService() != null) {
      endPerformanceTracking(tracker);
      return getVirtualThemeProviderService().getVirtualTheme(themePath);
    } else {
      LOG.error("Virtual theme provider service was null.");
    }
    endPerformanceTracking(tracker);
    throw new ResourceNotFoundException(themePath, "Theme reference resource missing or invalid.");
  }

  @Override
  @Nonnull
  public Theme getThemeForComponent(final BaseComponent component)
      throws InvalidThemeException, ResourceNotFoundException, ThemeRetrievalException {
    String tracker = startPerformanceTracking();
    if (component != null) {
      try {
        endPerformanceTracking(tracker);
        return getThemeForPage(component.getContainingPage());
      } catch (final NoValidAncestorException exception) {
        endPerformanceTracking(tracker);
        return getThemeFromFirstAncestor(component);
      }
    }
    endPerformanceTracking(tracker);
    throw new IllegalStateException();
  }

  @Nonnull
  private Theme getThemeFromFirstAncestor(final BaseResource resource)
      throws InvalidThemeException, ResourceNotFoundException, ThemeRetrievalException {
    String tracker = startPerformanceTracking();
    String themePath = StringUtils.EMPTY;
    BaseResource parentResource = resource;
    while (StringUtils.EMPTY.equals(themePath)) {
      themePath = parentResource.getProperty("kes:theme", StringUtils.EMPTY);
      try {
        parentResource = parentResource.getParent();
      } catch (final NoParentResourceException exception) {
        endPerformanceTracking(tracker);
        throw new InvalidThemeException(themePath,
            "No ancestor resource with configured Theme found.");
      }
    }
    endPerformanceTracking(tracker);
    return getVirtualThemeProviderService().getTheme(themePath);
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
    if (getVirtualThemeProviderService() == null) {
      log.warn("VirtualThemeProviderService was null.");
    }
  }

  @Override
  public PerformanceTrackerService getPerformanceTrackerService() {
    return performanceTrackerService;
  }
}
