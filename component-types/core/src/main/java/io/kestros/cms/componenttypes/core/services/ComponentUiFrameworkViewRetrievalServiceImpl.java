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

package io.kestros.cms.componenttypes.core.services;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;

import io.kestros.cms.componenttypes.api.exceptions.InvalidCommonUiFrameworkException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentUiFrameworkViewException;
import io.kestros.cms.componenttypes.api.models.CommonUiFrameworkView;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.componenttypes.api.models.ManagedComponentUiFrameworkView;
import io.kestros.cms.componenttypes.api.services.ComponentTypeRetrievalService;
import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewRetrievalService;
import io.kestros.cms.componenttypes.core.models.CommonUiFrameworkViewResource;
import io.kestros.cms.componenttypes.core.models.ComponentTypeResource;
import io.kestros.cms.componenttypes.core.models.ComponentUiFrameworkViewResource;
import io.kestros.cms.componenttypes.core.models.ManagedComponentUiFrameworkViewResource;
import io.kestros.cms.performanceservices.api.services.PerformanceService;
import io.kestros.cms.performanceservices.api.services.PerformanceTrackerService;
import io.kestros.cms.uiframeworks.api.models.ManagedUiFramework;
import io.kestros.cms.uiframeworks.api.models.ManagedVendorLibrary;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.models.VendorLibrary;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkRetrievalService;
import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.models.Version;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.commons.osgiserviceutils.services.BaseServiceResolverService;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ComponentUiFrameworkViewRetrievalService.
 */
@Component(immediate = true,
           service = ComponentUiFrameworkViewRetrievalService.class)
