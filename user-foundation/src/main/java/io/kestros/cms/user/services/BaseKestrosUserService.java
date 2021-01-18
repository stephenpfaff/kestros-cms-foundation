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

package io.kestros.cms.user.services;

import static io.kestros.cms.user.utils.UserUtils.getJackrabbitAuthorizable;
import static io.kestros.cms.user.utils.UserUtils.getJackrabbitSession;
import static io.kestros.cms.user.utils.UserUtils.getJackrabbitUserGroup;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getAllDescendantsOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;

import io.kestros.cms.user.KestrosAuthorizable;
import io.kestros.cms.user.KestrosUser;
import io.kestros.cms.user.exceptions.UserGroupRetrievalException;
import io.kestros.cms.user.exceptions.UserRetrievalException;
import io.kestros.cms.user.group.KestrosUserGroup;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves baseline User and Group instances.
 */
@Component(service = KestrosUserService.class,
           immediate = true,
           property = "service.ranking:Integer=100")
public class BaseKestrosUserService implements KestrosUserService {

  private static final Logger LOG = LoggerFactory.getLogger(BaseKestrosUserService.class);

  @Override
  @Nonnull
  public KestrosUser getCurrentUser(final ResourceResolver resourceResolver)
      throws UserRetrievalException {
    try {
      final String currentUser = getJackrabbitSession(resourceResolver).getUserID();
      return getUser(currentUser, resourceResolver);
    } catch (final RepositoryException e) {
      throw new UserRetrievalException("CURRENT USER", e.getMessage());
    }
  }

  @Override
  @Nonnull
  public KestrosUser getUser(final String userId, final ResourceResolver resourceResolver)
      throws UserRetrievalException {
    for (final KestrosUser user : getAllKestrosUsers(resourceResolver)) {
      if (user.getId().equals(userId)) {
        return user;
      }
    }
    throw new UserRetrievalException(userId,
        "Specified group either doesn't exist, or is a group.");
  }

  @Override
  @Nonnull
  public List<KestrosUser> getAllKestrosUsers(final ResourceResolver resourceResolver) {
    try {
      return getAllDescendantsOfType(getHomeResource(resourceResolver), KestrosUser.class);
    } catch (final ResourceNotFoundException exception) {
      LOG.error("Unable to build KestrosUser list. {}", exception.getMessage());
    }
    return Collections.emptyList();
  }

  @Override
  @Nonnull
  public KestrosUserGroup getKestrosUserGroup(final String groupId,
      final ResourceResolver resourceResolver) throws UserGroupRetrievalException {
    for (final KestrosUserGroup group : getAllKestrosUserGroups(resourceResolver)) {
      if (group.getId().equals(groupId)) {
        return group;
      }
    }
    throw new UserGroupRetrievalException(groupId,
        "Specified group either doesn't exist, or is not a group.");
  }

  @Override
  @Nonnull
  public List<KestrosUserGroup> getAllKestrosUserGroups(final ResourceResolver resourceResolver) {
    try {
      return getAllDescendantsOfType(getHomeResource(resourceResolver), KestrosUserGroup.class);
    } catch (final ResourceNotFoundException exception) {
      LOG.error("Unable to build KestrosUserGroup list. {}", exception.getMessage());
    }
    return Collections.emptyList();
  }

  @Override
  @Nonnull
  public List<KestrosUserGroup> getUserGroupsForAuthorizable(final KestrosAuthorizable authorizable,
      final ResourceResolver resourceResolver) {
    try {

      final Iterator<Group> groupIterator = getJackrabbitAuthorizable(authorizable.getId(),
          resourceResolver).memberOf();
      return getKestrosUserGroupsFromIterator(groupIterator, resourceResolver);
    } catch (final RepositoryException exception) {
      LOG.error("Unable to retrieve groups for authorizable {}. {}", authorizable.getId(),
          exception.getMessage());
    }
    return Collections.emptyList();
  }

  @Override
  @Nonnull
  public List<KestrosAuthorizable> getGroupMemberAuthorizables(final KestrosUserGroup group,
      final ResourceResolver resourceResolver)
      throws UserGroupRetrievalException, RepositoryException {
    final Iterator<Authorizable> members = getJackrabbitUserGroup(group.getId(),
        resourceResolver).getMembers();
    return getKestrosAuthorizablesFromIterator(members, resourceResolver);
  }

  @Override
  @Nonnull
  public List<KestrosUser> getGroupMemberUsers(final KestrosUserGroup group,
      final ResourceResolver resourceResolver)
      throws UserGroupRetrievalException, RepositoryException {
    final List<KestrosUser> users = new ArrayList<>();
    for (final KestrosAuthorizable authorizable : getGroupMemberAuthorizables(group,
        resourceResolver)) {
      if (authorizable instanceof KestrosUser) {
        users.add((KestrosUser) authorizable);
      }
    }
    return users;
  }

  @Override
  @Nonnull
  public List<KestrosUserGroup> getGroupMemberGroups(final KestrosUserGroup group,
      final ResourceResolver resourceResolver)
      throws UserGroupRetrievalException, RepositoryException {
    final List<KestrosUserGroup> groups = new ArrayList<>();
    for (final KestrosAuthorizable authorizable : getGroupMemberAuthorizables(group,
        resourceResolver)) {
      if (authorizable instanceof KestrosUserGroup) {
        groups.add((KestrosUserGroup) authorizable);
      }
    }
    return groups;
  }

  private static BaseResource getHomeResource(final ResourceResolver resolver)
      throws ResourceNotFoundException {
    return getResourceAsBaseResource("/home", resolver);
  }

  private List<KestrosUserGroup> getKestrosUserGroupsFromIterator(
      final Iterator<Group> groupIterator, final ResourceResolver resourceResolver) {
    final List<KestrosUserGroup> userGroups = new ArrayList<>();

    while (groupIterator.hasNext()) {
      final Group group = groupIterator.next();
      try {
        userGroups.add(getKestrosUserGroup(group.getID(), resourceResolver));
      } catch (final UserGroupRetrievalException | RepositoryException e) {
        LOG.warn("Group could not be retrieved. {}.", e.getMessage());
      }
    }
    return userGroups;
  }

  private List<KestrosAuthorizable> getKestrosAuthorizablesFromIterator(
      final Iterator<Authorizable> authorizablesIterator, final ResourceResolver resourceResolver) {
    final List<KestrosAuthorizable> authorizables = new ArrayList<>();
    try {

      while (authorizablesIterator.hasNext()) {
        final Authorizable member = authorizablesIterator.next();

        if (!member.isGroup()) {
          authorizables.add(getUser(member.getID(), resourceResolver));
        } else {
          authorizables.add(getKestrosUserGroup(member.getID(), resourceResolver));
        }
      }
      return authorizables;
    } catch (final UserRetrievalException | UserGroupRetrievalException | RepositoryException e) {
      LOG.warn("Authorizable could not be retrieved. {}.", e.getMessage());
    }
    return Collections.emptyList();
  }
}
