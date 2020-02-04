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

package io.kestros.cms.foundation.eventlisteners.pagecachepurge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import io.kestros.cms.foundation.eventlisteners.pagecachepurge.PageCachePurgeEventListener;
import io.kestros.cms.foundation.services.pagecacheservice.PageCacheService;
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