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

package io.kestros.cms.foundation.services.cache.htltemplate.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CachePurgeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseHtlTemplateCacheServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BaseHtlTemplateCacheService cacheService;

  private ResourceResolverFactory resourceResolverFactory;

  private ResourceResolver resourceResolver;

  private JobManager jobManager;

  private Map<String, Object> uiFrameworkProperties = new HashMap<>();

  private Map<String, Object> templatesFolderProperties = new HashMap<>();

  private Map<String, Object> templateFileProperties = new HashMap<>();

  private Map<String, Object> templateFileJcrContentProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    resourceResolverFactory = mock(ResourceResolverFactory.class);
    jobManager = mock(JobManager.class);
    resourceResolver = context.resourceResolver();

    context.registerService(ResourceResolverFactory.class, resourceResolverFactory);
    context.registerService(JobManager.class, jobManager);

    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    templateFileProperties.put("jcr:primaryType", "nt:file");
    templateFileJcrContentProperties.put("jcr:mimeType", "text/html");

    InputStream templateFileInputStream = new ByteArrayInputStream(
        "<template data-sly-template.testTemplateOne=\"${ @ text}\"></template>".getBytes());
    templateFileJcrContentProperties.put("jcr:data", templateFileInputStream);

    cacheService = spy(new BaseHtlTemplateCacheService());
    context.registerInjectActivateService(cacheService);
  }

  @Test
  public void testGetServiceCacheRootPath() {
    assertEquals("/apps/kestros/cache/compiled-htl-templates",
        cacheService.getServiceCacheRootPath());
  }

  @Test
  public void testGetServiceUserName() {
    assertEquals("kestros-htl-template-cache", cacheService.getServiceUserName());
  }

  @Test
  public void testGetResourceResolverFactory() {
    assertNotNull(cacheService.getResourceResolverFactory());
  }

  @Test
  public void testGetCacheCreationJobName() {
    assertNull(cacheService.getCacheCreationJobName());
  }

  @Test
  public void testGetJobManager() {
    assertNull(cacheService.getJobManager());
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("HTL Template Cache", cacheService.getDisplayName());
  }

  @Test
  public void testCacheAllUiFrameworkCompiledHtlTemplates()
      throws CacheBuilderException, ResourceNotFoundException, LoginException {
    doReturn(resourceResolver).when(cacheService).getServiceResourceResolver();

    context.create().resource("/apps/kestros/cache/compiled-htl-templates");

    context.create().resource("/etc/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-1/templates",
        templatesFolderProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-1/templates/template-file",
        templateFileProperties);
    context.create().resource(
        "/etc/ui-frameworks/ui-framework-1/templates/template-file/jcr:content",
        templateFileJcrContentProperties);

    context.create().resource("/etc/ui-frameworks/ui-framework-2", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-2/templates");
    context.create().resource("/etc/ui-frameworks/ui-framework-2/templates/template-file",
        templateFileProperties);
    context.create().resource(
        "/etc/ui-frameworks/ui-framework-2/templates/template-file/jcr:content",
        templateFileJcrContentProperties);

    cacheService.cacheAllUiFrameworkCompiledHtlTemplates();
    verify(cacheService, times(2)).cacheUiFrameworkCompiledHtlTemplates(any());
  }

  @Test
  public void testDoPurge() throws CacheBuilderException, CachePurgeException {
    doReturn(resourceResolver).when(cacheService).getServiceResourceResolver();

    context.create().resource("/apps/kestros/cache/compiled-htl-templates");

    context.create().resource("/etc/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-1/templates",
        templatesFolderProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-1/templates/template-file",
        templateFileProperties);
    context.create().resource(
        "/etc/ui-frameworks/ui-framework-1/templates/template-file/jcr:content",
        templateFileJcrContentProperties);

    context.create().resource("/etc/ui-frameworks/ui-framework-2", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-2/templates");
    context.create().resource("/etc/ui-frameworks/ui-framework-2/templates/template-file",
        templateFileProperties);
    context.create().resource(
        "/etc/ui-frameworks/ui-framework-2/templates/template-file/jcr:content",
        templateFileJcrContentProperties);

    cacheService.cacheAllUiFrameworkCompiledHtlTemplates();
    verify(cacheService, times(2)).cacheUiFrameworkCompiledHtlTemplates(any());
    cacheService.doPurge(context.resourceResolver());
    verify(cacheService, times(4)).cacheUiFrameworkCompiledHtlTemplates(any());
  }

  @Test
  public void testGetMinimumTimeBetweenCachePurges() {
    assertEquals(1000, cacheService.getMinimumTimeBetweenCachePurges());
  }
}