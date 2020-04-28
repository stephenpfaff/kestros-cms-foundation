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

package io.kestros.cms.foundation.content;

import static io.kestros.cms.foundation.utils.JcrPropertyUtils.getRelativeDate;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.adaptTo;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getFirstAncestorOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsClosestType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;
import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;
import static org.apache.jackrabbit.JcrConstants.NT_UNSTRUCTURED;
import static org.apache.sling.jcr.resource.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.kestros.cms.foundation.componenttypes.ComponentType;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.cms.foundation.content.sites.BaseSite;
import io.kestros.cms.foundation.exceptions.InvalidComponentTypeException;
import io.kestros.cms.foundation.utils.RelativeDate;
import io.kestros.cms.user.KestrosUser;
import io.kestros.cms.user.exceptions.UserRetrievalException;
import io.kestros.cms.user.services.KestrosUserService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.MatchingResourceTypeNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Baseline Component implementation, meant to be extended by other Components.
 */
@KestrosModel(docPaths = {"/content/guide-articles/kestros/site-management/creating-components",
    "/content/guide-articles/kestros/site-management/editing-components",
    "/content/guide-articles/kestros/getting-started/understanding-validation"},
              contextModel = ComponentRequestContext.class)
@Model(adaptables = Resource.class,
       resourceType = "sling/servlet/default")
@Exporter(name = "jackson",
          selector = "base-component",
          extensions = "json")
public class BaseComponent extends BaseResource {

  private static final Logger LOG = LoggerFactory.getLogger(BaseComponent.class);

  @SuppressWarnings("unused")
  @OSGiService
  private ModelFactory modelFactory;

  @SuppressWarnings("unused")
  @OSGiService
  @Optional
  private KestrosUserService userService;

  private BaseContentPage containingPage = null;
  private ComponentType componentType = null;

  /**
   * Initializes the ContentArea component.  Creates the resource if none exists, and assigns
   * sling:resourceType if needed.
   */
  @PostConstruct
  public void initialize() {
    if (getResourceResolver().getResource(getPath()) == null) {
      attemptContentAreaResourceCreation();
    }
    if (StringUtils.isEmpty(getSlingResourceType())) {
      attemptSlingResourceTypeAssignment();
    }
  }

  /**
   * ComponentType of the current Component implementation.
   *
   * @return ComponentType of the current Component implementation.
   * @throws InvalidComponentTypeException Specified ComponentType resource not found, or was
   *     invalid.
   */
  @Nonnull
  @JsonIgnore
  public ComponentType getComponentType() throws InvalidComponentTypeException {
    LOG.trace("Retrieving ComponentType for {}", getPath());

    if (this.componentType != null) {
      LOG.trace("Finished retrieving ComponentType for {}", getPath());
      return this.componentType;
    }

    final String resourceType = getResourceType();
    try {
      this.componentType = getResourceAsType(resourceType, getResourceResolver(),
          ComponentType.class);
      LOG.trace("Finished retrieving ComponentType for {}", getPath());
      return this.componentType;
    } catch (final ResourceNotFoundException | InvalidResourceTypeException e) {
      try {
        LOG.trace("Finished retrieving ComponentType for {}", getPath());
        this.componentType = getResourceAsType("/apps/" + resourceType, getResourceResolver(),
            ComponentType.class);
        return this.componentType;
      } catch (final ModelAdaptionException exception1) {
        try {
          LOG.trace("Finished retrieving ComponentType for {}", getPath());
          this.componentType = getResourceAsType("/libs/" + resourceType, getResourceResolver(),
              ComponentType.class);
          return this.componentType;
        } catch (final ModelAdaptionException exception2) {
          LOG.warn("Unable to retrieve ComponentType for {}: {}", getPath(),
              exception2.getMessage());
        }
      }
    }
    LOG.trace("Finished ComponentType for {}, but threw InvalidComponentTypeException", getPath());
    throw new InvalidComponentTypeException(getResourceType());
  }

