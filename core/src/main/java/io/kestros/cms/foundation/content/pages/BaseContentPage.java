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

package io.kestros.cms.foundation.content.pages;

import static io.kestros.cms.foundation.utils.DesignUtils.getAllUiFrameworks;
import static io.kestros.cms.foundation.utils.JcrPropertyUtils.getRelativeDate;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.adaptTo;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getAllDescendantsOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getFirstAncestorOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getParentResourceAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourcesAsType;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.kestros.cms.foundation.componenttypes.ComponentType;
import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.foundation.content.sites.BaseSite;
import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.cms.foundation.exceptions.InvalidComponentTypeException;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.cms.foundation.services.themeprovider.ThemeProviderService;
import io.kestros.cms.foundation.utils.RelativeDate;
import io.kestros.cms.user.KestrosUser;
import io.kestros.cms.user.exceptions.UserRetrievalException;
import io.kestros.cms.user.services.KestrosUserService;
import io.kestros.commons.structuredslingmodels.BasePage;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base content page to extend Page Types from.  Contains logic for themes, site relationships, and
 * retrieving child pages.
 */
@KestrosModel(validationService = BaseContentPageValidationService.class,
              docPaths = {"/content/guide-articles/kestros/site-management/creating-pages",
                  "/content/guide-articles/kestros/site-management/editing-page-properties",
                  "/content/guide-articles/kestros/site-management/creating-components",
                  "/content/guide-articles/kestros/getting-started/understanding-validation"},
              usesJcrContent = true)
@Model(adaptables = Resource.class,
       resourceType = "kes:Page")
@Exporter(name = "jackson",
          selector = "base-content-page",
          extensions = "json")
public class BaseContentPage extends BasePage {

  private static final Logger LOG = LoggerFactory.getLogger(BaseContentPage.class);

  @SuppressWarnings("unused")
  @OSGiService
  @Optional
  private ModelFactory modelFactory;

  @OSGiService
  @Optional
  private KestrosUserService kestrosUserService;

  @OSGiService
  @Optional
  private ThemeProviderService themeProviderService;

  /**
   * Display title of the current site.  Display title is generally used for frontend, whereas title
   * is used showing in the platform. Defaults to title.
   *
   * @return Display title of the current site.
   */
  public String getDisplayTitle() {
    String displayTitle = getProperties().get("displayTitle", StringUtils.EMPTY);
    if (StringUtils.isNotEmpty(displayTitle)) {
      return displayTitle;
    }
    return getTitle();
  }

  /**
   * Description to be displayed in administration UI.
   *
   * @return Description to be displayed in administration UI.
   */
  public String getDisplayDescription() {
    String displayDescription = getProperties().get("displayDescription", StringUtils.EMPTY);
    if (StringUtils.isNotEmpty(displayDescription)) {
      return displayDescription;
    }
    return getDescription();
  }

  /**
   * Metadata title. Defaults to display title.
   *
   * @return Metadata title. Defaults to display title.
   */
  @KestrosProperty(description = "Metadata title. Defaults to display title.",
                   jcrPropertyName = "metaTitle",
                   defaultValue = "",
                   configurable = true)
  public String getMetaTitle() {
    String metaTitle = getProperties().get("metaTitle", StringUtils.EMPTY);
    if (StringUtils.isNotEmpty(metaTitle)) {
      return metaTitle;
    }
    return getDisplayTitle();
  }

  /**
   * Description to be used in a page's meta description tag.
   *
   * @return Description to be used in a page's meta description tag.
   */
  @KestrosProperty(description = "Description to be used in a page's meta description tag",
                   jcrPropertyName = "metaDescription",
                   defaultValue = "",
                   configurable = true)
  public String getMetaDescription() {
    String metaDescription = getProperties().get("metaDescription", StringUtils.EMPTY);
    if (StringUtils.isNotEmpty(metaDescription)) {
      return metaDescription;
    }
    return getDisplayDescription();
  }

