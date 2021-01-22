package io.kestros.cms.versioning.api.models;

import java.util.List;
import org.apache.sling.api.resource.Resource;

public interface VersionableResource<T extends VersionResource> {

  Resource getResource();

  Class<T> getVersionResourceType();

  List<T> getVersions();

  T getCurrentVersion();

}