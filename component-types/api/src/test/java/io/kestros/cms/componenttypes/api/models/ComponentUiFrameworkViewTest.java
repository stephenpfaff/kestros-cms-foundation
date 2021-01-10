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

package io.kestros.cms.componenttypes.api.models;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.componenttypes.api.exceptions.InvalidScriptException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import io.kestros.commons.uilibraries.services.compilation.UiLibraryCompilationService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ComponentUiFrameworkViewTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ComponentUiFrameworkView componentUiFrameworkView;
  private UiLibraryCompilationService uiLibraryCompilationService;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();
  private Map<String, Object> componentTypeProperties = new HashMap<>();

  private Map<String, Object> validVariationProperties = new HashMap<>();

  private Map<String, Object> cssFolderProperties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> fileJcrContentProperties = new HashMap<>();


  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    uiLibraryCompilationService = mock(UiLibraryCompilationService.class);

    validVariationProperties.put("jcr:primaryType", "kes:ComponentVariation");
    fileProperties.put("jcr:primaryType", "nt:file");

  }

  @Test
  public void testGetTitle() {
    properties.put("jcr:title", "Title");
    resource = context.create().resource("/component/framework-view", properties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertEquals("Title", componentUiFrameworkView.getTitle());
  }

  @Test
  public void testGetTitleWhenNoTitleAndNoUiFramework() {
    resource = context.create().resource("/component/framework-view");

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertEquals("framework-view", componentUiFrameworkView.getTitle());
  }

  @Test
  public void testGetTitleWhenNoTitleAndHasUiFramework() {
    Map<String, Object> frameworkProperties = new HashMap<>();
    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    frameworkProperties.put("jcr:title", "Framework");
    frameworkProperties.put("kes:uiFrameworkCode", "framework");
    context.create().resource("/etc/ui-frameworks/framework", frameworkProperties);
    resource = context.create().resource("/component/framework");

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertEquals("Framework", componentUiFrameworkView.getTitle());
  }

  @Test
  public void testGetComponentType()
      throws InvalidResourceTypeException, NoParentResourceException {
    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
    context.create().resource("/apps/component", componentTypeProperties);

    resource = context.create().resource("/apps/component/framework-view");
    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertEquals("/apps/component", componentUiFrameworkView.getComponentType().getPath());
  }

  @Test
  public void testGetComponentTypeWhenOverlayingLibs()
      throws InvalidResourceTypeException, NoParentResourceException {
    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
    context.create().resource("/libs/component", componentTypeProperties);

    resource = context.create().resource("/apps/component/framework-view");
    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);
    assertEquals("/libs/component", componentUiFrameworkView.getComponentType().getPath());
  }

  @Test
  public void testGetComponentTypeWhenLibsResourceDoesNotExist()
      throws InvalidResourceTypeException {
    resource = context.create().resource("/apps/component/framework-view");
    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    Exception exception = null;
    try {
      componentUiFrameworkView.getComponentType().getPath();
    } catch (NoParentResourceException e) {
      exception = e;
    }
    assertEquals("Unable to retrieve parent of '/apps/component/framework-view':Parent not found.", exception.getMessage());
  }


  @Test
  public void testGetUiFrameworkViewScript() throws InvalidScriptException {
    fileJcrContentProperties.put("jcr:mimeType", "text/html");
    resource = context.create().resource("/component/framework-view");
    context.create().resource("/component/framework-view/content.html", fileProperties);
    context.create().resource("/component/framework-view/content.html/jcr:content",
        fileJcrContentProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertEquals("/component/framework-view/content.html",
        componentUiFrameworkView.getUiFrameworkViewScript("content.html").getPath());
  }

  @Test
  public void testGetUiFrameworkViewScriptWhenInvalidScriptType() {
    fileJcrContentProperties.put("jcr:mimeType", "text/css");
    resource = context.create().resource("/component/framework-view");
    context.create().resource("/component/framework-view/content.html", fileProperties);
    context.create().resource("/component/framework-view/content.html/jcr:content",
        fileJcrContentProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    try {
      componentUiFrameworkView.getUiFrameworkViewScript("content.html");
    } catch (InvalidScriptException e) {
      exception = e;
    }
    assertEquals(
        "Unable to adapt 'content.html' for ComponentUiFrameworkView '/component/framework-view':"
        + " Script not found.", exception.getMessage());
  }

  @Test
  public void testGetCssOutput() throws Exception {
    context.registerService(UiLibraryCompilationService.class, uiLibraryCompilationService);
    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        "body{ color:red;}");

    resource = context.create().resource("/component/framework-view");

    cssFolderProperties.put("include", new String[]{"my-css.css"});
    context.create().resource("/component/framework-view/css", cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/css/my-css.css", fileProperties);

    fileJcrContentProperties.put("jcr:mimeType", "text/css");
    context.create().resource("/component/framework-view/css/my-css.css/jcr:content",
        fileJcrContentProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertNotNull(componentUiFrameworkView.getOutput(CSS, false));

    assertEquals("body{ color:red;}", componentUiFrameworkView.getOutput(CSS, false));
  }

  @Test
  public void testGetCssOutputWhenUsingVariations() throws Exception {
    context.registerService(UiLibraryCompilationService.class, uiLibraryCompilationService);

    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        "body{ color:red;}").thenReturn("body{ color:variation-1;}").thenReturn(
        "body{ color:variation-2;}").thenReturn("body{ color:variation-3;}");

    fileJcrContentProperties.put("jcr:mimeType", "text/css");

    resource = context.create().resource("/component/framework-view");

    cssFolderProperties.put("include", new String[]{"my-css.css"});
    context.create().resource("/component/framework-view/css", cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/css/my-css.css", fileProperties);

    context.create().resource("/component/framework-view/css/my-css.css/jcr:content",
        fileJcrContentProperties);

    context.create().resource("/component/framework-view/variations");
    context.create().resource("/component/framework-view/variations/variation-1",
        validVariationProperties);

    cssFolderProperties.put("include", new String[]{"my-css.css"});
    context.create().resource("/component/framework-view/variations/variation-1/css",
        cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/variations/variation-1/css/my-css.css",
        fileProperties);
    context.create().resource(
        "/component/framework-view/variations/variation-1/css/my-css.css/jcr:content",
        fileJcrContentProperties);

    context.create().resource("/component/framework-view/variations/variation-2",
        validVariationProperties);

    cssFolderProperties.put("include", new String[]{"my-css.css"});
    context.create().resource("/component/framework-view/variations/variation-2/css",
        cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/variations/variation-2/css/my-css.css",
        fileProperties);
    context.create().resource(
        "/component/framework-view/variations/variation-2/css/my-css.css/jcr:content",
        fileJcrContentProperties);

    context.create().resource("/component/framework-view/variations/variation-3",
        validVariationProperties);

    cssFolderProperties.put("include", new String[]{"my-css.css"});
    context.create().resource("/component/framework-view/variations/variation-3/css",
        cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/variations/variation-3/css/my-css.css",
        fileProperties);
    context.create().resource(
        "/component/framework-view/variations/variation-3/css/my-css.css/jcr:content",
        fileJcrContentProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertEquals(
        "body{ color:red;}\n" + "body{ color:variation-1;}\n" + "body{ color:variation-2;}\n"
        + "body{ color:variation-3;}", componentUiFrameworkView.getOutput(CSS, false));
  }

  @Test
  public void testGetJavascriptOutput() throws Exception {

    context.registerService(UiLibraryCompilationService.class, uiLibraryCompilationService);

    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        "console.log('javascript-output');");

    resource = context.create().resource("/component/framework-view");

    cssFolderProperties.put("include", new String[]{"my-js.js"});
    context.create().resource("/component/framework-view/js", cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/js/my-js.js", fileProperties);
    fileJcrContentProperties.put("jcr:mimeType", "application/javascript");
    context.create().resource("/component/framework-view/js/my-js.js/jcr:content",
        fileJcrContentProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertNotNull(componentUiFrameworkView.getOutput(JAVASCRIPT, false));

    assertEquals("console.log('javascript-output');",
        componentUiFrameworkView.getOutput(JAVASCRIPT, false));
  }

  @Test
  public void testGetJavascriptOutputWhenUsingVariations() throws Exception {
    context.registerService(UiLibraryCompilationService.class, uiLibraryCompilationService);

    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        "console.log('0');").thenReturn("console.log('1');").thenReturn(
        "console.log('2');").thenReturn("console.log('3');");

    resource = context.create().resource("/component/framework-view");

    cssFolderProperties.put("include", new String[]{"my-js.js"});
    context.create().resource("/component/framework-view/js", cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/js/my-js.js", fileProperties);

    fileJcrContentProperties.put("jcr:mimeType", "application/javascript");
    context.create().resource("/component/framework-view/js/my-js.js/jcr:content",
        fileJcrContentProperties);

    context.create().resource("/component/framework-view/variations");
    context.create().resource("/component/framework-view/variations/variation-1",
        validVariationProperties);

    cssFolderProperties.put("include", new String[]{"my-js.js"});
    context.create().resource("/component/framework-view/variations/variation-1/js",
        cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/variations/variation-1/js/my-js.js",
        fileProperties);
    context.create().resource(
        "/component/framework-view/variations/variation-1/js/my-js.js/jcr:content",
        fileJcrContentProperties);

    context.create().resource("/component/framework-view/variations/variation-2",
        validVariationProperties);

    cssFolderProperties.put("include", new String[]{"my-js.js"});
    context.create().resource("/component/framework-view/variations/variation-2/js",
        cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/variations/variation-2/js/my-js.js",
        fileProperties);
    context.create().resource(
        "/component/framework-view/variations/variation-2/js/my-js.js/jcr:content",
        fileJcrContentProperties);

    context.create().resource("/component/framework-view/variations/variation-3",
        validVariationProperties);

    cssFolderProperties.put("include", new String[]{"my-js.js"});
    context.create().resource("/component/framework-view/variations/variation-3/js",
        cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/variations/variation-3/js/my-js.js",
        fileProperties);
    context.create().resource(
        "/component/framework-view/variations/variation-3/js/my-js.js/jcr:content",
        fileJcrContentProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertEquals("" + "console.log('0');\n" + "console.log('1');\n" + "console.log('2');\n"
                 + "console.log('3');", componentUiFrameworkView.getOutput(JAVASCRIPT, false));
  }


  @Test
  public void testGetVariations() {
    resource = context.create().resource("/component/framework-view");
    context.create().resource("/component/framework-view/variations");
    context.create().resource("/component/framework-view/variations/variation-1",
        validVariationProperties);
    context.create().resource("/component/framework-view/variations/variation-2",
        validVariationProperties);
    context.create().resource("/component/framework-view/variations/variation-3",
        validVariationProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertEquals(3, componentUiFrameworkView.getVariations().size());
    assertEquals("variation-1", componentUiFrameworkView.getVariations().get(0).getName());
    assertEquals("variation-2", componentUiFrameworkView.getVariations().get(1).getName());
    assertEquals("variation-3", componentUiFrameworkView.getVariations().get(2).getName());
  }

  @Test
  public void testGetFontAwesomeIcon() {
    properties.put("fontAwesomeIcon", "icon");
    resource = context.create().resource("/component/framework-view", properties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertEquals("icon", componentUiFrameworkView.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenInheritsFromUiFramework() {
    Map<String, Object> frameworkProperties = new HashMap<>();
    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    frameworkProperties.put("jcr:title", "Framework");
    frameworkProperties.put("kes:uiFrameworkCode", "framework-view");
    context.create().resource("/etc/ui-frameworks/framework", frameworkProperties);
    resource = context.create().resource("/component/framework-view", properties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);
    assertEquals("fas fa-palette", componentUiFrameworkView.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenNoUiFramework() {

    resource = context.create().resource("/component/framework-view", properties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);
    assertEquals("", componentUiFrameworkView.getFontAwesomeIcon());
  }
}