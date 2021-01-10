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

package io.kestros.cms.foundation.content.sites;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getAllDescendantsOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsClosestType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.foundation.content.ComponentRequestContext;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sling Model for site root pages. Adapted from resources with jcr:primaryType 'kes:Site'
 *
 * @param <T> Extends {@link BaseContentPage}
 */
@KestrosModel(usesJcrContent = true,
              contextModel = ComponentRequestContext.class)
@Model(adaptables = Resource.class,
       resourceType = "kes:Site")
@Exporter(name = "jackson",
          selector = "base-site",
          extensions = "json")
public class BaseSite<T extends BaseContentPage> extends BaseContentPage {

  private static final Logger LOG = LoggerFactory.getLogger(BaseSite.class);

  @OSGiService
  ModelFactory modelFactory;

  List<T> allPagesOfClosestType;

  /**
   * List of all descendant pages of the current site.  If a model adapter factory has been
   * provided, it will return all pages as their closest type.
   *
   * @return List of all descendant pages of the current site.
   */
  @JsonIgnore
  public List<T> getAllPages() {
    if (allPagesOfClosestType == null) {
      allPagesOfClosestType = new ArrayList<>();

      allPagesOfClosestType.add((T) this);

      for (final BaseContentPage page : getAllDescendantsOfType(this, BaseContentPage.class)) {
        try {
          final T adaptedPage = getResourceAsClosestType(page.getResource(), modelFactory);
          allPagesOfClosestType.add(adaptedPage);
        } catch (final InvalidResourceTypeException exception) {
          allPagesOfClosestType.add((T) page);
        }

      }
    }
    return allPagesOfClosestType;
  }

  /**
   * The last modified date of any ancestor page.
   *
   * @return The last modified date of any ancestor page.
   */
  @JsonIgnore
  public Date getAncestorPageLastModifiedDate() {
    final List<T> pages = getAllPages();
    pages.sort(new LastModifiedDateSorter());

    return pages.get(0).getLastModifiedDate();
  }

  /**
   * Font Awesome Icon class.
   *
   * @return Font Awesome Icon class.
   */
  @Override
  @JsonIgnore
  @KestrosProperty(description = "Font awesome icon class, used in the Kestros Site Admin UI",
                   jcrPropertyName = "fontAwesomeIcon",
                   defaultValue = "fa fa-sitemap",
                   configurable = true,
                   sampleValue = "fa fa-sitemap")
  public String getFontAwesomeIcon() {
    try {
      String iconPropertyValue = getProperty("fontAwesomeIcon", StringUtils.EMPTY);
      if (StringUtils.isNotEmpty(iconPropertyValue)) {
        return iconPropertyValue;
      }
      final String componentTypeFontAwesomeIcon = getComponentType().getFontAwesomeIcon();
      if (!"fa fa-cube".equals(componentTypeFontAwesomeIcon)) {
        return componentTypeFontAwesomeIcon;
      }
    } catch (final InvalidComponentTypeException exception) {
      LOG.debug("Unable to inherit icon from ComponentType for site {}. {}", getPath(),
          exception.getMessage());
    }
    return "fa fa-sitemap";
  }

  /**
   * Sorts pages by kes:LastModified.
   */
  private static class LastModifiedDateSorter implements Comparator<BaseContentPage>, Serializable {

    private static final long serialVersionUID = -8585373331311356206L;

    @Override
    public int compare(@Nonnull final BaseContentPage page1, @Nonnull final BaseContentPage page2) {
      if (page2.getLastModifiedDate() != null) {
        return page2.getLastModifiedDate().compareTo(page1.getLastModifiedDate());
      }
      return 0;
    }
  }

}