  /**
   * Page the current Component lives under.
   *
   * @return Page the current Component lives under.
   * @throws NoValidAncestorException No Page could be found as an ancestor to the current
   *     Component.
   */
  @JsonIgnore
  @KestrosProperty(description =
                       "Page that the current Component lives on.  If referenced from another "
                       + "page, will show its real parent.")
  @Nonnull
  public BaseContentPage getContainingPage() throws NoValidAncestorException {

    LOG.trace("Getting containing Page for {}", getPath());
    if (containingPage != null) {
      LOG.trace("Finished retrieving containing page for {}", getPath());
      return this.containingPage;
    }
    if (getPath().contains(JCR_CONTENT)) {
      LOG.trace("Doing fast page retrieval.");
      try {
        this.containingPage = getResourceAsType(getPath().split(JCR_CONTENT)[0],
            getResourceResolver(), BaseContentPage.class);
        LOG.trace("Finished fast page retrieval.");
        return this.containingPage;
      } catch (final InvalidResourceTypeException exception) {
        try {
          this.containingPage = getResourceAsType(getPath().split(JCR_CONTENT)[0],
              getResourceResolver(), BaseSite.class);
          return this.containingPage;
        } catch (final ModelAdaptionException exception1) {
          LOG.warn("jcr:content found for {}, but could not be adapted to a Page or Site.",
              getPath());
        }
      } catch (final ModelAdaptionException exception) {
        LOG.debug("Unable to find jcr:content resource in path for {}", getPath());
      }
    }
    try {
      LOG.trace("Finished retrieving containing page for {}", getPath());
      this.containingPage = getFirstAncestorOfType(this, BaseContentPage.class);
      return this.containingPage;
    } catch (final NoValidAncestorException exception) {
      try {
        this.containingPage = getFirstAncestorOfType(this, BaseSite.class);
        return this.containingPage;
      } catch (final NoValidAncestorException exception1) {
        throw new NoValidAncestorException(getPath(), BaseContentPage.class);
      }
    }
  }

  /**
   * Retrieves all child Components, adapted to their closest matching SlingModel, if it extends
   * BaseComponent.
   *
   * @param <T> extends BaseComponent.
   * @return All child Components.
   */
  @KestrosProperty(description = "Direct child Components.")
  @JsonInclude(Include.NON_EMPTY)
  public <T extends BaseComponent> List<T> getChildren() {
    final List<T> children = new ArrayList<>();
    for (final BaseResource child : getChildrenAsBaseResource(this)) {
      BaseComponent childComponent = null;
      try {
        final Object childObject = getResourceAsClosestType(child.getResource(), modelFactory);
        if (childObject instanceof BaseComponent) {
          childComponent = (T) childObject;
        }
      } catch (final MatchingResourceTypeNotFoundException exception) {
        try {
          childComponent = adaptTo(child, BaseComponent.class);
        } catch (final InvalidResourceTypeException exception1) {
          LOG.error("Unable to adapt {} into BaseComponentType. {}", child.getPath(),
              exception1.getMessage());
        }
      }
      if (childComponent != null) {
        children.add((T) childComponent);
      } else {
        LOG.error("Unable to adapt {} into BaseComponentType.", child.getPath());
      }
    }
    return children;
  }

  /**
   * Retrieves all child Resources, adapted to BaseComponent.
   *
   * @return All child Resources, adapted to BaseComponent.
   */
  @JsonIgnore
  public List<BaseComponent> getChildrenAsBaseComponent() {
    return getChildrenOfType(this, BaseComponent.class);
  }

  /**
   * Retrieves all descendent components, as their closest matching SlingModel, if it extends
   * BaseComponent.
   *
   * @param <T> Extends BaseComponent.
   * @return All descendent components, as their closest matching SlingModel.
   */
  @KestrosProperty(description = "All descendant Components.")
  @JsonIgnore
  public <T extends BaseComponent> List<T> getAllDescendantComponents() {
    final List<T> descendantComponents = new ArrayList<>();

    for (final BaseComponent child : getChildren()) {
      descendantComponents.add((T) child);
      for (final BaseComponent descendant : child.getAllDescendantComponents()) {
        descendantComponents.add((T) descendant);
      }
    }

    return descendantComponents;
  }


