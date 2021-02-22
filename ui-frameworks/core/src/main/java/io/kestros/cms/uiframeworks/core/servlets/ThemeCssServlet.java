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

package io.kestros.cms.uiframeworks.core.servlets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.uiframeworks.api.services.ThemeOutputCompilationService;
import io.kestros.cms.uiframeworks.api.services.ThemeRetrievalService;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import io.kestros.commons.uilibraries.api.services.UiLibraryCacheService;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.api.services.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.api.services.UiLibraryMinificationService;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import io.kestros.commons.uilibraries.core.servlets.BaseUiLibraryServlet;
import javax.servlet.Servlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet to output a Theme's CSS.
 */
@Component(service = Servlet.class,
           property = {"sling.servlet.resourceTypes=kes:Theme",
               "sling.servlet.resourceTypes=kestros/cms/theme", "sling.servlet.extensions=css",
               "sling.servlet.methods=GET"})
public class ThemeCssServlet extends BaseUiLibraryServlet {

  private static final long serialVersionUID = 9115879784266249170L;

  private static final Logger LOG = LoggerFactory.getLogger(ThemeCssServlet.class);

  @SuppressFBWarnings("SE_BAD_FIELD")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ThemeRetrievalService themeRetrievalService;

  @SuppressFBWarnings("SE_BAD_FIELD")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCacheService uiLibraryCacheService;

  @SuppressFBWarnings("SE_BAD_FIELD")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryConfigurationService uiLibraryConfigurationService;

  @SuppressFBWarnings("SE_BAD_FIELD")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ThemeOutputCompilationService themeOutputCompilationService;

  @SuppressFBWarnings("SE_BAD_FIELD")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryMinificationService uiLibraryMinificationService;

  @Override
  protected <T extends FrontendLibrary> T getLibrary(String libraryPath) {
    if (themeRetrievalService != null) {
      try {
        return (T) themeRetrievalService.getTheme(libraryPath);
      } catch (ModelAdaptionException e) {
        LOG.error("Unable to retrieve library {}, {}.", libraryPath, e.getMessage());
      }
    }
    return null;
  }

  @Override
  protected UiLibraryCompilationService getUiLibraryCompilationService() {
    return themeOutputCompilationService;
  }

  @Override
  protected UiLibraryConfigurationService getUiLibraryConfigurationService() {
    return uiLibraryConfigurationService;
  }

  @Override
  protected UiLibraryMinificationService getUiLibraryMinificationService() {
    return uiLibraryMinificationService;
  }

  @Override
  protected UiLibraryCacheService getUiLibraryCacheService() {
    return uiLibraryCacheService;
  }

  @Override
  protected ScriptTypes getScriptType() {
    return ScriptTypes.CSS;
  }
}