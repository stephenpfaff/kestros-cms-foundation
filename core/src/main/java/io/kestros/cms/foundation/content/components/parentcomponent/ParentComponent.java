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

package io.kestros.cms.foundation.content.components.parentcomponent;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.cms.foundation.services.ThemeProviderService;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides logic for dynamic script resolution, component variations and HTML attributes to the
 * kestros-parent wrapper component.
 */
@KestrosModel(docPaths = {
    "/content/guide-articles/kestros-cms/foundation/extending-the-parent-component",
    "/content/guide-articles/kestros-cms/foundation/creating-new-component-types",
    "/content/guide-articles/kestros-cms/foundation/implementing-ui-framework-views",
    "/content/guide-articles/kestros-cms/foundation/creating-component-variations"})
@Model(adaptables = Resource.class,
       resourceType = "kestros/commons/components/kestros-parent")
@Exporter(name = "jackson",
          selector = "parent-component",
          extensions = "json")
public class ParentComponent extends BaseComponent {

  private static final Logger LOG = LoggerFactory.getLogger(ParentComponent.class);

  @OSGiService
  private ThemeProviderService themeProviderService;

  private Theme theme;

  private UiFramework uiFramework;

  /**
   * HTML element ID to give to the component.
   *
   * @return HTML element ID to give to the component.
   */
  @Nonnull
  @KestrosProperty(description = "HTML element ID to give to the component.",
                   configurable = true,
                   jcrPropertyName = "id",
                   defaultValue = "")
  public String getId() {
    return getProperties().get("id", StringUtils.EMPTY);
  }

  /**
   * Additional CSS classes to add to the component.
   *
   * @return Additional CSS classes to add to the component.
   */
  @Nonnull
  @KestrosProperty(description = "Additional CSS classes to add to the component.",
                   configurable = true,
                   jcrPropertyName = "class",
                   defaultValue = "")
  public String getCssClass() {
    return getProperties().get("class", StringUtils.EMPTY);
  }

  /**
   * The current {@link Theme} for the current Page/Component.
   *
   * @return The current Theme.
   * @throws InvalidThemeException Theme could not be adapted to Theme.
   * @throws ResourceNotFoundException Component's expected Theme resource was missing.
   */
  @Nullable
  @KestrosProperty(description = "The active theme for the current Page/Component.")
  public Theme getTheme() throws ResourceNotFoundException, InvalidThemeException {
    LOG.trace("Retrieving theme for {}.", getPath());
    if (theme == null) {
      theme = themeProviderService.getThemeForComponent(this);
    }
    LOG.trace("Finished retrieving theme for {}.", getPath());
    return theme;
  }

  protected void setTheme(final Theme theme) {
    this.theme = theme;
  }

}