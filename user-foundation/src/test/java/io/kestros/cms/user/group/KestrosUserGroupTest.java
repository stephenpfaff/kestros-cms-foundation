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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.cms.user.KestrosUser;
import io.kestros.cms.user.exceptions.UserGroupRetrievalException;
import io.kestros.cms.user.services.KestrosUserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jcr.RepositoryException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class KestrosUserGroupTest {

  @Rule
  public SlingContext context = new SlingContext();

  private KestrosUserGroup userGroup;

  private KestrosUserService userService;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  private List<KestrosUserGroup> kestrosUserGroupList = new ArrayList<>();

  private List<KestrosUser> kestrosUserList = new ArrayList<>();

  private KestrosUser user;
  private KestrosUserGroup group;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    userService = mock(KestrosUserService.class);

    kestrosUserList.add(user);
    kestrosUserGroupList.add(group);

    context.registerService(KestrosUserService.class, userService);
    when(userService.getGroupMemberUsers(any(), any())).thenReturn(kestrosUserList);
    when(userService.getGroupMemberGroups(any(), any())).thenReturn(kestrosUserGroupList);
    properties.put("jcr:primaryType", "rep:Group");
  }

  @Test
  public void testGetMemberUsers() throws UserGroupRetrievalException, RepositoryException {
    resource = spy(context.create().resource("/home/group", properties));
    userGroup = resource.adaptTo(KestrosUserGroup.class);

    assertEquals(1, userGroup.getMemberUsers().size());
    verify(userService, times(1)).getGroupMemberUsers(userGroup, context.resourceResolver());
  }

  @Test
  public void testGetMemberUsersWhenException()
      throws UserGroupRetrievalException, RepositoryException {
    resource = spy(context.create().resource("/home/group", properties));
    userGroup = resource.adaptTo(KestrosUserGroup.class);

    when(userService.getGroupMemberUsers(any(), any())).thenThrow(RepositoryException.class);

    assertEquals(0, userGroup.getMemberUsers().size());
    verify(userService, times(1)).getGroupMemberUsers(userGroup, context.resourceResolver());
  }

  @Test
  public void testGetMemberGroups() throws UserGroupRetrievalException, RepositoryException {
    resource = spy(context.create().resource("/home/group", properties));
    userGroup = resource.adaptTo(KestrosUserGroup.class);

    assertEquals(1, userGroup.getMemberGroups().size());
    verify(userService, times(1)).getGroupMemberGroups(userGroup, context.resourceResolver());
  }

  @Test
  public void testGetMemberGroupsWhenException()
      throws UserGroupRetrievalException, RepositoryException {
    resource = spy(context.create().resource("/home/group", properties));
    userGroup = resource.adaptTo(KestrosUserGroup.class);

    when(userService.getGroupMemberGroups(any(), any())).thenThrow(RepositoryException.class);
    assertEquals(0, userGroup.getMemberGroups().size());
    verify(userService, times(1)).getGroupMemberGroups(userGroup, context.resourceResolver());
  }
}