package io.kestros.cms.foundation.services.resourcemodification;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.foundation.services.contentpublication.ContentPublicationService;
import io.kestros.kestros.cms.user.KestrosUser;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ModifiedResourceTimestamperService.class,
           immediate = true)
public class BaseModifiedResourceTimestamperService implements ModifiedResourceTimestamperService {

  private static final Logger LOG = LoggerFactory.getLogger(
      BaseModifiedResourceTimestamperService.class);

  @Reference
  private ContentPublicationService contentPublicationService;

  /**
   * Updates kes:created (Date) and kes:createdBy (UserID) properties on a specified BaseComponent.
   *
   * @param component BaseComponent to update.
   * @param user User to write in kes:createdBy.
   * @param resourceResolver ResourceResolver.
   * @throws PersistenceException Values could not be persisted to Resource Properties.
   */
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

  public void handleComponentCreationProperties(@Nonnull final BaseComponent component,
      @Nonnull final KestrosUser user, @Nonnull final ResourceResolver resourceResolver)
      throws PersistenceException {
    handleComponentCreationProperties(component, user.getId(), resourceResolver);
  }

  /**
   * Updates kes:lastModified (Date) and kes:lastModifiedBy (UserID) properties on a specified
   * BaseComponent.
   *
   * @param component BaseComponent to update.
   * @param user User to write in kes:lastModifiedBy.
   * @param resourceResolver ResourceResolver.
   * @throws PersistenceException Values could not be persisted to Resource Properties.
   */
  public void updateComponentLastModified(@Nonnull final BaseComponent component, @Nonnull
  final String user,
      @Nonnull final ResourceResolver resourceResolver) throws PersistenceException {
    LOG.debug("Adding/updating last modified properties on {}.", component.getPath());
    final ModifiableValueMap map = component.getResource().adaptTo(ModifiableValueMap.class);

    if (map != null) {
      map.put("kes:lastModifiedBy", user);
      map.put("kes:lastModified", System.currentTimeMillis());

      resourceResolver.commit();

      if (contentPublicationService != null) {
        contentPublicationService.setComponentToOutOfDate(component);
      }
    } else {
      LOG.error("Failed to add/updated last modified properties on {}.", component.getPath());
    }
  }

  public void updateComponentLastModified(@Nonnull final BaseComponent component,
      @Nonnull final KestrosUser user, @Nonnull final ResourceResolver resourceResolver)
      throws PersistenceException {
    updateComponentLastModified(component, user.getId(), resourceResolver);
  }

}
