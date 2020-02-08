package io.kestros.cms.foundation.content.components.inheritedcontentarea;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import io.kestros.cms.foundation.content.components.contentarea.ContentArea;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
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
  public boolean isAllowComponentsBefore() {
    return getProperty("allowComponentsBefore", Boolean.FALSE);
  }

  /**
   * Whether to allow the inherited content to be appended. Appended content is then inherited by
   * descendant InheritedContentAreas.
   *
   * @return Whether to allow the inherited content to be appended.
   */
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
  public boolean isReset() {
    return getProperty("reset", Boolean.FALSE);
  }

  /**
   * Whether the InheritedContentArea is the root ancestor.  This will be true if {@link #isReset()}
   * is true, or no InheritedContentArea could be found at the same relative path on the containing
   * page's parent page.
   *
   * @return Whether the InheritedContentArea is the root ancestor.
   */
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
