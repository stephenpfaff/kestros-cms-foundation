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

package io.kestros.cms.foundation.design.htltemplate;

import io.kestros.cms.foundation.componenttypes.HtmlFile;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTML File that contains HTL Templates.
 */
@KestrosModel(validationService = HtlTemplateFileValidationService.class)
@Model(adaptables = Resource.class,
       resourceType = JcrConstants.NT_FILE)
public class HtlTemplateFile extends HtmlFile {

  private static final Logger LOG = LoggerFactory.getLogger(HtlTemplateFile.class);
  public static final String EXTENSION_HTML = ".html";

  /**
   * Title of the current HTL Template file.  Derived from the file name by replacing `-` with ` `,
   * removing `.html` and capitalizing the first letter of each word.
   *
   * @return Title of the current HTL Template file.
   */
  @Nonnull
  @Override
  public String getTitle() {
    String title = getName();

    title = title.replaceAll("-", " ");
    title = title.replaceAll(EXTENSION_HTML, "");

    return WordUtils.capitalize(title);
  }

  /**
   * File path without the .html extension.
   *
   * @return File path without the .html extension.
   */
  public String getPathWithoutExtension() {
    return getPath().replace(".html", "");
  }

  /**
   * All HTL Templates in the current files.
   *
   * @return All HTL Templates in the current files.
   */
  public List<HtlTemplate> getTemplates() {
    final List<HtlTemplate> templates = new ArrayList<>();
    try {
      final Document templateFile = Jsoup.parse(getFileContent());
      templateFile.outputSettings().outline(true);
      templateFile.outputSettings().prettyPrint(false);
      for (final Node node : templateFile.child(0).childNodes().get(1).childNodes()) {
        if (StringUtils.isNotBlank(node.toString())) {
          final HtlTemplate template = new HtlTemplate(node, getPath());
          if (template.getName() != null) {
            templates.add(template);
          }
        }
      }
    } catch (final IOException exception) {
      LOG.warn(
          "Unable to get HtlTemplates for HTL Template compilation file {} due to IOException. {}",
          getPath(), exception.getMessage());
    }
    return templates;
  }

  /**
   * Retrieve a specified HTL Template.
   *
   * @param name Template name.
   * @return A specified HTL Template.
   */
  @Nullable
  public HtlTemplate getTemplate(String name) {
    for (HtlTemplate htlTemplate : getTemplates()) {
      if (htlTemplate.getName().equals(name)) {
        return htlTemplate;
      }
    }
    return null;
  }

  /**
   * Font Awesome Icon class.
   *
   * @return Font Awesome Icon class.
   */
  public String getFontAwesomeIcon() {
    return getProperty("fontAwesomeIcon", "fa fa-file");
  }

}
