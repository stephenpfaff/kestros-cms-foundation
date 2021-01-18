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

package io.kestros.cms.user;

import io.kestros.cms.user.group.KestrosUserGroup;
import io.kestros.cms.user.services.KestrosUserService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Baseline authorizable instance.  Extended by {@link KestrosUser} and {@link KestrosUserGroup}.
 */
@Model(adaptables = Resource.class)
public class KestrosAuthorizable extends BaseResource {

  @OSGiService
  private KestrosUserService userService;

  @Nonnull
  @Override
  public String getTitle() {
    return getId();
  }

  /**
   * The current User or Group's unique ID.
   *
   * @return The current User or Group's unique ID.
   */
  @Nonnull
  public String getId() {
    return getProperties().get("rep:principalName", StringUtils.EMPTY);
  }

  /**
   * {@link KestrosUserGroup} instances that the Authorizable is a member of.
   *
   * @return {@link KestrosUserGroup} instances that the Authorizable is a member of.
   */
  public List<KestrosUserGroup> getMemberOf() {
    return userService.getUserGroupsForAuthorizable(this, getResourceResolver());
  }

}
