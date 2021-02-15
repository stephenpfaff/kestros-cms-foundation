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

package io.kestros.cms.uiframeworks.core.services;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;

import io.kestros.cms.performanceservices.api.services.PerformanceService;
import io.kestros.cms.performanceservices.api.services.PerformanceTrackerService;
import io.kestros.cms.uiframeworks.api.exceptions.VendorLibraryRetrievalException;
import io.kestros.cms.uiframeworks.api.models.ManagedVendorLibrary;
import io.kestros.cms.uiframeworks.api.models.VendorLibrary;
import io.kestros.cms.uiframeworks.api.services.VendorLibraryRetrievalService;
import io.kestros.cms.uiframeworks.core.models.ManagedVendorLibraryResource;
import io.kestros.cms.uiframeworks.core.models.VendorLibraryResource;
import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.commons.osgiserviceutils.services.BaseServiceResolverService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves {@link VendorLibraryResource} objects.
 */
@Component(immediate = true,
           service = VendorLibraryRetrievalService.class)
public class VendorLibraryRetrievalServiceImpl extends BaseServiceResolverService
    implements VendorLibraryRetrievalService, PerformanceService {

  private static final Logger LOG = LoggerFactory.getLogger(
      VendorLibraryRetrievalServiceImpl.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ResourceResolverFactory resourceResolverFactory;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private VersionService versionService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PerformanceTrackerService performanceTrackerService;

  @Override
  public ManagedVendorLibrary getManagedVendorLibrary(String name, boolean includeEtc,
      boolean includeLibs) throws VendorLibraryRetrievalException {
    String tracker = startPerformanceTracking();
    try {
      if (includeEtc) {
        endPerformanceTracking(tracker);
        return getChildAsType(name, getEtcVendorLibrariesRootResources(),
            ManagedVendorLibraryResource.class);
      }
      if (includeLibs) {
        endPerformanceTracking(tracker);
        return getChildAsType(name, getLibsVendorLibrariesRootResources(),
            ManagedVendorLibraryResource.class);
      } else {
        endPerformanceTracking(tracker);
        throw new VendorLibraryRetrievalException(name,
            "Neither /etc nor /libs/kestros were included in ManagedVendorLibrary lookup, no "
            + "search attempted.");
      }
    } catch (ModelAdaptionException e) {
      endPerformanceTracking(tracker);
      throw new VendorLibraryRetrievalException(name, e.getMessage());
    }
  }

  @Override
  public VendorLibrary getVendorLibrary(String name, boolean includeEtc, boolean includeLibs)
      throws VendorLibraryRetrievalException, VersionRetrievalException {
    String tracker = startPerformanceTracking();
    boolean isManaged = name.contains("/");

    if (isManaged) {
      String libraryName = name.split("/")[0];
      String version = name.split("/")[1];
      ManagedVendorLibrary managedVendorLibrary = getManagedVendorLibrary(libraryName, includeEtc,
          includeLibs);
      if (versionService != null) {
        endPerformanceTracking(tracker);
        return versionService.getVersionResource(managedVendorLibrary, version);
      } else {
        endPerformanceTracking(tracker);
        throw new VersionRetrievalException(String.format(
            "Failed to find version %s for ManagedVendorLibrary %s. Version Service was null.",
            version, managedVendorLibrary.getPath()));
      }
    }
    if (includeEtc) {
      try {
        endPerformanceTracking(tracker);
        return getChildAsType(name, getEtcVendorLibrariesRootResources(),
            VendorLibraryResource.class);
      } catch (ChildResourceNotFoundException e) {
        LOG.trace(e.getMessage());
      } catch (ModelAdaptionException e) {
        LOG.warn("Failed to retrieve VendorLibrary {} from /etc/vendor-libraries. {}", name,
            e.getMessage());
      }
    }
    if (includeLibs) {
      try {
        endPerformanceTracking(tracker);
        return getChildAsType(name, getLibsVendorLibrariesRootResources(),
            VendorLibraryResource.class);
      } catch (ChildResourceNotFoundException e) {
        LOG.trace(e.getMessage());
      } catch (ModelAdaptionException e) {
        LOG.warn("Failed to retrieve VendorLibrary {} from /etc/vendor-libraries. {}", name,
            e.getMessage());
      }
    }
    if (!includeEtc && !includeLibs) {
      endPerformanceTracking(tracker);
      throw new VendorLibraryRetrievalException(name,
          "Neither /etc nor /libs/kestros were included in VendorLibrary lookup, no search "
          + "attempted.");
    }
    endPerformanceTracking(tracker);
    throw new VendorLibraryRetrievalException(name);
  }

  @Override
  public List<ManagedVendorLibrary> getAllManagedVendorLibraries(boolean includeEtc,
      boolean includeLibs) {
    String tracker = startPerformanceTracking();
    List<ManagedVendorLibrary> managedVendorLibraryList = new ArrayList<>();
    if (includeEtc) {
      try {
        managedVendorLibraryList.addAll(getChildrenOfType(getEtcVendorLibrariesRootResources(),
            ManagedVendorLibraryResource.class));
      } catch (ResourceNotFoundException e) {
        LOG.warn("Failed to find {} resource while building for ManagedVendorLibrary list.",
            "/etc/vendor-libraries");
      }
    }
    if (includeLibs) {
      try {
        managedVendorLibraryList.addAll(getChildrenOfType(getLibsVendorLibrariesRootResources(),
            ManagedVendorLibraryResource.class));
      } catch (ResourceNotFoundException e) {
        LOG.warn("Failed to find {} resource while building for ManagedVendorLibrary list.",
            "/libs/kestros/vendor-libraries");
      }
    }
    endPerformanceTracking(tracker);
    return managedVendorLibraryList;
  }

  @Override
  public List<VendorLibrary> getAllUnmanagedVendorLibraries(boolean includeEtc,
      boolean includeLibs) {
    String tracker = startPerformanceTracking();
    List<VendorLibrary> vendorLibraryList = new ArrayList<>();
    if (includeEtc) {
      try {
        vendorLibraryList.addAll(
            getChildrenOfType(getEtcVendorLibrariesRootResources(), VendorLibraryResource.class));
      } catch (ResourceNotFoundException e) {
        LOG.warn("Failed to find {} resource while building for unmanaged VendorLibrary list.",
            "/etc/vendor-libraries");
      }
    }
    if (includeLibs) {
      try {
        vendorLibraryList.addAll(
            getChildrenOfType(getLibsVendorLibrariesRootResources(), VendorLibraryResource.class));
      } catch (ResourceNotFoundException e) {
        LOG.warn("Failed to find {} resource while building for unmanaged VendorLibrary list.",
            "/libs/kestros/vendor-libraries");
      }
    }
    endPerformanceTracking(tracker);
    return vendorLibraryList;
  }

  @Override
  public String getDisplayName() {
    return "Vendor Library Retrieval Service";
  }

  @Override
  public void deactivate(ComponentContext componentContext) {
  }

  @Override
  protected String getServiceUserName() {
    return "vendor-library-retrieval";
  }

  @Override
  protected List<String> getRequiredResourcePaths() {
    return Arrays.asList("/etc/vendor-libraries", "/libs/kestros/vendor-libraries");
  }

  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

  private BaseResource getEtcVendorLibrariesRootResources() throws ResourceNotFoundException {
    return getResourceAsBaseResource("/etc/vendor-libraries", getServiceResourceResolver());
  }

  private BaseResource getLibsVendorLibrariesRootResources() throws ResourceNotFoundException {
    return getResourceAsBaseResource("/libs/kestros/vendor-libraries",
        getServiceResourceResolver());
  }

  @Override
  public PerformanceTrackerService getPerformanceTrackerService() {
    return performanceTrackerService;
  }
}
