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
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkRetrievalService;
import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.models.Version;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.commons.osgiserviceutils.services.BaseServiceResolverService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
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
 * Retrieves ComponentUiFrameworkViewResource objects.
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

  @Override
  @Nonnull
  public ComponentUiFrameworkView getComponentUiFrameworkView(@Nonnull ComponentType componentType,
      @Nonnull UiFramework uiFramework)
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException {
    String tracker = startPerformanceTracking();
    getServiceResourceResolver().refresh();
    Version version = null;
    try {
      version = uiFramework.getVersion();
    } catch (VersionFormatException e) {
      LOG.trace(e.getMessage());
    }
    try {
      if (versionService != null && version != null) {
        try {
          ManagedComponentUiFrameworkView managedComponentUiFrameworkView
              = getManagedComponentUiFrameworkView(componentType, uiFramework);
          try {
            endPerformanceTracking(tracker);
            return versionService.getClosestVersion(managedComponentUiFrameworkView,
                version.getFormatted());
          } catch (VersionRetrievalException e) {
            BaseResource versionsFolderResource = SlingModelUtils.getChildAsBaseResource("versions",
                managedComponentUiFrameworkView.getResource());

            ComponentUiFrameworkViewResource componentUiFrameworkViewResource
                = getChildAsBaseResource(version.getFormatted(),
                versionsFolderResource).getResource().adaptTo(
                ComponentUiFrameworkViewResource.class);
            if (componentUiFrameworkViewResource != null) {
              endPerformanceTracking(tracker);
              return componentUiFrameworkViewResource;
            }
          }
        } catch (ModelAdaptionException e) {
          endPerformanceTracking(tracker);
          return getCommonUiFrameworkView(componentType);
          //          throw new InvalidComponentUiFrameworkViewException(componentType.getPath(),
          //          uiFramework);
        }
      } else if (componentType instanceof ComponentTypeResource) {
        ComponentTypeResource componentTypeResource = (ComponentTypeResource) componentType;
        BaseResource frameworkViewResource;
        try {
          frameworkViewResource = getChildAsBaseResource(uiFramework.getFrameworkCode(),
              componentTypeResource);
        } catch (ChildResourceNotFoundException e) {
          try {
            frameworkViewResource = getChildAsBaseResource(uiFramework.getName(),
                componentTypeResource);
          } catch (ChildResourceNotFoundException childResourceNotFoundException) {
            try {
              ComponentType superType = componentType.getComponentSuperType();

              ComponentUiFrameworkView inheritedView = getComponentUiFrameworkView(superType,
                  uiFramework);
              if (!"common".equals(inheritedView.getName())) {
                endPerformanceTracking(tracker);
                return inheritedView;
              }
            } catch (InvalidComponentUiFrameworkViewException
                     | InvalidComponentTypeException exception) {
              LOG.debug(e.getMessage());
            }
            endPerformanceTracking(tracker);
            return getCommonUiFrameworkView(componentType);
            //            throw new InvalidComponentUiFrameworkViewException
            //            (componentTypeResource.getPath(),
            //                uiFramework);
          }
        }
        ComponentUiFrameworkViewResource view = frameworkViewResource.getResource().adaptTo(
            ComponentUiFrameworkViewResource.class);
        if (view != null) {
          endPerformanceTracking(tracker);
          return view;
        }
      } else {
        endPerformanceTracking(tracker);
        return getCommonUiFrameworkView(componentType);
        //        throw new InvalidComponentTypeException(componentType.getPath(),
        //            "Supplied componentType was not a ComponentTypeResource.");
      }
    } catch (InvalidCommonUiFrameworkException e) {
      LOG.error("Unable to determine version for UI");
    }
    endPerformanceTracking(tracker);
    throw new InvalidComponentUiFrameworkViewException(componentType.getPath(), uiFramework);
  }

  @Nonnull
  @Override
  public ManagedComponentUiFrameworkView getManagedComponentUiFrameworkView(
      @Nonnull ComponentType componentType, @Nonnull UiFramework uiFramework)
      throws ChildResourceNotFoundException, InvalidResourceTypeException {
    String tracker = startPerformanceTracking();
    if (componentType instanceof ComponentTypeResource) {
      ComponentTypeResource componentTypeResource = (ComponentTypeResource) componentType;
      BaseResource frameworkCodeResource = null;
      try {
        frameworkCodeResource = getChildAsBaseResource(uiFramework.getFrameworkCode(),
            componentTypeResource);
      } catch (ChildResourceNotFoundException e) {
        try {
          frameworkCodeResource = getChildAsBaseResource(uiFramework.getName(),
              componentTypeResource);
        } catch (ChildResourceNotFoundException exception) {
          if (componentTypeResource.getPath().startsWith("/libs")) {
            String appsComponentTypePath = componentTypeResource.getPath().replaceFirst("/libs/",
                "/apps/");
            try {
              BaseResource appsComponentTypeResource = getResourceAsBaseResource(
                  appsComponentTypePath, getServiceResourceResolver());
              frameworkCodeResource = getChildAsBaseResource(uiFramework.getFrameworkCode(),
                  appsComponentTypeResource);
            } catch (ResourceNotFoundException resourceNotFoundException) {
              resourceNotFoundException.printStackTrace();
            }
          }
        }
      }
      if (frameworkCodeResource != null && frameworkCodeResource.getResource().getChild("versions")
                                           != null) {
        ManagedComponentUiFrameworkViewResource managedComponentUiFrameworkViewResource
            = frameworkCodeResource.getResource().adaptTo(
            ManagedComponentUiFrameworkViewResource.class);
        if (managedComponentUiFrameworkViewResource != null) {
          endPerformanceTracking(tracker);
          return managedComponentUiFrameworkViewResource;
        }
      }
    }
    endPerformanceTracking(tracker);
    throw new InvalidResourceTypeException(
        String.format("%s/%s", componentType.getPath(), uiFramework.getFrameworkCode()),
        ManagedComponentUiFrameworkViewResource.class);
  }

  @Override
  @Nonnull
  public List<ComponentUiFrameworkView> getComponentViews(@Nonnull UiFramework uiFramework,
      @Nonnull Boolean includeApps, @Nonnull Boolean includeLibsCommons,
      @Nonnull Boolean includeAllLibs) {
    String tracker = startPerformanceTracking();
    List<ComponentUiFrameworkView> componentUiFrameworkViewList = new ArrayList<>();
    if (componentTypeRetrievalService != null) {
      for (ComponentType componentType : componentTypeRetrievalService.getAllComponentTypes(
          includeApps, includeLibsCommons, includeAllLibs)) {
        try {
          componentUiFrameworkViewList.add(getComponentUiFrameworkView(componentType, uiFramework));
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
  public List<ComponentUiFrameworkView> getUiFrameworkViews(@Nonnull ComponentType componentType,
      Boolean includeEtcFrameworks, Boolean includeLibsFrameworks) {
    String tracker = startPerformanceTracking();
    List<ComponentUiFrameworkView> uiFrameworkViewList = new ArrayList<>();
    try {
      uiFrameworkViewList.add(getCommonUiFrameworkView(componentType));
    } catch (Exception e) {
      LOG.debug("Unable to retrieve common view for componentType {}. {}", componentType.getPath(),
          e.getMessage());
    }
    if (uiFrameworkRetrievalService != null) {
      for (UiFramework uiFramework :
          uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(
          includeEtcFrameworks, includeLibsFrameworks)) {
        try {
          ComponentUiFrameworkView view = getComponentUiFrameworkView(componentType, uiFramework);
          if (!"common".equals(view.getName())) {
            uiFrameworkViewList.add(view);
          }

        } catch (InvalidComponentTypeException e) {
          LOG.debug("Unable to retrieve view for componentType {} and UiFramework {}. {}",
              componentType.getPath(), uiFramework.getPath(), e.getMessage());
        } catch (InvalidComponentUiFrameworkViewException e) {
          LOG.debug("Unable to retrieve view for componentType {} and UiFramework {}. {} ",
              componentType.getPath(), uiFramework.getPath(), e.getMessage());
        }
      }
    }
    endPerformanceTracking(tracker);
    return uiFrameworkViewList;
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
