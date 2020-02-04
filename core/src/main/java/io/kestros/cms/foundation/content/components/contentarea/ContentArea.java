package io.kestros.cms.foundation.content.components.contentarea;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.commons.structuredslingmodels.annotation.StructuredModel;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Component used for containing other components.  When referenced in a script without an existing
 * resource, it will attempt to create itself, and assign the proper sling:resourceType property
 * value.
 */
@StructuredModel(docPaths = {
    "/content/guide-articles/kestros-cms/site-building/defining-content-areas"})
@Model(adaptables = Resource.class,
       resourceType = "kestros/commons/components/content-area")
public class ContentArea extends BaseComponent {

  /**
   * The relative path of the ContentArea. Does not include jcr:content.
   *
   * @return The relative path of the ContentArea. Does not include jcr:content.
   */
  public String getRelativePath() {
    String path = getPath();
    String jcrContentRelativePath = "/" + JcrConstants.JCR_CONTENT + "/";
    if (path.contains(jcrContentRelativePath)) {
      path = path.split(jcrContentRelativePath)[1];
    }
    return path;
  }
}