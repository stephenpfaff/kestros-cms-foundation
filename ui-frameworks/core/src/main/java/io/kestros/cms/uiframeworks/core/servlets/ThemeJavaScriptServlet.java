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
 * Servlet to output a Theme's JavaScript.
 */
@Component(service = Servlet.class,
           property = {"sling.servlet.resourceTypes=kes:Theme",
               "sling.servlet.resourceTypes=kestros/cms/theme", "sling.servlet.extensions=js",
               "sling.servlet.methods=GET",})
public class ThemeJavaScriptServlet extends BaseUiLibraryServlet {

  private static final long serialVersionUID = 7574658580922282342L;

  private static final Logger LOG = LoggerFactory.getLogger(ThemeJavaScriptServlet.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private transient ThemeRetrievalService themeRetrievalService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCacheService uiLibraryCacheService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private transient UiLibraryConfigurationService uiLibraryConfigurationService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private transient ThemeOutputCompilationService themeOutputCompilationService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private transient UiLibraryMinificationService uiLibraryMinificationService;

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
    return ScriptTypes.JAVASCRIPT;
  }
}
