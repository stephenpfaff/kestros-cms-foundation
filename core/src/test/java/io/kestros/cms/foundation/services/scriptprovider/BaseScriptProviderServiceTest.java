package io.kestros.cms.foundation.services.scriptprovider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Test;

public class BaseScriptProviderServiceTest {

  public SlingContext context = new SlingContext();

  private BaseScriptProviderService scriptProviderService;

  private ResourceResolverFactory resourceResolverFactory;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    resourceResolverFactory = mock(ResourceResolverFactory.class);

    context.registerService(ResourceResolverFactory.class, resourceResolverFactory);

    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    scriptProviderService = new BaseScriptProviderService();
    context.registerInjectActivateService(scriptProviderService);
  }

  @Test
  public void testGetServiceUserName() {
    assertEquals("kestros-htl-template-cache-purge", scriptProviderService.getServiceUserName());
  }

  @Test
  public void testGetResourceResolverFactory() {
    assertNotNull(scriptProviderService.getResourceResolverFactory());
  }
}