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

package io.kestros.cms.foundation.servlets.validation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.commons.structuredslingmodels.BaseSlingModel;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BasicValidationServletTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BasicValidationServlet basicValidationServlet;

  private BaseSlingModel model;

  private BaseContentPage baseContentPage;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  private ModelFactory modelFactory;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    modelFactory = mock(ModelFactory.class);

    context.registerService(ModelFactory.class, modelFactory);
  }

  @Test
  public void doValidation() {
    model = mock(BaseSlingModel.class);
    basicValidationServlet = new BasicValidationServlet();
    basicValidationServlet.doValidation(model);
    verify(model, never()).doDetailedValidation();
    verify(model, times(1)).validate();
  }

  @Test
  public void doGet() throws ServletException, IOException {

    properties.put("sling:resourceType", "kes:Page");
    resource = context.create().resource("/resource", properties);
    context.request().setResource(resource);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    when(modelFactory.getModelFromResource(resource)).thenReturn(baseContentPage);
    basicValidationServlet = new BasicValidationServlet();
    context.registerInjectActivateService(basicValidationServlet);
    basicValidationServlet.doGet(context.request(), context.response());

    assertEquals(200, context.response().getStatus());
  }

  @Test
  public void doGetWhenInvalidResourceType() throws ServletException, IOException {
    when(modelFactory.getModelFromResource(resource)).thenReturn(
        InvalidResourceTypeException.class);
    resource = context.create().resource("/resource");
    context.request().setResource(resource);

    basicValidationServlet = new BasicValidationServlet();

    basicValidationServlet.doGet(context.request(), context.response());

    assertEquals(400, context.response().getStatus());
  }
}