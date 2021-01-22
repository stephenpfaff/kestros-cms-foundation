package io.kestros.cms.versioning.api.models;

import static java.lang.Integer.parseInt;

import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;

public interface VersionResource<T extends VersionableResource> {

  Resource getResource();

  default Boolean isDeprecated() {
    return getResource().getValueMap().get("deprecated", Boolean.FALSE);
  }

  T getRootResource() throws NoValidAncestorException;

  default Version getVersion() throws VersionFormatException {
    String version = getResource().getValueMap().get("versionNumber", StringUtils.EMPTY);

    if (version.split("\\.").length == 3) {
      String[] versionParts = version.split("\\.");
      return new Version(parseInt(versionParts[0]), parseInt(versionParts[1]),
          parseInt(versionParts[2]));
    }
    throw new VersionFormatException();
  }

}