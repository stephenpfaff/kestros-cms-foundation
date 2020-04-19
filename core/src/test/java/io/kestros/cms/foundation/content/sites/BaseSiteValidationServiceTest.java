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

package io.kestros.cms.foundation.content.sites;

import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import io.kestros.cms.foundation.services.themeprovider.BaseThemeProviderService;
import io.kestros.cms.foundation.services.themeprovider.ThemeProviderService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseSiteValidationServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BaseSiteValidationService validationService;

  private BaseSite site;

  private Resource resource;

  private Map<String, Object> pageProperties = new HashMap<>();

  private ThemeProviderService themeProviderService = new BaseThemeProviderService();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    context.registerService(ThemeProviderService.class, themeProviderService);

    validationService = new BaseSiteValidationService();
    validationService = spy(BaseSiteValidationService.class);

    resource = context.create().resource("/site");
    site = resource.adaptTo(BaseSite.class);

    doReturn(site).when(validationService).getGenericModel();

    pageProperties.put("jcr:primaryType", "kes:Page");
  }

  @Test
  public void testGetModel() {
    assertNotNull(validationService);
    assertEquals(BaseSite.class, validationService.getModel().getClass());
  }

  @Test
  public void testHasPages() {
    context.create().resource("/site/page", pageProperties);

    assertNotNull(validationService);

    validationService.registerBasicValidators();

    assertTrue(validationService.hasPages().isValid());
    assertEquals("Site has pages.", validationService.hasPages().getMessage());
    assertEquals(ERROR, validationService.hasPages().getType());
  }

  @Test
  public void testHasPagesWhenNoPages() {
    assertNotNull(validationService);

    validationService.registerBasicValidators();

    assertFalse(validationService.hasPages().isValid());
  }
}