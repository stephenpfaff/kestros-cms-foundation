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

package io.kestros.cms.foundation.services.scriptprovider;

import io.kestros.cms.foundation.content.components.parentcomponent.ParentComponent;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.cms.foundation.content.sites.BaseSite;
import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.cms.foundation.exceptions.InvalidScriptException;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.BaseServiceResolverService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides script paths for {@link ParentComponent}.  Looks up the {@link
 * io.kestros.cms.foundation.componenttypes.frameworkview.ComponentUiFrameworkView} for the current
 * page checks if a matching script is found. Falls back to the `common` ComponentUiFrameworkView.
 */
@Component(immediate = true,
           service = ScriptProviderService.class,
           property = "service.ranking:Integer=1")
public class BaseScriptProviderService extends BaseServiceResolverService
    implements ScriptProviderService {

  public static final String KESTROS_HTL_TEMPLATE_CACHE_PURGE_SERVICE_USER
      = "kestros-script-provider";

  private static final Logger LOG = LoggerFactory.getLogger(BaseScriptProviderService.class);

  @Reference
  ResourceResolverFactory resourceResolverFactory;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ComponentViewScriptResolutionCacheService componentViewScriptResolutionCacheService;

  @Override
  protected String getServiceUserName() {
    return KESTROS_HTL_TEMPLATE_CACHE_PURGE_SERVICE_USER;
  }

  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
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
   */
  public String getScriptPath(ParentComponent parentComponent, final String scriptName,
      SlingHttpServletRequest request) throws InvalidScriptException {
    LOG.trace("Retrieving Script Path {}", scriptName);

    if (componentViewScriptResolutionCacheService != null) {
      try {
        LOG.trace("Finished retrieving Script Path {}", scriptName);
        return componentViewScriptResolutionCacheService.getCachedScriptPath(scriptName,
            parentComponent.getComponentType(), parentComponent.getTheme().getUiFramework());
      } catch (ModelAdaptionException e) {
        LOG.warn("Unable to attempt component view script resolution via cache. {}.",
            e.getMessage());
      } catch (CacheRetrievalException e) {
        LOG.debug("Failed to retrieve cached script resolution. {}.", e.getMessage());
      }
    } else {
      LOG.warn(
          "Unable to attempt component view script resolution via cache. No service registered.");
    }

    Theme theme = null;
    String requestContext = request.getRequestURI().split(".html")[0];
    try {
      theme = SlingModelUtils.getResourceAsType(requestContext, request.getResourceResolver(),
          BaseContentPage.class).getTheme();
    } catch (ResourceNotFoundException e) {
      try {
        theme = parentComponent.getTheme();
      } catch (ResourceNotFoundException | InvalidThemeException exception) {
        LOG.trace(exception.getMessage());
      }
    } catch (InvalidThemeException exception) {
      LOG.trace(exception.getMessage());
    } catch (InvalidResourceTypeException e) {
      try {
        theme = SlingModelUtils.getResourceAsType(requestContext, request.getResourceResolver(),
            BaseSite.class).getTheme();
      } catch (ResourceNotFoundException | InvalidThemeException exception) {
        LOG.trace(exception.getMessage());
      } catch (InvalidResourceTypeException ex) {
        try {
          theme = parentComponent.getTheme();
        } catch (ResourceNotFoundException | InvalidThemeException exception) {
          LOG.trace(exception.getMessage());
        }
      }
    }

    try {
      if (theme == null) {
        theme = parentComponent.getTheme();
      }
      String resolvedScriptPath = parentComponent.getComponentType().getScript(scriptName,
          theme.getUiFramework()).getPath();
      componentViewScriptResolutionCacheService.cacheComponentViewScriptPath(scriptName,
          parentComponent.getComponentType(), theme.getUiFramework(), resolvedScriptPath);
      LOG.trace("Finished retrieving Script Path {}", scriptName);
      return resolvedScriptPath;
    } catch (final Exception exception) {
      try {
        String resolvedScriptPath = parentComponent.getComponentType().getScript(scriptName,
            null).getPath();
        componentViewScriptResolutionCacheService.cacheComponentViewScriptPath(scriptName,
            parentComponent.getComponentType(), theme.getUiFramework(), resolvedScriptPath);
        LOG.trace("Finished retrieving Script Path {}", scriptName);
        return resolvedScriptPath;
      } catch (final ModelAdaptionException exception1) {
        throw new InvalidScriptException(scriptName, exception.getMessage());
      }
    }
  }
}
