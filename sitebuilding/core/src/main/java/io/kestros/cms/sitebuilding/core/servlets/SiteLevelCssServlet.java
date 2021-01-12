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

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;

import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import javax.servlet.Servlet;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * <p>
 * Servlet for rendering a sites {@link Theme} CSS from requests made to the .css extension of the
 * site root.
 * </p>
 * <p>
 * Sample path - /content/site.ui-framework-name.theme-name.css
 * </p>
 */
@Component(service = {Servlet.class},
           property = {"sling.servlet.resourceTypes=kes:Site", "sling.servlet.extensions=css",
               "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class SiteLevelCssServlet extends SiteLevelScriptServlet {

  private static final long serialVersionUID = 6383115665070503260L;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCacheService uiLibraryCacheService;

  @Override
  public UiLibraryCacheService getUiLibraryCacheService() {
    return uiLibraryCacheService;
  }

  @Override
  public ScriptType getScriptType() {
    return CSS;
  }
}
