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

package io.kestros.cms.sitebuilding.core.servlets;


import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.exceptions.ThemeRetrievalException;
import io.kestros.cms.uiframeworks.api.exceptions.UiFrameworkRetrievalException;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.ThemeOutputCompilationService;
import io.kestros.cms.uiframeworks.api.services.ThemeRetrievalService;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkRetrievalService;
import io.kestros.cms.versioning.api.models.Version;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.api.exceptions.NoMatchingCompilerException;
import io.kestros.commons.uilibraries.api.exceptions.ScriptCompressionException;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.services.UiLibraryCacheService;
import io.kestros.commons.uilibraries.api.services.UiLibraryMinificationService;
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

  /**
   * UiLibrary Cache Service.
   *
   * @return UiLibrary Cache Service.
   */
  public abstract UiLibraryCacheService getUiLibraryCacheService();

  /**
   * UiFramework Retrieval Service.
   *
   * @return UiFramework Retrieval Service.
   */
  public abstract UiFrameworkRetrievalService getUiFrameworkRetrievalService();

  /**
   * Theme Retrieval Service.
   *
   * @return Theme Retrieval Service.
   */
  public abstract ThemeRetrievalService getThemeRetrievalService();

  /**
   * Theme Output Compilation Service.
   *
   * @return Theme Output Compilation Service.
   */
  public abstract ThemeOutputCompilationService getThemeOutputCompilationService();

  /**
   * Minification Service.
   *
   * @return Minification Service.
   */
  public abstract UiLibraryMinificationService getUiLibraryMinificationService();

  /**
   * {@link ScriptType} to render.
   *
   * @return {@link ScriptType} to render.
   */
  public abstract ScriptType getScriptType();

  /**
   * Theme retrieval service.
   *
   * @return Theme retrieval service.
   */
  public abstract ThemeRetrievalService getVirtualThemeProviderService();

  @Override
  public void doGet(@Nonnull final SlingHttpServletRequest request,
      @Nonnull final SlingHttpServletResponse response) {
    final String[] selectors = request.getRequestPathInfo().getSelectors();
    if (selectors.length == 2) {
      try {
        boolean performCache = false;
        String output = null;

        final UiFramework uiFramework = getUiFrameworkRetrievalService().getUiFrameworkByCode(
            selectors[0], true, false, null);

        if (getUiLibraryCacheService() != null) {
          try {
            Theme theme = getThemeRetrievalService().getTheme(selectors[1], uiFramework);
            output = getUiLibraryCacheService().getCachedOutput(theme, getScriptType(),
                isMinified(request));
          } catch (CacheRetrievalException e) {
            performCache = true;
          } catch (ThemeRetrievalException exception) {
            exception.printStackTrace();
          }
        }

        if (StringUtils.isEmpty(output)) {
          Theme theme = getThemeRetrievalService().getTheme(selectors[1], uiFramework);
          output = getThemeOutputCompilationService().getUiLibraryOutput(theme, getScriptType());
        }

        if (isMinified(request)) {
          try {
            output = getUiLibraryMinificationService().getMinifiedOutput(output, getScriptType());
          } catch (ScriptCompressionException e) {
            e.printStackTrace();
          }
        }

        response.setContentType(getScriptType().getOutputContentType());
        response.setStatus(200);
        response.getWriter().write(output);

        if (StringUtils.isNotEmpty(output)) {
          if (getUiLibraryCacheService() != null && performCache) {
            try {
              Theme theme = getThemeRetrievalService().getTheme(selectors[1], uiFramework);
              getUiLibraryCacheService().cacheUiLibraryScript(theme.getPath(), output,
                  getScriptType(), isMinified(request));
            } catch (final CacheBuilderException e) {
              LOG.warn("Unable to build UiFramework Cache. {}", e.getMessage());
            }
          }
        }
      } catch (final IOException | InvalidResourceTypeException exception) {
        LOG.error("Unable to render site level {} response for {}. {}", getScriptType().getName(),
            request.getResource().getPath(), exception.getMessage());
        response.setStatus(400);
      } catch (UiFrameworkRetrievalException e) {
        e.printStackTrace();
      } catch (ThemeRetrievalException exception) {
        exception.printStackTrace();
      } catch (NoMatchingCompilerException e) {
        e.printStackTrace();
      }
    } else if (selectors.length == 5) {
      UiFramework uiFramework = null;
      Theme theme = null;
      String uiFrameworkCode = selectors[0];
      Integer majorVersion = Integer.parseInt(selectors[1]);
      Integer minorVersion = Integer.parseInt(selectors[2]);
      Integer patchVersion = Integer.parseInt(selectors[3]);
      String themeName = selectors[4];
      Version version = new Version(majorVersion, minorVersion, patchVersion);
      // todo do this in a service
      try {
        // todo clean this up.
        uiFramework = getUiFrameworkRetrievalService().getUiFrameworkByCode(uiFrameworkCode, true,
            true, version.getFormatted());

        //        BaseResource uiFrameworksRoot = DesignUtils.getUiFrameworksEtcRootResource(
        //            request.getResourceResolver());
        //        for (ManagedUiFramework managedUiFramework : SlingModelUtils.getChildrenOfType(
        //            uiFrameworksRoot, ManagedUiFramework.class)) {
        //          for (Object version : managedUiFramework.getVersions()) {
        //            if (version instanceof BaseResource) {
        //              try {
        //                UiFramework adaptedFramework = SlingModelUtils.adaptTo((BaseResource)
        //                version,
        //                    UiFramework.class);
        //                if (adaptedFramework.getFrameworkCode().equals(uiFrameworkCode)) {
        //                  if (majorVersion.equals(adaptedFramework.getVersion().getMajorVersion())
        //                      && minorVersion.equals(adaptedFramework.getVersion()
        //                      .getMinorVersion())
        //                      && patchVersion.equals(adaptedFramework.getVersion()
        //                      .getPatchVersion())) {
        //                    uiFramework = adaptedFramework;
        //                  }
        //                }
        //              } catch (InvalidResourceTypeException e) {
        //                e.printStackTrace();
        //              } catch (VersionFormatException e) {
        //                e.printStackTrace();
        //              }
        //            }
        //          }
        //        }
        if (uiFramework != null && getVirtualThemeProviderService() != null) {
          theme = getVirtualThemeProviderService().getVirtualTheme(
              uiFramework.getPath() + "/themes/" + themeName);

          String output = "";
          if (StringUtils.isEmpty(output)) {
            output = getThemeOutputCompilationService().getUiLibraryOutput(theme, getScriptType());
          }

          if (isMinified(request)) {
            try {
              output = getUiLibraryMinificationService().getMinifiedOutput(output, getScriptType());
            } catch (ScriptCompressionException e) {
              e.printStackTrace();
            }
          }

          response.setContentType(getScriptType().getOutputContentType());
          response.setStatus(200);
          response.getWriter().write(output);
        }
      } catch (InvalidThemeException | InvalidResourceTypeException | IOException e) {
        response.setStatus(400);
      } catch (UiFrameworkRetrievalException e) {
        e.printStackTrace();
      } catch (ThemeRetrievalException exception) {
        exception.printStackTrace();
      } catch (NoMatchingCompilerException e) {
        e.printStackTrace();
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
