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

package io.kestros.cms.user.group;

import io.kestros.cms.user.KestrosAuthorizable;
import io.kestros.cms.user.KestrosUser;
import io.kestros.cms.user.exceptions.UserGroupRetrievalException;
import io.kestros.cms.user.services.KestrosUserService;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Baseline Sling Model for Kestros user groups.
 */
@Model(adaptables = Resource.class,
       resourceType = "rep:Group")
public class KestrosUserGroup extends KestrosAuthorizable {

  private static final Logger LOG = LoggerFactory.getLogger(KestrosUserGroup.class);

  @OSGiService
  private KestrosUserService userService;

  /**
   * Retrieves all users that are members of current group.
   *
   * @return All users that are members of current group.
   */
  @Nonnull
  public List<KestrosUser> getMemberUsers() {
    try {
      return userService.getGroupMemberUsers(this, getResourceResolver());
    } catch (final RepositoryException | UserGroupRetrievalException e) {
      LOG.error("Unable to retrieve member users for group {} at {}. {}", getId(), getPath(),
          e.getMessage());
    }
    return Collections.emptyList();
  }

  /**
   * Retrieves all groups that are members of current group.
   *
   * @return All groups that are members of current group.
   */
  @Nonnull
  public List<KestrosUserGroup> getMemberGroups() {
    try {
      return userService.getGroupMemberGroups(this, getResourceResolver());
    } catch (final RepositoryException | UserGroupRetrievalException e) {
      LOG.error("Unable to retrieve member groups for group {} at {}. {}", getId(), getPath(),
          e.getMessage());
    }
    return Collections.emptyList();
  }
}
