package io.kestros.cms.foundation.services.resourcemodification;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.user.KestrosUser;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

public interface ModifiedResourceTimestamperService {

  void handleComponentCreationProperties(@Nonnull BaseComponent component, @Nonnull String user,
      @Nonnull ResourceResolver resourceResolver) throws PersistenceException;

  void handleComponentCreationProperties(@Nonnull BaseComponent component,
      @Nonnull KestrosUser user, @Nonnull ResourceResolver resourceResolver)
      throws PersistenceException;

  void updateComponentLastModified(BaseComponent component, String user,
      ResourceResolver resourceResolver) throws PersistenceException;

  void updateComponentLastModified(BaseComponent component, KestrosUser user,
      ResourceResolver resourceResolver) throws PersistenceException;
}


