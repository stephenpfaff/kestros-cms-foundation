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

package io.kestros.cms.foundation.componenttypes;

import static io.kestros.cms.foundation.utils.ComponentTypeUtils.getAllComponentTypes;
import static io.kestros.cms.foundation.utils.ComponentTypeUtils.getComponentTypeGroups;
import static io.kestros.cms.foundation.utils.DesignUtils.getAllUiFrameworks;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.foundation.componenttypes.frameworkview.ComponentUiFrameworkView;
import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.cms.foundation.exceptions.InvalidCommonUiFrameworkException;
import io.kestros.cms.foundation.exceptions.InvalidComponentTypeException;
import io.kestros.cms.foundation.exceptions.InvalidComponentUiFrameworkViewException;
import io.kestros.cms.foundation.exceptions.InvalidScriptException;
import io.kestros.cms.foundation.services.scriptprovider.CachedScriptProviderService;
import io.kestros.cms.foundation.utils.ComponentTypeUtils;
import io.kestros.cms.foundation.utils.DesignUtils;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model for resource that will be implemented by Components via sling:resourceType property.
 */
@KestrosModel(validationService = ComponentTypeValidationService.class,
              docPaths = {"/content/guide-articles/kestros/components/creating-new-component-types",
                  "/content/guide-articles/kestros/components/implementing-ui-framework-views",
                  "/content/guide-articles/kestros/components/defining-content-areas",
                  "/content/guide-articles/kestros-cms/foundation/creating-component-variations",
                  "/content/guide-articles/kestros-cms/foundation/grouping-components"})
@Model(adaptables = Resource.class,
       resourceType = "kes:ComponentType")
@Exporter(name = "jackson",
          selector = "component-type",
          extensions = "json")
public class ComponentType extends BaseResource {

  private static final Logger LOG = LoggerFactory.getLogger(ComponentType.class);

  public static final String COMMON_UI_FRAMEWORK_VIEW_NAME = "common";
  public static final String PN_COMPONENT_GROUP = "componentGroup";
  public static final String GROUP_PREFIX = "group:";
  public static final String PN_EXCLUDED_UI_FRAMEWORKS = "excludedUiFrameworks";
  public static final String PN_EXCLUDED_COMPONENT_TYPES = "excludedComponentTypes";
  public static final String PN_ALLOWED_COMPONENT_TYPES = "allowedComponentTypes";
  public static final String PN_ALLOW_LIBS_KESTROS_COMMONS = "allowLibsCommons";

  @OSGiService
  @Optional
  private CachedScriptProviderService cachedScriptProviderService;

  private List<ComponentUiFrameworkView> componentUiFrameworkViews;

  private ComponentUiFrameworkView commonUiFrameworkView;

  @Override
  @KestrosProperty(description =
                       "Path to the extended ComponentType. Baseline components should extend "
                       + "the Kestros Parent ComponentType",
                   jcrPropertyName = "sling:resourceSuperType",
                   defaultValue = "",
                   sampleValue = "kestros/commons/components/kestros-parent",
                   configurable = true)
  public String getResourceSuperType() {
    return super.getResourceSuperType();
  }

  /**
   * Group the current ComponentType belongs to. Set by the componentGroup property.
   *
   * @return Group the current ComponentType belongs to.
   */
  @KestrosProperty(description = "Group the component belongs to.",
                   jcrPropertyName = PN_COMPONENT_GROUP,
                   defaultValue = "",
                   configurable = true)
  public String getComponentGroup() {
    return getProperties().get(PN_COMPONENT_GROUP, StringUtils.EMPTY);
  }

  /**
   * The sling:resourceSuperType as a ComponentType.
   *
   * @return The sling:resourceSuperType as a ComponentType.
   * @throws InvalidComponentTypeException ComponentSuperType could not be found, or failed
   *     adaption.
   */
  @JsonIgnore
  public ComponentType getComponentSuperType() throws InvalidComponentTypeException {
    try {
      return getResourceAsType(getResourceSuperType(), getResourceResolver(), ComponentType.class);
    } catch (final InvalidResourceTypeException exception) {
      LOG.debug("Unable to retrieve superType of {}: {}", getPath(), exception.getMessage());
      throw new InvalidComponentTypeException(getResourceSuperType(),
          "Invalid resourceType, must be 'kes:ComponentType'.");
    } catch (final ResourceNotFoundException exception) {
      LOG.debug("Unable to retrieve superType of {}: {}", getPath(), exception.getMessage());
      throw new InvalidComponentTypeException(getResourceSuperType(), "Resource not found.");
    }
  }

