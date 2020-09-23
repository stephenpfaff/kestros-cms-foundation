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

package io.kestros.cms.foundation.utils;

import static io.kestros.cms.foundation.design.DesignConstants.UI_FRAMEWORKS_ETC_ROOT_PATH;
import static io.kestros.cms.foundation.design.DesignConstants.UI_FRAMEWORKS_LIBS_ROOT_PATH;
import static io.kestros.cms.foundation.design.DesignConstants.VENDOR_LIBRARIES_ETC_ROOT_PATH;
import static io.kestros.cms.foundation.design.DesignConstants.VENDOR_LIBRARIES_LIBS_ROOT_PATH;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;

import io.kestros.cms.foundation.componenttypes.ComponentType;
import io.kestros.cms.foundation.componenttypes.frameworkview.ComponentUiFrameworkView;
import io.kestros.cms.foundation.design.htltemplate.HtlTemplate;
import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.cms.foundation.design.vendorlibrary.VendorLibrary;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for retrieving UiFrameworks and VendorLibraries.
 */
public class DesignUtils {

  private static final Logger LOG = LoggerFactory.getLogger(DesignUtils.class);
  public static final String LIBS_KESTROS_ROOT_PATH = "/libs/kestros";
  public static final String PATH_PREFIX_LIBS = "/libs/";
  public static final String PATH_PREFIX_APPS = "/apps/";

  private DesignUtils() {
  }

  /**
   * Retrieves vendor library root resource.
   *
   * @param uiFramework UiFramework to retrieve the Vendor Libraries root resource for, or null
   *     to retrieve /etc/vendor-libraries.
   * @param resolver Resource Resolver
   * @return VendorLibraries root Resource.
   * @throws ResourceNotFoundException Vendor Libraries root Resource not found in JCR.
   */
  @Nonnull
  public static BaseResource getVendorLibrariesRootResourceForUiFramework(
      @Nullable final UiFramework uiFramework, final ResourceResolver resolver)
      throws ResourceNotFoundException {
    if (uiFramework != null && uiFramework.getPath().startsWith(LIBS_KESTROS_ROOT_PATH)) {
      return getResourceAsBaseResource(VENDOR_LIBRARIES_LIBS_ROOT_PATH, resolver);
    } else {
      return getResourceAsBaseResource(VENDOR_LIBRARIES_ETC_ROOT_PATH, resolver);
    }
  }

  /**
   * Retrieves /etc/kestros/vendor-libraries resource.
   *
   * @param resourceResolver ResourceResolver.
   * @return /etc/kestros/vendor-libraries resource.
   * @throws ResourceNotFoundException Resource not found.
   */
  @Nonnull
  public static BaseResource getVendorLibrariesEtcRootResource(
      @Nonnull final ResourceResolver resourceResolver) throws ResourceNotFoundException {
    return getResourceAsBaseResource(VENDOR_LIBRARIES_ETC_ROOT_PATH, resourceResolver);
  }

  /**
   * Retrieves /libs/kestros/vendor-libraries resource.
   *
   * @param resourceResolver ResourceResolver.
   * @return /libs/kestros/vendor-libraries resource.
   * @throws ResourceNotFoundException Resource not found.
   */
  @Nonnull
  public static BaseResource getVendorLibrariesLibsRootResource(
      @Nonnull final ResourceResolver resourceResolver) throws ResourceNotFoundException {
    return getResourceAsBaseResource(VENDOR_LIBRARIES_LIBS_ROOT_PATH, resourceResolver);
  }

  /**
   * Retrieves all VendorLibraries from specified paths (/etc and /libs).
   *
   * @param resolver Resource Resolver
   * @param includeEtcVendorLibraries Include VendorLibraries from /etc.
   * @param includeLibsVendorLibraries Include VendorLibraries from /libs.
   * @return All VendorLibraries.
   * @throws ResourceNotFoundException VendorLibraries root Resource not found in JCR.
   */
  @Nonnull
  public static List<VendorLibrary> getAllVendorLibraries(final ResourceResolver resolver,
      final Boolean includeEtcVendorLibraries, final Boolean includeLibsVendorLibraries)
      throws ResourceNotFoundException {
    final List<VendorLibrary> vendorLibraries = new ArrayList<>();
    if (includeEtcVendorLibraries) {
      vendorLibraries.addAll(
          getChildrenOfType(getVendorLibrariesEtcRootResource(resolver), VendorLibrary.class));
    }
    if (includeLibsVendorLibraries) {
      vendorLibraries.addAll(
          getChildrenOfType(getVendorLibrariesLibsRootResource(resolver), VendorLibrary.class));
    }
    return vendorLibraries;
  }

