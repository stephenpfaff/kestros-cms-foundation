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

package io.kestros.cms.foundation.design.uiframework;

import static io.kestros.cms.foundation.design.DesignConstants.NN_THEMES;
import static io.kestros.cms.foundation.design.DesignConstants.PN_UI_FRAMEWORK_CODE;
import static io.kestros.cms.foundation.design.DesignConstants.PN_VENDOR_LIBRARIES;
import static io.kestros.cms.foundation.utils.DesignUtils.getVendorLibrariesRootResourceForUiFramework;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getAllDescendantsOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import io.kestros.cms.foundation.componenttypes.ComponentType;
import io.kestros.cms.foundation.componenttypes.frameworkview.ComponentUiFrameworkView;
import io.kestros.cms.foundation.design.htltemplate.HtlTemplate;
import io.kestros.cms.foundation.design.htltemplate.HtlTemplateFile;
import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.cms.foundation.design.vendorlibrary.VendorLibrary;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.cms.foundation.services.cache.htltemplate.HtlTemplateCacheService;
import io.kestros.cms.foundation.services.componenttypecache.ComponentTypeCache;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Structured UiLibrary, which compiles CSS and JS script from VendorLibraries, script that belong
 * to it, and ComponentUiFrameworkViews that match its framework code.
 */
@KestrosModel(validationService = UiFrameworkValidationService.class,
              docPaths = {"/content/guide-articles/kestros/ui-frameworks/create-a-new-ui-framework",
                  "/content/guide-articles/kestros/ui-frameworks/create-a-new-vendor-library",
                  "/content/guide-articles/kestros/ui-frameworks/creating-themes"})
@Model(adaptables = Resource.class,
       resourceType = "kes:UiFramework")
@Exporter(name = "jackson",
          selector = "ui-framework",
          extensions = "json")
public class UiFramework extends UiLibrary {

  private static final Logger LOG = LoggerFactory.getLogger(UiFramework.class);
  public static final String EXTENSION_HTML = ".html";

  @OSGiService
  @Optional
  HtlTemplateCacheService htlTemplateCacheService;

  @OSGiService
  @Optional
  private ComponentTypeCache componentTypeCache;

  /**
   * Unique code associated with the current UiFramework. ComponentTypes use this to render the
   * proper content script.
   *
   * @return Unique code associated with the current UiFramework.
   */
  @Nonnull
  @KestrosProperty(description =
                       "Unique code associated with the current UiFramework. ComponentTypes "
                       + "use this to render the proper content script.",
                   jcrPropertyName = PN_UI_FRAMEWORK_CODE,
                   defaultValue = "common",
                   configurable = true)
  public String getFrameworkCode() {
    return getProperties().get(PN_UI_FRAMEWORK_CODE, "common");
  }

  /**
   * All Vendor Libraries compiled into the current UiFramework.
   *
   * @return All Vendor Libraries compiled into the current UiFramework.
   */
  @Nonnull
  @KestrosProperty(description = "All Vendor Libraries that are to be compiled into the "
                                 + "UiFramework",
                   defaultValue = "[]")
  public List<VendorLibrary> getVendorLibraries() {
    final List<VendorLibrary> vendorLibraries = new ArrayList<>();
    for (final String vendorLibraryName : getIncludedVendorLibraryNames()) {
      try {
        vendorLibraries.add(getChildAsType(vendorLibraryName,
            getVendorLibrariesRootResourceForUiFramework(this, getResourceResolver()),
            VendorLibrary.class));
      } catch (final ResourceNotFoundException exception) {
        LOG.error("Unable to get Vendor Library '{}'.  Vendor Libraries root Resource not found.",
            getPath());
      } catch (final InvalidResourceTypeException exception) {
        LOG.warn(
            "Unable to get Vendor Library '{}'.  Resource was found, but could not be adapted to "
            + "a VendorLibrary.", getPath());
      } catch (final ChildResourceNotFoundException exception) {
        LOG.warn("Unable to get Vendor Library '{}'.  Resource not found.", getPath());
      }
    }

    return vendorLibraries;
  }

