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

package io.kestros.cms.uiframeworks.core.models;

import io.kestros.cms.filetypes.HtmlFile;
import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Sling Model for {@link HtlTemplateFile}.
 */
@KestrosModel()
@Model(adaptables = Resource.class,
       resourceType = JcrConstants.NT_FILE)
public class HtlTemplateFileResource extends HtmlFile implements HtlTemplateFile {

  public static final String EXTENSION_HTML = ".html";

  @Nonnull
  @Override
  public String getTitle() {
    String title = getName();

    title = title.replaceAll("-", " ");
    title = title.replaceAll(EXTENSION_HTML, "");

    return WordUtils.capitalize(title);
  }

  @Override
  public String getPathWithoutExtension() {
    return getPath().replace(".html", "");
  }


  @Override
  public String getFontAwesomeIcon() {
    return getProperty("fontAwesomeIcon", "fa fa-file");
  }

}
