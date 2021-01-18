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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.cms.user.group.KestrosUserGroup;
import io.kestros.cms.user.services.KestrosUserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class KestrosAuthorizableTest {

  @Rule
  public SlingContext context = new SlingContext();

  private KestrosAuthorizable kestrosAuthorizable;

  private KestrosUserService userService;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  private List<KestrosUserGroup> kestrosUserGroupList = new ArrayList<>();

  private KestrosUserGroup group;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    userService = mock(KestrosUserService.class);
    context.registerService(KestrosUserService.class, userService);

    group = mock(KestrosUserGroup.class);
    kestrosUserGroupList.add(group);
    when(userService.getUserGroupsForAuthorizable(any(), any())).thenReturn(kestrosUserGroupList);
  }

  @Test
  public void testGetTitle() {
    properties.put("rep:principalName", "user-id");
    resource = context.create().resource("/user", properties);
    kestrosAuthorizable = resource.adaptTo(KestrosAuthorizable.class);

    assertEquals("user-id", kestrosAuthorizable.getTitle());
  }

  @Test
  public void testGetId() {
    properties.put("rep:principalName", "user-id");
    resource = context.create().resource("/user", properties);
    kestrosAuthorizable = resource.adaptTo(KestrosAuthorizable.class);

    assertEquals("user-id", kestrosAuthorizable.getId());
  }

  @Test
  public void testGetIdWhenPropertyIsMissing() {
    resource = context.create().resource("/user", properties);
    kestrosAuthorizable = resource.adaptTo(KestrosAuthorizable.class);

    assertEquals("", kestrosAuthorizable.getId());
  }

  @Test
  public void testGetMemberOf() {
    resource = context.create().resource("/user", properties);
    kestrosAuthorizable = resource.adaptTo(KestrosAuthorizable.class);
    kestrosAuthorizable.getMemberOf();
    verify(userService, times(1)).getUserGroupsForAuthorizable(kestrosAuthorizable,
        context.resourceResolver());
    assertEquals(1, kestrosAuthorizable.getMemberOf().size());
  }
}