  /**
   * The current component's path, with characters escaped.
   *
   * @return The current component's path, with characters escaped.
   */
  @JsonIgnore
  public String getEscapedPath() {
    return getPath().replace(JCR_CONTENT, "_jcr_content");
  }

  /**
   * User who created the current component.
   *
   * @return User who created the current component.
   */
  @JsonIgnore
  public KestrosUser getCreatedBy() {
    final String username = getProperties().get("kes:createdBy", StringUtils.EMPTY);
    try {
      return userService.getUser(username, getResourceResolver());
    } catch (final UserRetrievalException e) {
      LOG.debug("Failed to retrieve user {}.", username);
      return null;
    }
  }

  /**
   * User who last modified the current component.
   *
   * @return User who last modified the current component.
   */
  @JsonIgnore
  public KestrosUser getLastModifiedBy() {
    final String username = getProperties().get("kes:lastModifiedBy", StringUtils.EMPTY);
    try {
      return userService.getUser(username, getResourceResolver());
    } catch (final UserRetrievalException e) {
      LOG.debug("Failed to retrieve user {}.", username);
      return null;
    }
  }

  /**
   * When the current component was last modified.
   *
   * @return When the current component was last modified.
   */
  @Nullable
  @JsonIgnore
  public RelativeDate getLastModified() {
    return getRelativeDate(this, "kes:lastModified");
  }

  /**
   * When the current component was created.
   *
   * @return When the current component was created.
   */
  @JsonIgnore
  public RelativeDate getCreated() {
    return getRelativeDate(this, "kes:created");
  }

  private void attemptContentAreaResourceCreation() {
    final Map<String, Object> properties = new HashMap<>();
    properties.put(SLING_RESOURCE_TYPE_PROPERTY, getResource().getResourceType());
    final ValueMap valueMap = new ValueMapDecorator(properties);
    try {
      final Resource parent = getResource().getParent();
      if (parent != null) {

        getResourceResolver().create(parent, getName(), valueMap);
        getResourceResolver().commit();
      } else {
        LOG.error("Unable to create ContentArea resource {} due to null parent resource.",
            getPath());
      }
    } catch (final PersistenceException exception) {
      LOG.error("Unable to create ContentArea resource {} due to persistence" + " exception.",
          getPath());
    }
  }

  @Override
  @KestrosProperty(description = "")
  public String getTitle() {
    return super.getTitle();
  }

  @Override
  @KestrosProperty(description = "")
  public String getDescription() {
    return super.getDescription();
  }

  /**
   * Variations property value.
   *
   * @return Variations property value.
   */
  @SuppressWarnings("unused")
  @KestrosProperty(description = "Variations applied to the component. Only valid variations are "
                                 + "rendered.",
                   jcrPropertyName = "variations",
                   defaultValue = "[]",
                   sampleValue = "[]",
                   configurable = true)
  @Nonnull
  @JsonIgnore
  private String[] getVariations() {
    return getProperties().get("variations", new String[]{});
  }

  private void attemptSlingResourceTypeAssignment() {
    if (StringUtils.isBlank(getJcrPrimaryType()) || getJcrPrimaryType().equals(NT_UNSTRUCTURED)) {
      try {
        final ModifiableValueMap modifiableValueMap = getResource().adaptTo(
            ModifiableValueMap.class);
        if (modifiableValueMap != null) {
          modifiableValueMap.put(SLING_RESOURCE_TYPE_PROPERTY, getResource().getResourceType());
          getResourceResolver().commit();
        } else {
          LOG.error(
              "Unable to update sling:resourceType property value for ContentArea resource {} due "
              + "to null modifiableValueMap.", getPath());
        }
      } catch (final PersistenceException exception) {
        LOG.error(
            "Unable to update sling:resourceType property value for ContentArea resource {} due to"
            + " persistence exception.", getPath());
      }
    }
  }

}