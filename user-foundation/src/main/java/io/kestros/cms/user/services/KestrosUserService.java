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

import io.kestros.cms.user.KestrosAuthorizable;
import io.kestros.cms.user.KestrosUser;
import io.kestros.cms.user.exceptions.UserGroupRetrievalException;
import io.kestros.cms.user.exceptions.UserRetrievalException;
import io.kestros.cms.user.group.KestrosUserGroup;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Service for retrieving {@link KestrosUser} and {@link KestrosUserGroup} instances.
 */
public interface KestrosUserService {

  /**
   * Returns the current user.
   *
   * @param resourceResolver resourceResolver from the current user.
   * @return Current User.
   * @throws UserRetrievalException Failed to retrieve a current logged in {@link KestrosUser}.
   */
  KestrosUser getCurrentUser(ResourceResolver resourceResolver) throws UserRetrievalException;

  /**
   * Retrieves a specified {@link KestrosUser}.
   *
   * @param userId User to retrieve.
   * @param resourceResolver ResourceResolver.
   * @return Specified {@link KestrosUser}.
   * @throws UserRetrievalException Expected user was not found or could not be adapted to
   *     KestrosUser.
   */
  KestrosUser getUser(String userId, ResourceResolver resourceResolver)
      throws UserRetrievalException;

  /**
   * Retrieves all KestrosUsers.
   *
   * @param resourceResolver ResourceResolver.
   * @return All KestrosUsers.
   */
  List<KestrosUser> getAllKestrosUsers(ResourceResolver resourceResolver);

  /**
   * Retrieves a specified {@link KestrosUserGroup}.
   *
   * @param groupId UserGroup to retrieve.
   * @param resourceResolver ResourceResolver.
   * @return Specified {@link KestrosUser}.
   * @throws UserGroupRetrievalException Expected user group was not found or could not be
   *     adapted to KestrosUser.
   */
  KestrosUserGroup getKestrosUserGroup(String groupId, ResourceResolver resourceResolver)
      throws UserGroupRetrievalException;

  /**
   * Retrieves all KestrosUserGroups.
   *
   * @param resourceResolver ResourceResolver.
   * @return All KestrosUserGroups.
   */
  List<KestrosUserGroup> getAllKestrosUserGroups(ResourceResolver resourceResolver);

  /**
   * Retrieves all {@link KestrosUserGroup} instances that a {@link KestrosUser} or {@link
   * KestrosUserGroup} belongs to.
   *
   * @param authorizable User or Group.
   * @param resourceResolver ResourceResolver.
   * @return All {@link KestrosUserGroup} instances that a {@link KestrosUser} or {@link
   *     KestrosUserGroup} belongs to.
   */
  List<KestrosUserGroup> getUserGroupsForAuthorizable(KestrosAuthorizable authorizable,
      ResourceResolver resourceResolver);

  /**
   * Retrieves all User or Group members of a {@link KestrosUserGroup}.
   *
   * @param group KestrosUserGroup to get members of.
   * @param resourceResolver ResourceResolver.
   * @return all User or Group members of a {@link KestrosUserGroup}.
   * @throws UserGroupRetrievalException Failed to retrieve the specified {@link
   *     KestrosUserGroup}
   * @throws RepositoryException Failed to build member list for specified {@link
   *     KestrosUserGroup}
   */
  List<KestrosAuthorizable> getGroupMemberAuthorizables(KestrosUserGroup group,
      ResourceResolver resourceResolver) throws UserGroupRetrievalException, RepositoryException;

  /**
   * Retrieves all User members of a {@link KestrosUserGroup}.
   *
   * @param group KestrosUserGroup to get user members of.
   * @param resourceResolver ResourceResolver.
   * @return all User members of a {@link KestrosUserGroup}.
   * @throws UserGroupRetrievalException Failed to retrieve the specified {@link
   *     KestrosUserGroup}
   * @throws RepositoryException Failed to build member list for specified {@link
   *     KestrosUserGroup}
   */
  List<KestrosUser> getGroupMemberUsers(KestrosUserGroup group, ResourceResolver resourceResolver)
      throws UserGroupRetrievalException, RepositoryException;

  /**
   * Retrieves all KestrosUserGroup members of a {@link KestrosUserGroup}.
   *
   * @param group KestrosUserGroup to get user members of.
   * @param resourceResolver ResourceResolver.
   * @return all Group members of a {@link KestrosUserGroup}.
   * @throws UserGroupRetrievalException Failed to retrieve the specified {@link
   *     KestrosUserGroup}
   * @throws RepositoryException Failed to build member list for specified {@link
   *     KestrosUserGroup}
   */
  List<KestrosUserGroup> getGroupMemberGroups(KestrosUserGroup group,
      ResourceResolver resourceResolver) throws UserGroupRetrievalException, RepositoryException;

}
