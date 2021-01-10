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

package io.kestros.cms.foundation.content;

import static io.kestros.cms.uiframeworks.api.DesignConstants.NN_VARIATIONS;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentUiFrameworkViewException;
import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.componenttypes.api.models.ComponentVariation;
import io.kestros.cms.foundation.content.components.parentcomponent.ParentComponent;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.cms.foundation.content.sites.BaseSite;
import io.kestros.cms.foundation.services.ThemeProviderService;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidUiFrameworkException;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.structuredslingmodels.BaseRequestContext;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component request context.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class ComponentRequestContext extends BaseRequestContext {

  private static final Logger LOG = LoggerFactory.getLogger(ComponentRequestContext.class);

  @OSGiService
  private ThemeProviderService themeProviderService;

  private Theme theme;

  private UiFramework uiFramework;

  private ComponentUiFrameworkView componentUiFrameworkView;

  private List<ComponentVariation> appliedComponentVariations;


  /**
   * Requested page.
   *
   * @return requested page.
   */
  @KestrosProperty(description = "The requested page.")
  public BaseContentPage getCurrentPage() {
    String pagePath = getRequest().getRequestURI().split(".html")[0];
    if (pagePath.contains("/jcr:content")) {
      pagePath = pagePath.split("/jcr:content")[0];
    } else if (pagePath.contains("/_jcr_content")) {
      pagePath = pagePath.split("/_jcr_content")[0];
    }
    try {
      return getResourceAsType(pagePath, getResourceResolver(), BaseContentPage.class);
    } catch (InvalidResourceTypeException e) {
      try {
        return getResourceAsType(pagePath, getResourceResolver(), BaseSite.class);
      } catch (InvalidResourceTypeException invalidResourceTypeException) {
        LOG.debug("Unable to adapt current page resource to BaseContentPage or BaseSite for "
                  + "ComponentRequestContext.");
      } catch (ResourceNotFoundException resourceNotFoundException) {
        LOG.warn("Unable to find current page resource for ComponentRequestContext.");
      }
    } catch (ResourceNotFoundException e) {
      LOG.warn("Unable to find current page resource for ComponentRequestContext.");
    }

    try {
      BaseComponent component = SlingModelUtils.adaptTo(getRequest().getResource(),
          BaseComponent.class);

      return component.getContainingPage();
    } catch (ModelAdaptionException e) {
      LOG.warn("Unable to find current page resource for NavigationContext. {}", e.getMessage());
    }
    return null;
  }


  /**
   * Applied inline variation CSS classes. Will need to be implemented with a component's
   * content.html script.
   *
   * @return Applied inline variation CSS classes
   */
  @KestrosProperty(description = "All applied inline variation CSS classes.")
  @Nonnull
  public String getInlineVariations() {
    LOG.trace("Getting applied inline variations as String.");
    final StringBuilder variationsStringBuilder = new StringBuilder();
    ParentComponent parentComponent = getRequest().getResource().adaptTo(ParentComponent.class);
    if (parentComponent != null) {
      for (final ComponentVariation variation : getAppliedVariations()) {
        if (variation.isInlineVariation()) {
          variationsStringBuilder.append(variation.getName());
          variationsStringBuilder.append(" ");
        }
      }
    }
    if (variationsStringBuilder.length() > 1) {
      variationsStringBuilder.setLength(variationsStringBuilder.length() - 1);
    }
    LOG.trace("Retrieved applied inline variations as string.");
    return variationsStringBuilder.toString();
  }


  /**
   * Applied ComponentVariation names, to be read by the HTML class attribute. Only variations to be
   * applied on the component's wrapper div are included.
   *
   * @return Applied ComponentVariation names.  Only variations to be applied on the component's
   *     wrapper div are included.
   */
  @Nonnull
  public String getWrapperVariations() {
    LOG.trace("Getting applied wrapper variations as String.");
    final StringBuilder variationsStringBuilder = new StringBuilder();
    for (final ComponentVariation variation : getAppliedVariations()) {
      if (!variation.isInlineVariation()) {
        variationsStringBuilder.append(variation.getName());
        variationsStringBuilder.append(" ");
      }
    }
    if (variationsStringBuilder.length() > 1) {
      variationsStringBuilder.setLength(variationsStringBuilder.length() - 1);
    }
    LOG.trace("Retrieved applied wrapper variations as string.");
    return variationsStringBuilder.toString();
  }

  /**
   * List of ComponentVariations applied to the current Component.
   *
   * @return List of ComponentVariations applied to the current Component.
   */
  @Nonnull
  public List<ComponentVariation> getAppliedVariations() {
    LOG.trace("Retrieving applied variations for {}", getRequest().getResource().getPath());

    if (appliedComponentVariations != null) {
      LOG.trace("Finished retrieving applied variations for {}",
          getRequest().getResource().getPath());
      return appliedComponentVariations;
    }

    final List<ComponentVariation> appliedVariations = new ArrayList<>();
    final List<String> appliedVariationNames = Arrays.asList(
        getComponent().getProperties().get(NN_VARIATIONS, new String[]{}));

    if (!appliedVariationNames.isEmpty()) {
      try {
        final ComponentUiFrameworkView uiFrameworkView = getComponentUiFrameworkView();
        for (final String appliedVariation : appliedVariationNames) {
          for (final ComponentVariation variation : uiFrameworkView.getVariations()) {
            if (variation.getPath().equals(appliedVariation) || variation.getName().equals(
                appliedVariation)) {
              appliedVariations.add(variation);
            }
          }
        }
      } catch (final ModelAdaptionException exception) {
        LOG.warn("Unable to variations list for {}. {}", getComponent().getPath(),
            exception.getMessage());
      }
    }

    if (appliedVariationNames.isEmpty() && !getComponent().getResource().getValueMap().containsKey(
        "variations")) {
      try {
        for (ComponentVariation variation : getComponentUiFrameworkView().getVariations()) {
          if (variation.isDefault()) {
            appliedVariations.add(variation);
          }
        }
      } catch (ModelAdaptionException e) {
        LOG.debug("Unable to apply default variations to {}. {}.", getComponent().getPath(),
            e.getMessage());
      }
    }

    LOG.trace("Finished retrieving applied variations for {}", getComponent().getPath());
    appliedComponentVariations = appliedVariations;
    return appliedComponentVariations;
  }

  /**
   * The current {@link Theme} for the current Page/Component.
   *
   * @return The current Theme.
   * @throws InvalidThemeException Theme could not be adapted to Theme.
   * @throws ResourceNotFoundException Component's expected Theme resource was missing.
   */
  @Nullable
  @KestrosProperty(description = "The current page's theme.")
  public Theme getTheme() throws ResourceNotFoundException, InvalidThemeException {
    LOG.trace("Retrieving theme for {}.", getBaseResource().getPath());
    if (theme == null && getCurrentPage() != null) {
      theme = getCurrentPage().getTheme();
    }
    LOG.trace("Finished retrieving theme for {}.", getBaseResource().getPath());
    return theme;
  }

  protected void setTheme(final Theme theme) {
    this.theme = theme;
  }

  private UiFramework getUiFramework()
      throws InvalidThemeException, ResourceNotFoundException, InvalidUiFrameworkException {
    LOG.trace("Retrieving UiFramework for {}", getComponent().getPath());
    if (uiFramework == null) {
      try {
        uiFramework = getTheme().getUiFramework();
      } catch (InvalidUiFrameworkException e) {
        e.printStackTrace();
      }
    }
    LOG.trace("Finished retrieving UiFramework for {}", getComponent().getPath());
    return uiFramework;
  }


  /**
   * Retrieves the ComponentUiFramework the current Component will use to render.
   *
   * @return The ComponentUiFramework the current Component will use to render
   * @throws InvalidComponentUiFrameworkViewException ComponentUiFramework could not be found,
   *     or failed adaption.
   * @throws InvalidComponentTypeException ComponentType could not be found, or failed
   *     adaption.
   * @throws InvalidThemeException Theme could not be found, or failed adaption.
   * @throws InvalidUiFrameworkException UiFramework for the current component failed
   *     adaptation.
   * @throws ResourceNotFoundException UiFramework for the current component could not be
   *     found.
   */
  public ComponentUiFrameworkView getComponentUiFrameworkView()
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             InvalidThemeException, ResourceNotFoundException, InvalidUiFrameworkException {

    LOG.trace("Retrieving Component UiFrameworkView.");

    if (componentUiFrameworkView != null) {
      LOG.trace("Finished retrieving Component UI FrameworkView.");
      return this.componentUiFrameworkView;
    }

    final ComponentUiFrameworkView componentUiFrameworkView
        = getComponent().getComponentType().getComponentUiFrameworkView(getUiFramework());
    this.componentUiFrameworkView = componentUiFrameworkView;
    LOG.trace("Finished retrieving Component UI FrameworkView.");
    return this.componentUiFrameworkView;
  }

  private BaseComponent getComponent() {
    try {
      return SlingModelUtils.adaptTo(getRequest().getResource(), BaseComponent.class);
    } catch (InvalidResourceTypeException e) {
      throw new IllegalStateException();
    }
  }
}
