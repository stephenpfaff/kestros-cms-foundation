package io.kestros.cms.versioning.api.models;

public interface VersionResource {

  Boolean isDeprecated();

  String getVersionNumber();

  Integer getMajorReleaseVersion();

  Integer getMinorReleaseVersion();

  Integer getTrivialReleaseVersion();

}
