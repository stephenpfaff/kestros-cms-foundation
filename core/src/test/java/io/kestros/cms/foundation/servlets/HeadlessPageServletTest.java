package io.kestros.cms.foundation.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.kestros.cms.foundation.services.headlessrender.HeadlessRenderService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HeadlessPageServletTest {

  @Rule
  public SlingContext context = new SlingContext();

  private HeadlessPageServlet headlessPageServlet;

  private HeadlessRenderService headlessRenderService;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    headlessPageServlet = spy(new HeadlessPageServlet());
  }

  @Test
  public void testDoGet() throws ServletException, IOException, InvalidResourceTypeException {
    headlessRenderService = mock(HeadlessRenderService.class);
    context.registerService(HeadlessRenderService.class, headlessRenderService);

    context.registerInjectActivateService(headlessPageServlet);

    headlessPageServlet.doGet(context.request(), context.response());
    assertEquals(200, context.response().getStatus());
    verify(headlessRenderService, times(1)).renderHeadlessResponse(context.request(),
        context.response());
  }

  @Test
  public void testDoGetWhenInvalidResourceType()
      throws ServletException, IOException, InvalidResourceTypeException {
    headlessRenderService = mock(HeadlessRenderService.class);
    context.registerService(HeadlessRenderService.class, headlessRenderService);
    doThrow(InvalidResourceTypeException.class).when(headlessRenderService).renderHeadlessResponse(
        context.request(), context.response());
    context.registerInjectActivateService(headlessPageServlet);

    headlessPageServlet.doGet(context.request(), context.response());
    assertEquals(400, context.response().getStatus());
    verify(headlessRenderService, times(1)).renderHeadlessResponse(context.request(),
        context.response());
  }

  @Test
  public void testDoGetWhenHeadlessRenderServiceIsNull()
      throws ServletException, IOException, InvalidResourceTypeException {
    headlessRenderService = mock(HeadlessRenderService.class);
    context.registerService(HeadlessRenderService.class, headlessRenderService);

    context.registerInjectActivateService(headlessPageServlet);

    doReturn(null).when(headlessPageServlet).getHeadlessRenderService();
    headlessPageServlet.doGet(context.request(), context.response());
    assertEquals(400, context.response().getStatus());
    verify(headlessRenderService, never()).renderHeadlessResponse(context.request(),
        context.response());

  }
}