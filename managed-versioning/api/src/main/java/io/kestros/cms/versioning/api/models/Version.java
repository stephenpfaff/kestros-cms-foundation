package io.kestros.cms.versioning.api.models;

public class Version {

  private Integer majorRelease;
  private Integer minorRelease;
  private Integer patchRelease;

  public Version(Integer majorRelease, Integer minorRelease, Integer patchRelease) {
    this.majorRelease = majorRelease;
    this.minorRelease = minorRelease;
    this.patchRelease = patchRelease;
  }

  public String getFormatted() {
    return String.format("%s.%s.%s", majorRelease, minorRelease, patchRelease);
  }

  public Integer getMajorVersion() {
    return majorRelease;
  }

  public Integer getMinorVersion() {
    return minorRelease;
  }

  public Integer getPatchVersion() {
    return patchRelease;
  }

}