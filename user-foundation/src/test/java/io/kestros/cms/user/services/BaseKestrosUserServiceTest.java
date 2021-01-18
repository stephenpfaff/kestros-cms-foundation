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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.cms.user.exceptions.UserGroupRetrievalException;
import io.kestros.cms.user.exceptions.UserRetrievalException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseKestrosUserServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BaseKestrosUserService userService;

  private JackrabbitSession jackrabbitSession;

  private UserManager userManager;

  private User jackrabbitUser;

  private Iterator<Group> groupList = new ArrayIterator();

  private Iterator<Authorizable> authorizableIterator = new ArrayIterator();

  private ResourceResolver resourceResolver;

  private Map<String, Object> userProperties = new HashMap<>();

  private Map<String, Object> groupProperties = new HashMap<>();
  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    jackrabbitSession = mock(JackrabbitSession.class);
    userManager = mock(UserManager.class);
    jackrabbitUser = mock(User.class);
    exception = null;

    resourceResolver = spy(context.resourceResolver());

    userProperties.put("jcr:primaryType", "rep:User");
    groupProperties.put("jcr:primaryType", "rep:Group");

    userProperties.put("rep:principalName", "current-user");
    context.create().resource("/home/user-1", userProperties);

    userProperties.put("rep:principalName", "user-2");
    context.create().resource("/home/user-2", userProperties);

    userProperties.put("rep:principalName", "user-3");
    context.create().resource("/home/user-3", userProperties);

    groupProperties.put("rep:principalName", "group-1");
    context.create().resource("/home/group-1", groupProperties);
    groupProperties.put("rep:principalName", "group-2");
    context.create().resource("/home/group-2", groupProperties);
    groupProperties.put("rep:principalName", "group-3");
    context.create().resource("/home/group-3", groupProperties);

    resourceResolver = spy(context.resourceResolver());

    doReturn(jackrabbitSession).when(resourceResolver).adaptTo(any());
    when(jackrabbitSession.getUserManager()).thenReturn(userManager);
    when(userManager.getAuthorizable(anyString())).thenReturn(jackrabbitUser);

    userService = new BaseKestrosUserService();
    context.registerService(KestrosUserService.class, userService);
  }

  @Test
  public void testGetCurrentUser() throws UserRetrievalException {
    when(jackrabbitSession.getUserID()).thenReturn("current-user");
    assertEquals("current-user", userService.getCurrentUser(resourceResolver).getId());
  }

  @Test
  public void testGetCurrentUserWhenUserDoesNotExist() throws PersistenceException {
    resourceResolver.delete(resourceResolver.getResource("/home"));

    when(jackrabbitSession.getUserID()).thenReturn("current-user");
    try {
      userService.getCurrentUser(resourceResolver);
    } catch (UserRetrievalException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve user current-user. Specified group either doesn't exist, or is a "
        + "group.", exception.getMessage());
  }

  @Test
  public void testGetCurrentUserWhenRepositoryException() {
    doReturn(null).when(resourceResolver).adaptTo(Session.class);

    when(jackrabbitSession.getUserID()).thenReturn("current-user");
    try {
      userService.getCurrentUser(resourceResolver);
    } catch (UserRetrievalException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve user CURRENT USER. ResourceResolver could not be adapted to Session.",
        exception.getMessage());
  }

  @Test
  public void testGetUser() throws UserRetrievalException {
    assertEquals("current-user", userService.getUser("current-user", resourceResolver).getId());
    assertEquals("user-2", userService.getUser("user-2", resourceResolver).getId());
    assertEquals("user-3", userService.getUser("user-3", resourceResolver).getId());
  }

  @Test
  public void testGetUserWhenUserDoesNotExist() {
    try {
      userService.getUser("invalid-user", resourceResolver).getId();
    } catch (UserRetrievalException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve user invalid-user. Specified group either doesn't exist, or is a "
        + "group.", exception.getMessage());
  }

  @Test
  public void testGetAllKestrosUsers() {
    assertEquals(3, userService.getAllKestrosUsers(resourceResolver).size());
    assertEquals("current-user", userService.getAllKestrosUsers(resourceResolver).get(0).getId());
    assertEquals("user-2", userService.getAllKestrosUsers(resourceResolver).get(1).getId());
    assertEquals("user-3", userService.getAllKestrosUsers(resourceResolver).get(2).getId());
  }

  @Test
  public void testGetKestrosUserGroup() throws UserGroupRetrievalException {
    assertEquals("group-1", userService.getKestrosUserGroup("group-1", resourceResolver).getId());
    assertEquals("group-2", userService.getKestrosUserGroup("group-2", resourceResolver).getId());
    assertEquals("group-3", userService.getKestrosUserGroup("group-3", resourceResolver).getId());
  }

  @Test
  public void testGetKestrosUserGroupWhenGroupDoesNotExist() {
    try {
      userService.getKestrosUserGroup("invalid-group", resourceResolver);
    } catch (UserGroupRetrievalException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve group invalid-group. Specified group either doesn't exist, or is not "
        + "a group.", exception.getMessage());
  }

  @Test
  public void testGetAllKestrosUserGroups() {
    assertEquals(3, userService.getAllKestrosUserGroups(resourceResolver).size());
    assertEquals("group-1", userService.getAllKestrosUserGroups(resourceResolver).get(0).getId());
    assertEquals("group-2", userService.getAllKestrosUserGroups(resourceResolver).get(1).getId());
    assertEquals("group-3", userService.getAllKestrosUserGroups(resourceResolver).get(2).getId());
  }

  @Test
  public void testGetAllKestrosUserGroupsWhenHomeResourceDoesNotExist()
      throws PersistenceException {
    resourceResolver.delete(resourceResolver.getResource("/home"));
    assertEquals(0, userService.getAllKestrosUserGroups(resourceResolver).size());
  }

  @Test
  public void testGetUserGroupsForAuthorizable()
      throws RepositoryException, UserRetrievalException {
    Group group1 = mock(Group.class);
    when(group1.getID()).thenReturn("group-1");
    Group group2 = mock(Group.class);
    when(group2.getID()).thenReturn("group-2");
    Group[] groups = {group1, group2};

    groupList = new ArrayIterator(groups);
    when(jackrabbitUser.memberOf()).thenReturn(groupList);
    assertEquals(2, userService.getUserGroupsForAuthorizable(
        userService.getUser("current-user", resourceResolver), resourceResolver).size());

    groupList = new ArrayIterator(groups);
    when(jackrabbitUser.memberOf()).thenReturn(groupList);
    assertEquals("group-1", userService.getUserGroupsForAuthorizable(
        userService.getUser("current-user", resourceResolver), resourceResolver).get(0).getId());

    groupList = new ArrayIterator(groups);
    when(jackrabbitUser.memberOf()).thenReturn(groupList);
    assertEquals("group-2", userService.getUserGroupsForAuthorizable(
        userService.getUser("current-user", resourceResolver), resourceResolver).get(1).getId());
  }

  @Test
  public void testGetUserGroupsForAuthorizableWhenRepositoryException()
      throws RepositoryException, UserRetrievalException {
    Group group1 = mock(Group.class);
    when(group1.getID()).thenReturn("group-1");
    Group group2 = mock(Group.class);
    when(group2.getID()).thenReturn("group-2");
    Group[] groups = {group1, group2};

    doReturn(null).when(resourceResolver).adaptTo(any());

    groupList = new ArrayIterator(groups);
    when(jackrabbitUser.memberOf()).thenReturn(groupList);
    assertEquals(0, userService.getUserGroupsForAuthorizable(
        userService.getUser("current-user", resourceResolver), resourceResolver).size());
  }

  @Test
  public void testGetUserGroupsForAuthorizableWhenUserRetrievalException()
      throws RepositoryException, UserRetrievalException, UserGroupRetrievalException {
    Group group1 = mock(Group.class);
    when(group1.getID()).thenReturn("group-1");
    Group group2 = mock(Group.class);
    when(group2.getID()).thenReturn("group-2");
    Group[] groups = {group1, group2};

    userService = spy(userService);
    doThrow(UserGroupRetrievalException.class).when(userService).getKestrosUserGroup(any(), any());

    groupList = new ArrayIterator(groups);
    when(jackrabbitUser.memberOf()).thenReturn(groupList);
    assertEquals(0, userService.getUserGroupsForAuthorizable(
        userService.getUser("current-user", resourceResolver), resourceResolver).size());
  }


  @Test
  public void testGetUserGroupsForUserWhenMemberOfNoGroups()
      throws RepositoryException, UserRetrievalException {
    when(jackrabbitUser.memberOf()).thenReturn(groupList);
    assertEquals(0, userService.getUserGroupsForAuthorizable(
        userService.getUser("current-user", resourceResolver), resourceResolver).size());
  }

  @Test
  public void testGetGroupMemberAuthorizables()
      throws UserGroupRetrievalException, RepositoryException {
    Group group1 = mock(Group.class);
    when(group1.getID()).thenReturn("group-1");
    when(group1.isGroup()).thenReturn(true);
    when(userManager.getAuthorizable("group-1")).thenReturn(group1);

    Group group2 = mock(Group.class);
    when(group2.getID()).thenReturn("group-2");
    when(group2.isGroup()).thenReturn(true);
    when(userManager.getAuthorizable("group-2")).thenReturn(group2);

    User user1 = mock(User.class);
    when(user1.getID()).thenReturn("current-user");
    when(user1.isGroup()).thenReturn(false);
    when(userManager.getAuthorizable("current-user")).thenReturn(user1);

    User user2 = mock(User.class);
    when(user2.getID()).thenReturn("user-2");
    when(user2.isGroup()).thenReturn(false);
    when(userManager.getAuthorizable("user-2")).thenReturn(user2);

    authorizableIterator = new ArrayIterator(new Authorizable[]{group2, user1, user2});
    when(group1.getMembers()).thenReturn(authorizableIterator);
    assertEquals(3, userService.getGroupMemberAuthorizables(
        userService.getKestrosUserGroup("group-1", resourceResolver), resourceResolver).size());

    authorizableIterator = new ArrayIterator(new Authorizable[]{group2, user1, user2});
    when(group1.getMembers()).thenReturn(authorizableIterator);
    assertEquals("group-2", userService.getGroupMemberAuthorizables(
        userService.getKestrosUserGroup("group-1", resourceResolver), resourceResolver).get(
        0).getId());

    authorizableIterator = new ArrayIterator(new Authorizable[]{group2, user1, user2});
    when(group1.getMembers()).thenReturn(authorizableIterator);
    assertEquals("current-user", userService.getGroupMemberAuthorizables(
        userService.getKestrosUserGroup("group-1", resourceResolver), resourceResolver).get(
        1).getId());

    authorizableIterator = new ArrayIterator(new Authorizable[]{group2, user1, user2});
    when(group1.getMembers()).thenReturn(authorizableIterator);
    assertEquals("user-2", userService.getGroupMemberAuthorizables(
        userService.getKestrosUserGroup("group-1", resourceResolver), resourceResolver).get(
        2).getId());
  }

  @Test
  public void testGetGroupMemberAuthorizablesWhenRepositoryException()
      throws UserGroupRetrievalException, RepositoryException, UserRetrievalException {
    Group group1 = mock(Group.class);
    when(group1.getID()).thenReturn("group-1");
    when(group1.isGroup()).thenReturn(true);
    when(userManager.getAuthorizable("group-1")).thenReturn(group1);

    Group group2 = mock(Group.class);
    when(group2.getID()).thenReturn("group-2");
    when(group2.isGroup()).thenReturn(true);
    when(userManager.getAuthorizable("group-2")).thenReturn(group2);

    User user1 = mock(User.class);
    when(user1.getID()).thenReturn("current-user");
    when(user1.isGroup()).thenReturn(false);
    when(userManager.getAuthorizable("current-user")).thenReturn(user1);

    User user2 = mock(User.class);
    when(user2.getID()).thenReturn("user-2");
    when(user2.isGroup()).thenReturn(false);
    when(userManager.getAuthorizable("user-2")).thenReturn(user2);

    userService = spy(userService);
    doThrow(UserRetrievalException.class).when(userService).getUser(any(), any());

    authorizableIterator = new ArrayIterator(new Authorizable[]{group2, user1, user2});
    when(group1.getMembers()).thenReturn(authorizableIterator);
    assertEquals(0, userService.getGroupMemberAuthorizables(
        userService.getKestrosUserGroup("group-1", resourceResolver), resourceResolver).size());
  }

  @Test
  public void testGetGroupMemberGroups() throws UserGroupRetrievalException, RepositoryException {
    Group group1 = mock(Group.class);
    when(group1.getID()).thenReturn("group-1");
    when(group1.isGroup()).thenReturn(true);
    when(userManager.getAuthorizable("group-1")).thenReturn(group1);

    Group group2 = mock(Group.class);
    when(group2.getID()).thenReturn("group-2");
    when(group2.isGroup()).thenReturn(true);
    when(userManager.getAuthorizable("group-2")).thenReturn(group2);

    User user1 = mock(User.class);
    when(user1.getID()).thenReturn("current-user");
    when(user1.isGroup()).thenReturn(false);
    when(userManager.getAuthorizable("current-user")).thenReturn(user1);

    User user2 = mock(User.class);
    when(user2.getID()).thenReturn("user-2");
    when(user2.isGroup()).thenReturn(false);
    when(userManager.getAuthorizable("user-2")).thenReturn(user2);

    authorizableIterator = new ArrayIterator(new Authorizable[]{group2, user1, user2});
    when(group1.getMembers()).thenReturn(authorizableIterator);
    assertEquals(1, userService.getGroupMemberGroups(
        userService.getKestrosUserGroup("group-1", resourceResolver), resourceResolver).size());

    authorizableIterator = new ArrayIterator(new Authorizable[]{group2, user1, user2});
    when(group1.getMembers()).thenReturn(authorizableIterator);
    assertEquals("group-2", userService.getGroupMemberGroups(
        userService.getKestrosUserGroup("group-1", resourceResolver), resourceResolver).get(
        0).getId());
  }

  @Test
  public void testGetGroupMemberUsers() throws UserGroupRetrievalException, RepositoryException {
    Group group1 = mock(Group.class);
    when(group1.getID()).thenReturn("group-1");
    when(group1.isGroup()).thenReturn(true);
    when(userManager.getAuthorizable("group-1")).thenReturn(group1);

    Group group2 = mock(Group.class);
    when(group2.getID()).thenReturn("group-2");
    when(group2.isGroup()).thenReturn(true);
    when(userManager.getAuthorizable("group-2")).thenReturn(group2);

    User user1 = mock(User.class);
    when(user1.getID()).thenReturn("current-user");
    when(user1.isGroup()).thenReturn(false);
    when(userManager.getAuthorizable("current-user")).thenReturn(user1);

    User user2 = mock(User.class);
    when(user2.getID()).thenReturn("user-2");
    when(user2.isGroup()).thenReturn(false);
    when(userManager.getAuthorizable("user-2")).thenReturn(user2);

    authorizableIterator = new ArrayIterator(new Authorizable[]{group2, user1, user2});
    when(group1.getMembers()).thenReturn(authorizableIterator);
    assertEquals(2, userService.getGroupMemberUsers(
        userService.getKestrosUserGroup("group-1", resourceResolver), resourceResolver).size());

    authorizableIterator = new ArrayIterator(new Authorizable[]{group2, user1, user2});
    when(group1.getMembers()).thenReturn(authorizableIterator);
    assertEquals("current-user", userService.getGroupMemberUsers(
        userService.getKestrosUserGroup("group-1", resourceResolver), resourceResolver).get(
        0).getId());

    authorizableIterator = new ArrayIterator(new Authorizable[]{group2, user1, user2});
    when(group1.getMembers()).thenReturn(authorizableIterator);
    assertEquals("user-2", userService.getGroupMemberUsers(
        userService.getKestrosUserGroup("group-1", resourceResolver), resourceResolver).get(
        1).getId());
  }

}