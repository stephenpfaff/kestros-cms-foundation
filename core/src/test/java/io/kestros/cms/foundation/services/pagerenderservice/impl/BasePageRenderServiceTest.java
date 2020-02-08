package io.kestros.cms.foundation.services.pagerenderservice.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.cms.foundation.services.pagerendermethod.PageRenderMethod;
import java.io.IOException;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BasePageRenderServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BasePageRenderService pageRenderService;

  private PageRenderMethod pageRenderMethod1;
  private PageRenderMethod pageRenderMethod2;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    pageRenderMethod1 = mock(PageRenderMethod.class);
    pageRenderMethod2 = mock(PageRenderMethod.class);
    context.registerService(PageRenderMethod.class, pageRenderMethod1);
    context.registerService(PageRenderMethod.class, pageRenderMethod2);

    pageRenderService = new BasePageRenderService();
  }

  @Test
  public void testActivate() {
    pageRenderService.activate(context.componentContext());
    assertNotNull(pageRenderService);
  }

  @Test
  public void testRender() throws IOException {
    context.registerInjectActivateService(pageRenderService);
    pageRenderService.activate(context.componentContext());

    when(pageRenderMethod1.useRenderMethod(any())).thenReturn(true);
    when(pageRenderMethod2.useRenderMethod(any())).thenReturn(false);

    pageRenderService.render(context.request(), context.response());

    verify(pageRenderMethod1, times(1)).doRender(context.request(), context.response());
    verify(pageRenderMethod2, never()).doRender(context.request(), context.response());
  }

  @Test
  public void testRenderWhenFirstMethodShouldNotRender() throws IOException {
    context.registerInjectActivateService(pageRenderService);
    pageRenderService.activate(context.componentContext());

    when(pageRenderMethod1.useRenderMethod(context.request())).thenReturn(false);
    when(pageRenderMethod2.useRenderMethod(context.request())).thenReturn(true);

    pageRenderService.render(context.request(), context.response());
    verify(pageRenderMethod1, never()).doRender(context.request(), context.response());
    verify(pageRenderMethod2, times(1)).doRender(context.request(), context.response());
  }

  @Test
  public void testGetPageRenderMethods() {
    context.registerInjectActivateService(pageRenderService);
    pageRenderService.activate(context.componentContext());
    assertEquals(2, pageRenderService.getPageRenderMethods().size());
  }
}