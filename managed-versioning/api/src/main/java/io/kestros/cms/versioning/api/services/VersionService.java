package io.kestros.cms.versioning.api.services;

import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.cms.versioning.api.models.VersionableResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import java.util.List;

public interface VersionService {

  <T extends BaseResource> T getCurrentVersion(VersionableResource resource,
      Class<T> type);

  <T extends BaseResource> T getVersionResource(VersionableResource resource,
      String versionNumber, Class<T> type) throws VersionRetrievalException;

  <T extends BaseResource> List<T> getVersionHistory(VersionableResource resource,
      Class<T> type);

}