  /**
   * List of all child Themes.
   *
   * @return List of all child Themes.
   */
  @Nonnull
  public List<Theme> getThemes() {
    try {
      return getChildrenOfType(getThemeRootResource(), Theme.class);
    } catch (final ChildResourceNotFoundException exception) {
      return Collections.emptyList();
    }
  }

  /**
   * Retrieves a specified child Theme.
   *
   * @param name Theme name.
   * @return A specified child Theme.
   * @throws ChildResourceNotFoundException Theme matching specified name could not be found.
   * @throws InvalidResourceTypeException Resource matching the specified name was found, but
   *     could not be adapted to a Theme.
   */
  public Theme getTheme(@Nonnull final String name)
      throws ChildResourceNotFoundException, InvalidResourceTypeException {
    return getChildAsType(name, getThemeRootResource(), Theme.class);
  }

  /**
   * The UiFramework's default Theme. Should be named 'default'.
   *
   * @return The UiFramework's default Theme.
   * @throws InvalidThemeException Default theme was invalid.
   * @throws ChildResourceNotFoundException Default theme was not found.
   */
  @Nullable
  public Theme getDefaultTheme() throws InvalidThemeException, ChildResourceNotFoundException {
    try {
      return getChildAsType("default", getThemeRootResource(), Theme.class);
    } catch (final InvalidResourceTypeException exception) {
      LOG.error("Unable to retrieve default Theme for {}. {}", getPath(), exception.getMessage());
      throw new InvalidThemeException(getPath(), "default",
          "Could not adapt to Theme. Resource must have jcr:primaryType 'kes:Theme'.");
    }
  }

  /**
   * All HTL Templates associated to the current UiFramework.
   *
   * @return All HTL Templates associated to the current UiFramework.
   */
  @Nonnull
  public List<HtlTemplate> getTemplates() {
    final List<HtlTemplate> templates = new ArrayList<>();

    for (final HtlTemplateFile htlTemplateFile : getTemplateFiles()) {
      templates.addAll(htlTemplateFile.getTemplates());
    }

    return templates;
  }

  /**
   * CSS or JS output compiled from VendorLibraries, scripts that live under the current
   * UiFramework, and ComponentUiFramework views that match the current UiFramework's framework
   * code.
   *
   * @param scriptType scriptType (CSS or JS) to get.
   * @return CSS or JS output.
   * @throws InvalidResourceTypeException One of the dependency VendorLibraries, or
   *     ComponentUiFrameworkViews were invalid or missing.
   */
  @Nonnull
  public String getOutput(final ScriptType scriptType) throws InvalidResourceTypeException {
    final StringBuilder output = new StringBuilder();

    for (final VendorLibrary vendorLibrary : getVendorLibraries()) {
      output.append(vendorLibrary.getOutput(scriptType, false));
    }

    output.append(super.getOutput(scriptType, false));

    for (final ComponentUiFrameworkView componentUiFrameworkView : getComponentViews()) {
      output.append(componentUiFrameworkView.getOutput(scriptType, false));
    }

    return output.toString();
  }

  /**
   * Path to HTL compiled templates file. Looks up value based on HtlTemplateCacheService.
   *
   * @return Path to HTL compiled templates file.
   * @throws ResourceNotFoundException Compiled HTL Templates file could not be found.
   */
  public String getTemplatesPath() throws ResourceNotFoundException {
    if (htlTemplateCacheService != null) {
      try {
        return getResourceAsBaseResource(
            htlTemplateCacheService.getServiceCacheRootPath() + getPath() + EXTENSION_HTML,
            getResourceResolver()).getPath();
      } catch (final ResourceNotFoundException e) {
        try {
          htlTemplateCacheService.cacheAllUiFrameworkCompiledHtlTemplates();
          return getResourceAsBaseResource(
              htlTemplateCacheService.getServiceCacheRootPath() + getPath() + EXTENSION_HTML,
              getResourceResolver()).getPath();
        } catch (final CacheBuilderException ex) {
          throw new ResourceNotFoundException(
              htlTemplateCacheService.getServiceCacheRootPath() + getPath() + EXTENSION_HTML);
        }
      }
    }
    return StringUtils.EMPTY;
  }

