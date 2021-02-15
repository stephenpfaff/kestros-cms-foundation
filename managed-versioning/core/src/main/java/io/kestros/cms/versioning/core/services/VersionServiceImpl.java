/*
 *      Copyright (C) 2020  Kestros, Inc.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.kestros.cms.versioning.core.services;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenAsBaseResource;

import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.cms.versioning.api.models.VersionableResource;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.cms.versioning.core.utils.VersionResourceSorter;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs version lookups.
 */
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
    }
    Collections.sort(versions, new VersionResourceSorter());
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

  @Override
  public <T extends BaseResource> T getClosestVersion(VersionableResource resource,
      String versionNumber) {
    return null;
  }

  BaseResource getVersionsFolderResource(VersionableResource versionableResource)
      throws ChildResourceNotFoundException {
    return getChildAsBaseResource("versions", versionableResource.getResource());
  }

}