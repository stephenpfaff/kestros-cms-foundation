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

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getFirstAncestorOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import io.kestros.cms.performanceservices.api.services.PerformanceService;
import io.kestros.cms.performanceservices.api.services.PerformanceTrackerService;
import io.kestros.cms.uiframeworks.api.exceptions.UiFrameworkRetrievalException;
import io.kestros.cms.uiframeworks.api.models.ManagedUiFramework;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkRetrievalService;
import io.kestros.cms.uiframeworks.core.models.ManagedUiFrameworkResource;
import io.kestros.cms.uiframeworks.core.models.ThemeResource;
import io.kestros.cms.uiframeworks.core.models.UiFrameworkResource;
import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.commons.osgiserviceutils.services.BaseServiceResolverService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves {@link UiFrameworkResource} objects.
 */
@Component(immediate = true,
           service = UiFrameworkRetrievalService.class)
public class UiFrameworkRetrievalServiceImpl extends BaseServiceResolverService
    implements UiFrameworkRetrievalService, PerformanceService {

  private static final Logger LOG = LoggerFactory.getLogger(ThemeResource.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ResourceResolverFactory resourceResolverFactory;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private VersionService versionService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private transient PerformanceTrackerService performanceTrackerService;

  @Override
  public List<ManagedUiFramework> getAllManagedUiFrameworks(Boolean includeEtc,
      Boolean includeLibs) {
    List<ManagedUiFramework> managedUiFrameworkList = new ArrayList<>();

    if (includeEtc) {
      try {
        managedUiFrameworkList.addAll(
            getChildrenOfType(getEtcRootResource(), ManagedUiFrameworkResource.class));
      } catch (ResourceNotFoundException e) {
        LOG.warn("Failed to find {} resource while building for ManagedUiFramework list.",
            "/etc/ui-frameworks");
      }
    }
    if (includeLibs) {
      try {
        managedUiFrameworkList.addAll(
            getChildrenOfType(getLibsRootResource(), ManagedUiFrameworkResource.class));
      } catch (ResourceNotFoundException e) {
        LOG.warn("Failed to find {} resource while building for ManagedUiFramework list.",
            "/libs/kestros/ui-frameworks");
      }
    }
    return managedUiFrameworkList;
  }

  @Override
  public List<UiFramework> getAllUnmanagedUiFrameworks(Boolean includeEtc, Boolean includeLibs) {
    List<UiFramework> uiFrameworkList = new ArrayList<>();
    if (includeEtc) {
      try {
        uiFrameworkList.addAll(getChildrenOfType(getEtcRootResource(), UiFrameworkResource.class));
      } catch (ResourceNotFoundException e) {
        LOG.warn("Failed to find {} resource while building for UiFramework list.",
            "/etc/ui-frameworks");
      }
    }
    if (includeLibs) {
      try {
        uiFrameworkList.addAll(getChildrenOfType(getLibsRootResource(), UiFrameworkResource.class));
      } catch (ResourceNotFoundException e) {
        LOG.warn("Failed to find {} resource while building for UiFramework list.",
            "/libs/kestros/ui-frameworks");
      }
    }
    return uiFrameworkList;
  }

  @Override
  public List<UiFramework> getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(
      Boolean includeEtc, Boolean includeLibs) {
    List<UiFramework> uiFrameworkList = new ArrayList<>();
    for (ManagedUiFramework managedUiFramework : getAllManagedUiFrameworks(includeEtc,
        includeLibs)) {
      uiFrameworkList.addAll(managedUiFramework.getVersions());
    }
    uiFrameworkList.addAll(getAllUnmanagedUiFrameworks(includeEtc, includeLibs));
    return uiFrameworkList;
  }

  @Override
  public UiFramework getUiFramework(Theme theme) throws UiFrameworkRetrievalException {
    if (theme instanceof ThemeResource) {
      Resource themeResource = ((ThemeResource) theme).getResource();
      try {
        return getFirstAncestorOfType(themeResource, UiFrameworkResource.class);
      } catch (NoValidAncestorException e) {
        throw new UiFrameworkRetrievalException(theme);
      }
    } else {
      throw new UiFrameworkRetrievalException(theme);
    }
  }

  @Override
  public UiFramework getUiFramework(String path) throws UiFrameworkRetrievalException {
    try {
      return getResourceAsType(path, getServiceResourceResolver(), UiFrameworkResource.class);
    } catch (ModelAdaptionException e) {
      throw new UiFrameworkRetrievalException(path, e.getMessage());
    }
  }

  @Override
  @Nonnull
  public UiFramework getUiFrameworkByCode(@Nonnull String code, @Nonnull Boolean includeEtc,
      @Nonnull Boolean includeLibs, @Nonnull String version) throws UiFrameworkRetrievalException {
    if (versionService != null) {
      for (ManagedUiFramework managedUiFramework : this.getAllManagedUiFrameworks(includeEtc,
          includeLibs)) {
        if (code.equals(managedUiFramework.getFrameworkCode())) {
          try {
            return versionService.getClosestVersion(managedUiFramework, version);
          } catch (VersionRetrievalException e) {
            throw new UiFrameworkRetrievalException(
                String.format("UiFramework found, but had no versions earlier than %s", version));
          }
        }
      }
    }

    for (UiFramework uiFramework : getAllUnmanagedUiFrameworks(includeEtc, includeLibs)) {
      if (code.equals(uiFramework.getFrameworkCode())) {
        return uiFramework;
      }
    }
    throw new UiFrameworkRetrievalException(code);
  }

  @Override
  public ManagedUiFramework getManagedUiFramework(String path)
      throws UiFrameworkRetrievalException {
    try {
      return getResourceAsType(path, getServiceResourceResolver(),
          ManagedUiFrameworkResource.class);
    } catch (ModelAdaptionException e) {
      throw new UiFrameworkRetrievalException(path, e.getMessage());
    }
  }

  @Override
  protected String getServiceUserName() {
    return "ui-framework-retrieval";
  }

  @Override
  protected List<String> getRequiredResourcePaths() {
    return Arrays.asList("/etc/ui-frameworks", "/libs/kestros/ui-frameworks");
  }

  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

  @Override
  public String getDisplayName() {
    return "UI Framework Retrieval Service";
  }

  @Override
  public void deactivate(ComponentContext componentContext) {
    LOG.info("Deactivating {}.", getClass().getSimpleName());
  }

  private BaseResource getEtcRootResource() throws ResourceNotFoundException {
    return getResourceAsBaseResource("/etc/ui-frameworks", getServiceResourceResolver());
  }

  private BaseResource getLibsRootResource() throws ResourceNotFoundException {
    return getResourceAsBaseResource("/libs/kestros/ui-frameworks", getServiceResourceResolver());
  }

  @Override
  public PerformanceTrackerService getPerformanceTrackerService() {
    return performanceTrackerService;
  }
}
