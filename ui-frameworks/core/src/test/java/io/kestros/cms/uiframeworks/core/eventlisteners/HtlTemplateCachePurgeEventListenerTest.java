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

package io.kestros.cms.uiframeworks.core.eventlisteners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import io.kestros.cms.uiframeworks.api.services.HtlTemplateCacheService;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HtlTemplateCachePurgeEventListenerTest {

  @Rule
  public SlingContext context = new SlingContext();

  private HtlTemplateCachePurgeEventListener eventListener;

  private HtlTemplateCacheService htlTemplateCacheService;

  private ResourceResolverFactory resourceResolverFactory;

  @Before
  public void setUp() throws Exception {
    eventListener = new HtlTemplateCachePurgeEventListener();
    htlTemplateCacheService = mock(HtlTemplateCacheService.class);
    resourceResolverFactory = mock(ResourceResolverFactory.class);

    context.registerService(HtlTemplateCacheService.class, htlTemplateCacheService);

    context.registerInjectActivateService(eventListener);
  }

  @Test
  public void testGetCacheService() {
    assertEquals(htlTemplateCacheService, eventListener.getCacheServices().get(0));
  }

  @Test
  public void testGetResourceResolverFactory() {
    assertNotNull(eventListener.getResourceResolverFactory());
  }

  @Test
  public void testGetServiceUserName() {
    assertEquals("kestros-htl-template-cache-purge", eventListener.getServiceUserName());
  }
}