  /**
   * The common ComponentUiFrameworkView, to be shown when no other valid views are found.
   *
   * @return The common ComponentUiFrameworkView,to be shown when no other valid views are found.
   * @throws InvalidCommonUiFrameworkException Common ComponentUiFramework could not be found,
   *     or failed adaption.
   */
  @JsonIgnore
  @Nonnull
  public ComponentUiFrameworkView getCommonUiFrameworkView()
      throws InvalidCommonUiFrameworkException {

    if (this.commonUiFrameworkView != null) {
      return this.commonUiFrameworkView;
    }

    try {
      final ComponentUiFrameworkView commonUiFrameworkView = getChildAsBaseResource(
          COMMON_UI_FRAMEWORK_VIEW_NAME, this).getResource().adaptTo(
          ComponentUiFrameworkView.class);
      if (commonUiFrameworkView != null) {
        this.commonUiFrameworkView = commonUiFrameworkView;
        return commonUiFrameworkView;
      }
    } catch (final Exception exception) {
      LOG.debug("Unable to retrieve 'common' ComponentUiFrameworkView of {}: {}", getPath(),
          exception.getMessage());
    }
    String libsComponentTypePath = getPath().replaceFirst("/apps/", "/libs/");
    ComponentType libsComponentType = null;
    try {
      libsComponentType = SlingModelUtils.getResourceAsType(libsComponentTypePath,
          getResourceResolver(), ComponentType.class);

      ComponentUiFrameworkView commonView = getChildAsBaseResource(COMMON_UI_FRAMEWORK_VIEW_NAME,
          libsComponentType).getResource().adaptTo(ComponentUiFrameworkView.class);
      if (commonView != null) {
        this.commonUiFrameworkView = commonView;
        return commonView;
      }
    } catch (InvalidResourceTypeException | ResourceNotFoundException
                         | ChildResourceNotFoundException e) {
      LOG.debug("Unable to retrieve common UI Framework View for {}. {}", getPath(),
          e.getMessage());
    }
    throw new InvalidCommonUiFrameworkException(getPath());
  }

  @KestrosProperty(description =
                       "Whether the ComponentType is allowed to bypass validators that check "
                       + "if the ComponentType has views for all UiFrameworks, or a view for "
                       + "common.",
                   jcrPropertyName = "bypassUiFrameworks",
                   defaultValue = "false",
                   configurable = true,
                   sampleValue = "false")
  public boolean isBypassUiFrameworks() {
    return getProperties().get("bypassUiFrameworks", Boolean.FALSE);
  }

  /**
   * All UI Framework view that belong to the current ComponentType.
   *
   * @return All UI Framework view that belong to the current ComponentType.
   */
  @JsonIgnore
  @Nonnull
  public List<ComponentUiFrameworkView> getUiFrameworkViews() {
    if (this.componentUiFrameworkViews != null) {
      return this.componentUiFrameworkViews;
    }
    final List<ComponentUiFrameworkView> uiFrameworkViews = new ArrayList<>();

    for (final BaseResource uiFrameworkViewResource : getChildrenAsBaseResource(this)) {
      if (!getExcludedUiFrameworkPaths().contains(uiFrameworkViewResource.getName())) {
        final ComponentUiFrameworkView uiFrameworkView
            = uiFrameworkViewResource.getResource().adaptTo(ComponentUiFrameworkView.class);
        uiFrameworkViews.add(uiFrameworkView);
      }
    }

    this.componentUiFrameworkViews = uiFrameworkViews;
    return this.componentUiFrameworkViews;
  }

