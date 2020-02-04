package io.kestros.cms.foundation.eventlisteners.htltemplatecachepurge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import io.kestros.cms.foundation.services.cache.htltemplate.HtlTemplateCacheService;
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