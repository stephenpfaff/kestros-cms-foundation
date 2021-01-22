package io.kestros.cms.versioning.core.services;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils
                  .getChildrenAsBaseResource;

import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.cms.versioning.api.models.VersionableResource;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true,
           service = VersionService.class)
public class VersionServiceImpl implements VersionService {

  @Override
  public <T extends BaseResource> T getCurrentVersion(VersionableResource resource) {
    List<T> versionHistory = getVersionHistory(resource);
    return versionHistory.get(versionHistory.size() - 1);
  }

  @Override
  public <T extends BaseResource> T getVersionResource(VersionableResource resource,
      String versionNumber) throws VersionRetrievalException {
    try {
      BaseResource versionsFolder = getVersionsFolderResource(resource);

      T child = getChildAsType(versionNumber, versionsFolder,
          (Class<T>) resource.getVersionResourceType());
      if (child instanceof VersionResource) {
        return child;
      } else {
        // todo log.
      }
    } catch (ChildResourceNotFoundException e) {
      // todo log.
      //      return null;
    } catch (InvalidResourceTypeException e) {
      //      e.printStackTrace();
    }
    throw new VersionRetrievalException();
  }

  @Override
  public <T extends BaseResource> List<T> getVersionHistory(VersionableResource resource) {
    List<T> versions = new ArrayList<>();
    try {
      BaseResource versionsFolder = getVersionsFolderResource(resource);
      for (BaseResource versionResource : getChildrenAsBaseResource(versionsFolder)) {
        versions.add((T) versionResource);
      }
    } catch (ChildResourceNotFoundException e) {
      e.printStackTrace();
    }

    return versions;
  }

  BaseResource getVersionsFolderResource(VersionableResource versionableResource)
      throws ChildResourceNotFoundException {
    return getChildAsBaseResource("versions", versionableResource.getResource());
  }

}
