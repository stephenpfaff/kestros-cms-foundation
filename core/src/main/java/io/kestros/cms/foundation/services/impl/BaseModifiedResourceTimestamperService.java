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

package io.kestros.cms.foundation.services.impl;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.foundation.services.ContentPublicationService;
import io.kestros.cms.foundation.services.ModifiedResourceTimestamperService;
import io.kestros.cms.user.KestrosUser;
import javax.annotation.Nonnull;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides logic for updating creation and lastModified properties for Components.
 */
@Component(service = ModifiedResourceTimestamperService.class,
           immediate = true,
           property = "service.ranking:Integer=100")
public class BaseModifiedResourceTimestamperService implements ModifiedResourceTimestamperService {

  private static final Logger LOG = LoggerFactory.getLogger(
      BaseModifiedResourceTimestamperService.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ContentPublicationService contentPublicationService;

  @Override
  public void handleComponentCreationProperties(@Nonnull final BaseComponent component,
      @Nonnull final String user, @Nonnull final ResourceResolver resourceResolver)
      throws PersistenceException {
    LOG.debug("Adding/updating creation properties on {}.", component.getPath());
    final ModifiableValueMap map = component.getResource().adaptTo(ModifiableValueMap.class);
    if (map != null) {
      map.put("kes:createdBy", user);
      map.put("kes:created", System.currentTimeMillis());
      resourceResolver.commit();
    } else {
      LOG.error("Failed to add/updated creation properties on {}.", component.getPath());
    }
  }

  @Override
  public void handleComponentCreationProperties(@Nonnull final BaseComponent component,
      @Nonnull final KestrosUser user, @Nonnull final ResourceResolver resourceResolver)
      throws PersistenceException {
    handleComponentCreationProperties(component, user.getId(), resourceResolver);
  }

  @Override
  public void updateComponentLastModified(@Nonnull final BaseComponent component,
      @Nonnull final String user, @Nonnull final ResourceResolver resourceResolver)
      throws PersistenceException {
    LOG.debug("Adding/updating last modified properties on {}.", component.getPath());
    final ModifiableValueMap map = component.getResource().adaptTo(ModifiableValueMap.class);

    if (map != null) {
      map.put("kes:lastModifiedBy", user);
      map.put("kes:lastModified", System.currentTimeMillis());

      resourceResolver.commit();

      if (contentPublicationService != null) {
        contentPublicationService.setResourceToOutOfDate(component);
      }
    } else {
      LOG.error("Failed to add/updated last modified properties on {}.", component.getPath());
    }
  }

  @Override
  public void updateComponentLastModified(@Nonnull final BaseComponent component,
      @Nonnull final KestrosUser user, @Nonnull final ResourceResolver resourceResolver)
      throws PersistenceException {
    updateComponentLastModified(component, user.getId(), resourceResolver);
  }

  @Override
  public String getDisplayName() {
    return "Modified Resource Timestamper Service";
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
