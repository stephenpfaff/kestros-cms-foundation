package io.kestros.cms.versioning.core.services;

import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.cms.versioning.api.models.VersionableResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class,
       resourceType = "version")
public class SampleVersion extends BaseResource implements VersionResource {

  public Class getManagingResourceType() {
    return SampleVersionable.class;
  }

  @Override
  public VersionableResource getRootResource() throws NoValidAncestorException {
    return SlingModelUtils.getFirstAncestorOfType(this, SampleVersionable.class);
  }
}
