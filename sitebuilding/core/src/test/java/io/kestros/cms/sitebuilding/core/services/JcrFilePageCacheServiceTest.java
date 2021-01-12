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

package io.kestros.cms.sitebuilding.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import io.kestros.cms.sitebuilding.api.models.BaseContentPage;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class JcrFilePageCacheServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private JcrFilePageCacheService cacheService;

  private ResourceResolverFactory resourceResolverFactory;

  private BaseContentPage page;

  private Resource resource;

  private Map<String, Object> pageProperties = new HashMap<>();
  private Map<String, Object> pageJcrContentProperties = new HashMap<>();

  private JobManager jobManager;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    resourceResolverFactory = mock(ResourceResolverFactory.class);
    jobManager = mock(JobManager.class);

    context.registerService(ResourceResolverFactory.class, resourceResolverFactory);
    context.registerService(JobManager.class, jobManager);

    cacheService = new JcrFilePageCacheService();
    context.registerInjectActivateService(cacheService);
  }

  @Test
  public void testGetServiceCacheRootPath() {
    assertEquals("/var/cache/pages", cacheService.getServiceCacheRootPath());
  }

  @Test
  public void testGetServiceUserName() {
    assertEquals("kestros-page-cache", cacheService.getServiceUserName());
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("Page Cache Service", cacheService.getDisplayName());
  }

  @Test
  public void testGetResourceResolverFactory() {
    assertNotNull(cacheService.getResourceResolverFactory());
  }

  @Test
  public void testGetMinimumTimeBetweenCachePurges() {
    assertEquals(1000, cacheService.getMinimumTimeBetweenCachePurges());
  }

  @Test
  public void testGetCacheCreationJobName() {
    assertNull(cacheService.getCacheCreationJobName());
  }

  @Test
  public void testGetJobManager() {
    assertEquals(jobManager, cacheService.getJobManager());
  }

  @Test
  public void testCachePage() throws CacheBuilderException {
    context.create().resource("/var/cache/pages");

    resource = context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageJcrContentProperties);

    page = resource.adaptTo(BaseContentPage.class);

    cacheService.cachePage(page, "<p>output</p>");
    assertNotNull(context.resourceResolver().getResource("/var/cache/pages/content/page.html"));
  }

  @Test
  public void testGetCachedOutput() throws CacheBuilderException, CacheRetrievalException {
    context.create().resource("/var/cache/pages");
    resource = context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageJcrContentProperties);
    page = resource.adaptTo(BaseContentPage.class);
    cacheService.cachePage(page, "<p>output</p>");
    assertEquals("<p>output</p>", cacheService.getCachedOutput(page));
  }
}