  /**
   * Retrieves parent page.
   *
   * @return Parent page.
   * @throws NoParentResourceException No parent page was found, or parent page could not be
   *     adapted to BaseContentPage.
   */
  @JsonIgnore
  @Override
  public BaseContentPage getParent() throws NoParentResourceException {
    try {
      return getParentResourceAsType(this, BaseContentPage.class);
    } catch (final InvalidResourceTypeException e) {
      try {
        return getParentResourceAsType(this, BaseSite.class);
      } catch (final InvalidResourceTypeException ex) {
        throw new NoParentResourceException(
            String.format("Unable to retrieve parent page of %s. %s", getPath(), ex.getMessage()));
      }
    }
  }

  /**
   * Theme currently applied to the page.
   *
   * @return Theme currently applied to the page.
   * @throws ResourceNotFoundException Theme could not be found.
   * @throws InvalidThemeException Theme was specified, and a matching Resource was found, but
   *     could not be adapted to Theme.
   */
  @Nullable
  @JsonIgnore
  @KestrosProperty(description = "Theme currently applied to the page.",
                   jcrPropertyName = "kes:theme",
                   defaultValue = "",
                   configurable = true)
  public Theme getTheme() throws ResourceNotFoundException, InvalidThemeException {
    return themeProviderService.getThemeForPage(this);
  }

  /**
   * Site that the current Page belongs to.
   *
   * @return Site that the current Page belongs to.
   */
  @Nullable
  @JsonIgnore
  public BaseSite getSite() {
    try {
      return getFirstAncestorOfType(this, BaseSite.class);
    } catch (final NoValidAncestorException exception) {
      // do nothing.
    }
    try {
      return adaptTo(this, BaseSite.class);
    } catch (final InvalidResourceTypeException exception) {
      // do nothing.
    }
    LOG.warn("Unable to retrieve ancestor Site for Page {}", getPath());
    return null;
  }

  /**
   * All Child Pages of the current Page.
   *
   * @return All Child Pages of the current Page.
   */
  @Nonnull
  @JsonProperty("childPages")
  @JsonIgnoreProperties("childPages")
  public List<BaseContentPage> getChildPages() {
    return getChildrenOfType(this, BaseContentPage.class);
  }

  /**
   * List of all components on the current page, as BaseComponent.
   *
   * @return List of all components on the current page, as BaseComponent.
   */
  @Nonnull
  @JsonIgnore
  public List<BaseComponent> getAllComponents() {
    final List<BaseComponent> components = new ArrayList<>();
    for (final BaseComponent component : getAllDescendantsOfType(getContentComponent(),
        BaseComponent.class)) {
      if (isNotEmpty(component.getSlingResourceType())
          && !component.getSlingResourceType().contains(
          "kestros/commons/components/content-area")) {
        components.add(component);
      }
    }
    return components;
  }

  /**
   * ComponentType of the current Page.  (The type that the Component's jcr:content -
   * sling:resourceType resolves to.
   *
   * @return ComponentType of the current Page.
   * @throws InvalidComponentTypeException Specified sling:resourceType was either not a valid
   *     ComponentType, or was missing.
   */
  @Nullable
  @JsonIgnore
  public ComponentType getComponentType() throws InvalidComponentTypeException {
    try {
      return getResourceAsType(getResourceType(), getResourceResolver(), ComponentType.class);
    } catch (final Exception exception) {
      try {
        return getResourceAsType("/libs/" + getResourceType(), getResourceResolver(),
            ComponentType.class);
      } catch (final InvalidResourceTypeException exception1) {
        LOG.warn(
            "Unable to retrieve ComponentType for {}.  {} resourceType found, but could not be "
            + "adapted to ComponentType.", getPath(), getResourceType());
      } catch (final ResourceNotFoundException exception1) {
        LOG.warn("Unable to retrieve ComponentType for {}.  resourceType {} not found.", getPath(),
            getResourceType());

      }
      LOG.warn("Unable to retrieve ComponentType for {}: {}", getPath(), exception.getMessage());
    }
    throw new InvalidComponentTypeException(getPath());
  }

  /**
   * All UiFrameworks that the current Page is allowed to use.
   *
   * @return All UiFrameworks that the current Page is allowed to use.
   */
  @JsonIgnore
  public List<UiFramework> getAllowedUiFrameworks() {
    final List<String> allowedFrameworkPaths = Arrays.asList(getAllowedUiFrameworkPaths());

    if (!allowedFrameworkPaths.isEmpty()) {
      return getResourcesAsType(allowedFrameworkPaths, getResourceResolver(), UiFramework.class);
    }

    return getAllUiFrameworks(getResourceResolver(), true, false);
  }

