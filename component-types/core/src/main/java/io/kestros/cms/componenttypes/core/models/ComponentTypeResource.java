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

package io.kestros.cms.componenttypes.core.models;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sling Model for {@link ComponentType}.
 */
@Model(adaptables = Resource.class,
       resourceType = "kes:ComponentType")
public class ComponentTypeResource extends BaseResource implements ComponentType {

  private static final Logger LOG = LoggerFactory.getLogger(ComponentTypeResource.class);

  public static final String COMMON_UI_FRAMEWORK_VIEW_NAME = "common";
  public static final String PN_COMPONENT_GROUP = "componentGroup";
  public static final String GROUP_PREFIX = "group:";
  public static final String PN_EXCLUDED_UI_FRAMEWORKS = "excludedUiFrameworks";
  public static final String PN_EXCLUDED_COMPONENT_TYPES = "excludedComponentTypes";
  public static final String PN_ALLOWED_COMPONENT_TYPES = "allowedComponentTypes";
  public static final String PN_ALLOW_LIBS_KESTROS_COMMONS = "allowLibsCommons";

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


  @Override
  public ValueMap getProperties() {
    if (!"kes:ComponentType".equals(getResource().getResourceType()) && getPath().startsWith(
        "/apps")) {
      try {
        return getResourceAsType(getPath().replace("/apps/", "/libs/"), getResourceResolver(),
            ComponentTypeResource.class).getProperties();
      } catch (ModelAdaptionException e) {
        LOG.debug("Unable to get properties for /libs resource for {}. {}.", getPath(),
            e.getMessage());
      }
    }
    return super.getProperties();
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
  public ComponentTypeResource getComponentSuperType() throws InvalidComponentTypeException {
    try {
      return getResourceAsType(getResourceSuperType(), getResourceResolver(),
          ComponentTypeResource.class);
    } catch (final InvalidResourceTypeException exception) {
      LOG.debug("Unable to retrieve superType of {}: {}", getPath(), exception.getMessage());
      throw new InvalidComponentTypeException(getResourceSuperType(),
          "Invalid superTyped resource. jcr:primaryType must be 'kes:ComponentType'.");
    } catch (final ResourceNotFoundException exception) {
      LOG.debug("Unable to retrieve superType of {}: {}", getPath(), exception.getMessage());
      throw new InvalidComponentTypeException(getPath(), getResourceSuperType(),
          "SuperTyped resource not found.");
    }
  }

  /**
   * Whether the ComponentType is allowed to have missing ComponentUiFramework views.
   *
   * @return Whether the ComponentType is allowed to have missing ComponentUiFramework views.
   */
  @KestrosProperty(description =
                       "Whether the ComponentType is allowed to bypass validators that check "
                       + "if the ComponentType has views for all UiFrameworks, or a view for "
                       + "common.",
                   jcrPropertyName = "bypassUiFrameworks",
                   defaultValue = "false",
                   configurable = true,
                   sampleValue = "false")
  public boolean isBypassUiFrameworks() {
    boolean bypassUiFrameworksCheck = getProperties().get("bypassUiFrameworks", Boolean.FALSE);
    if (!bypassUiFrameworksCheck) {
      if (getPath().startsWith("/libs/kestros/components")) {
        return true;
      }
    }
    return bypassUiFrameworksCheck;
  }

  //  @Nonnull
  //  @Override
  //  public List<ComponentTypeGroupModel> getAllowedComponentTypeGroups() {
  //    return null;
  //  }

  @Nonnull
  @Override
  public Boolean isAllowLibsCommonsComponents() {
    return getProperty(PN_ALLOW_LIBS_KESTROS_COMMONS, Boolean.TRUE);
  }

  //  @Nonnull
  //  @Override
  //  public HtmlFile getScript(@Nonnull String scriptName, @Nullable UiFramework uiFramework) {
  //    return null;
  //  }

  @KestrosProperty(description = "Whether the component type will show actions in it's editbar.",
                   jcrPropertyName = "editable",
                   defaultValue = "true",
                   configurable = true,
                   sampleValue = "true")
  @Override
  public boolean isEditable() {
    return getProperty("editable", Boolean.TRUE);
  }

  @Override
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

}
