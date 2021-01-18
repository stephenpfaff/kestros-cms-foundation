/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.kestros.cms.user.utils;

import io.kestros.cms.user.exceptions.UserGroupRetrievalException;
import io.kestros.cms.user.exceptions.UserRetrievalException;
import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for retrieving Jackrabbit Users, UserGroups and Authorizables.
 */
public class UserUtils {

  private static final Logger LOG = LoggerFactory.getLogger(UserUtils.class);

  private UserUtils() {
  }

  /**
   * Retrieves the specified Jackrabbit Group.
   *
   * @param id groupId
   * @param resourceResolver resourceResolver
   * @return specified Jackrabbit Group
   * @throws UserGroupRetrievalException group could not be retrieved because it did not exist,
   *     or an authorizable exists but is not a group.
   */
  public static Group getJackrabbitUserGroup(final String id,
      final ResourceResolver resourceResolver) throws UserGroupRetrievalException {
    try {
      final Authorizable authorizable = getJackrabbitAuthorizable(id, resourceResolver);
      if (authorizable.isGroup()) {
        return (Group) authorizable;
      } else {
        throw new UserGroupRetrievalException(id, "Specified authorizable is not a group.");
      }
    } catch (final RepositoryException e) {
      throw new UserGroupRetrievalException(id, e.getMessage());
    }
  }

  /**
   * Retrieves the specified Jackrabbit User.
   *
   * @param id groupId
   * @param resourceResolver resourceResolver
   * @return specified Jackrabbit User.
   * @throws UserRetrievalException user could not be retrieved because it did not exist, or an
   *     authorizable exists but is  a group.
   */
  public static User getJackrabbitUser(final String id, final ResourceResolver resourceResolver)
      throws UserRetrievalException {
    try {
      final Authorizable authorizable = getJackrabbitAuthorizable(id, resourceResolver);
      if (!authorizable.isGroup()) {
        return (User) authorizable;
      } else {
        throw new UserRetrievalException(id, "Authorizable was found, but was a group.");
      }
    } catch (final RepositoryException e) {
      throw new UserRetrievalException(id, e.getMessage());
    }
  }

  /**
   * Retrieves the specified JackRabbit Authorizable.
   *
   * @param id Authorizable to retrieve.
   * @param resourceResolver ResourceResolver.
   * @return The specified JackRabbit Authorizable.
   * @throws RepositoryException Failed to retrieve a JackRabbitUserManager from the
   *     ResourceResolver.
   */
  public static Authorizable getJackrabbitAuthorizable(final String id,
      final ResourceResolver resourceResolver) throws RepositoryException {
    return getJackrabbitUserManager(resourceResolver).getAuthorizable(id);
  }

  /**
   * Jackrabbit Session.
   *
   * @param resourceResolver ResourceResolver to retrieve the session for.
   * @return JackrabbitSession for the current ResourceResolver.
   * @throws RepositoryException ResourceResolver could not be adapted to Session.
   */
  @Nonnull
  public static JackrabbitSession getJackrabbitSession(final ResourceResolver resourceResolver)
      throws RepositoryException {
    final JackrabbitSession session = (JackrabbitSession) resourceResolver.adaptTo(Session.class);
    if (session != null) {
      return session;
    }
    throw new RepositoryException("ResourceResolver could not be adapted to Session.");
  }

  /**
   * Jackrabbit UserManager from the passed ResourceResolver.
   *
   * @param resourceResolver ResourceResolver.
   * @return Jackrabbit UserManager from the passed ResourceResolver.
   * @throws RepositoryException Could not retrieve a JackRabbit Session from the
   *     ResourceResolver.
   */
  public static UserManager getJackrabbitUserManager(final ResourceResolver resourceResolver)
      throws RepositoryException {
    return getJackrabbitSession(resourceResolver).getUserManager();
  }
}