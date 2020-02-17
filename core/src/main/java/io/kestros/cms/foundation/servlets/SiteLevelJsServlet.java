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

import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;

import io.kestros.commons.uilibraries.filetypes.ScriptType;
import javax.servlet.Servlet;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.service.component.annotations.Component;

/**
 * <p>
 * Servlet for rendering a sites {@link io.kestros.cms.foundation.design.theme.Theme} JavaScript
 * from requests made to the .js extension of the site root.
 * </p>
 * <p>
 * Sample path - /content/site.ui-framework-name.theme-name.js
 * </p>
 */
@Component(service = {Servlet.class},
           property = {"sling.servlet.resourceTypes=kes:Site", "sling.servlet.extensions=js",
               "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class SiteLevelJsServlet extends SiteLevelScriptServlet {

  private static final long serialVersionUID = -372985760220947749L;

  @Override
  public ScriptType getScriptType() {
    return JAVASCRIPT;
  }
}
