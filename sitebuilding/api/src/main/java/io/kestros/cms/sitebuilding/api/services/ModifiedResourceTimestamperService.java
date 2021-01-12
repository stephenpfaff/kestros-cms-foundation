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

package io.kestros.cms.sitebuilding.api.services;

import io.kestros.cms.sitebuilding.api.models.BaseComponent;
import io.kestros.cms.user.KestrosUser;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Provides logic for updating creation and lastModified properties for Components.
 */
public interface ModifiedResourceTimestamperService extends ManagedService {

  /**
   * Adds 'kes:created' {@link java.util.Date} property and 'kes:createdBy' userId property to a
   * Component or Page.
   *
   * @param component Component to update
   * @param user User which created to resource.
   * @param resourceResolver ResourceResolver.
   * @throws PersistenceException failed to save changes to the component Resource.
   */
  void handleComponentCreationProperties(@Nonnull BaseComponent component, @Nonnull String user,
      @Nonnull ResourceResolver resourceResolver) throws PersistenceException;

  /**
   * Adds 'kes:created' {@link java.util.Date} property and 'kes:createdBy' userId property to a
   * Component or Page.
   *
   * @param component Component to update
   * @param user User which created to resource.
   * @param resourceResolver ResourceResolver.
   * @throws PersistenceException failed to save changes to the component Resource.
   */
  void handleComponentCreationProperties(@Nonnull BaseComponent component,
      @Nonnull KestrosUser user, @Nonnull ResourceResolver resourceResolver)
      throws PersistenceException;

  /**
   * Adds 'kes:lastModified' {@link java.util.Date} property and 'kes:lastModifiedBy' userId
   * property to a Component or Page.
   *
   * @param component Component to update
   * @param user User which created to resource.
   * @param resourceResolver ResourceResolver.
   * @throws PersistenceException failed to save changes to the component Resource.
   */
  void updateComponentLastModified(BaseComponent component, String user,
      ResourceResolver resourceResolver) throws PersistenceException;

  /**
   * Adds 'kes:lastModified' {@link java.util.Date} property and 'kes:lastModifiedBy' userId
   * property to a Component or Page.
   *
   * @param component Component to update
   * @param user User which created to resource.
   * @param resourceResolver ResourceResolver.
   * @throws PersistenceException failed to save changes to the component Resource.
   */
  void updateComponentLastModified(BaseComponent component, KestrosUser user,
      ResourceResolver resourceResolver) throws PersistenceException;
}


