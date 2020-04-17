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
    assertEquals("kestros-script-provider", scriptProviderService.getServiceUserName());
  }

  @Test
  public void testGetResourceResolverFactory() {
    assertNotNull(scriptProviderService.getResourceResolverFactory());
  }
}