package io.kestros.cms.versioning.api.models;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getFirstAncestorOfType;
import static java.lang.Integer.parseInt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public interface VersionResource<T extends VersionableResource> {

  Class<T> getManagingResourceType();

  Resource getResource();

  default Boolean isDeprecated() {
    return getResource().getValueMap().get("deprecated", Boolean.FALSE);
  }

  @JsonIgnore
  default <S extends BaseResource> T getRootResource() throws NoValidAncestorException {
    Class type = getManagingResourceType();
    BaseResource resource = getFirstAncestorOfType(((S) this), ((Class<S>) type));
    if (resource instanceof VersionableResource) {
      return (T) resource;
    } else {
      throw new NoValidAncestorException(String.format(
          "Versionable resource %s could not be cast to %s. Resource was not an instance of "
          + "VersionableResource", resource.getPath(), getManagingResourceType().getSimpleName()),
          getManagingResourceType());
    }
  }

  default Version getVersion() throws VersionFormatException {
    String version = getResource().getValueMap().get("versionNumber", StringUtils.EMPTY);
    if (StringUtils.isEmpty(version)) {
      version = getResource().getName();
    }
    if (version.split("\\.").length == 3) {
      String[] versionParts = version.split("\\.");
      return new Version(parseInt(versionParts[0]), parseInt(versionParts[1]),
          parseInt(versionParts[2]));
    }
    throw new VersionFormatException();
  }

}