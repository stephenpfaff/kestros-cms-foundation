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

package io.kestros.cms.foundation.servlets;

import static io.kestros.cms.foundation.utils.DesignUtils.getUiFrameworkByFrameworkCode;

import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Baseline servlet logic for scripts that are rendered at the site level.  UiFramework name and
 * Theme name are retrieved as selector, and the ScriptType is determined by the extension.
 * </p>
 * <p>
 * Sample path - /content/site.ui-framework-name.theme-name.css
 * </p>
 * <p>
 * Sample path - /content/site.ui-framework-name.theme-name.js
 * </p>
 */
public abstract class SiteLevelScriptServlet extends SlingSafeMethodsServlet {

  private static final Logger LOG = LoggerFactory.getLogger(SiteLevelScriptServlet.class);
  private static final long serialVersionUID = -5328841199087488311L;

  public abstract UiLibraryCacheService getUiLibraryCacheService();

  /**
   * {@link ScriptType} to render.
   *
   * @return {@link ScriptType} to render.
   */
  public abstract ScriptType getScriptType();

  @Override
  public void doGet(@Nonnull final SlingHttpServletRequest request,
      @Nonnull final SlingHttpServletResponse response) {
    final String[] selectors = request.getRequestPathInfo().getSelectors();
    if (selectors.length == 2) {
      try {

        boolean performCache = false;
        String output = null;

        final UiFramework uiFramework = getUiFrameworkByFrameworkCode(selectors[0], true, false,
            request.getResourceResolver());

        if (getUiLibraryCacheService() != null) {
          try {
            output = getUiLibraryCacheService().getCachedOutput(uiFramework.getTheme(selectors[1]),
                getScriptType(), isMinified(request));
          } catch (CacheRetrievalException e) {
            performCache = true;
          }
        }

        if (StringUtils.isEmpty(output)) {
          output = uiFramework.getTheme(selectors[1]).getOutput(getScriptType(),
              isMinified(request));
        }

        response.setContentType(getScriptType().getOutputContentType());
        response.setStatus(200);
        response.getWriter().write(output);

        if (StringUtils.isNotEmpty(output)) {
          if (getUiLibraryCacheService() != null && performCache) {
            try {
              getUiLibraryCacheService().cacheUiLibraryScripts(uiFramework.getTheme(selectors[1]),
                  isMinified(request));
            } catch (final CacheBuilderException e) {
              LOG.warn("Unable to build UiFramework Cache. {}", e.getMessage());
            }
          }
        }
      } catch (final ResourceNotFoundException | IOException | ChildResourceNotFoundException
                                   | InvalidResourceTypeException exception) {
        LOG.error("Unable to render site level {} response for {}. {}", getScriptType().getName(),
            request.getResource().getPath(), exception.getMessage());
        response.setStatus(400);
      }
    } else {
      LOG.debug(
          "Unable to render site level css response for request {}. Does not contain exactly two "
          + "selectors", request.getContextPath());
      response.setStatus(400);
    }
  }

  boolean isMinified(SlingHttpServletRequest request) {
    return request.getRequestURI().startsWith("/public/");
  }

}
