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