public class ComponentUiFrameworkViewRetrievalServiceImpl extends BaseServiceResolverService
    implements ComponentUiFrameworkViewRetrievalService, PerformanceService {

  private static final Logger LOG = LoggerFactory.getLogger(
      ComponentUiFrameworkViewRetrievalServiceImpl.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ResourceResolverFactory resourceResolverFactory;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private VersionService versionService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PerformanceTrackerService performanceTrackerService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiFrameworkRetrievalService uiFrameworkRetrievalService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ComponentTypeRetrievalService componentTypeRetrievalService;

  @Nonnull
  @Override
  public CommonUiFrameworkView getCommonUiFrameworkView(@Nonnull ComponentType componentType)
      throws InvalidCommonUiFrameworkException, InvalidComponentTypeException {
    String tracker = startPerformanceTracking();
    if (componentType instanceof ComponentTypeResource) {
      try {
        CommonUiFrameworkViewResource commonView = getChildAsBaseResource("common",
            (ComponentTypeResource) componentType).getResource().adaptTo(
            CommonUiFrameworkViewResource.class);
        if (commonView != null) {
          endPerformanceTracking(tracker);
          return commonView;
        }
      } catch (ChildResourceNotFoundException e) {
        endPerformanceTracking(tracker);
        throw new InvalidCommonUiFrameworkException(componentType.getPath());
      }
    }
    endPerformanceTracking(tracker);
    throw new InvalidComponentTypeException("", componentType.getPath());
  }

  @Nonnull
  @Override
  public ComponentUiFrameworkView getComponentUiFrameworkViewFromStandaloneUiFramework(
      @Nonnull ComponentType componentType, @Nonnull UiFramework uiFramework)
      throws InvalidComponentUiFrameworkViewException, ChildResourceNotFoundException {
    String tracker = startPerformanceTracking();
    getServiceResourceResolver().refresh();

    ComponentUiFrameworkViewResource componentUiFrameworkViewResource = getChildAsBaseResource(
        uiFramework.getFrameworkCode(), componentType.getResource()).getResource().adaptTo(
        ComponentUiFrameworkViewResource.class);
    if (componentUiFrameworkViewResource != null) {
      endPerformanceTracking(tracker);
      return componentUiFrameworkViewResource;
    }
    endPerformanceTracking(tracker);
    throw new InvalidComponentUiFrameworkViewException(componentType.getPath(), uiFramework);
  }

  @Nonnull
  @Override
  public ComponentUiFrameworkView getComponentUiFrameworkViewFromStandaloneVendorLibrary(
      @Nonnull ComponentType componentType, @Nonnull VendorLibrary vendorLibrary)
      throws InvalidComponentUiFrameworkViewException, ResourceNotFoundException {
    String tracker = startPerformanceTracking();
    getServiceResourceResolver().refresh();

    ComponentUiFrameworkViewResource componentUiFrameworkViewResource = getResourceAsBaseResource(
        componentType.getPath() + vendorLibrary.getPath(),
        getServiceResourceResolver()).getResource().adaptTo(ComponentUiFrameworkViewResource.class);
    if (componentUiFrameworkViewResource != null) {
      endPerformanceTracking(tracker);
      return componentUiFrameworkViewResource;
    }
    endPerformanceTracking(tracker);
    throw new ResourceNotFoundException(componentType.getPath() + vendorLibrary.getPath());
  }

  @Nonnull
  @Override
  public ComponentUiFrameworkView getComponentUiFrameworkViewWithFallback(
      @Nonnull ComponentType componentType, @Nonnull UiFramework uiFramework)
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException {
    try {
      return getComponentUiFrameworkViewFromStandaloneUiFramework(componentType, uiFramework);
    } catch (ChildResourceNotFoundException exception) {
      LOG.trace(exception.getMessage());
    }
    try {
      ManagedUiFramework managedUiFramework = (ManagedUiFramework) uiFramework.getRootResource();
      return getComponentUiFrameworkViewFromManagedUiFramework(componentType, managedUiFramework,
          uiFramework.getVersion());
    } catch (VersionFormatException | ChildResourceNotFoundException
               | InvalidResourceTypeException | NoValidAncestorException e) {
      LOG.trace(e.getMessage());
    }
    try {
      return getComponentUiFrameworkViewFromVendorLibraryList(componentType, uiFramework);
    } catch (ResourceNotFoundException e) {
      LOG.trace(e.getMessage());
    }
    try {
      return getCommonUiFrameworkView(componentType);
    } catch (InvalidCommonUiFrameworkException | InvalidComponentTypeException e) {
      LOG.trace(e.getMessage());
    }
    throw new InvalidComponentUiFrameworkViewException(componentType.getPath(), uiFramework);
  }

  @Nonnull
  @Override
  public ComponentUiFrameworkView getComponentUiFrameworkViewFromManagedUiFramework(
      @Nonnull ComponentType componentType, @Nonnull ManagedUiFramework managedUiFramework,
      @Nonnull Version maxVersion)
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             ChildResourceNotFoundException, InvalidResourceTypeException {
    if (versionService != null) {
      try {
        ManagedComponentUiFrameworkView managedComponentUiFrameworkView
            = getManagedComponentUiFrameworkViewFromManagedUiFramework(componentType,
            managedUiFramework);
        return versionService.getClosestVersion(managedComponentUiFrameworkView,
            maxVersion.getFormatted());
      } catch (VersionRetrievalException e) {
        LOG.trace(e.getMessage());
      }
    }
    throw new InvalidComponentUiFrameworkViewException(componentType.getPath(), managedUiFramework,
        maxVersion);
  }

  @Nonnull
  @Override
  public ComponentUiFrameworkView getComponentUiFrameworkViewFromManagedVendorLibrary(
      @Nonnull ComponentType componentType, @Nonnull ManagedVendorLibrary managedVendorLibrary,
      @Nonnull Version maxVersion)
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             ChildResourceNotFoundException, InvalidResourceTypeException {
    if (versionService != null) {
      try {
        ManagedComponentUiFrameworkView managedComponentUiFrameworkView
            = getManagedComponentUiFrameworkViewFromManagedVendorLibrary(componentType,
            managedVendorLibrary);
        return versionService.getClosestVersion(managedComponentUiFrameworkView,
            maxVersion.getFormatted());
      } catch (VersionRetrievalException | ResourceNotFoundException e) {
        LOG.trace(e.getMessage());
      }
    }
    throw new InvalidComponentUiFrameworkViewException(componentType.getPath(),
        managedVendorLibrary, maxVersion);
  }

  @Nonnull
  @Override
  public ComponentUiFrameworkView getComponentUiFrameworkViewFromVendorLibraryList(
      @Nonnull ComponentType componentType, @Nonnull UiFramework uiFramework)
      throws ResourceNotFoundException {

    List<VendorLibrary> vendorLibraryList = uiFramework.getVendorLibraries();
    for (int i = vendorLibraryList.size() - 1; i >= 0; i--) {
      VendorLibrary vendorLibrary = vendorLibraryList.get(i);

      if (versionService != null) {
        try {
          ManagedVendorLibrary managedVendorLibrary
              = (ManagedVendorLibrary) vendorLibrary.getRootResource();
          return getComponentUiFrameworkViewFromManagedVendorLibrary(componentType,
              managedVendorLibrary, vendorLibrary.getVersion());
        } catch (NoValidAncestorException e) {
          try {
            return getComponentUiFrameworkViewFromStandaloneVendorLibrary(componentType,
                vendorLibrary);
          } catch (ResourceNotFoundException resourceNotFoundException) {
            LOG.debug(resourceNotFoundException.getMessage());
          } catch (InvalidComponentUiFrameworkViewException exception) {
            LOG.debug(exception.getMessage());
          }

        } catch (VersionFormatException exception) {
          LOG.debug(exception.getMessage());
        } catch (ChildResourceNotFoundException exception) {
          LOG.debug(exception.getMessage());
        } catch (InvalidResourceTypeException exception) {
          LOG.debug(exception.getMessage());
        } catch (InvalidComponentTypeException e) {
          LOG.debug(e.getMessage());
        } catch (InvalidComponentUiFrameworkViewException exception) {
          LOG.debug(exception.getMessage());
        }
      } else {
        try {
          return getComponentUiFrameworkViewFromStandaloneVendorLibrary(componentType,
              vendorLibrary);
        } catch (InvalidComponentUiFrameworkViewException exception) {
          LOG.debug(exception.getMessage());
        }
      }

    }
    throw new ResourceNotFoundException("");
  }

  @Nonnull
  @Override
  public ManagedComponentUiFrameworkView getManagedComponentUiFrameworkViewFromManagedUiFramework(
      @Nonnull ComponentType componentType, @Nonnull ManagedUiFramework managedUiFramework)
      throws ChildResourceNotFoundException, InvalidResourceTypeException {
    if (versionService != null) {
      ManagedComponentUiFrameworkViewResource managedComponentUiFrameworkViewResource
          = getChildAsBaseResource(managedUiFramework.getFrameworkCode(),
          componentType.getResource()).getResource().adaptTo(
          ManagedComponentUiFrameworkViewResource.class);
      if (managedComponentUiFrameworkViewResource != null) {
        return managedComponentUiFrameworkViewResource;
      }
    }
    throw new ChildResourceNotFoundException(managedUiFramework.getFrameworkCode(),
        componentType.getPath());
  }

  @Nonnull
  @Override
  public ManagedComponentUiFrameworkView getManagedComponentUiFrameworkViewFromManagedVendorLibrary(
      @Nonnull ComponentType componentType, @Nonnull ManagedVendorLibrary managedVendorLibrary)
      throws ChildResourceNotFoundException, InvalidResourceTypeException,
             ResourceNotFoundException {
    if (versionService != null) {
      ManagedComponentUiFrameworkViewResource managedComponentUiFrameworkViewResource = null;
      try {
        managedComponentUiFrameworkViewResource = getResourceAsBaseResource(
            componentType.getPath() + managedVendorLibrary.getPath(),
            getServiceResourceResolver()).getResource().adaptTo(
            ManagedComponentUiFrameworkViewResource.class);
      } catch (ResourceNotFoundException e) {
        if (componentType.getPath().startsWith("/libs/")) {
          String componentTypePath = componentType.getPath().replace("/libs/", "/apps/");
          managedComponentUiFrameworkViewResource = getResourceAsBaseResource(
              componentTypePath + managedVendorLibrary.getPath(),
              getServiceResourceResolver()).getResource().adaptTo(
              ManagedComponentUiFrameworkViewResource.class);
        }
      }
      if (managedComponentUiFrameworkViewResource != null) {
        return managedComponentUiFrameworkViewResource;
      }
    }
    throw new ChildResourceNotFoundException(managedVendorLibrary.getPath(),
        componentType.getPath());
  }

  @Nonnull
  @Override
  public List<ComponentUiFrameworkView> getComponentViews(UiFramework uiFramework,
      Boolean includeApps, Boolean includeLibsCommons, Boolean includeAllLibs) {
    String tracker = startPerformanceTracking();
    List<ComponentUiFrameworkView> componentUiFrameworkViewList = new ArrayList<>();
    if (componentTypeRetrievalService != null) {
      for (ComponentType componentType : componentTypeRetrievalService.getAllComponentTypes(
          includeApps, includeLibsCommons, includeAllLibs)) {
        try {
          componentUiFrameworkViewList.add(
              getComponentUiFrameworkViewWithFallback(componentType, uiFramework));
        } catch (InvalidComponentUiFrameworkViewException e) {
          LOG.warn("Unable to add ComponentView for componentType {} and UiFramework {} to "
                   + "componentViews list. {}", componentType.getPath(), uiFramework.getPath(),
              e.getMessage());
        } catch (InvalidComponentTypeException e) {
          LOG.warn("Unable to add ComponentView for componentType {} and UiFramework {} to "
                   + "componentViews list. {}", componentType.getPath(), uiFramework.getPath(),
              e.getMessage());
        }
      }
    } else {
      LOG.error(
          "Unable to retrieve ComponentUiFrameworkViews for {}. ComponentTypeRetrievalService was"
          + " null.", uiFramework.getPath());
    }
    endPerformanceTracking(tracker);
    return componentUiFrameworkViewList;
  }

  @Nonnull
  @Override
  public List<ComponentUiFrameworkView> getComponentViews(VendorLibrary vendorLibrary,
      Boolean includeApps, Boolean includeLibsCommons, Boolean includeAllLibs) {
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public List<ComponentUiFrameworkView> getUiFrameworkViews(@Nonnull ComponentType componentType,
      Boolean includeEtcFrameworks, Boolean includeLibsFrameworks) {
    return Collections.emptyList();
  }

  @Override
  public String getDisplayName() {
    return "Component UI Framework View Retrieval Service";
  }

  @Override
  protected String getServiceUserName() {
    return "component-ui-framework-view-retrieval";
  }

  @Override
  public void deactivate(ComponentContext componentContext) {
    LOG.info("Deactivating {}.", getDisplayName());
  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {

  }

  @Override
  protected List<String> getRequiredResourcePaths() {
    return Collections.emptyList();
  }

  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

  @Override
  public PerformanceTrackerService getPerformanceTrackerService() {
    return performanceTrackerService;
  }
}
