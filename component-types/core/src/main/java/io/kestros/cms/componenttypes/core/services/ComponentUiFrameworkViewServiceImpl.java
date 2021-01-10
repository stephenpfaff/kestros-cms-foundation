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

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getAllDescendantsOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.componenttypes.api.services.ComponentTypeCacheService;
import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewService;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.felix.hc.api.FormattingResultLog;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Service which providers {@link ComponentUiFrameworkView} instances for a given {@link
 * UiFramework}.
 */
@Component(immediate = true,
           service = ComponentUiFrameworkViewService.class)
public class ComponentUiFrameworkViewServiceImpl implements ComponentUiFrameworkViewService {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ComponentTypeCacheService componentTypeCache;

  /**
   * All ComponentUiFrameworkViews under /apps and /libs that implement the current UiFramework.
   *
   * @param uiFramework UiFramework to retrieve views for.
   * @return All ComponentUiFrameworkViews under /apps and /libs that implement the current
   *     UiFramework.
   */
  @Nonnull
  @JsonIgnore
  public List<ComponentUiFrameworkView> getComponentViews(UiFramework uiFramework) {
    final List<ComponentUiFrameworkView> componentUiFrameworkViews = new ArrayList<>(
        getAllComponentUiFrameworkViewsInADirectory("/apps", uiFramework));
    componentUiFrameworkViews.addAll(
        getAllComponentUiFrameworkViewsInADirectory("/libs/kestros/commons", uiFramework));
    componentUiFrameworkViews.addAll(
        getAllComponentUiFrameworkViewsInADirectory("/libs/kestros/components", uiFramework));
    componentUiFrameworkViews.addAll(
        getAllComponentUiFrameworkViewsInADirectory("/libs/kestros/cms", uiFramework));
    return componentUiFrameworkViews;
  }

  @Nonnull
  List<ComponentUiFrameworkView> getAllComponentUiFrameworkViewsInADirectory(
      final String componentPath, UiFramework uiFramework) {
    final List<ComponentUiFrameworkView> componentUiFrameworkViews = new ArrayList<>();

    for (final ComponentType componentType : getAllComponentTypesInDirectory(componentPath,
        uiFramework)) {
      try {
        componentUiFrameworkViews.add(componentType.getComponentUiFrameworkView(uiFramework));
      } catch (final ModelAdaptionException exception) {
        // todo log.
        //          LOG.debug(
        //              "Unable to retrieve view for {} for component {} due to missing or
        //              invalid Resource",
        //              getFrameworkCode(), componentType.getPath());
      }
    }
    return componentUiFrameworkViews;
  }

  @Nonnull
  private List<ComponentType> getAllComponentTypesInDirectory(@Nonnull final String path,
      UiFramework uiFramework) {
    if (componentTypeCache != null) {
      try {
        return SlingModelUtils.getResourcesAsType(componentTypeCache.getCachedComponentTypes(path),
            uiFramework.getResourceResolver(), ComponentType.class);
      } catch (CacheRetrievalException e) {
        //todo log
        //        LOG.debug(e.getMessage());
      }
    }
    final List<ComponentType> componentTypeList = new ArrayList<>();
    try {
      final BaseResource root = getResourceAsType(path, uiFramework.getResourceResolver(),
          BaseResource.class);
      componentTypeList.addAll(getAllDescendantsOfType(root, ComponentType.class));
      final List<String> componentTypePathList = new ArrayList<>();

      for (ComponentType componentType : componentTypeList) {
        componentTypePathList.add(componentType.getPath());
      }

      if (componentTypeCache != null) {
        componentTypeCache.cacheComponentTypePathList(path, componentTypePathList);
      }
      return componentTypeList;
    } catch (final ModelAdaptionException exception) {
      // todo log.
      //      LOG.debug(
      //          "Unable to retrieve resource {} while getting all ComponentType for UiFramework
      //          {} due "
      //          + "to missing or invalid Resource.", path, getPath());
    } catch (CacheBuilderException e) {
      // todo log.
      //      LOG.error(e.getMessage());
    }
    return componentTypeList;
  }

  @Override
  public String getDisplayName() {
    return "Component UI Framework View Service";
  }

  @Override
  public void activate(ComponentContext componentContext) {

  }

  @Override
  public void deactivate(ComponentContext componentContext) {

  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {

  }
}