  /**
   * UiFrameworks root Resource under /etc.
   *
   * @param resolver Resource Resolver
   * @return UiFrameworks root Resource under /etc.
   * @throws ResourceNotFoundException UiFrameworks root Resource not found in JCR.
   */
  @Nonnull
  public static BaseResource getUiFrameworksEtcRootResource(final ResourceResolver resolver)
      throws ResourceNotFoundException {
    return getResourceAsBaseResource(UI_FRAMEWORKS_ETC_ROOT_PATH, resolver);
  }

  /**
   * UiFrameworks root resource under /libs.
   *
   * @param resolver Resource Resolver
   * @return UiFrameworks root Resource.
   * @throws ResourceNotFoundException UiFrameworks root Resource not found in JCR.
   */
  @Nonnull
  public static BaseResource getUiFrameworksLibsRootResource(final ResourceResolver resolver)
      throws ResourceNotFoundException {
    return getResourceAsBaseResource(UI_FRAMEWORKS_LIBS_ROOT_PATH, resolver);
  }

  /**
   * Retrieves all UiFrameworks from allowed paths (/etc and libs).
   *
   * @param resolver Resource Resolver
   * @param includeEtc Include UiFrameworks from /etc.
   * @param includeLibs Include UiFrameworks from /libs.
   * @return All UiFrameworks.
   */
  @Nonnull
  public static List<UiFramework> getAllUiFrameworks(final ResourceResolver resolver,
      final Boolean includeEtc, final Boolean includeLibs) {
    final List<UiFramework> uiFrameworks = new ArrayList<>();
    if (includeEtc) {
      try {
        uiFrameworks.addAll(
            getChildrenOfType(getUiFrameworksEtcRootResource(resolver), UiFramework.class));
      } catch (final ResourceNotFoundException e) {
        LOG.error("Failed to build list of UiFrameworks under /etc. {}", e.getMessage());
      }
    }
    if (includeLibs) {
      try {
        uiFrameworks.addAll(
            getChildrenOfType(getUiFrameworksLibsRootResource(resolver), UiFramework.class));
      } catch (final ResourceNotFoundException exception) {
        LOG.error("Failed to build list of UiFrameworks under /libs. {}", exception.getMessage());
      }
    }
    return uiFrameworks;
  }

  /**
   * Retrieves the specified UiFramework under /etc or /libs. /etc will take priority if a
   * UiFramework with the same name exists under both (and both are included in the lookup).
   *
   * @param name framework name.
   * @param includeEtc whether to include frameworks from /etc.
   * @param includeLibs whether to include frameworks from /libs.
   * @param resourceResolver Resource Resolver.
   * @return Specified UiFramework.
   * @throws ResourceNotFoundException Specified UiFramework could not be found.
   */
  @Nonnull
  public static UiFramework getUiFramework(@Nonnull final String name, final Boolean includeEtc,
      final Boolean includeLibs, @Nonnull final ResourceResolver resourceResolver)
      throws ResourceNotFoundException {

    if (includeEtc) {
      try {
        return getChildAsType(name, getUiFrameworksEtcRootResource(resourceResolver),
            UiFramework.class);
      } catch (final ResourceNotFoundException | InvalidResourceTypeException exception) {
        LOG.error("Unable to retrieve UiFramework '{}' under /etc. {}", name,
            exception.getMessage());
      } catch (final ChildResourceNotFoundException exception) {
        LOG.debug("Unable to retrieve UiFramework '{}' under /etc. {}", name,
            exception.getMessage());
      }
    }
    if (includeLibs) {
      try {
        return getChildAsType(name, getUiFrameworksLibsRootResource(resourceResolver),
            UiFramework.class);
      } catch (final ResourceNotFoundException | InvalidResourceTypeException exception) {
        LOG.error("Unable to retrieve UiFramework '{}' under /libs. {}", name,
            exception.getMessage());
      } catch (final ChildResourceNotFoundException exception) {
        LOG.debug("Unable to retrieve UiFramework '{}' under /libs. {}", name,
            exception.getMessage());
      }

    }
    throw new ResourceNotFoundException(name,
        String.format("Unable to find UiFramework '%s'.", name));
  }

