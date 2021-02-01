package io.kestros.cms.versioning.api.services;

import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.cms.versioning.api.models.VersionableResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import java.util.List;

public interface VersionService {

  <T extends BaseResource> T getCurrentVersion(VersionableResource resource);

  <T extends BaseResource> T getVersionResource(VersionableResource resource, String versionNumber)
      throws VersionRetrievalException;

  <T extends BaseResource> List<T> getVersionHistory(VersionableResource resource);

  <T extends BaseResource> T getPreviousVersion(VersionResource resource)
      throws NoValidAncestorException;

  <T extends BaseResource> T getNextVersion(VersionResource resource)
      throws NoValidAncestorException;

}
