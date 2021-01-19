package io.kestros.cms.uiframeworks.api.models;

import io.kestros.cms.modeltypes.VersionResource;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import io.kestros.commons.uilibraries.UiLibrary;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class VendorLibraryVersion extends UiLibrary implements VersionResource {

  public String getVersionNumber() {
    return getProperty("versionNumber", StringUtils.EMPTY);
  }

  @Override
  public Integer getMajorReleaseVersion() {
    if (getVersionNumber().split(".").length == 3) {
      return Integer.parseInt(getVersionNumber().split(".")[0]);
    }
    return 0;
  }

  @Override
  public Integer getMinorReleaseVersion() {
    if (getVersionNumber().split(".").length == 3) {
      return Integer.parseInt(getVersionNumber().split(".")[1]);
    }
    return 0;
  }

  @Override
  public Integer getTrivialReleaseVersion() {
    if (getVersionNumber().split(".").length == 3) {
      return Integer.parseInt(getVersionNumber().split(".")[2]);
    }
    return 0;
  }

  @Nullable
  public VendorLibrary getVendorLibrary() {
    try {
      return SlingModelUtils.getFirstAncestorOfType(this, VendorLibrary.class);
    } catch (NoValidAncestorException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String getDocumentationUrl() {
    return null;
  }
}
