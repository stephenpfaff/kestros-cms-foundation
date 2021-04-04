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

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getFirstAncestorOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getParentResourceAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import io.kestros.cms.componenttypes.api.exceptions.ComponentTypeRetrievalException;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.componenttypes.api.services.ComponentTypeCacheService;
import io.kestros.cms.componenttypes.api.services.ComponentTypeRetrievalService;
import io.kestros.cms.componenttypes.core.models.ComponentTypeResource;
import io.kestros.cms.componenttypes.core.models.ComponentUiFrameworkViewResource;
import io.kestros.cms.performanceservices.api.services.PerformanceService;
import io.kestros.cms.performanceservices.api.services.PerformanceTrackerService;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.BaseServiceResolverService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
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
 * Retrieves ComponentTypeResource objects.
 */
@Component(immediate = true,
           service = ComponentTypeRetrievalService.class)
public class ComponentTypeRetrievalServiceImpl extends BaseServiceResolverService
    implements ComponentTypeRetrievalService, PerformanceService {

  private static Logger LOG = LoggerFactory.getLogger(ComponentTypeRetrievalServiceImpl.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ResourceResolverFactory resourceResolverFactory;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PerformanceTrackerService performanceTrackerService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ComponentTypeCacheService componentTypeCacheService;

  @Nonnull
  @Override
  public ComponentType getComponentType(@Nonnull String path)
      throws ComponentTypeRetrievalException {
    String tracker = startPerformanceTracking();
    getServiceResourceResolver().refresh();
    try {
      return getResourceAsType(path, getServiceResourceResolver(), ComponentTypeResource.class);
    } catch (ResourceNotFoundException | InvalidResourceTypeException e) {
      String libsPath = "";
      if (path.startsWith("/apps/")) {
        libsPath = path.replace("/apps/", "/libs/");
      } else if (!path.startsWith("/apps/") && !path.startsWith("/libs/")) {
        libsPath = "/libs/" + path;
      }
      try {
        endPerformanceTracking(tracker);
        if (!libsPath.equals(path) && StringUtils.isNotEmpty(libsPath)) {
          return getComponentType(libsPath);
        } else {
          throw new ComponentTypeRetrievalException(path, e.getMessage());
        }
      } catch (ModelAdaptionException invalidResourceTypeException) {
        endPerformanceTracking(tracker);
        throw new ComponentTypeRetrievalException(path, e.getMessage());
      }
    } catch (ModelAdaptionException e) {
      endPerformanceTracking(tracker);
      throw new ComponentTypeRetrievalException(path, e.getMessage());
    }

  }

  @Nonnull
  @Override
  public ComponentType getComponentType(@Nonnull ComponentUiFrameworkView componentUiFrameworkView)
      throws ComponentTypeRetrievalException {
    String tracker = startPerformanceTracking();
    getServiceResourceResolver().refresh();
    if (componentUiFrameworkView instanceof ComponentUiFrameworkViewResource) {
      ComponentUiFrameworkViewResource componentUiFrameworkViewResource
          = (ComponentUiFrameworkViewResource) componentUiFrameworkView;
      try {
        endPerformanceTracking(tracker);
        return getParentResourceAsType(componentUiFrameworkViewResource,
            ComponentTypeResource.class);
      } catch (InvalidResourceTypeException e) {
        try {
          endPerformanceTracking(tracker);
          return getFirstAncestorOfType(componentUiFrameworkViewResource,
              ComponentTypeResource.class, true);
        } catch (NoValidAncestorException noValidAncestorException) {
          endPerformanceTracking(tracker);
          throw new ComponentTypeRetrievalException(componentUiFrameworkView.getPath(),
              "No parent component type.");
        }
        //        String libsPath = componentUiFrameworkView.getPath().replaceFirst("/apps/",
        //        "/libs/");
        //        libsPath = libsPath.replace("/" + componentUiFrameworkView.getName(), "");
        //        try {
        //          endPerformanceTracking(tracker);
        //          return getResourceAsType(libsPath, getServiceResourceResolver(),
        //              ComponentTypeResource.class);
        //        } catch (ResourceNotFoundException resourceNotFoundException) {
        //          endPerformanceTracking(tracker);
        //          throw new ComponentTypeRetrievalException(componentUiFrameworkView.getPath(),
        //              "No parent component type.");
        //        } catch (InvalidResourceTypeException invalidResourceTypeException) {
        //          endPerformanceTracking(tracker);
        //          throw new ComponentTypeRetrievalException(componentUiFrameworkView.getPath(),
        //              "Invalid ResourceType.");
        //        }
      } catch (NoParentResourceException e) {
        endPerformanceTracking(tracker);
        throw new ComponentTypeRetrievalException(componentUiFrameworkView.getPath(),
            e.getMessage());
      }
    }
    endPerformanceTracking(tracker);
    throw new ComponentTypeRetrievalException(componentUiFrameworkView.getPath(),
        "No parent component type.");
  }

  @Nonnull
  @Override
  public List<ComponentType> getAllComponentTypes(Boolean includeApps, Boolean includeLibsCommons,
      Boolean includeAllLibs) {
    String tracker = startPerformanceTracking();
    List<ComponentType> componentTypeList = new ArrayList<>();
    if (includeApps) {
      componentTypeList.addAll(getAllComponentTypesInDirectory("/apps"));
    }
    if (includeAllLibs) {
      componentTypeList.addAll(getAllComponentTypesInDirectory("/libs/kestros"));
    } else if (includeLibsCommons) {
      componentTypeList.addAll(getAllComponentTypesInDirectory("/libs/kestros/commons"));
    }
    endPerformanceTracking(tracker);
    return componentTypeList;
  }

  @Nonnull
  @Override
  public List<ComponentType> getAllComponentTypesInDirectory(String rootPath) {
    List<ComponentType> componentTypeList = new ArrayList<>();
    if (componentTypeCacheService != null) {
      try {
        for (String componentTypePath : componentTypeCacheService.getCachedComponentTypes(
            rootPath)) {
          componentTypeList.add(getComponentType(componentTypePath));
        }
      } catch (CacheRetrievalException e) {
        componentTypeList.addAll(getAllUncachedComponentTypesInADirectory(rootPath));

        List<String> componentTypePaths = new ArrayList<>();
        for (ComponentType componentType : componentTypeList) {
          componentTypePaths.add(componentType.getPath());
        }
        try {
          componentTypeCacheService.cacheComponentTypePathList(rootPath, componentTypePaths);
        } catch (CacheBuilderException exception) {
          LOG.warn(e.getMessage());
        }
      } catch (ComponentTypeRetrievalException e) {
        LOG.warn(e.getMessage());
      }
    } else {
      componentTypeList.addAll(getAllUncachedComponentTypesInADirectory(rootPath));
    }
    return componentTypeList;
  }

  private List<ComponentType> getAllUncachedComponentTypesInADirectory(String rootPath) {
    List<ComponentType> componentTypeList = new ArrayList<>();
    try {
      ComponentTypeResource rootComponentType = getResourceAsType(rootPath,
          getServiceResourceResolver(), ComponentTypeResource.class);
      componentTypeList.add(rootComponentType);
    } catch (InvalidResourceTypeException exception) {
      BaseResource rootResource = null;
      try {
        rootResource = getResourceAsBaseResource(rootPath, getServiceResourceResolver());
        for (BaseResource childResource : getChildrenAsBaseResource(rootResource)) {
          componentTypeList.addAll(getAllComponentTypesInDirectory(childResource.getPath()));
        }
      } catch (ResourceNotFoundException resourceNotFoundException) {
        LOG.debug(resourceNotFoundException.getMessage());
      }
    } catch (ResourceNotFoundException exception) {
      LOG.debug(exception.getMessage());
    }
    return componentTypeList;
  }

  @Override
  protected String getServiceUserName() {
    return "component-type-retrieval";
  }

  @Override
  protected List<String> getRequiredResourcePaths() {
    return Arrays.asList("/apps", "/libs");
  }

  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

  @Override
  public String getDisplayName() {
    return "Component Type Retrieval Service";
  }

  @Override
  public void deactivate(ComponentContext componentContext) {
    LOG.info("Deactivating {}.", getDisplayName());
  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {
    super.runAdditionalHealthChecks(log);
    if (this.getAllComponentTypes(true, false, true).isEmpty()) {
      log.warn("No ComponentType detected.");
    }
  }

  @Override
  public PerformanceTrackerService getPerformanceTrackerService() {
    return performanceTrackerService;
  }
}
