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

package io.kestros.cms.sitebuilding.api.models;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import javax.annotation.Nullable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Content area component, which looks to InheritedContentArea component matching same the relative
 * path on the parent page to determine rendered content. Descendant components can prepend or
 * append content, or reset inheritance altogether.
 */
@KestrosModel
@Model(adaptables = Resource.class,
       resourceType = "kestros/commons/components/inherited-content-area")
public class InheritedContentArea extends ContentArea {

  private static final Logger LOG = LoggerFactory.getLogger(InheritedContentArea.class);

  /**
   * Whether to allow the inherited content to be prepended. Appended content is then inherited by
   * descendant InheritedContentAreas.
   *
   * @return Whether to allow the inherited content to be prepended.
   */
  @KestrosProperty(description = "Whether to allow the inherited content to be prepended. "
                                 + "Appended content is then inherited by descendant "
                                 + "InheritedContentAreas.",
                   configurable = true,
                   jcrPropertyName = "allowComponentsBefore",
                   defaultValue = "false",
                   sampleValue = "false")
  public boolean isAllowComponentsBefore() {
    return getProperty("allowComponentsBefore", Boolean.FALSE);
  }

  /**
   * Whether to allow the inherited content to be appended. Appended content is then inherited by
   * descendant InheritedContentAreas.
   *
   * @return Whether to allow the inherited content to be appended.
   */
  @KestrosProperty(description = "Whether to allow the inherited content to be appended. "
                                 + "Appended content is then inherited by descendant "
                                 + "InheritedContentAreas",
                   configurable = true,
                   jcrPropertyName = "allowComponentsAfter",
                   defaultValue = "false",
                   sampleValue = "false")
  public boolean isAllowComponentsAfter() {
    return getProperty("allowComponentsAfter", Boolean.FALSE);
  }

  /**
   * Whether to reset inheritance on InheritedContentAreas with the same relative path as the
   * current InheritedContentArea component.  When true, this component instance will be treated as
   * the root.
   *
   * @return Whether to reset inheritance on InheritedContentAreas with the same relative path as
   *     the current InheritedContentArea component
   */
  @KestrosProperty(description = "Whether the current InheritedContentArea resets inheritance. "
                                 + "When true, descendants will only retrieve inherited content "
                                 + "up to this resource/page.",
                   configurable = true,
                   jcrPropertyName = "reset",
                   defaultValue = "false",
                   sampleValue = "false")
  public boolean isReset() {
    return getProperty("reset", Boolean.FALSE);
  }

  /**
   * Whether the InheritedContentArea is the root level ancestor.  This will be true if {@link
   * #isReset()} is true, or no InheritedContentArea could be found at the same relative path on the
   * containing page's parent page.
   *
   * @return Whether the InheritedContentArea is the root ancestor.
   */
  @KestrosProperty(description = "Whether the InheritedContentArea is the root level ancestor.")
  public boolean isRootLevelContentArea() {
    return isReset() || getInheritedFromContentArea() == null;
  }

  /**
   * Retrieves the ContentArea which should appear before the inherited content area.  Content added
   * in this ContentArea will be inherited in subsequent InheritedContentAreas.
   *
   * @return the ContentArea which should appear before the inherited content area.
   */
  @Nullable
  @KestrosProperty(description = "Content area which should appear before the inherited content "
                                 + "area.")
  public ContentArea getBeforeContentArea() {
    if (isAllowComponentsBefore() && !isRootLevelContentArea()) {
      try {
        return getChildAsType("before", this, ContentArea.class);
      } catch (final InvalidResourceTypeException | ChildResourceNotFoundException e) {
        LOG.warn("Unable to retrieve 'before' ContentArea for InheritedContentArea {}. {}",
            getPath(), e.getMessage());
      }
    }
    return null;
  }

  /**
   * Retrieves the ContentArea which should appear after the inherited content area.  Content added
   * in this ContentArea will be inherited in subsequent InheritedContentAreas.
   *
   * @return the ContentArea which should appear after the inherited content area.
   */
  @Nullable
  @KestrosProperty(description = "Content area which should appear after the inherited content "
                                 + "area.")
  public ContentArea getAfterContentArea() {
    if (isAllowComponentsAfter() && !isRootLevelContentArea()) {
      try {
        return getChildAsType("after", this, ContentArea.class);
      } catch (final InvalidResourceTypeException | ChildResourceNotFoundException e) {
        LOG.warn("Unable to retrieve 'after' ContentArea for InheritedContentArea {}. {}",
            getPath(), e.getMessage());
      }
    }
    return null;
  }

  /**
   * The InheritedContentArea matching the relative path of the current InheritedContentArea. All
   * content from the InheritedContentArea will immutably render, unless the `reset` property is set
   * to true.
   *
   * @return The InheritedContentArea matching the relative path of the current
   *     InheritedContentArea.
   */
  @Nullable
  @KestrosProperty(description = "The content area from the parent page with the same relative "
                                 + "path, or null.")
  public InheritedContentArea getInheritedFromContentArea() {
    InheritedContentArea inheritedFromContentArea = null;
    BaseContentPage page;
    try {
      page = getContainingPage().getParent();
    } catch (final NoParentResourceException | NoValidAncestorException e) {
      page = null;
    }
    while (page != null && inheritedFromContentArea == null) {
      try {
        inheritedFromContentArea = getResourceAsType(
            page.getPath() + "/jcr:content/" + getRelativePath(), getResourceResolver(),
            InheritedContentArea.class);
      } catch (final InvalidResourceTypeException e) {
        LOG.error("Breaking inheritance for InheritedContentArea {} due to issue with inherited "
                  + "resource. {}", getPath(), e.getMessage());
        return null;
      } catch (final ResourceNotFoundException e) {
        try {
          page = page.getParent();
        } catch (final NoParentResourceException ex) {
          page = null;
        }
      }
    }
    return inheritedFromContentArea;
  }

}
