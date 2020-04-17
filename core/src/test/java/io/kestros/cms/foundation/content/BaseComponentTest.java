/*
 *      Copyright (C) 2020  Kestros, Inc.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.kestros.cms.foundation.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.cms.foundation.content.components.contentarea.ContentArea;
import io.kestros.cms.foundation.exceptions.InvalidComponentTypeException;
import io.kestros.cms.foundation.services.themeprovider.ThemeProviderService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.cms.user.KestrosUser;
import io.kestros.cms.user.exceptions.UserRetrievalException;
import io.kestros.cms.user.services.KestrosUserService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseComponentTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private Resource resource;

  private BaseComponent baseComponent;

  private Map<String, Object> properties = new HashMap<>();

  private ValueMap syntheticResourceValueMap = mock(ValueMap.class);

  private ThemeProviderService themeProviderService = mock(ThemeProviderService.class);

  private KestrosUserService userService;

  private KestrosUser kestrosUser;

  private Exception exception;

  @Before
  public void initialSetup() throws UserRetrievalException {
    context.addModelsForPackage("io.kestros");
    userService = mock(KestrosUserService.class);
    context.registerService(KestrosUserService.class, userService);
    context.registerService(ThemeProviderService.class, themeProviderService);
    kestrosUser = mock(KestrosUser.class);

    when(userService.getUser("user", context.resourceResolver())).thenReturn(kestrosUser);
    when(kestrosUser.getId()).thenReturn("user");
    exception = null;
  }

  @Test
  public void testInitializeWhenResourceDoesNotExistYet() throws PersistenceException {
    Resource parentResource = context.create().resource("/apps");
    resource = context.create().resource("/apps/resource", properties);
    baseComponent = spy(resource.adaptTo(BaseComponent.class));

    context.resourceResolver().delete(resource);

    assertNull(context.resourceResolver().getResource("/resource"));
    assertEquals("/apps/resource", baseComponent.getResource().getPath());

    Resource syntheticResource = mock(Resource.class);
    when(syntheticResource.getParent()).thenReturn(parentResource);
    when(syntheticResourceValueMap.get("sling:resourceType")).thenReturn("synthetic-resource-type");
    when(syntheticResource.getResourceType()).thenReturn("synthetic-resource-type");
    when(syntheticResource.getPath()).thenReturn("/apps/resource");
    when(syntheticResource.getValueMap()).thenReturn(syntheticResourceValueMap);
    when(syntheticResource.getResourceResolver()).thenReturn(context.resourceResolver());

    doReturn(syntheticResource).when(baseComponent).getResource();
    baseComponent.initialize();

    assertNotNull(baseComponent.getResource());
    assertNotNull(context.resourceResolver().getResource("/apps/resource"));
    assertEquals("synthetic-resource-type", baseComponent.getResourceType());
  }

  @Test
  public void testGetComponentType() throws InvalidComponentTypeException {
    Map<String, Object> componentTypeProperties = new HashMap<>();
    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");

    properties.put("sling:resourceType", "component");

    context.create().resource("/apps/component", componentTypeProperties);
    resource = context.create().resource("/resource", properties);

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertNotNull(baseComponent.getComponentType());
    assertEquals("/apps/component", baseComponent.getComponentType().getPath());
  }

  @Test
  public void testGetComponentTypeWhenFallsBackToLibs() throws InvalidComponentTypeException {
    Map<String, Object> componentTypeProperties = new HashMap<>();
    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");

    properties.put("sling:resourceType", "component");

    context.create().resource("/libs/component", componentTypeProperties);
    resource = context.create().resource("/resource", properties);

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertNotNull(baseComponent.getComponentType());
    assertEquals("/libs/component", baseComponent.getComponentType().getPath());
  }

  @Test
  public void testGetComponentTypeWhenComponentTypeIsInvalid() {
    Map<String, Object> componentTypeProperties = new HashMap<>();

    properties.put("sling:resourceType", "component");

    context.create().resource("/libs/component", componentTypeProperties);
    resource = context.create().resource("/resource", properties);

    baseComponent = resource.adaptTo(BaseComponent.class);

    try {
      assertNotNull(baseComponent.getComponentType());
    } catch (InvalidComponentTypeException e) {
      exception = e;
    }
    assertEquals("Unable to adapt 'component': Invalid or missing ComponentType resource.",
        exception.getMessage());
  }

  @Test
  public void testGetContainingPage()
      throws InvalidResourceTypeException, NoValidAncestorException {
    Map<String, Object> pageProperties = new HashMap<>();
    pageProperties.put("jcr:primaryType", "kes:Page");
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content");
    resource = context.create().resource("/page/jcr:content/resource");

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertEquals("/page", baseComponent.getContainingPage().getPath());
  }

  @Test
  public void testGetContainingPageWhenJcrContentIsNotChildOfASiteOrPage() {
    Map<String, Object> pageProperties = new HashMap<>();
    pageProperties.put("jcr:primaryType", "nt:unstructured");
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content");
    resource = context.create().resource("/page/jcr:content/resource");

    baseComponent = resource.adaptTo(BaseComponent.class);

    Exception exception = null;
    try {
      assertEquals("/page", baseComponent.getContainingPage().getPath());
    } catch (NoValidAncestorException e) {
      exception = e;
    }
    assertEquals("Unable to retrieve ancestor matching type BaseContentPage for "
                 + "/page/jcr:content/resource: No valid ancestor found.", exception.getMessage());
  }

  @Test
  public void testGetContainingPageWhenJcrContentIsNotChildOfASiteOrPageAndGrandParentPageExists()
      throws NoValidAncestorException {
    Map<String, Object> pageProperties = new HashMap<>();
    pageProperties.put("jcr:primaryType", "kes:Page");
    context.create().resource("/page", pageProperties);

    context.create().resource("/page/jcr:content");
    pageProperties.put("jcr:primaryType", "nt:unstructured");
    context.create().resource("/page/child", pageProperties);
    context.create().resource("/page/child/jcr:content");

    resource = context.create().resource("/page/child/jcr:content/resource");

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertEquals("/page", baseComponent.getContainingPage().getPath());
  }

  @Test
  public void testGetContainingPageWhenNoJcrContentFound()
      throws InvalidResourceTypeException, NoValidAncestorException {
    Map<String, Object> pageProperties = new HashMap<>();
    pageProperties.put("jcr:primaryType", "kes:Page");
    context.create().resource("/page", pageProperties);
    resource = context.create().resource("/page/resource");

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertEquals("/page", baseComponent.getContainingPage().getPath());
  }

  @Test
  public void testGetContainingPageWhenNoAncestorPage() {
    resource = context.create().resource("/resource");

    baseComponent = resource.adaptTo(BaseComponent.class);

    try {
      baseComponent.getContainingPage().getPath();
    } catch (NoValidAncestorException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve ancestor matching type BaseContentPage for /resource: No valid "
        + "ancestor found.", exception.getMessage());
  }

  @Test
  public void testGetContainingPageWhenContainingPageIsSite()
      throws InvalidResourceTypeException, NoValidAncestorException {

    Map<String, Object> pageProperties = new HashMap<>();

    pageProperties.put("jcr:primaryType", "kes:Site");
    context.create().resource("/site", pageProperties);
    context.create().resource("/site/jcr:content");
    resource = context.create().resource("/site/jcr:content/resource");

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertEquals("/site", baseComponent.getContainingPage().getPath());
  }

  @Test
  public void testGetChildren() throws InvalidResourceTypeException {
    resource = context.create().resource("/resource");

    properties.put("sling:resourceType", "kestros/commons/components/content-area");

    context.create().resource("/resource/child-1", properties);
    context.create().resource("/resource/child-2", properties);

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertEquals(2, baseComponent.getChildren().size());
    assertEquals("child-1", baseComponent.getChildren().get(0).getName());
    assertEquals(ContentArea.class, baseComponent.getChildren().get(0).getClass());
    assertEquals("child-2", baseComponent.getChildren().get(1).getName());
    assertEquals(ContentArea.class, baseComponent.getChildren().get(1).getClass());
  }

  @Test
  public void testGetChildrenWhenMatchingResourceTypeNotFound()
      throws InvalidResourceTypeException {
    resource = context.create().resource("/resource");

    context.create().resource("/resource/child-1", properties);
    context.create().resource("/resource/child-2", properties);

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertEquals(2, baseComponent.getChildren().size());
    assertEquals("child-1", baseComponent.getChildren().get(0).getName());
    assertEquals(BaseComponent.class, baseComponent.getChildren().get(0).getClass());
    assertEquals("child-2", baseComponent.getChildren().get(1).getName());
    assertEquals(BaseComponent.class, baseComponent.getChildren().get(1).getClass());
  }

  @Test
  public void testGetChildrenAsBaseComponent() throws InvalidResourceTypeException {
    resource = context.create().resource("/resource");

    properties.put("sling:resourceType", "kestros/commons/components/content-area");

    context.create().resource("/resource/child-1", properties);
    context.create().resource("/resource/child-2", properties);

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertEquals(2, baseComponent.getChildrenAsBaseComponent().size());
    assertEquals("child-1", baseComponent.getChildrenAsBaseComponent().get(0).getName());
    assertEquals("child-2", baseComponent.getChildrenAsBaseComponent().get(1).getName());
  }

  @Test
  public void testGetAllDescendantComponents() {
    resource = context.create().resource("/resource");

    properties.put("sling:resourceType", "kestros/commons/components/content-area");

    context.create().resource("/resource/child-1", properties);
    context.create().resource("/resource/child-1/grand-child-1", properties);
    context.create().resource("/resource/child-2", properties);
    context.create().resource("/resource/child-2/grand-child-2", properties);

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertEquals(4, baseComponent.getAllDescendantComponents().size());
    assertEquals("child-1", baseComponent.getAllDescendantComponents().get(0).getName());
    assertEquals("grand-child-1", baseComponent.getAllDescendantComponents().get(1).getName());
    assertEquals("child-2", baseComponent.getAllDescendantComponents().get(2).getName());
    assertEquals("grand-child-2", baseComponent.getAllDescendantComponents().get(3).getName());
  }

  @Test
  public void testGetLastModified() {
    properties.put("kes:lastModified", new Date().getTime());
    resource = context.create().resource("/resource", properties);

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertEquals("Just now", baseComponent.getLastModified().getTimeAgo());
  }

  @Test
  public void testGetCreated() {
    properties.put("kes:created", new Date().getTime());
    resource = context.create().resource("/resource", properties);

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertEquals("Just now", baseComponent.getCreated().getTimeAgo());
  }

  @Test
  public void testGetLastModifiedBy() {
    properties.put("kes:lastModifiedBy", "user");
    resource = context.create().resource("/resource", properties);

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertEquals("user", baseComponent.getLastModifiedBy().getId());
  }

  @Test
  public void testGetLastModifiedByWhenUserRetrievalException() throws UserRetrievalException {
    when(userService.getUser(any(), any())).thenThrow(UserRetrievalException.class);
    properties.put("kes:lastModifiedBy", "user");
    resource = context.create().resource("/resource", properties);

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertNull(baseComponent.getLastModifiedBy());
  }

  @Test
  public void testGetEscapedPath() {
    resource = context.create().resource("/content/pages/page/jcr:content/test/resource");
    baseComponent = resource.adaptTo(BaseComponent.class);

    assertEquals("/content/pages/page/_jcr_content/test/resource", baseComponent.getEscapedPath());
  }

  @Test
  public void testGetCreatedBy() {
    properties.put("kes:createdBy", "user");
    resource = context.create().resource("/resource", properties);

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertEquals("user", baseComponent.getCreatedBy().getId());
  }

  @Test
  public void testGetCreatedByWhenUserRetrievalException() throws UserRetrievalException {
    when(userService.getUser(any(), any())).thenThrow(UserRetrievalException.class);
    properties.put("kes:createdBy", "user");
    resource = context.create().resource("/resource", properties);

    baseComponent = resource.adaptTo(BaseComponent.class);

    assertNull(baseComponent.getCreatedBy());
  }
}