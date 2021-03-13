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

package io.kestros.cms.componenttypes.core.models;

import static org.apache.jackrabbit.vault.util.JcrConstants.JCR_TITLE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.componenttypes.api.exceptions.ComponentTypeRetrievalException;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.componenttypes.api.services.ComponentTypeRetrievalService;
import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewRetrievalService;
import io.kestros.cms.componenttypes.api.services.ComponentVariationRetrievalService;
import io.kestros.cms.uiframeworks.api.exceptions.UiFrameworkRetrievalException;
import io.kestros.cms.uiframeworks.api.exceptions.VendorLibraryRetrievalException;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.models.VendorLibrary;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkRetrievalService;
import io.kestros.cms.uiframeworks.api.services.VendorLibraryRetrievalService;
import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.uilibraries.core.UiLibraryResource;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component view that is specific to a single UiFramework.  Created as a child resource to
 * ComponentTypes.
 */
@KestrosModel(docPaths = {
    "/content/guide-articles/kestros-cms/site-building/implementing-ui-framework" + "-views",
    "/content/guide-articles/kestros-cms/site-building/creating-new-component" + "-types",
    "/content/guide-articles/kestros-cms/site-building/creating-ui-frameworks"})
@Model(adaptables = Resource.class)
public class ComponentUiFrameworkViewResource extends UiLibraryResource
    implements ComponentUiFrameworkView {

  private static final Logger LOG = LoggerFactory.getLogger(ComponentUiFrameworkViewResource.class);

  @OSGiService
  @Optional
  private VersionService versionService;

  @OSGiService
  @Optional
  private ComponentUiFrameworkViewRetrievalService componentUiFrameworkViewRetrievalService;

  @OSGiService
  @Optional
  private ComponentTypeRetrievalService componentTypeRetrievalService;

  @OSGiService
  @Optional
  private ComponentVariationRetrievalService componentVariationRetrievalService;

  @OSGiService
  @Optional
  private UiFrameworkRetrievalService uiFrameworkRetrievalService;

  @OSGiService
  @Optional
  private VendorLibraryRetrievalService vendorLibraryRetrievalService;

  @Override
  public String getTitle() {
    try {
      return getProperty(JCR_TITLE, getUiFramework().getTitle());
    } catch (ResourceNotFoundException e) {
      LOG.debug("UiFramework not found for Component View {} while retrieving title.", getPath());
    }
    return super.getTitle();
  }

  /**
   * Parent ComponentType.
   *
   * @return Parent ComponentType.
   * @throws ComponentTypeRetrievalException Parent ComponentType could not be found.
   */
  @JsonIgnore
  @Nonnull
  public ComponentType getComponentType() throws ComponentTypeRetrievalException {
    return componentTypeRetrievalService.getComponentType(this);
  }

  //  /**
  //   * Retrieves the specified script.
  //   *
  //   * @param scriptName Script to retrieve.
  //   * @return The specified script
  //   * @throws InvalidScriptException The specified script could not be found, or failed to
  //   *     adapted to HtmlFile.
  //   */
  //  public HtmlFile getUiFrameworkViewScript(final String scriptName) throws
  //  InvalidScriptException {
  //    try {
  //      return getChildAsFileType(scriptName, this, HtmlFile.class);
  //    } catch (final ModelAdaptionException exception) {
  //      LOG.trace(exception.getMessage());
  //      throw new InvalidScriptException(scriptName, getPath());
  //    }
  //  }

  //  /**
  //   * All Variations that descend from the current ComponentUiFrameworkView.
  //   *
  //   * @return All Variations that descend from the current ComponentUiFrameworkView.
  //   */
  //  @Nonnull
  //  public List<ComponentVariation> getVariations() {
  //    List<ComponentVariation> variationList = new ArrayList<>();
  //    if (isInheritVariations()) {
  //      try {
  //        ComponentUiFrameworkView superComponentView
  //            = componentUiFrameworkViewRetrievalService.getComponentUiFrameworkView(
  //            getComponentType().getComponentSuperType(), getUiFramework());
  //        variationList.addAll(
  //            componentVariationRetrievalService.getComponentVariations(superComponentView));
  //      } catch (ModelAdaptionException e) {
  //        // todo log.
  //      }
  //    }
  //    try {
  //      variationList.addAll(getChildrenOfType(getComponentVariationsRootResource(),
  //          ComponentVariationResource.class));
  //    } catch (final ModelAdaptionException exception) {
  //      LOG.debug("Unable to find Variations for {} due to missing {} resource.", getPath(),
  //          DesignConstants.NN_VARIATIONS);
  //    }
  //    return variationList;
  //  }

  //  /**
  //   * The raw output for a given ScriptType.
  //   *
  //   * @param scriptType ScriptType to retrieve.
  //   * @return The raw output for a given ScriptType.
  //   */
  //  @Override
  //  public String getOutput(final ScriptType scriptType, final boolean minify)
  //      throws InvalidResourceTypeException {
  //    final StringBuilder output = new StringBuilder();
  //
  //    output.append(super.getOutput(scriptType, false));
  //
  //    for (final ComponentVariationResource variation : getVariations()) {
  //      if (StringUtils.isNotEmpty(output.toString())) {
  //        output.append("\n");
  //      }
  //      output.append(variation.getOutput(scriptType, false));
  //    }
  //
  //    return output.toString();
  //  }

  @Override
  public String getTemplatesPath() {
    UiFramework uiFramework = null;
    try {
      uiFramework = getUiFramework();
    } catch (ResourceNotFoundException e) {
      e.printStackTrace();
    }
    if (uiFramework != null) {
      return uiFramework.getTemplatesPath();
    } else if (vendorLibraryRetrievalService != null) {
      try {
        String vendorLibraryPath = getPath().split(
            getComponentType().getName().replaceFirst("/libs/", "").replaceFirst("/apps/", ""))[1];
        VendorLibrary vendorLibrary = vendorLibraryRetrievalService.getVendorLibrary(
            vendorLibraryPath);
        return vendorLibrary.getTemplatesPath();
      } catch (ComponentTypeRetrievalException e) {
        e.printStackTrace();
      } catch (VendorLibraryRetrievalException e) {
        e.printStackTrace();
      } catch (VersionRetrievalException e) {
        e.printStackTrace();
      }
    }
    return StringUtils.EMPTY;
  }

  @Override
  public UiFramework getUiFramework() throws ResourceNotFoundException {
    String version = "";
    try {
      if (getVersion() != null) {
        version = getVersion().getFormatted();
      }
    } catch (VersionFormatException e) {
      LOG.error(e.getMessage());
    }
    try {
      Resource parentResource = getResource().getParent();
      if (parentResource != null && "versions".equals(parentResource.getName())) {
        Resource viewResource = parentResource.getParent();
        if (viewResource != null) {
          ManagedComponentUiFrameworkViewResource managingRootResource = viewResource.adaptTo(
              ManagedComponentUiFrameworkViewResource.class);
          if (managingRootResource != null) {
            return uiFrameworkRetrievalService.getUiFrameworkByCode(managingRootResource.getName(),
                true, true, version);
          }
        }
      }
      return uiFrameworkRetrievalService.getUiFrameworkByCode(getName(), true, true, version);
    } catch (UiFrameworkRetrievalException e) {
      LOG.error(e.getMessage());
    }
    return null;
  }

  /**
   * Whether the view should inherit variations from the matching view on the super typed
   * ComponentType.
   *
   * @return Whether the view should inherit variations from the matching view on the super typed
   *     ComponentType.
   */
  @KestrosProperty(description = "Whether the view should inherit variations from the matching "
                                 + "view on the super typed ComponentType.",
                   configurable = true,
                   jcrPropertyName = "inheritVariations",
                   defaultValue = "true",
                   sampleValue = "true")
  public boolean isInheritVariations() {
    return getProperty("inheritVariations", Boolean.TRUE);
  }

  /**
   * Font Awesome Icon class.
   *
   * @return Font Awesome Icon class.
   */
  public String getFontAwesomeIcon() {
    String icon = getProperty("fontAwesomeIcon", StringUtils.EMPTY);
    try {
      if (StringUtils.isEmpty(icon)) {
        icon = getUiFramework().getFontAwesomeIcon();
      }
    } catch (ResourceNotFoundException e) {
      LOG.debug("UiFramework not found for Component View {} while retrieving font awesome icon.",
          getPath());
    }
    return icon;
  }

  @Override
  public Class getManagingResourceType() {
    return ManagedComponentUiFrameworkViewResource.class;
  }
}
