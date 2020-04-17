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

package io.kestros.cms.foundation.design.theme;

import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.config.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import io.kestros.commons.uilibraries.servlets.BaseJavaScriptServlet;
import javax.servlet.Servlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Servlet to output a Theme's CSS.
 */
@Component(service = Servlet.class,
           property = {"sling.servlet.resourceTypes=kes:Theme",
               "sling.servlet.resourceTypes=kestros/cms/theme", "sling.servlet.extensions=js",
               "sling.servlet.methods=GET",})
public class ThemeJavaScriptServlet extends BaseJavaScriptServlet {

  private static final long serialVersionUID = 7574658580922282342L;

  @SuppressWarnings("unused")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryConfigurationService uiLibraryConfigurationService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCacheService uiLibraryCacheService;

  @Override
  public UiLibraryConfigurationService getUiLibraryConfigurationService() {
    return uiLibraryConfigurationService;
  }

  @Override
  protected UiLibraryCacheService getUiLibraryCacheService() {
    return uiLibraryCacheService;
  }

  @Override
  public Class<? extends UiLibrary> getUiLibraryClass() {
    return Theme.class;
  }
}
