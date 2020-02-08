package io.kestros.cms.foundation.services.resourcemodification;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.user.KestrosUser;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Provides logic for updating creation and lastModified properties for Components.
 */
public interface ModifiedResourceTimestamperService {

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
   * Adds 'kes:lastModified' {@link java.util.Date} property and 'kes:lastModifiedBy' userId property to a
   * Component or Page.
   *
   * @param component Component to update
   * @param user User which created to resource.
   * @param resourceResolver ResourceResolver.
   * @throws PersistenceException failed to save changes to the component Resource.
   */
  void updateComponentLastModified(BaseComponent component, String user,
      ResourceResolver resourceResolver) throws PersistenceException;

  /**
   * Adds 'kes:lastModified' {@link java.util.Date} property and 'kes:lastModifiedBy' userId property to a
   * Component or Page.
   *
   * @param component Component to update
   * @param user User which created to resource.
   * @param resourceResolver ResourceResolver.
   * @throws PersistenceException failed to save changes to the component Resource.
   */
  void updateComponentLastModified(BaseComponent component, KestrosUser user,
      ResourceResolver resourceResolver) throws PersistenceException;
}


