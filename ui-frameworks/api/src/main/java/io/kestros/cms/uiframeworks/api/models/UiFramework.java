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

package io.kestros.cms.uiframeworks.api.models;

import static io.kestros.cms.uiframeworks.api.DesignConstants.NN_THEMES;
import static io.kestros.cms.uiframeworks.api.DesignConstants.PN_UI_FRAMEWORK_CODE;
import static io.kestros.cms.uiframeworks.api.DesignConstants.PN_VENDOR_LIBRARIES;
import static io.kestros.cms.uiframeworks.api.utils.DesignUtils.getVendorLibrariesRootResourceForUiFramework;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourcesAsType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.modeltypes.IconResource;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateCacheService;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkOutputCompilationService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
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
 * <p>
 * Structured UiLibrary, which determines the HTML a requested Component will render.
 * </p>
 * <p>
 * Compiles CSS, JS and HTL templates from: Vendor Libraries, ComponentUiFrameworkViews, and
 * itself.
 * </p>
 */
@KestrosModel(docPaths = {"/content/guide-articles/kestros/ui-frameworks/ui-frameworks",
    "/content/guide-articles/kestros/ui-frameworks/themes",
    "/content/guide-articles/kestros/ui-frameworks/assigning-htl-templates",
    "/content/guide-articles/kestros/ui-frameworks/vendor-libraries"})
@Model(adaptables = Resource.class,
       resourceType = "kes:UiFramework")
@Exporter(name = "jackson",
          selector = "ui-framework",
          extensions = "json")
public class UiFramework extends UiLibrary implements IconResource {

  private static final Logger LOG = LoggerFactory.getLogger(UiFramework.class);
  public static final String EXTENSION_HTML = ".html";

  private static final String PN_EXTERNALIZED_FILES = "externalizedFiles";

  @OSGiService
  @Optional
  private HtlTemplateCacheService htlTemplateCacheService;

  @OSGiService
  @Optional
  private UiFrameworkOutputCompilationService uiFrameworkOutputCompilationService;

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
  @JsonIgnore
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
  @JsonIgnore
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
    if (uiFrameworkOutputCompilationService != null) {
      return uiFrameworkOutputCompilationService.getUiFrameworkOutput(this, scriptType);
    }
    return StringUtils.EMPTY;
  }

  @Override
  public String getOutput(final ScriptType scriptType, boolean minify)
      throws InvalidResourceTypeException {
    return getOutput(scriptType);
  }

  /**
   * Path to HTL compiled templates file. Looks up value based on HtlTemplateCacheService.
   *
   * @return Path to HTL compiled templates file.
   * @throws ResourceNotFoundException Compiled HTL Templates file could not be found.
   */
  public String getTemplatesPath() throws ResourceNotFoundException {
    if (htlTemplateCacheService != null) {
      return htlTemplateCacheService.getCompiledTemplateFilePath(this);
    }
    return StringUtils.EMPTY;
  }

  /**
   * Vendor Libraries that are to be compiled.
   *
   * @return Vendor Libraries that are to be compiled.
   */
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
   * List of template files associated to the current UiFramework.
   *
   * @return List of template files associated to the current UiFramework.
   */
  @Nonnull
  @JsonIgnore
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
   * List of CDN JavaScript script file URL that need to be included as their own script files.
   *
   * @return List of CDN JavaScript script file URL that need to be included as their own script
   *     files.
   */
  public List<String> getIncludedCdnJsScripts() {
    List<String> includedCdnJsScripts = new ArrayList<>();
    for (VendorLibrary vendorLibrary : getVendorLibraries()) {
      includedCdnJsScripts.addAll(vendorLibrary.getIncludedCdnJsScripts());
    }
    includedCdnJsScripts.addAll(Arrays.asList(getProperty("includedCdnJsScripts", new String[]{})));
    return includedCdnJsScripts;
  }

  /**
   * List of CDN CSS script file URL that need to be included as their own script files.
   *
   * @return List of CDN CSS script file URL that need to be included as their own script files.
   */
  public List<String> getIncludedCdnCssScripts() {
    List<String> includedCdnCssScripts = new ArrayList<>();
    for (VendorLibrary vendorLibrary : getVendorLibraries()) {
      includedCdnCssScripts.addAll(vendorLibrary.getIncludedCdnCssScripts());
    }
    includedCdnCssScripts.addAll(
        Arrays.asList(getProperty("includedCdnCssScripts", new String[]{})));
    return includedCdnCssScripts;
  }

  /**
   * List of files that should be externalized (fonts, images, etc).
   *
   * @return List of files that should be externalized (fonts, images, etc).
   */
  @Nonnull
  public List<BaseResource> getExternalizedFiles() {
    List<BaseResource> externalizedFiles = new ArrayList<>();
    for (VendorLibrary vendorLibrary : getVendorLibraries()) {
      externalizedFiles.addAll(vendorLibrary.getExternalizedFiles());
    }

    externalizedFiles.addAll(
        getResourcesAsType(getExternalizedFilesProperty(), getResourceResolver(),
            BaseResource.class));

    return externalizedFiles;
  }

  /**
   * Font Awesome Icon class.
   *
   * @return Font Awesome Icon class.
   */
  @Override
  @JsonIgnore
  @KestrosProperty(description = "Font awesome icon class, used in the Kestros Site Admin UI",
                   jcrPropertyName = "fontAwesomeIcon",
                   defaultValue = "fas fa-palette",
                   configurable = true,
                   sampleValue = "fas fa-palette")
  public String getFontAwesomeIcon() {
    return getProperty("fontAwesomeIcon", "fas fa-palette");
  }


  private List<String> getExternalizedFilesProperty() {
    return Arrays.asList(getProperties().get(PN_EXTERNALIZED_FILES, new String[]{}));
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