  @Nonnull
  @KestrosProperty(description = "Vendor libraries to compile in the UiFramework.",
                   jcrPropertyName = PN_VENDOR_LIBRARIES,
                   defaultValue = "[]",
                   configurable = true)
  public List<String> getIncludedVendorLibraryNames() {
    return Arrays.asList(getProperties().get(PN_VENDOR_LIBRARIES, new String[]{}));
  }

  @Nonnull
  private BaseResource getThemeRootResource() throws ChildResourceNotFoundException {
    return getChildAsBaseResource(NN_THEMES, this);
  }

  /**
   * All ComponentUiFrameworkViews under /apps & /libs that implement the current UiFramework.
   *
   * @return All ComponentUiFrameworkViews under /apps & /libs that implement the current
   *     UiFramework.
   */
  @Nonnull
  private List<ComponentUiFrameworkView> getComponentViews() {
    final List<ComponentUiFrameworkView> componentUiFrameworkViews = new ArrayList<>(
        getAllComponentUiFrameworkViewsInADirectory("/apps"));
    componentUiFrameworkViews.addAll(
        getAllComponentUiFrameworkViewsInADirectory("/libs/kestros/components"));
    return componentUiFrameworkViews;
  }

  @Nonnull
  List<ComponentUiFrameworkView> getAllComponentUiFrameworkViewsInADirectory(
      final String componentPath) {
    final List<ComponentUiFrameworkView> componentUiFrameworkViews = new ArrayList<>();

    for (final ComponentType componentType : getAllComponentTypesInDirectory(componentPath)) {
      try {
        componentUiFrameworkViews.add(componentType.getComponentUiFrameworkView(this));
      } catch (final ModelAdaptionException exception) {
        LOG.debug(
            "Unable to retrieve view for {} for component {} due to missing or invalid Resource",
            getFrameworkCode(), componentType.getPath());
      }
    }
    return componentUiFrameworkViews;
  }

  @Nonnull
  private List<ComponentType> getAllComponentTypesInDirectory(@Nonnull final String path) {
    if (componentTypeCache != null
        && !componentTypeCache.getAllCachedComponentTypePaths().isEmpty()) {
      return SlingModelUtils.getResourcesAsType(componentTypeCache.getAllCachedComponentTypePaths(),
          getResourceResolver(), ComponentType.class);
    }
    try {
      final BaseResource root = getResourceAsType(path, getResourceResolver(), BaseResource.class);
      final List<ComponentType> componentTypeList = getAllDescendantsOfType(root,
          ComponentType.class);
      final List<String> componentTypePathList = new ArrayList<>();

      for (ComponentType componentType : componentTypeList) {
        componentTypePathList.add(componentType.getPath());
      }

      componentTypeCache.cacheComponentTypePathList(componentTypePathList);
      return componentTypeList;
    } catch (final ModelAdaptionException exception) {
      LOG.debug(
          "Unable to retrieve resource {} while getting all ComponentType for UiFramework {} due "
          + "to missing or invalid Resource.", path, getPath());
    }
    return Collections.emptyList();
  }

  /**
   * List of template files associated to the current UiFramework.
   *
   * @return List of template files associated to the current UiFramework.
   */
  @Nonnull
  public List<HtlTemplateFile> getTemplateFiles() {
    final List<HtlTemplateFile> templateFiles = new ArrayList<>();

    final BaseResource templatesFolder;

    for (final VendorLibrary vendorLibrary : getVendorLibraries()) {
      templateFiles.addAll(vendorLibrary.getTemplateFiles());
    }

    try {
      templatesFolder = getChildAsBaseResource("templates", this);
      templateFiles.addAll(getChildrenOfType(templatesFolder, HtlTemplateFile.class));
    } catch (final ChildResourceNotFoundException exception) {
      LOG.debug("Unable to get HTL Template Files for {}, template directory not found", getPath());
    }

    templateFiles.sort(new HtlTemplateFileSorter());

    return templateFiles;
  }

  /**
   * Sorts HtlTemplates by Title (alphabetically).
   */
  private static class HtlTemplateFileSorter implements Comparator<HtlTemplateFile>, Serializable {

    private static final long serialVersionUID = -365044729256904870L;

    @Override
    public int compare(final HtlTemplateFile o1, final HtlTemplateFile o2) {
      return o1.getTitle().compareTo(o2.getTitle());
    }
  }
}