  /**
   * Filters ComponentTypes within a list of ComponentTypeGroups to retrieve only the allowed
   * components.
   *
   * @return Filters ComponentTypes within a list of ComponentTypeGroups to retrieve only the
   *     allowed components.
   */
  @Nonnull
  public List<ComponentTypeGroup> getAllowedComponentTypeGroups() {
    if (!getAllowedComponentTypePaths().isEmpty()) {
      return ComponentTypeUtils.getComponentTypeGroupsFromComponentTypeList(
          SlingModelUtils.getResourcesAsType(getAllowedComponentTypePaths(), getResourceResolver(),
              ComponentType.class));
    }
    return getComponentTypeGroups(
        getAllComponentTypes(true, false, isAllowLibsCommonsComponents(), getResourceResolver()),
        getAllowedComponentTypePaths(), getExcludedComponentTypePaths(),
        getAllowedComponentTypeGroupNames(), getExcludedComponentTypeGroups());
  }

  /**
   * Whether to allow components that live under /libs/kestros/commons to be added as children.
   *
   * @return Whether to allow components that live under /libs/kestros/commons to be added as
   *     children.
   */
  @Nonnull
  public Boolean isAllowLibsCommonsComponents() {
    return getProperty(PN_ALLOW_LIBS_KESTROS_COMMONS, Boolean.TRUE);
  }

  /**
   * The ComponentUiFrameworkView for the specified UiFramework, or the Common
   * ComponentUiFrameworkView.
   *
   * @param uiFramework The UiFramework to lookup a view for.
   * @return The ComponentUiFrameworkView for the specified UiFramework, or the Common
   *     CommponetUiFrameworkView.
   * @throws InvalidComponentUiFrameworkViewException ComponentUiFramework for the specified
   *     UiFramework was not found, or failed adaption.
   */
  @Nonnull
  public ComponentUiFrameworkView getComponentUiFrameworkView(
      @Nonnull final UiFramework uiFramework) throws InvalidComponentUiFrameworkViewException {

    try {
      return DesignUtils.getComponentUiFrameworkView(uiFramework.getFrameworkCode(), this);

    } catch (final ChildResourceNotFoundException exception) {
      LOG.debug("{} view was not found for {}. Returning common view.",
          uiFramework.getFrameworkCode(), getPath());
    }
    try {
      return DesignUtils.getComponentUiFrameworkView(uiFramework.getName(), this);
    } catch (final ChildResourceNotFoundException exception) {
      LOG.debug("{} view was not found for {}. Returning common view.", uiFramework.getName(),
          getPath());
    }
    try {
      return getCommonUiFrameworkView();
    } catch (final InvalidCommonUiFrameworkException exception) {
      LOG.debug("Unable to retrieve ComponentUiFrameworkView for ComponentType {}", getPath());
    }
    throw new InvalidComponentUiFrameworkViewException(getPath(), uiFramework);
  }

  /**
   * Gets the specified HtmlFile script for the specified script name and UiFramework.
   *
   * @param scriptName script to lookup.
   * @param uiFramework UiFramework used to determine which ComponentUiFrameworkView to find the
   *     script in.
   * @return the specified HtmlFile script for the specified script name and UiFramework.
   * @throws InvalidScriptException Script was not found, or failed adaption.
   * @throws InvalidCommonUiFrameworkException ComponentType fell back to 'common' view, but
   *     common view could not be found, or was invalid.
   */
  @Nonnull
  public HtmlFile getScript(@Nonnull final String scriptName,
      @Nullable final UiFramework uiFramework)
      throws InvalidScriptException, InvalidCommonUiFrameworkException {
    if (uiFramework != null) {
      try {
        return getComponentUiFrameworkView(uiFramework).getUiFrameworkViewScript(scriptName);
      } catch (final ModelAdaptionException exception) {
        LOG.trace("Script {} not found for {} while using UiFramework {}. Looking to superType",
            scriptName, getPath(), uiFramework.getPath());
      }
      try {
        return getScriptFromSuperType(scriptName, uiFramework);
      } catch (final InvalidComponentTypeException exception) {
        LOG.debug(exception.getMessage());
      }
    }
    return getCommonUiFrameworkView().getUiFrameworkViewScript(scriptName);
  }

