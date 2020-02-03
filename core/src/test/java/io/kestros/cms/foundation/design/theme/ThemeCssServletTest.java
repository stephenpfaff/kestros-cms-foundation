package io.kestros.cms.foundation.design.theme;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import io.kestros.commons.uilibraries.config.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ThemeCssServletTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ThemeCssServlet servlet;

  private UiLibraryCacheService uiLibraryCacheService;
  private UiLibraryConfigurationService uiLibraryConfigurationService;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("com.slingware");

    uiLibraryConfigurationService = mock(UiLibraryConfigurationService.class);
    uiLibraryCacheService = mock(UiLibraryCacheService.class);
    context.registerService(UiLibraryConfigurationService.class, uiLibraryConfigurationService);
    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);

    servlet = new ThemeCssServlet();
    context.registerInjectActivateService(servlet);
  }

  @Test
  public void testGetUiLibraryCacheService() {
    assertEquals(uiLibraryCacheService, servlet.getUiLibraryCacheService());
  }

  @Test
  public void testGetUiLibraryConfigurationService() {
    assertEquals(uiLibraryConfigurationService, servlet.getUiLibraryConfigurationService());
  }

  @Test
  public void testGetUiLibraryClass() {
    assertEquals(Theme.class, servlet.getUiLibraryClass());
  }
}