  /**
   * JcrContent Resource of the current Page, adapted to BaseComponent.  If no jcr:content is found,
   * the current resource is returned, adapted to BaseComponent.
   *
   * @return * JcrContent Resource of the current Page.
   */
  @Nonnull
  @JsonIgnore
  public BaseComponent getContentComponent() {
    BaseComponent contentComponent;
    try {
      contentComponent = getChildAsBaseResource(JCR_CONTENT, this).getResource().adaptTo(
          BaseComponent.class);
      if (contentComponent != null) {
        return contentComponent;
      }
      throw new IllegalStateException();
    } catch (final ModelAdaptionException exception) {
      LOG.warn("Unable to get jcr:content Resource for {}. jcr:content Resource not found.",
          getPath());
    }
    contentComponent = getResource().adaptTo(BaseComponent.class);
    if (contentComponent != null) {
      return contentComponent;
    }
    throw new IllegalStateException();
  }

  /**
   * Retrieves all components as the respective types.
   *
   * @param <T> Type, extends BaseComponent
   * @return All components as the respective types.
   */
  @KestrosProperty(description = "Direct child Components.")
  @JsonInclude(Include.NON_EMPTY)
  @JsonProperty("components")
  public <T extends BaseComponent> List<T> getTopLevelComponents() {
    return getContentComponent().getChildren();
  }

  @KestrosProperty(description = "All descendant Components.")
  @JsonIgnore
  protected <T extends BaseComponent> List<T> getAllDescendantComponents() {
    return getContentComponent().getAllDescendantComponents();
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
      return kestrosUserService.getUser(username, getResourceResolver());
    } catch (final UserRetrievalException e) {
      LOG.debug("Failed to retrieve user {}.", username);
      return null;
    }
  }

  /**
   * User who last modified the current page.
   *
   * @return User who last modified the current page.
   */
  @JsonIgnore
  @Nullable
  public KestrosUser getLastModifiedBy() {
    final String username = getProperties().get("kes:lastModifiedBy", StringUtils.EMPTY);
    try {
      return kestrosUserService.getUser(username, getResourceResolver());
    } catch (final UserRetrievalException e) {
      LOG.debug("Failed to retrieve user {}.", username);
      return null;
    }
  }

  /**
   * When the current page was last modified.
   *
   * @return When the current page was last modified.
   */
  @Nullable
  @JsonIgnore
  public RelativeDate getLastModified() {
    return getRelativeDate(this, "kes:lastModified");
  }

  /**
   * When the current page was created.
   *
   * @return When the current page was created.
   */
  @Nullable
  @JsonIgnore
  public RelativeDate getCreated() {
    return getRelativeDate(this, "kes:created");
  }


  /**
   * Path to page's /image resource, or EMPTY String resource is missing.
   *
   * @return Path to page's /image resource, or EMPTY String resource is missing.
   */
  @JsonIgnore
  public String getImagePath() {
    try {
      return getChildAsBaseResource("image", getContentComponent()).getPath();
    } catch (final ChildResourceNotFoundException exception) {
      LOG.debug(exception.getMessage());
    }
    return StringUtils.EMPTY;
  }

  /**
   * Font Awesome Icon class.
   *
   * @return Font Awesome Icon class.
   */
  @JsonIgnore
  @KestrosProperty(description = "Font awesome icon class, used in the Kestros Site Admin UI",
                   jcrPropertyName = "fontAwesomeIcon",
                   defaultValue = "fa fa-file",
                   configurable = true,
                   sampleValue = "fa fa-file")
  public String getFontAwesomeIcon() {
    try {
      final String fontAwesomeIcon = getComponentType().getFontAwesomeIcon();
      if (!"fa fa-cube".equals(fontAwesomeIcon)) {
        return fontAwesomeIcon;
      }
    } catch (final InvalidComponentTypeException exception) {
      LOG.warn("Unable to find FontAwesomeIcon for {} due to InvalidResourceType on {}.", getPath(),
          getResourceType());
    }
    return "fa fa-file";
  }

  String[] getAllowedUiFrameworkPaths() {
    return getProperty("allowedUiFrameworks", new String[]{});
  }

}
