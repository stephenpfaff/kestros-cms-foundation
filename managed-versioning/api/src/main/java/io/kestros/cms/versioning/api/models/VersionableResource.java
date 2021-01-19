package io.kestros.cms.versioning.api.models;

import java.util.List;

public interface VersionableResource {

  List<VersionResource> getVersions();

  VersionResource getCurrentVersion();

}
