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

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentUiFrameworkViewException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidScriptException;
import io.kestros.cms.componenttypes.api.exceptions.ScriptRetrievalException;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.componenttypes.api.services.ComponentViewScriptResolutionCacheService;
import io.kestros.cms.componenttypes.api.services.ScriptRetrievalService;
import io.kestros.cms.performanceservices.api.services.PerformanceService;
import io.kestros.cms.performanceservices.api.services.PerformanceTrackerService;
import io.kestros.cms.sitebuilding.api.models.BaseContentPage;
import io.kestros.cms.sitebuilding.api.models.BaseSite;
import io.kestros.cms.sitebuilding.api.models.ComponentRequestContext;
import io.kestros.cms.sitebuilding.api.models.ParentComponent;
import io.kestros.cms.sitebuilding.api.services.ScriptProviderService;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.exceptions.ThemeRetrievalException;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkRetrievalService;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.BaseServiceResolverService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides script paths for {@link ParentComponent}.  Looks up the {@link ComponentUiFrameworkView}
 * for the current page checks if a matching script is found. Falls back to the `common`
 * ComponentUiFrameworkView.
 */
@Component(immediate = true,
           service = ScriptProviderService.class,
           property = "service.ranking:Integer=1")
public class BaseScriptProviderService extends BaseServiceResolverService
    implements ScriptProviderService, PerformanceService {

  public static final String KESTROS_HTL_TEMPLATE_CACHE_PURGE_SERVICE_USER
      = "kestros-script-provider";

  private static final Logger LOG = LoggerFactory.getLogger(BaseScriptProviderService.class);

  @Reference
  ResourceResolverFactory resourceResolverFactory;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ComponentViewScriptResolutionCacheService componentViewScriptResolutionCacheService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ScriptRetrievalService scriptRetrievalService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiFrameworkRetrievalService uiFrameworkRetrievalService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PerformanceTrackerService performanceTrackerService;

  @Override
  protected String getServiceUserName() {
    return KESTROS_HTL_TEMPLATE_CACHE_PURGE_SERVICE_USER;
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
    return "Cached Script Provider Service.";
  }

  @Override
  public void deactivate(ComponentContext componentContext) {

  }

  /**
   * Retrieves the path to a specified script name, resolved proper to the ComponentUiFramework
   * view.
   *
   * @param parentComponent Component to retrieve the script for.
   * @param scriptName Script to retrieve.
   * @param request current SlingHttpServletRequest. Used to find script paths for referenced
   *     components.
   * @return The path to a specified script.
   * @throws InvalidScriptException The script was not found, or could not be adapt to *
   *     HtmlFile.
   * @throws InvalidComponentTypeException expected componentType for the request component was
   *     missing or invalid.
   */
  public String getScriptPath(ParentComponent parentComponent, final String scriptName,
      SlingHttpServletRequest request)
      throws InvalidScriptException, InvalidComponentTypeException {
    String tracker = startPerformanceTracking();

    ComponentType componentType = parentComponent.getComponentType();
    UiFramework uiFramework = getUiFrameworkForComponentRequest(parentComponent, request);
    if (componentViewScriptResolutionCacheService != null) {
      if (uiFramework != null) {
        try {
          LOG.trace("Finished retrieving Script Path {}", scriptName);
          return componentViewScriptResolutionCacheService.getCachedScriptPath(scriptName,
              componentType, uiFramework, request);
        } catch (CacheRetrievalException e) {
          LOG.debug("Failed to retrieve cached script resolution. {}.", e.getMessage());
        }
      } else {
        LOG.warn(
            "Unable to attempt component view script resolution via cache. UiFramework was null.");
      }
    } else {
      LOG.warn(
          "Unable to attempt component view script resolution via cache. No service registered.");
    }

    //    try {
    try {
      if (scriptRetrievalService != null) {
        String resolvedScriptPath = scriptRetrievalService.getScript(scriptName, componentType,
            uiFramework).getPath();

        if (componentViewScriptResolutionCacheService != null) {
          componentViewScriptResolutionCacheService.cacheComponentViewScriptPath(scriptName,
              componentType, uiFramework, resolvedScriptPath, request);
        }
        endPerformanceTracking(tracker);
        return resolvedScriptPath;
      }
    } catch (InvalidComponentUiFrameworkViewException | ScriptRetrievalException e) {
      LOG.error("Unable to retrieve script {} for resource {}, componentType. {}", scriptName,
          parentComponent.getPath(), componentType.getPath(), e.getMessage());
    }
    endPerformanceTracking(tracker);
    return null;

    //    }
    /*    catch (final ModelAdaptionException exception) {
      LOG.trace("Attempting to retrieve and cache common view for script resolution for {}.",
          componentType.getPath());
      String resolvedScriptPath = scriptRetrievalService.getScript(scriptName, componentType,
          null).getPath();
      if (uiFramework != null) {
        if (componentViewScriptResolutionCacheService != null) {
          componentViewScriptResolutionCacheService.cacheComponentViewScriptPath(scriptName,
              componentType, uiFramework, resolvedScriptPath, request);
        }
      }
      LOG.trace("Finished retrieving Script Path {}", scriptName);
      return resolvedScriptPath;
    }*/

    //    throw new InvalidScriptException(scriptName,
    //        String.format("Unable to retrieve theme for resource %s, with request URI %s.",
    //            parentComponent.getPath(), request.getRequestURI()));
  }

  @Nullable
  private UiFramework getUiFrameworkFromPageOrSite(final SlingHttpServletRequest request) {
    String tracker = startPerformanceTracking();
    String requestContext = request.getRequestURI().split(".html")[0];
    UiFramework uiFramework = null;
    try {
      uiFramework = getResourceAsType(requestContext, request.getResourceResolver(),
          BaseContentPage.class).getTheme().getUiFramework();
    } catch (ResourceNotFoundException | InvalidThemeException exception) {
      LOG.trace(exception.getMessage());
    } catch (InvalidResourceTypeException e) {
      try {
        uiFramework = getResourceAsType(requestContext, request.getResourceResolver(),
            BaseSite.class).getTheme().getUiFramework();
      } catch (ModelAdaptionException exception) {
        LOG.trace(exception.getMessage());
      }
    } catch (ThemeRetrievalException exception) {
      exception.printStackTrace();
    }
    endPerformanceTracking(tracker);
    return uiFramework;
  }

  @Nullable
  private UiFramework getUiFrameworkForComponentRequest(final ParentComponent parentComponent,
      final SlingHttpServletRequest request) {
    final String tracker = startPerformanceTracking();
    UiFramework uiFramework = null;
    try {
      ComponentRequestContext requestContext = request.adaptTo(ComponentRequestContext.class);
      if (requestContext != null && requestContext.getCurrentPage() != null) {
        uiFramework = requestContext.getCurrentPage().getTheme().getUiFramework();
      } else {
        uiFramework = parentComponent.getTheme().getUiFramework();
      }
    } catch (ModelAdaptionException e) {
      LOG.trace(e.getMessage());
    }
    if (uiFramework == null) {
      uiFramework = getUiFrameworkFromPageOrSite(request);
    }

    if (uiFramework == null) {
      try {
        uiFramework = parentComponent.getTheme().getUiFramework();
      } catch (ModelAdaptionException e) {
        LOG.trace(e.getMessage());
      }
    }
    if (uiFramework == null) {
      uiFramework = getUiFrameworkFromRequestParameter(request);
    }
    endPerformanceTracking(tracker);
    return uiFramework;
  }

  @Nullable
  private UiFramework getUiFrameworkFromRequestParameter(final SlingHttpServletRequest request) {
    String tracker = startPerformanceTracking();
    String frameworkCode = request.getParameter("ui-framework");
    for (UiFramework uiFramework :
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(
        true, true)) {
      if (uiFramework.getFrameworkCode().equals(frameworkCode)) {
        endPerformanceTracking(tracker);
        return uiFramework;
      }
    }
    endPerformanceTracking(tracker);
    return null;
  }

  @Override
  public PerformanceTrackerService getPerformanceTrackerService() {
    return performanceTrackerService;
  }
}
