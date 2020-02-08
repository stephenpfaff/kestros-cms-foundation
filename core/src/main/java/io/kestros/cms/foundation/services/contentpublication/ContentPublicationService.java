package io.kestros.cms.foundation.services.contentpublication;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import java.util.List;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Manages publication and publication status for Pages and Components.
 */
public interface ContentPublicationService {

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
   * Publishes the specified Component.
   *
   * @param page Page to publish.
   * @param resourceResolver ResourceResolver.
   * @param publishChildPages Whether to publish child pages.
   * @param publishReferences Whether to publish referenced Resources.
   * @throws NoParentResourceException Failed to publish the Resource because the destination
   *     parent did not exist.
   * @throws PersistenceException ResourceResolver failed to commit changes.
   */
  void publishPage(BaseContentPage page, ResourceResolver resourceResolver,
      Boolean publishChildPages, Boolean publishAllComponents, Boolean publishReferences)
      throws InvalidResourceTypeException, NoParentResourceException, PersistenceException;

  void unpublishResource(BaseResource resource, ResourceResolver resourceResolver);

  void setResourceToOutOfDate(BaseResource component);

  void setComponentToOutOfDate(BaseComponent component);

  void movePublishedResource();

  void deletePublishedResource();

  void rewriteReferenceValues(BaseResource resource);

}
