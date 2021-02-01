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

package io.kestros.cms.uiframeworks.refactored.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.exceptions.HtlTemplateFileRetrievalException;
import io.kestros.cms.uiframeworks.refactored.models.UiFrameworkResource;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CachePurgeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HtlTemplateCacheServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private HtlTemplateCacheServiceImpl cacheService;

  private HtlTemplateFileRetrievalServiceImpl htlTemplateFileRetrievalService;

  private UiFrameworkRetrievalServiceImpl uiFrameworkRetrievalService;

  private ResourceResolverFactory resourceResolverFactory;

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

    context.registerService(ResourceResolverFactory.class, resourceResolverFactory);
    context.registerService(JobManager.class, jobManager);

    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    templateFileProperties.put("jcr:primaryType", "nt:file");
    templateFileJcrContentProperties.put("jcr:mimeType", "text/html");

    InputStream templateFileInputStream = new ByteArrayInputStream(
        "<template data-sly-template.testTemplateOne=\"${ @ text}\"></template>".getBytes());
    templateFileJcrContentProperties.put("jcr:data", templateFileInputStream);

    cacheService = spy(new HtlTemplateCacheServiceImpl());
    uiFrameworkRetrievalService = spy(new UiFrameworkRetrievalServiceImpl());
    htlTemplateFileRetrievalService = spy(new HtlTemplateFileRetrievalServiceImpl());
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
    context.registerInjectActivateService(cacheService);

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
    assertEquals("HTL Template Cache Service", cacheService.getDisplayName());
  }

  @Test
  public void testCacheCompiledHtlTemplates()
      throws LoginException, CacheBuilderException, HtlTemplateFileRetrievalException {
    doReturn(resourceResolverFactory).when(cacheService).getResourceResolverFactory();
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerInjectActivateService(htlTemplateFileRetrievalService);
    context.registerInjectActivateService(uiFrameworkRetrievalService);
    context.registerInjectActivateService(cacheService);

    context.create().resource("/apps/kestros/cache/compiled-htl-templates");

    Resource resource = context.create().resource("/etc/ui-frameworks/ui-framework-1",
        uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-1/templates",
        templatesFolderProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-1/templates/template-file",
        templateFileProperties);
    context.create().resource(
        "/etc/ui-frameworks/ui-framework-1/templates/template-file/jcr:content",
        templateFileJcrContentProperties);

    UiFrameworkResource uiFramework = resource.adaptTo(UiFrameworkResource.class);
    cacheService.cacheCompiledHtlTemplates(uiFramework);
  }

  @Test
  public void testCacheAllUiFrameworkCompiledHtlTemplates()
      throws CacheBuilderException, ResourceNotFoundException, LoginException {
    doReturn(resourceResolverFactory).when(cacheService).getResourceResolverFactory();
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerInjectActivateService(htlTemplateFileRetrievalService);
    context.registerInjectActivateService(uiFrameworkRetrievalService);
    context.registerInjectActivateService(cacheService);

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
    verify(cacheService, times(2)).cacheCompiledHtlTemplates(any());
  }

  @Test
  public void testDoPurge()
      throws CacheBuilderException, CachePurgeException, HtlTemplateFileRetrievalException,
             LoginException {
    doReturn(resourceResolverFactory).when(cacheService).getResourceResolverFactory();
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerInjectActivateService(htlTemplateFileRetrievalService);
    context.registerInjectActivateService(uiFrameworkRetrievalService);
    context.registerInjectActivateService(cacheService);

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
    verify(cacheService, times(2)).cacheCompiledHtlTemplates(any());
    cacheService.doPurge(context.resourceResolver());
    verify(cacheService, times(4)).cacheCompiledHtlTemplates(any());
  }

  @Test
  public void testRunAdditionalHealthChecksWhenNoCacheRootResources() throws LoginException {
    doReturn(resourceResolverFactory).when(cacheService).getResourceResolverFactory();
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerInjectActivateService(htlTemplateFileRetrievalService);
    context.registerInjectActivateService(uiFrameworkRetrievalService);
    context.registerInjectActivateService(cacheService);

    FormattingResultLog log = spy(new FormattingResultLog());
    cacheService.runAdditionalHealthChecks(log);

    verify(log, times(1)).critical("Root template cache folder not found.");
    verify(log, never()).warn(anyString());
    verify(log, never()).info(anyString());
    verify(log, never()).debug(anyString());
  }

  @Test
  public void testRunAdditionalHealthChecksWhenNoCachedFiles()
      throws CacheBuilderException, LoginException {
    doReturn(resourceResolverFactory).when(cacheService).getResourceResolverFactory();
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerInjectActivateService(htlTemplateFileRetrievalService);
    context.registerInjectActivateService(uiFrameworkRetrievalService);
    context.registerInjectActivateService(cacheService);

    context.create().resource("/apps/kestros/cache/compiled-htl-templates");

    FormattingResultLog log = spy(new FormattingResultLog());

    cacheService.runAdditionalHealthChecks(log);

    verify(log, times(0)).critical(anyString());
    verify(log, times(1)).warn("HtlTemplateCacheService has no cached compilation files.");
    verify(log, never()).info(anyString());
    verify(log, never()).debug(anyString());
  }

  @Test
  public void testRunAdditionalHealthChecksWhenHasCachedFiles()
      throws CacheBuilderException, LoginException {
    doReturn(resourceResolverFactory).when(cacheService).getResourceResolverFactory();
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerInjectActivateService(htlTemplateFileRetrievalService);
    context.registerInjectActivateService(uiFrameworkRetrievalService);
    context.registerInjectActivateService(cacheService);

    context.create().resource("/apps/kestros/cache/compiled-htl-templates");

    FormattingResultLog log = spy(new FormattingResultLog());

    context.create().resource("/etc/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-1/templates",
        templatesFolderProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-1/templates/template-file.html",
        templateFileProperties);
    context.create().resource(
        "/etc/ui-frameworks/ui-framework-1/templates/template-file.html/jcr:content",
        templateFileJcrContentProperties);

    cacheService.cacheAllUiFrameworkCompiledHtlTemplates();

    cacheService.runAdditionalHealthChecks(log);

    verify(log, never()).critical(anyString());
    verify(log, never()).warn(anyString());
    verify(log, never()).info(anyString());
    verify(log, never()).debug(anyString());
  }

  @Test
  public void testGetCompiledTemplateFilePath()
      throws ResourceNotFoundException, CacheBuilderException, LoginException {
    doReturn(resourceResolverFactory).when(cacheService).getResourceResolverFactory();
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerInjectActivateService(htlTemplateFileRetrievalService);
    context.registerInjectActivateService(uiFrameworkRetrievalService);
    context.registerInjectActivateService(cacheService);

    context.create().resource("/apps/kestros/cache/compiled-htl-templates");

    Resource resource = context.create().resource("/etc/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-1/templates",
        templatesFolderProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-1/templates/template-file",
        templateFileProperties);
    context.create().resource(
        "/etc/ui-frameworks/ui-framework-1/templates/template-file/jcr:content",
        templateFileJcrContentProperties);

    UiFrameworkResource uiFramework = resource.adaptTo(UiFrameworkResource.class);
    cacheService.cacheAllUiFrameworkCompiledHtlTemplates();

    assertEquals("/apps/kestros/cache/compiled-htl-templates/etc/ui-frameworks/ui-framework-1.html",
        cacheService.getCompiledTemplateFilePath(uiFramework));
  }

  @Test
  public void testGetCompiledTemplateFilePathWhenRootResourceNotFound() throws LoginException {
    doReturn(resourceResolverFactory).when(cacheService).getResourceResolverFactory();
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerInjectActivateService(htlTemplateFileRetrievalService);
    context.registerInjectActivateService(uiFrameworkRetrievalService);
    context.registerInjectActivateService(cacheService);

    UiFrameworkResource uiFramework = context.create().resource("/etc/ui-frameworks/ui-framework-1",
        uiFrameworkProperties).adaptTo(UiFrameworkResource.class);
    Exception e = null;

    try {
      cacheService.getCompiledTemplateFilePath(uiFramework);
    } catch (ResourceNotFoundException resourceNotFoundException) {
      e = resourceNotFoundException;
    }
    assertNotNull(e);
    assertEquals(
        "Unable to adapt '/apps/kestros/cache/compiled-htl-templates': Resource not found.",
        e.getMessage());
  }

  @Test
  public void testGetMinimumTimeBetweenCachePurges() {
    assertEquals(1000, cacheService.getMinimumTimeBetweenCachePurges());
  }
}