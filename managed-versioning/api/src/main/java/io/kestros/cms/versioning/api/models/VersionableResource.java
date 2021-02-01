package io.kestros.cms.versioning.api.models;

import java.util.List;
import org.apache.sling.api.resource.Resource;

public interface VersionableResource<T extends VersionResource> {

  String getTitle();

  String getPath();

  String getName();

  Resource getResource();

  Class<T> getVersionResourceType();

  List<T> getVersions();

  T getCurrentVersion();

}