  /**
   * Retrieves UiFramework by frameworkCode.
   *
   * @param code UiFramework code.
   * @param includeEtc whether to include frameworks from apps.
   * @param includeLibs whether to include frameworks from libs.
   * @param resourceResolver Resource Resolver.
   * @return Specified UiFramework.
   * @throws ResourceNotFoundException Specified UiFramework could not be found.
   */
  public static UiFramework getUiFrameworkByFrameworkCode(@Nonnull final String code,
      final Boolean includeEtc, final Boolean includeLibs,
      @Nonnull final ResourceResolver resourceResolver) throws ResourceNotFoundException {

    for (final UiFramework uiFramework : getAllUiFrameworks(resourceResolver, includeEtc,
        includeLibs)) {
      if (uiFramework.getFrameworkCode().equals(code)) {
        return uiFramework;
      }
    }
    throw new ResourceNotFoundException(code,
        String.format("Unable to find UiFramework matching code '%s'.", code));
  }

  /**
   * Retrieves ComponentUiFramework matching `name` from the specified ComponentType.
   *
   * @param name ComponentUiFramework name to retrieve.
   * @param componentType ComponentType to retrieve ComponentUiFrameworkView from.
   * @return ComponentUiFramework matching `name` from the specified ComponentType.
   * @throws ChildResourceNotFoundException ComponentUiFrameworkView matching specified name
   *     could not be found.
   */
  @Nonnull
  public static ComponentUiFrameworkView getComponentUiFrameworkView(@Nonnull final String name,
      @Nonnull final ComponentType componentType) throws ChildResourceNotFoundException {
    ComponentUiFrameworkView componentUiFrameworkView = null;
    if (componentType.getPath().startsWith(PATH_PREFIX_LIBS)) {
      try {
        final BaseResource appsComponentTypeResource = getResourceAsBaseResource(
            componentType.getPath().replaceFirst(PATH_PREFIX_LIBS, PATH_PREFIX_APPS),
            componentType.getResourceResolver());
        componentUiFrameworkView = getChildAsBaseResource(name,
            appsComponentTypeResource).getResource().adaptTo(ComponentUiFrameworkView.class);
      } catch (final ResourceNotFoundException | ChildResourceNotFoundException exception) {
        componentUiFrameworkView = getChildAsBaseResource(name,
            componentType).getResource().adaptTo(ComponentUiFrameworkView.class);
      }
    } else if (componentType.getPath().startsWith(PATH_PREFIX_APPS)) {
      try {
        componentUiFrameworkView = getChildAsBaseResource(name,
            componentType).getResource().adaptTo(ComponentUiFrameworkView.class);
      } catch (final ChildResourceNotFoundException e) {
        try {
          final BaseResource libsComponentTypeResource = getResourceAsBaseResource(
              componentType.getPath().replaceFirst(PATH_PREFIX_APPS, PATH_PREFIX_LIBS),
              componentType.getResourceResolver());
          componentUiFrameworkView = getChildAsBaseResource(name,
              libsComponentTypeResource).getResource().adaptTo(ComponentUiFrameworkView.class);
        } catch (final ResourceNotFoundException ex) {
          LOG.debug(ex.getMessage());
        } catch (final ChildResourceNotFoundException exception) {
          componentUiFrameworkView = getChildAsBaseResource(name,
              componentType).getResource().adaptTo(ComponentUiFrameworkView.class);
        }
      }
    } else {
      componentUiFrameworkView = getChildAsBaseResource(name, componentType).getResource().adaptTo(
          ComponentUiFrameworkView.class);
    }
    if (componentUiFrameworkView != null) {
      return componentUiFrameworkView;
    }

    throw new ChildResourceNotFoundException(name, componentType.getPath(), "Child not found.");
  }

  /**
   * Retrieves a specified HTL Template from a UiFramework.
   *
   * @param uiFramework UiFramework to retrieve template from.
   * @param templateName Name of template to look up.
   * @return A specified HTL Template from a UiFramework.
   */
  @Nullable
  public static HtlTemplate getHtlTemplateFromUiFramework(@Nonnull UiFramework uiFramework,
      @Nonnull String templateName) {
    for (HtlTemplate htlTemplate : uiFramework.getTemplates()) {
      if (htlTemplate.getName().equalsIgnoreCase(templateName)) {
        return htlTemplate;
      }
    }
    // TODO throw exception?
    return null;
  }
}