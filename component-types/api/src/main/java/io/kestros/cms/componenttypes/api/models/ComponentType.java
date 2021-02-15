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

package io.kestros.cms.componenttypes.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.modeltypes.IconResource;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

/**
 * Model for resource that will be implemented by Components via sling:resourceType property.
 */
public interface ComponentType extends IconResource {

  /**
   * ComponentType title.
   *
   * @return ComponentType title.
   */
  String getTitle();

  /**
   * ComponentType path.
   *
   * @return ComponentType path.
   */
  String getPath();

  /**
   * Sling Resosource.
   *
   * @return Sling Resource.
   */
  Resource getResource();

  /**
   * ComponentType name.
   *
   * @return ComponentType name.
   */
  String getName();

  /**
   * sling:resourceSuperType value.
   *
   * @return sling:resourceSuperType value.
   */
  String getResourceSuperType();

  /**
   * Properties valuemap.
   *
   * @return Properties valuemap.
   */
  ValueMap getProperties();

  /**
   * Group the current ComponentType belongs to. Set by the componentGroup property.
   *
   * @return Group the current ComponentType belongs to.
   */
  String getComponentGroup();

  /**
   * The sling:resourceSuperType as a ComponentType.
   *
   * @return The sling:resourceSuperType as a ComponentType.
   * @throws InvalidComponentTypeException ComponentSuperType could not be found, or failed
   *     adaption.
   */
  @JsonIgnore
  ComponentType getComponentSuperType() throws InvalidComponentTypeException;


  /**
   * Whether the ComponentType is allowed to have missing ComponentUiFramework views.
   *
   * @return Whether the ComponentType is allowed to have missing ComponentUiFramework views.
   */
  boolean isBypassUiFrameworks();

  //  /**
  //   * Filters ComponentTypes within a list of ComponentTypeGroups to retrieve only the allowed
  //   * components.
  //   *
  //   * @return Filters ComponentTypes within a list of ComponentTypeGroups to retrieve only the
  //   *     allowed components.
  //   */
  //  @Nonnull
  //  List<ComponentTypeGroupModel> getAllowedComponentTypeGroups();

  /**
   * Whether to allow components that live under /libs/kestros/commons to be added as children.
   *
   * @return Whether to allow components that live under /libs/kestros/commons to be added as
   *     children.
   */
  @Nonnull
  Boolean isAllowLibsCommonsComponents();

  //
  //  /**
  //   * Gets the specified HtmlFile script for the specified script name and UiFramework.
  //   *
  //   * @param scriptName script to lookup.
  //   * @param uiFramework UiFramework used to determine which ComponentUiFrameworkView to find the
  //   *     script in.
  //   * @return the specified HtmlFile script for the specified script name and UiFramework.
  //   * @throws InvalidScriptException Script was not found, or failed adaption.
  //   * @throws InvalidCommonUiFrameworkException ComponentType fell back to 'common' view, but
  //   *     common view could not be found, or was invalid.
  //   */
  //  @Nonnull
  //  HtmlFile getScript(@Nonnull final String scriptName, @Nullable final UiFramework uiFramework);

  /**
   * Whether the component type will show actions in it's edit bar.
   *
   * @return Whether the component type will show actions in it's editbar
   */
  boolean isEditable();

}


