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

package io.kestros.cms.sitebuilding.core.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.kestros.cms.sitebuilding.api.services.HeadlessRenderService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class HeadlessPageServletTest {

  @Rule
  public SlingContext context = new SlingContext();

  private HeadlessPageServlet headlessPageServlet;

  private HeadlessRenderService headlessRenderService;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    headlessPageServlet = Mockito.spy(new HeadlessPageServlet());
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