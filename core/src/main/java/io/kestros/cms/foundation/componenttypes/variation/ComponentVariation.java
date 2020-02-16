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

import io.kestros.commons.structuredslingmodels.annotation.StructuredModel;
import io.kestros.commons.uilibraries.UiLibrary;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Style variation types for ComponentUiFrameworkViews.
 */
@StructuredModel(docPaths = {
    "/content/guide-articles/kestros-cms/foundation/creating-new-component-types",
    "/content/guide-articles/kestros-cms/foundation/implementing-ui-framework-views",
    "/content/guide-articles/kestros-cms/foundation/defining-content-areas",
    "/content/guide-articles/kestros-cms/foundation/creating-component-variations",
    "/content/guide-articles/kestros-cms/foundation/grouping-components"})
@Model(adaptables = Resource.class,
       resourceType = "kes:ComponentVariation")
public class ComponentVariation extends UiLibrary {

}
