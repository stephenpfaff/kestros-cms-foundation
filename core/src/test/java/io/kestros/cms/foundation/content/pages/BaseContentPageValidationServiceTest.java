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

package io.kestros.cms.foundation.content.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.foundation.services.themeprovider.BaseThemeProviderService;
import io.kestros.cms.foundation.services.themeprovider.ThemeProviderService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseContentPageValidationServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BaseContentPageValidationService validationService;

  private BaseContentPage page;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  private Map<String, Object> jcrContentProperties = new HashMap<>();

  private List<BaseContentPage> childPageList = new ArrayList<>();

  private List<BaseComponent> componentList = new ArrayList<>();

  private List<String> componentErrorMessages = new ArrayList<>();

  private List<String> componentWarningMessages = new ArrayList<>();

  private ModelValidator modelValidator;

  private ThemeProviderService themeProviderService;

  @Before
  public void setUp() throws Exception {
    context.addModelsForClasses("io.kestros");

    themeProviderService = new BaseThemeProviderService();

    context.registerService(ThemeProviderService.class, themeProviderService);

    validationService = spy(new BaseContentPageValidationService());

    modelValidator = mock(ModelValidator.class);

    properties.put("jcr:primaryType", "kes:Page");

    componentErrorMessages.add("Error message.");
    componentWarningMessages.add("Warning message.");
  }

  @Test
  public void getModel() {
    assertEquals(page, validationService.getModel());
  }

  @Test
  public void registerBasicValidators() {
    resource = context.create().resource("/page", properties);
    context.create().resource("/page/jcr:content", jcrContentProperties);

    page = resource.adaptTo(BaseContentPage.class);

    doReturn(page).when(validationService).getGenericModel();

    validationService.registerBasicValidators();

    assertEquals(3, validationService.getBasicValidators().size());

    assertFalse(validationService.getBasicValidators().get(0).isValid());
    assertEquals("Title is configured.",
        validationService.getBasicValidators().get(0).getMessage());
    assertEquals(ModelValidationMessageType.ERROR,
        validationService.getBasicValidators().get(0).getType());

    assertFalse(validationService.getBasicValidators().get(1).isValid());
    assertEquals("Description is configured.",
        validationService.getBasicValidators().get(1).getMessage());
    assertEquals(ModelValidationMessageType.WARNING,
        validationService.getBasicValidators().get(1).getType());

    assertFalse(validationService.getBasicValidators().get(2).isValid());
    assertEquals("Must have an assigned Theme.",
        validationService.getBasicValidators().get(2).getMessage());
    assertEquals(ModelValidationMessageType.ERROR,
        validationService.getBasicValidators().get(2).getType());
  }

  @Test
  public void registerBasicValidatorsHasChildComponents() {
    BaseComponent component1 = mock(BaseComponent.class);
    BaseComponent component2 = mock(BaseComponent.class);

    componentList.add(component1);
    componentList.add(component2);

    when(modelValidator.isValid()).thenReturn(false);
    component1.getValidators().add(modelValidator);
    component2.getValidators().add(modelValidator);

    resource = context.create().resource("/page", properties);
    context.create().resource("/page/jcr:content", jcrContentProperties);

    page = spy(resource.adaptTo(BaseContentPage.class));

    when(component1.getErrorMessages()).thenReturn(componentErrorMessages);
    when(component1.getWarningMessages()).thenReturn(componentWarningMessages);
    when(component2.getErrorMessages()).thenReturn(componentErrorMessages);
    when(component2.getWarningMessages()).thenReturn(componentWarningMessages);

    doReturn(componentList).when(page).getAllDescendantComponents();
    doReturn(page).when(validationService).getGenericModel();

    validationService.registerBasicValidators();

    assertEquals(7, validationService.getBasicValidators().size());
  }

  @Test
  public void registerDetailedValidators() {
    BaseContentPage page1 = mock(BaseContentPage.class);
    BaseContentPage page2 = mock(BaseContentPage.class);

    when(page1.getPath()).thenReturn("/page/page-1");
    when(page2.getPath()).thenReturn("/page/page-2");

    when(page1.getErrorMessages()).thenReturn(componentErrorMessages);
    when(page1.getWarningMessages()).thenReturn(componentWarningMessages);

    when(page2.getErrorMessages()).thenReturn(componentErrorMessages);
    when(page2.getWarningMessages()).thenReturn(componentWarningMessages);
    childPageList.add(page1);
    childPageList.add(page2);

    resource = context.create().resource("/page", properties);
    context.create().resource("/page/jcr:content", jcrContentProperties);

    page = spy(resource.adaptTo(BaseContentPage.class));

    doReturn(childPageList).when(page).getChildPages();
    doReturn(page).when(validationService).getGenericModel();

    validationService.registerDetailedValidators();

    assertEquals(4, validationService.getDetailedValidators().size());
  }
}