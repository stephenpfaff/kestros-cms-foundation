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

package io.kestros.cms.foundation.services.pagerendermethod.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.cms.foundation.services.pagecacheservice.GeneralPageCacheService;
import io.kestros.cms.foundation.services.pagecacheservice.impl.JcrFilePageCacheService;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.apache.sling.testing.mock.sling.servlet.MockRequestDispatcherFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BasePageRenderMethodTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BasePageRenderMethod pageRenderMethod;
  private MockRequestDispatcherFactory mockRequestDispatcherFactory;
  private GeneralPageCacheService generalPageCacheService;

  private RequestDispatcher requestDispatcher;

  private Resource resource;


  private Map<String, Object> pageProperties = new HashMap<>();
  private Map<String, Object> pageJcrContentProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    requestDispatcher = mock(RequestDispatcher.class);
    generalPageCacheService = mock(JcrFilePageCacheService.class);
    context.registerService(GeneralPageCacheService.class, generalPageCacheService);

    pageRenderMethod = spy(new BasePageRenderMethod());

    pageProperties.put("jcr:primaryType", "kes:Page");
  }

  @Test
  public void testDoRender() throws IOException, CacheBuilderException, CacheRetrievalException {

    mockRequestDispatcherFactory = new MockRequestDispatcherFactory() {
      @Override
      public RequestDispatcher getRequestDispatcher(String path, RequestDispatcherOptions options) {
        return requestDispatcher;
      }

      @Override
      public RequestDispatcher getRequestDispatcher(Resource resource,
          RequestDispatcherOptions options) {
        return requestDispatcher;
      }
    };

    context.request().setRequestDispatcherFactory(mockRequestDispatcherFactory);

    resource = context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", pageJcrContentProperties);
    context.request().setResource(resource);

    doReturn(generalPageCacheService).when(pageRenderMethod).getPageCacheService();
    doThrow(CacheRetrievalException.class).when(generalPageCacheService).getCachedOutput(any());

    pageRenderMethod.doRender(context.request(), context.response());

    //    verify(generalPageCacheService, times(1)).cachePage(resource.adaptTo(BaseContentPage
    //    .class),
    //        context.response());
    assertEquals(200, context.response().getStatus());
    assertEquals("text/html", context.response().getContentType());
  }

  @Test
  public void testDoRenderWhenPageIsCached() throws IOException, CacheRetrievalException {

    doReturn(generalPageCacheService).when(pageRenderMethod).getPageCacheService();

    when(generalPageCacheService.getCachedOutput(any())).thenReturn("<p>cachedOutput</p>");

    resource = context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", pageJcrContentProperties);
    context.request().setResource(resource);

    context.registerInjectActivateService(pageRenderMethod);
    pageRenderMethod.doRender(context.request(), context.response());

    assertNotNull(pageRenderMethod.getPageCacheService());
    verify(generalPageCacheService, times(1)).getCachedOutput(any());
    assertEquals(200, context.response().getStatus());
    assertEquals("text/html", context.response().getContentType());
  }

  @Test
  public void testDoRenderWhenRequestDispatcherIsNull() throws IOException {

    mockRequestDispatcherFactory = new MockRequestDispatcherFactory() {
      @Override
      public RequestDispatcher getRequestDispatcher(String path, RequestDispatcherOptions options) {
        return null;
      }

      @Override
      public RequestDispatcher getRequestDispatcher(Resource resource,
          RequestDispatcherOptions options) {
        return null;
      }
    };

    context.request().setRequestDispatcherFactory(mockRequestDispatcherFactory);

    resource = context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", pageJcrContentProperties);
    context.request().setResource(resource);
    pageRenderMethod.doRender(context.request(), context.response());
    assertEquals(404, context.response().getStatus());
  }

  @Test
  public void testDoRenderWhenJcrContentRequested() throws IOException {
    context.create().resource("/page", pageProperties);
    resource = context.create().resource("/page/jcr:content", pageJcrContentProperties);
    context.request().setResource(resource);
    pageRenderMethod.doRender(context.request(), context.response());
    assertEquals(404, context.response().getStatus());
  }


  @Test
  public void testDoRenderWhenPageDoesNotHaveJcrContent() throws IOException {
    resource = context.create().resource("/page", pageProperties);
    context.request().setResource(resource);
    pageRenderMethod.doRender(context.request(), context.response());
    assertEquals(404, context.response().getStatus());
  }


  @Test
  public void testUseRenderMethod() {
    assertTrue(pageRenderMethod.useRenderMethod(context.request()));
  }
}