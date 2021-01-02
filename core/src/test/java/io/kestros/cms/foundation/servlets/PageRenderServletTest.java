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

package io.kestros.cms.foundation.servlets;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.kestros.cms.foundation.services.PageRenderService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class PageRenderServletTest {

  @Rule
  public SlingContext context = new SlingContext();

  private PageRenderServlet pageRenderServlet;

  private PageRenderService pageRenderService;

  private Resource resource;

  private Resource jcrContentResource;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    pageRenderService = mock(PageRenderService.class);
    pageRenderServlet = spy(new PageRenderServlet());
  }

  @Test
  public void testDoGet() throws Exception {
    resource = context.create().resource("/page");
    jcrContentResource = context.create().resource("/page/jcr:content");

    context.request().setResource(resource);

    doReturn(pageRenderService).when(pageRenderServlet).getPageRenderService();
    pageRenderServlet.doGet(context.request(), context.response());

    verify(pageRenderService, times(1)).render(context.request(), context.response());
  }

  @Test
  public void testDoGetWhenNullPageRenderService() throws Exception {
    resource = context.create().resource("/page");
    jcrContentResource = context.create().resource("/page/jcr:content");

    context.request().setResource(resource);

    pageRenderServlet.doGet(context.request(), context.response());

    verify(pageRenderService, never()).render(context.request(), context.response());
  }
}