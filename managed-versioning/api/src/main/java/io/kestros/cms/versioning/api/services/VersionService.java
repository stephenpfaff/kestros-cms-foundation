package io.kestros.cms.versioning.api.services;

import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.cms.versioning.api.models.VersionableResource;
import java.util.List;

public interface VersionService {

  VersionResource getCurrentVersion(VersionableResource resource);

  VersionResource getVersion(VersionableResource resource, String versionNumber)
      throws VersionRetrievalException;

  List<VersionResource> getVersionHistory(VersionableResource resource);

}
