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

package io.kestros.cms.foundation.services;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import java.util.List;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Manages publication and publication status for Pages and Components.
 */
public interface ContentPublicationService extends ManagedService {

  /**
   * Publishes the specified Resource.
   *
   * @param resource Resource to publish.
   * @param resourceResolver ResourceResolver.
   * @param publishAllDescendants Whether to publish all descendant resource.
   * @param stopOnResourceTypes ResourceTypes to end publishAllDescendants recursion on.
   * @throws NoParentResourceException Failed to publish the Resource because the destination
   *     parent did not exist.
   * @throws PersistenceException ResourceResolver failed to commit changes.
   */
  void publishResource(BaseResource resource, ResourceResolver resourceResolver,
      Boolean publishAllDescendants, List<String> stopOnResourceTypes)
      throws NoParentResourceException, PersistenceException;

  /**
   * Publishes the specified Component.
   *
   * @param component Component to publish.
   * @param resourceResolver ResourceResolver.
   * @param publishAllDescendants Whether to publish all descendant resource.
   * @param publishReferences Whether to publish referenced Resources.
   * @throws NoParentResourceException Failed to publish the Resource because the destination
   *     parent did not exist.
   * @throws PersistenceException ResourceResolver failed to commit changes.
   */
  void publishComponent(BaseComponent component, ResourceResolver resourceResolver,
      Boolean publishAllDescendants, Boolean publishReferences)
      throws PersistenceException, NoParentResourceException;

  /**
   * Publishes the specified Page.
   *
   * @param page Page to publish.
   * @param resourceResolver ResourceResolver.
   * @param publishChildPages Whether to publish child pages.
   * @param publishAllComponents Whether to publish all Components on the given page.
   * @param publishReferences Whether to publish referenced Resources.
   * @throws NoParentResourceException Failed to publish the Resource because the destination
   *     parent did not exist.
   * @throws PersistenceException ResourceResolver failed to commit changes.
   */
  void publishPage(BaseContentPage page, ResourceResolver resourceResolver,
      Boolean publishChildPages, Boolean publishAllComponents, Boolean publishReferences)
      throws NoParentResourceException, PersistenceException;

  /**
   * Unpublishes the specified Page.
   *
   * @param page Page to unpublish.
   * @param resourceResolver ResourceResolver.
   */
  void unpublishPage(BaseContentPage page, ResourceResolver resourceResolver);

  /**
   * Sets a Resource publication status to out of date.
   *
   * @param resource Resource to update the publication status of.
   */
  void setResourceToOutOfDate(BaseResource resource);

  /**
   * Moves the published copy of a page to the specified destination.
   *
   * @param page Page to move.
   * @param destinationResource Destination resource.
   */
  void movePublishedPage(BaseContentPage page, Resource destinationResource);

  /**
   * Deletes the published copy of a page.
   *
   * @param page Page to delete the published copy of.
   */
  void deletePublishedPage(BaseContentPage page);

  /**
   * Rewrites Resource reference properties on the specified resource so that they look to the
   * published copy of the reference Resource.
   *
   * @param resource Resource to update referenced values of.
   */
  void rewriteReferenceValues(BaseResource resource);

}
