package io.kestros.cms.modeltypes;

public interface VersionResource {

  String getVersionNumber();
  Integer getMajorReleaseVersion();
  Integer getMinorReleaseVersion();
  Integer getTrivialReleaseVersion();

}
