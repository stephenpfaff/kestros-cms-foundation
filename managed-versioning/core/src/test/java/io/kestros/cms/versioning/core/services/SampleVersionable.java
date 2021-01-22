package io.kestros.cms.versioning.core.services;

import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.cms.versioning.api.models.VersionableResource;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import java.util.List;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = Resource.class,
       resourceType = "versionable")
public class SampleVersionable extends BaseResource implements VersionableResource {

  @OSGiService
  @Optional
  private VersionService versionService;


  @Override
  public Class getVersionResourceType() {
    return SampleVersion.class;
  }

  @Override
  public List getVersions() {
    return versionService.getVersionHistory(this);
  }

  @Override
  public VersionResource getCurrentVersion() {
    BaseResource resource = versionService.getCurrentVersion(this);
    if (resource instanceof VersionResource) {
      return (VersionResource) resource;
    }
    return null;
  }
}
