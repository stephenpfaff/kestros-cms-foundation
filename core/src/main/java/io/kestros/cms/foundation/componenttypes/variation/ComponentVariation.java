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

package io.kestros.cms.foundation.componenttypes.variation;

import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.uilibraries.UiLibrary;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Style variation types for ComponentUiFrameworkViews.
 */
@KestrosModel(docPaths = {
    "/content/guide-articles/kestros-cms/foundation/creating-new-component-types",
    "/content/guide-articles/kestros-cms/foundation/implementing-ui-framework-views",
    "/content/guide-articles/kestros-cms/foundation/defining-content-areas",
    "/content/guide-articles/kestros-cms/foundation/creating-component-variations",
    "/content/guide-articles/kestros-cms/foundation/grouping-components"},
              validationService = ComponentVariationValidationService.class)
@Model(adaptables = Resource.class,
       resourceType = "kes:ComponentVariation")
public class ComponentVariation extends UiLibrary {

  /**
   * Whether the variation must be included in a componentTypes's content script. When false, the
   * variation's class will be added to a component's wrapper div.
   *
   * @return Whether the variation must be included in a componentTypes's content script. When
   *     false, the variation's class will be added to a component's wrapper div.
   */
  @KestrosProperty(description =
                       "When set to true, the variation will not be added to a component's "
                       + "wrapper div when applied, and must be included in the component's "
                       + "content script using inlineVariations.",
                   jcrPropertyName = "inline",
                   configurable = true,
                   defaultValue = "false",
                   sampleValue = "false")
  public boolean isInlineVariation() {
    return getProperty("inline", Boolean.FALSE);
  }

  /**
   * Components without variation properties will be assigned this (and possibly other) variation by
   * default.
   *
   * @return Components without variation properties will be assigned this (and possibly other)
   *     variation by default.
   */
  @KestrosProperty(description = "When set to true, components without variation properties will "
                                 + "be assigned this (and possibly other) variation by default.",
                   jcrPropertyName = "inline",
                   configurable = true,
                   defaultValue = "false",
                   sampleValue = "false")
  public boolean isDefault() {
    return getProperty("default", Boolean.FALSE);
  }

  /**
   * Font Awesome Icon class.
   *
   * @return Font Awesome Icon class.
   */
  public String getFontAwesomeIcon() {
    return getProperty("fontAwesomeIcon", "fas fa-paint-brush");
  }

}
