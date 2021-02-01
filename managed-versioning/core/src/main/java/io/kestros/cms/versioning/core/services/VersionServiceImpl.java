package io.kestros.cms.versioning.core.services;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenAsBaseResource;

import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.models.Version;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.cms.versioning.api.models.VersionableResource;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true,
           service = VersionService.class)
public class VersionServiceImpl implements VersionService {

  private static final Logger LOG = LoggerFactory.getLogger(VersionServiceImpl.class);

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
        throw new VersionRetrievalException(String.format(
            "Failed to find version %s for %s %s. %s was not a VersionResource instance ( was %s).",
            versionNumber, resource.getClass().getSimpleName(), resource.getPath(), child.getPath(),
            child.getClass().getSimpleName()));
      }
    } catch (ModelAdaptionException e) {
      throw new VersionRetrievalException(
          String.format("Failed to find version %s for %s %s. %s", versionNumber,
              resource.getClass().getSimpleName(), resource.getPath(), e.getMessage()));
    }
  }

  @Override
  public <T extends BaseResource> List<T> getVersionHistory(VersionableResource resource) {
    List<T> versions = new ArrayList<>();
    try {
      BaseResource versionsFolder = getVersionsFolderResource(resource);
      for (BaseResource versionResource : getChildrenAsBaseResource(versionsFolder)) {
        versions.add((T) versionResource.getResource().adaptTo(resource.getVersionResourceType()));
      }
    } catch (ChildResourceNotFoundException e) {
      LOG.warn("Unable to retrieve version history for {}. {}", resource.getPath(), e.getMessage());
    } Collections.sort(versions, new VersionResourceSorter());
    return versions;
  }

  @Override
  public <T extends BaseResource> T getPreviousVersion(VersionResource resource)
      throws NoValidAncestorException {
    List<T> versionResources = getVersionHistory(resource.getRootResource());
    for (int i = 0; i < versionResources.size(); i++) {
      if (versionResources.get(i).getName().equals(resource.getResource().getName())) {
        if (i > 0) {
          return versionResources.get(i - 1);
        }
      }
    }
    return null;
  }

  @Override
  public <T extends BaseResource> T getNextVersion(VersionResource resource)
      throws NoValidAncestorException {
    List<T> versionResources = getVersionHistory(resource.getRootResource());
    for (int i = 0; i < versionResources.size(); i++) {
      if (versionResources.get(i).getName().equals(resource.getResource().getName())) {
        if (i > 0) {
          if (versionResources.size() > i) {
            return versionResources.get(i + 1);
          }
        }
      }
    }
    return null;
  }

  BaseResource getVersionsFolderResource(VersionableResource versionableResource)
      throws ChildResourceNotFoundException {
    return getChildAsBaseResource("versions", versionableResource.getResource());
  }


}

class VersionResourceSorter implements Comparator<BaseResource> {

  @Override
  public int compare(BaseResource resource1, BaseResource resource2) {
    VersionResource versionResource1 = (VersionResource) resource1;
    VersionResource versionResource2 = (VersionResource) resource2;
    Version version1;
    Version version2;
    try {
      version1 = versionResource1.getVersion();
    } catch (VersionFormatException e) {
      return -1;
    }
    try {
      version2 = versionResource2.getVersion();
    } catch (VersionFormatException e) {
      return 1;
    }
    return version1.compareTo(version2);
  }
}

