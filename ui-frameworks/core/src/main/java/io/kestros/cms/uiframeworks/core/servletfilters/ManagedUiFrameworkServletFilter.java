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

package io.kestros.cms.uiframeworks.core.servletfilters;

import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.exceptions.ThemeRetrievalException;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.services.ThemeOutputCompilationService;
import io.kestros.cms.uiframeworks.api.services.ThemeRetrievalService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.api.exceptions.NoMatchingCompilerException;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.services.UiLibraryMinificationService;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import java.io.IOException;
import java.util.Locale;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.servlets.annotations.SlingServletFilter;
import org.apache.sling.servlets.annotations.SlingServletFilterScope;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filters requests to UiFramework paths, and renders virtual theme responses if needed.
 */
@Component
@SlingServletFilter(scope = {SlingServletFilterScope.REQUEST},
                    pattern = "/etc/ui-frameworks/.*",
                    methods = {HttpConstants.METHOD_GET})
public class ManagedUiFrameworkServletFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(ManagedUiFrameworkServletFilter.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ThemeRetrievalService themeRetrievalService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ThemeOutputCompilationService themeOutputCompilationService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryMinificationService minificationService;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    return;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    LOG.info("servlet filter.");
    if (servletRequest instanceof SlingHttpServletRequest
        && servletResponse instanceof SlingHttpServletResponse) {
      SlingHttpServletRequest request = (SlingHttpServletRequest) servletRequest;
      SlingHttpServletResponse response = (SlingHttpServletResponse) servletResponse;
      String requestUri = request.getRequestURI();
      String scriptName = requestUri.split("/")[requestUri.split("/").length - 1];
      String themeName = scriptName.split("\\.")[0];
      String extension = scriptName.split("\\.")[1];
      String uiFrameworkPath = requestUri.split("/themes")[0];

      if ("js".equals(extension)) {
        extension = "JAVASCRIPT";
      }
      if ("JAVASCRIPT".equals(extension) || "css".equals(extension)) {
        if (themeRetrievalService != null) {
          try {
            Theme theme = themeRetrievalService.getTheme(uiFrameworkPath + "/themes/" + themeName);
            LOG.debug("Theme {} found. Virtual theme response not needed.", theme.getPath());
            filterChain.doFilter(request, response);
          } catch (ThemeRetrievalException exception) {
            try {
              ScriptType scriptType = ScriptTypes.valueOf(extension.toUpperCase(Locale.US));
              Theme theme = themeRetrievalService.getVirtualTheme(
                  uiFrameworkPath + "/themes/" + themeName);
              String output = themeOutputCompilationService.getUiLibraryOutput(theme, scriptType);

              response.setContentType(scriptType.getOutputContentType());
              response.getWriter().write(output);

            } catch (InvalidResourceTypeException e) {
              response.setStatus(404);
            } catch (InvalidThemeException e) {
              response.setStatus(404);
            } catch (ThemeRetrievalException e) {
              response.setStatus(404);
            } catch (NoMatchingCompilerException e) {
              response.setStatus(404);
            }
          }

        }
      } else {
        filterChain.doFilter(servletRequest, servletResponse);
      }
    }
  }

  @Override
  public void destroy() {
    return;
  }
}
