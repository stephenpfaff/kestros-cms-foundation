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

package io.kestros.cms.sitebuilding.core.eventlisteners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import io.kestros.cms.sitebuilding.api.services.PageCacheService;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class PageCachePurgeEventListenerTest {

  @Rule
  public SlingContext context = new SlingContext();

  private PageCachePurgeEventListener eventListener;

  private PageCacheService pageCacheService;

  private ResourceResolverFactory resourceResolverFactory;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    pageCacheService = mock(PageCacheService.class);
    resourceResolverFactory = mock(ResourceResolverFactory.class);

    context.registerService(PageCacheService.class, pageCacheService);
    context.registerService(ResourceResolverFactory.class, resourceResolverFactory);

    eventListener = new PageCachePurgeEventListener();
    context.registerInjectActivateService(eventListener);
  }

  @Test
  public void testGetServiceUserName() {
    assertEquals("kestros-page-cache-purge", eventListener.getServiceUserName());
  }

  @Test
  public void testGetCacheServices() {
    assertEquals(1, eventListener.getCacheServices().size());
  }

  @Test
  public void testGetResourceResolverFactory() {
    assertNotNull(eventListener.getResourceResolverFactory());
  }
}