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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.cms.user.exceptions.UserGroupRetrievalException;
import io.kestros.cms.user.exceptions.UserRetrievalException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UserUtilsTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ResourceResolver resourceResolver;

  private JackrabbitSession session;

  private UserManager userManager;

  private Group group;

  private User user;

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    user = mock(User.class);
    group = mock(Group.class);
    session = mock(JackrabbitSession.class);
    userManager = mock(UserManager.class);

    when(session.getUserManager()).thenReturn(userManager);

    when(user.getID()).thenReturn("user");
    when(group.getID()).thenReturn("group");

    resourceResolver = spy(context.resourceResolver());

    doReturn(session).when(resourceResolver).adaptTo(Session.class);

    when(userManager.getAuthorizable("group")).thenReturn(group);
    when(userManager.getAuthorizable("user")).thenReturn(user);
  }

  @Test
  public void testGetJackrabbitUserGroup() throws UserGroupRetrievalException, RepositoryException {
    when(group.isGroup()).thenReturn(true);
    assertEquals("group", UserUtils.getJackrabbitUserGroup("group", resourceResolver).getID());
  }

  @Test
  public void testGetJackrabbitUserGroupWhenNotGroup() {
    when(group.isGroup()).thenReturn(false);
    try {
      UserUtils.getJackrabbitUserGroup("group", resourceResolver);
    } catch (UserGroupRetrievalException e) {
      exception = e;
    }
    assertEquals("Unable to retrieve group group. Specified authorizable is not a group.",
        exception.getMessage());
  }

  @Test
  public void testGetJackrabbitUserGroupWhenSessionCannotBeAdapted() {
    doReturn(null).when(resourceResolver).adaptTo(Session.class);
    when(group.isGroup()).thenReturn(false);
    try {
      UserUtils.getJackrabbitUserGroup("group", resourceResolver);
    } catch (UserGroupRetrievalException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve group group. ResourceResolver could not be adapted to Session.",
        exception.getMessage());
  }

  @Test
  public void testGetJackrabbitUser() throws RepositoryException, UserRetrievalException {
    when(user.isGroup()).thenReturn(false);
    assertEquals("user", UserUtils.getJackrabbitUser("user", resourceResolver).getID());
  }

  @Test
  public void testGetJackrabbitUserWhenGroup() {
    when(user.isGroup()).thenReturn(true);
    try {
      UserUtils.getJackrabbitUser("user", resourceResolver);
    } catch (UserRetrievalException e) {
      exception = e;
    }
    assertEquals("Unable to retrieve user user. Authorizable was found, but was a group.",
        exception.getMessage());
  }

  @Test
  public void testGetJackrabbitUserWhenSessionCannotBeAdapted() {
    doReturn(null).when(resourceResolver).adaptTo(Session.class);
    when(user.isGroup()).thenReturn(true);
    try {
      UserUtils.getJackrabbitUser("user", resourceResolver);
    } catch (UserRetrievalException e) {
      exception = e;
    }
    assertEquals("Unable to retrieve user user. ResourceResolver could not be adapted to Session.",
        exception.getMessage());
  }


  @Test
  public void testGetAuthorizable() throws RepositoryException {
    assertEquals("user", UserUtils.getJackrabbitAuthorizable("user", resourceResolver).getID());
  }

  @Test(expected = RepositoryException.class)
  public void testGetAuthorizableWhenSessionCannotBeAdapted() throws RepositoryException {
    doReturn(null).when(resourceResolver).adaptTo(Session.class);
    assertEquals("user", UserUtils.getJackrabbitAuthorizable("user", resourceResolver).getID());
  }

  @Test
  public void testGetSession() throws RepositoryException {
    assertNotNull(UserUtils.getJackrabbitSession(resourceResolver));
  }

  @Test(expected = RepositoryException.class)
  public void testGetSessionWhenSessionCannotBeAdapted() throws RepositoryException {
    doReturn(null).when(resourceResolver).adaptTo(Session.class);
    UserUtils.getJackrabbitSession(resourceResolver);
  }

  @Test(expected = RepositoryException.class)
  public void testGetUserManagerWhenSessionCannotBeAdapted() throws RepositoryException {
    doReturn(null).when(resourceResolver).adaptTo(Session.class);
    assertNotNull(UserUtils.getJackrabbitUserManager(resourceResolver));
  }
}