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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.cms.user.services.KestrosUserService;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class KestrosUserTest {

  @Rule
  public SlingContext context = new SlingContext();

  private KestrosUser user;

  private KestrosUserService userService;

  private JackrabbitSession session;

  private UserManager userManager;

  private User jackrabbitUser;

  private ResourceResolver resourceResolver;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    userService = mock(KestrosUserService.class);
    userManager = mock(UserManager.class);
    session = mock(JackrabbitSession.class);
    resourceResolver = mock(ResourceResolver.class);
    jackrabbitUser = mock(User.class);

    context.registerService(KestrosUserService.class, userService);
  }

  @Test
  public void testIsAdmin() throws RepositoryException {
    resource = spy(context.create().resource("/home/user", properties));
    user = resource.adaptTo(KestrosUser.class);
    user = spy(user);

    resourceResolver = spy(context.resourceResolver());

    doReturn(resourceResolver).when(resource).getResourceResolver();

    doReturn(session).when(resourceResolver).adaptTo(any());
    when(session.getUserManager()).thenReturn(userManager);
    when(userManager.getAuthorizable(anyString())).thenReturn(jackrabbitUser);
    when(jackrabbitUser.isAdmin()).thenReturn(true);

    assertTrue(user.isAdmin());
  }

  @Test
  public void testIsAdminWhenNotAdmin() throws RepositoryException {
    resource = spy(context.create().resource("/home/user", properties));
    user = resource.adaptTo(KestrosUser.class);
    user = spy(user);

    resourceResolver = spy(context.resourceResolver());

    doReturn(resourceResolver).when(resource).getResourceResolver();

    doReturn(session).when(resourceResolver).adaptTo(any());
    when(session.getUserManager()).thenReturn(userManager);
    when(userManager.getAuthorizable(anyString())).thenReturn(jackrabbitUser);
    when(jackrabbitUser.isAdmin()).thenReturn(false);

    assertFalse(user.isAdmin());
  }

  @Test
  public void testIsAdminWhenRepositoryException() throws RepositoryException {
    resource = spy(context.create().resource("/home/user", properties));
    user = resource.adaptTo(KestrosUser.class);
    user = spy(user);

    resourceResolver = spy(context.resourceResolver());

    doReturn(resourceResolver).when(resource).getResourceResolver();

    doReturn(session).when(resourceResolver).adaptTo(any());
    when(session.getUserManager()).thenThrow(RepositoryException.class);

    assertFalse(user.isAdmin());
  }
}