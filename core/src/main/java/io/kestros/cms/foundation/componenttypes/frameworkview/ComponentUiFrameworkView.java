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

package io.kestros.cms.foundation.componenttypes.frameworkview;

import static io.kestros.cms.foundation.design.DesignConstants.NN_VARIATIONS;
import static io.kestros.commons.structuredslingmodels.utils.FileModelUtils.getChildAsFileType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenOfType;

import io.kestros.cms.foundation.componenttypes.HtmlFile;
import io.kestros.cms.foundation.componenttypes.variation.ComponentVariation;
import io.kestros.cms.foundation.exceptions.InvalidScriptException;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component view that is specific to a single UiFramework.  Created as a child resource to
 * ComponentTypes.
 */
@KestrosModel(validationService = ComponentUiFrameworkViewValidationService.class,
              docPaths = {
                     "/content/guide-articles/kestros-cms/site-building/implementing-ui-framework"
                     + "-views",
                     "/content/guide-articles/kestros-cms/site-building/creating-new-component"
                     + "-types",
                     "/content/guide-articles/kestros-cms/site-building/creating-ui-frameworks"})
@Model(adaptables = Resource.class)
public class ComponentUiFrameworkView extends UiLibrary {

  private static final Logger LOG = LoggerFactory.getLogger(ComponentUiFrameworkView.class);

  /**
   * Retrieves the specified script.
   *
   * @param scriptName Script to retrieve.
   * @return The specified script
   * @throws InvalidScriptException The specified script could not be found, or failed to
   *     adapted to HtmlFile.
   */
  public HtmlFile getUiFrameworkViewScript(final String scriptName) throws InvalidScriptException {
    try {
      return getChildAsFileType(scriptName, this, HtmlFile.class);
    } catch (final ModelAdaptionException exception) {
      LOG.trace(exception.getMessage());
      throw new InvalidScriptException(scriptName, getPath());
    }
  }

  /**
   * All Variations that descend from the current ComponentUiFrameworkView.
   *
   * @return All Variations that descend from the current ComponentUiFrameworkView.
   */
  @Nonnull
  public List<ComponentVariation> getVariations() {
    try {
      return getChildrenOfType(getComponentVariationsRootResource(), ComponentVariation.class);
    } catch (final ModelAdaptionException exception) {
      LOG.debug("Unable to find Variations for {} due to missing {} resource.", getPath(),
          NN_VARIATIONS);
    }
    return Collections.emptyList();
  }

  /**
   * The raw output for a given ScriptType.
   *
   * @param scriptType ScriptType to retrieve.
   * @return The raw output for a given ScriptType.
   */
  @Override
  public String getOutput(final ScriptType scriptType, final boolean minify)
      throws InvalidResourceTypeException {
    final StringBuilder output = new StringBuilder();

    output.append(super.getOutput(scriptType, false));

    for (final ComponentVariation variation : getVariations()) {
      output.append(variation.getOutput(scriptType, false));
    }

    return output.toString();
  }


  private BaseResource getComponentVariationsRootResource()
      throws InvalidResourceTypeException, ChildResourceNotFoundException {
    return getChildAsType(NN_VARIATIONS, this, BaseResource.class);
  }

}
