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

package io.kestros.cms.uiframeworks.core.services;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import io.kestros.cms.performanceservices.api.services.PerformanceService;
import io.kestros.cms.performanceservices.api.services.PerformanceTrackerService;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.exceptions.ThemeRetrievalException;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.ThemeRetrievalService;
import io.kestros.cms.uiframeworks.core.models.ThemeResource;
import io.kestros.cms.uiframeworks.core.models.UiFrameworkResource;
import io.kestros.cms.uiframeworks.core.models.VirtualTheme;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.commons.osgiserviceutils.services.BaseServiceResolverService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves {@link ThemeResource} objects.
 */
@Component(immediate = true,
           service = ThemeRetrievalService.class)
public class ThemeRetrievalServiceImpl extends BaseServiceResolverService
    implements ThemeRetrievalService, PerformanceService {

  private static final Logger LOG = LoggerFactory.getLogger(ThemeRetrievalServiceImpl.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ResourceResolverFactory resourceResolverFactory;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private VersionService versionService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PerformanceTrackerService performanceTrackerService;

  @Override
  public Theme getTheme(String themeName, UiFramework uiFramework) throws ThemeRetrievalException {
    String tracker = startPerformanceTracking();
    try {
      BaseResource themesFolderResource = getChildAsBaseResource("themes",
          uiFramework.getResource());
      endPerformanceTracking(tracker);
      return getChildAsType(themeName, themesFolderResource, ThemeResource.class);
    } catch (ModelAdaptionException e) {
      endPerformanceTracking(tracker);
      throw new ThemeRetrievalException(themeName, uiFramework);
    }
  }

  @Override
  public Theme getTheme(String themePath) throws ThemeRetrievalException {
    String tracker = startPerformanceTracking();
    try {
      endPerformanceTracking(tracker);
      return getResourceAsType(themePath, getServiceResourceResolver(), ThemeResource.class);
    } catch (ModelAdaptionException e) {
      endPerformanceTracking(tracker);
      throw new ThemeRetrievalException(themePath);
    }
  }

  @Override
  public List<Theme> getThemes(UiFramework uiFramework) {
    String tracker = startPerformanceTracking();
    List<Theme> themeList = new ArrayList<>();
    try {
      BaseResource themesFolderResource = getChildAsBaseResource("themes",
          uiFramework.getResource());
      themeList.addAll(getChildrenOfType(themesFolderResource, ThemeResource.class));
      return themeList;
    } catch (ChildResourceNotFoundException e) {
      themeList.addAll(getInheritedVirtualThemes(uiFramework));
    }
    endPerformanceTracking(tracker);
    return themeList;
  }

  @Override
  public Theme getVirtualTheme(String virtualThemePath)
      throws InvalidThemeException, ThemeRetrievalException {
    String tracker = startPerformanceTracking();
    String themeName = virtualThemePath.split("/themes/")[virtualThemePath.split("/themes/").length
                                                          - 1];
    String uiFrameworkPath = virtualThemePath.split("/themes/")[0];
    UiFrameworkResource uiFramework = null;
    try {
      uiFramework = getResourceAsType(uiFrameworkPath, getServiceResourceResolver(),
          UiFrameworkResource.class);
      for (Theme theme : getThemes(uiFramework)) {
        if (theme.getPath().equals(virtualThemePath)) {
          endPerformanceTracking(tracker);
          return theme;
        }
      }
      endPerformanceTracking(tracker);
      throw new ThemeRetrievalException(uiFramework);
    } catch (InvalidResourceTypeException e) {
      endPerformanceTracking(tracker);
      throw new InvalidThemeException(themeName, uiFrameworkPath);
    } catch (ResourceNotFoundException e) {
      endPerformanceTracking(tracker);
      throw new InvalidThemeException(themeName, uiFrameworkPath);
    }


  }

  @Override
  public List<Theme> getInheritedVirtualThemes(UiFramework uiFramework) {
    List<Theme> inheritedThemeList = new ArrayList<>();
    List<Theme> virtualThemeList = new ArrayList<>();

    if (versionService != null) {
      try {
        UiFramework previousUiFrameworkVersion = versionService.getPreviousVersion(uiFramework);
        if (previousUiFrameworkVersion != null) {
          inheritedThemeList.addAll(getThemes(previousUiFrameworkVersion));
          if (inheritedThemeList.isEmpty()) {
            inheritedThemeList.addAll(getInheritedVirtualThemes(previousUiFrameworkVersion));
          }
        }
      } catch (NoValidAncestorException e) {
        LOG.debug(
            "Could not inherit themes for UiFramework {}. No parent ManagedUiFramework found.",
            uiFramework.getPath());
      }
    } else {
      LOG.warn("Could not create virtual themes for {}, VersionService was null.",
          uiFramework.getPath());
    }
    for (Theme theme : inheritedThemeList) {
      virtualThemeList.add(new VirtualTheme(theme, uiFramework));
    }
    return virtualThemeList;
  }

  @Override
  protected String getServiceUserName() {
    return "theme-retrieval";
  }

  @Override
  protected List<String> getRequiredResourcePaths() {
    return Collections.emptyList();
  }

  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

  @Override
  public String getDisplayName() {
    return "Theme Retrieval Service";
  }

  @Override
  public void deactivate(ComponentContext componentContext) {
    LOG.info("Deactivating {}.", getClass().getSimpleName());
  }

  @Override
  public PerformanceTrackerService getPerformanceTrackerService() {
    return performanceTrackerService;
  }
}