  /**
   * Font Awesome Icon class.
   *
   * @return Font Awesome Icon class.
   */
  @KestrosProperty(description = "Font awesome icon class, used in the Kestros Site Admin UI",
                   jcrPropertyName = "fontAwesomeIcon",
                   defaultValue = "fa fa-cube",
                   configurable = true,
                   sampleValue = "fa fa-cube")
  public String getFontAwesomeIcon() {
    String fontAwesomeIcon = getProperty("fontAwesomeIcon", StringUtils.EMPTY);
    if (StringUtils.isBlank(fontAwesomeIcon)) {
      try {
        fontAwesomeIcon = getComponentSuperType().getFontAwesomeIcon();
      } catch (final InvalidComponentTypeException exception) {
        LOG.debug("Unable to find FontAwesomeIcon for {} due to InvalidComponentType on {}.",
            getPath(), getResourceType());
      }
    }
    if (StringUtils.isBlank(fontAwesomeIcon)) {
      fontAwesomeIcon = "fa fa-cube";
    }
    return fontAwesomeIcon;
  }

  List<String> getAllowedComponentTypeGroupNames() {
    final List<String> allowedComponentTypeGroups = new ArrayList<>();

    for (final String type : getAllowedComponentTypes()) {
      if (type.startsWith(GROUP_PREFIX)) {
        final String groupName = type.replaceFirst(GROUP_PREFIX, StringUtils.EMPTY);
        allowedComponentTypeGroups.add(groupName);
      }
    }

    return allowedComponentTypeGroups;
  }

  List<String> getAllowedComponentTypePaths() {
    final List<String> allowedComponentTypePaths = new ArrayList<>();

    for (final String type : getAllowedComponentTypes()) {
      if (!type.startsWith(GROUP_PREFIX)) {
        allowedComponentTypePaths.add(type);
      }
    }

    return allowedComponentTypePaths;
  }

  String[] getAllowedComponentTypes() {
    return getProperty(PN_ALLOWED_COMPONENT_TYPES, new String[]{});
  }

  List<String> getExcludedComponentTypeGroups() {
    final List<String> excludedComponentTypeGroups = new ArrayList<>();

    for (final String type : getExcludedComponentTypes()) {
      if (type.startsWith(GROUP_PREFIX)) {
        final String groupName = type.replaceFirst(GROUP_PREFIX, StringUtils.EMPTY);
        excludedComponentTypeGroups.add(groupName);
      }
    }

    return excludedComponentTypeGroups;
  }

  List<String> getExcludedComponentTypePaths() {
    final List<String> excludedComponentTypePaths = new ArrayList<>();

    for (final String type : getExcludedComponentTypes()) {
      if (!type.startsWith(GROUP_PREFIX)) {
        excludedComponentTypePaths.add(type);
      }
    }

    return excludedComponentTypePaths;
  }

  List<String> getMissingUiFrameworkCodes() {
    final List<String> missingUiFrameworkCodes = new ArrayList<>();
    for (final UiFramework uiFramework : getAllUiFrameworks(getResourceResolver(), true, true)) {
      boolean found = false;
      ComponentUiFrameworkView componentUiFrameworkView = null;
      try {
        componentUiFrameworkView = getComponentUiFrameworkView(uiFramework);
      } catch (final InvalidComponentUiFrameworkViewException e) {
        LOG.debug("Unable to find ComponentUiFrameworkView '{}' for '{}' while building "
                  + "missingUiFrameworkCode List", uiFramework.getName(), getPath());
      }
      if (componentUiFrameworkView != null && !componentUiFrameworkView.getName().equals(
          COMMON_UI_FRAMEWORK_VIEW_NAME)) {
        found = true;
      }

      if (!found && !getExcludedUiFrameworkPaths().contains(uiFramework.getFrameworkCode())
          && !getExcludedUiFrameworkPaths().contains(uiFramework.getName())) {
        missingUiFrameworkCodes.add(uiFramework.getFrameworkCode());
      }
    }

    return missingUiFrameworkCodes;
  }

  private List<String> getExcludedUiFrameworkPaths() {
    return Arrays.asList(getProperties().get(PN_EXCLUDED_UI_FRAMEWORKS, new String[]{}));
  }

  private List<String> getExcludedComponentTypes() {
    return Arrays.asList(getProperty(PN_EXCLUDED_COMPONENT_TYPES, new String[]{}));
  }

  private HtmlFile getScriptFromSuperType(final String scriptName, final UiFramework uiFramework)
      throws InvalidComponentTypeException, InvalidScriptException,
             InvalidCommonUiFrameworkException {
    return this.getComponentSuperType().getScript(scriptName, uiFramework);
  }


}