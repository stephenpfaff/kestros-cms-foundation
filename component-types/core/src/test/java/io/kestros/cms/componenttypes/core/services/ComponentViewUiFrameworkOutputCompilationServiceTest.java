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

package io.kestros.cms.componenttypes.core.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewService;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.uilibraries.services.compilation.UiLibraryCompilationService;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ComponentViewUiFrameworkOutputCompilationServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ComponentViewUiFrameworkOutputCompilationServiceImpl compilationService;
  private ComponentUiFrameworkViewService componentUiFrameworkViewService;
  private UiLibraryCompilationService uiLibraryCompilationService;
  private UiFramework uiFramework;
  private Resource resource;

  private Map<String, Object> uiFrameworkProperties = new HashMap<>();
  private Map<String, Object> componentTypeProperties = new HashMap<>();
  private Map<String, Object> variationProperties = new HashMap<>();

  @Before
  public void setup() {
    context.addModelsForPackage("io.kestros");
    uiLibraryCompilationService = mock(UiLibraryCompilationService.class);
    componentUiFrameworkViewService = spy(new ComponentUiFrameworkViewServiceImpl());

    context.registerService(UiLibraryCompilationService.class, uiLibraryCompilationService);
    compilationService = new ComponentViewUiFrameworkOutputCompilationServiceImpl();
    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
    variationProperties.put("jcr:primaryType", "kes:ComponentVariation");
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("Component UI Framework View Output Compilation Service",
        compilationService.getDisplayName());
  }

  @Test
  public void testRunAdditionalHealthChecks() {
    FormattingResultLog log = spy(new FormattingResultLog());
    compilationService.runAdditionalHealthChecks(log);

    verify(log, never()).debug(anyString());
    verify(log, never()).info(anyString());
    verify(log, never()).warn(anyString());
    verify(log, never()).critical(anyString());
    verify(log, never()).healthCheckError(anyString());
  }

  @Test
  public void testGetCssOutputWhenComponentUiFrameworkViewServiceIsNull() throws Exception {
    resource = context.create().resource("/etc/ui-frameworks/framework", uiFrameworkProperties);
    assertEquals("", compilationService.getAppendedOutput(uiFramework, ScriptType.CSS));
  }

  @Test
  public void testGetCssOutputWhenFrameworkHasNoViews() throws Exception {
    context.registerInjectActivateService(compilationService);
    resource = context.create().resource("/etc/ui-frameworks/framework", uiFrameworkProperties);
    assertEquals("", compilationService.getAppendedOutput(uiFramework, ScriptType.CSS));
  }

  @Test
  public void testGetCssOutputWhenFrameworkHasViews() throws Exception {
    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        ".component-1{}").thenReturn(".component-2{}");
    context.registerService(ComponentUiFrameworkViewService.class, componentUiFrameworkViewService);
    context.registerInjectActivateService(compilationService);
    context.create().resource("/apps/component-1", componentTypeProperties);
    context.create().resource("/apps/component-1/framework");
    context.create().resource("/apps/component-2", componentTypeProperties);
    context.create().resource("/apps/component-2/framework");

    context.registerInjectActivateService(compilationService);
    resource = context.create().resource("/etc/ui-frameworks/framework", uiFrameworkProperties);
    uiFramework = resource.adaptTo(UiFramework.class);
    assertEquals(".component-1{}\n.component-2{}",
        compilationService.getAppendedOutput(uiFramework, ScriptType.CSS));
    verify(componentUiFrameworkViewService, times(1)).getComponentViews(any());
  }

  @Test
  public void testGetCssOutputWhenFrameworkHasViewsAndVariations() throws Exception {
    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        ".component-1{}").thenReturn(".variation-1{}").thenReturn(".component-2{}").thenReturn(
        ".variation-2{}");
    context.registerService(ComponentUiFrameworkViewService.class, componentUiFrameworkViewService);
    context.registerInjectActivateService(compilationService);
    context.create().resource("/apps/component-1", componentTypeProperties);
    context.create().resource("/apps/component-1/framework");
    context.create().resource("/apps/component-1/framework/variations/variation-1",variationProperties);
    context.create().resource("/apps/component-2", componentTypeProperties);
    context.create().resource("/apps/component-2/framework");
    context.create().resource("/apps/component-2/framework/variations/variation-2",variationProperties);

    context.registerInjectActivateService(compilationService);
    resource = context.create().resource("/etc/ui-frameworks/framework", uiFrameworkProperties);
    uiFramework = resource.adaptTo(UiFramework.class);
    assertEquals(".component-1{}\n.variation-1{}\n.component-2{}\n.variation-2{}",
        compilationService.getAppendedOutput(uiFramework, ScriptType.CSS));
    verify(componentUiFrameworkViewService, times(1)).getComponentViews(any());
  }

}