package io.kestros.cms.foundation.services.contentpublication;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import java.util.List;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

public interface ContentPublicationService {

  void publishResource(BaseResource resource, ResourceResolver resourceResolver,
      Boolean publishAllDescendants, List<String> stopOnResourceTypes)
      throws NoParentResourceException, PersistenceException;

  void publishComponent(BaseResource resource, ResourceResolver resourceResolver,
      Boolean includeAllDescendants, Boolean publishReferences)
      throws PersistenceException, NoParentResourceException, InvalidResourceTypeException;

  void publishPage(BaseResource pageResource, ResourceResolver resourceResolver,
      Boolean includeChildPages, Boolean publishAllComponents, Boolean publishReferences)
      throws InvalidResourceTypeException, NoParentResourceException, PersistenceException;

  void unpublishResource(BaseResource resource, ResourceResolver resourceResolver);

  void setResourceToOutOfDate(BaseResource component);

  void setComponentToOutOfDate(BaseComponent component);

  void movePublishedResource();

  void deletePublishedResource();

  void rewriteReferenceValues(BaseResource resource);

}
