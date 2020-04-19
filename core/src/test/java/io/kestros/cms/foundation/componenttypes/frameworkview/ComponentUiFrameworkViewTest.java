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

package io.kestros.cms.foundation.componenttypes.frameworkview;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.kestros.cms.foundation.exceptions.InvalidScriptException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  private Map<String, Object> validVariationProperties = new HashMap<>();

  private Map<String, Object> cssFolderProperties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> fileJcrContentProperties = new HashMap<>();


  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    validVariationProperties.put("jcr:primaryType", "kes:ComponentVariation");
    fileProperties.put("jcr:primaryType", "nt:file");

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

    InputStream importerInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    resource = context.create().resource("/component/framework-view");

    cssFolderProperties.put("include", new String[]{"my-css.css"});
    context.create().resource("/component/framework-view/css", cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/css/my-css.css", fileProperties);

    fileJcrContentProperties.put("jcr:data", importerInputStream);
    fileJcrContentProperties.put("jcr:mimeType", "text/less");
    context.create().resource("/component/framework-view/css/my-css.css/jcr:content",
        fileJcrContentProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertNotNull(componentUiFrameworkView.getOutput(CSS, false));

    assertEquals("body{ color:red;}", componentUiFrameworkView.getOutput(CSS, false));
  }

  @Test
  public void testGetCssOutputWhenUsingVariations() throws Exception {

    InputStream importerInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    InputStream variation1CssInputStream = new ByteArrayInputStream(
        "body{ color:variation-1;}".getBytes());
    InputStream variation2CssInputStream = new ByteArrayInputStream(
        "body{ color:variation-2;}".getBytes());
    InputStream variation3CssInputStream = new ByteArrayInputStream(
        "body{ color:variation-3;}".getBytes());

    fileJcrContentProperties.put("jcr:mimeType", "text/less");

    resource = context.create().resource("/component/framework-view");

    cssFolderProperties.put("include", new String[]{"my-css.css"});
    context.create().resource("/component/framework-view/css", cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/css/my-css.css", fileProperties);

    fileJcrContentProperties.put("jcr:data", importerInputStream);
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
    fileJcrContentProperties.put("jcr:data", variation1CssInputStream);
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
    fileJcrContentProperties.put("jcr:data", variation2CssInputStream);
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
    fileJcrContentProperties.put("jcr:data", variation3CssInputStream);
    context.create().resource(
        "/component/framework-view/variations/variation-3/css/my-css.css/jcr:content",
        fileJcrContentProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertNotNull(componentUiFrameworkView.getOutput(CSS, false));

    assertEquals("body{ color:red;}body{ color:variation-1;}body{ color:variation-2;}body{ "
                 + "color:variation-3;}", componentUiFrameworkView.getOutput(CSS, false));
  }

  @Test
  public void testGetJavascriptOutput() throws Exception {

    InputStream importerInputStream = new ByteArrayInputStream(
        "console.log('javascript-output');".getBytes());

    resource = context.create().resource("/component/framework-view");

    cssFolderProperties.put("include", new String[]{"my-js.js"});
    context.create().resource("/component/framework-view/js", cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/js/my-js.js", fileProperties);
    fileJcrContentProperties.put("jcr:data", importerInputStream);
    fileJcrContentProperties.put("jcr:mimeType", "application/javascript");
    context.create().resource("/component/framework-view/js/my-js.js/jcr:content",
        fileJcrContentProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertNotNull(componentUiFrameworkView.getOutput(JAVASCRIPT, false));

    assertEquals("console.log('javascript-output');\n",
        componentUiFrameworkView.getOutput(JAVASCRIPT, false));
  }

  @Test
  public void testGetJavascriptOutputWhenUsingVariations() throws Exception {

    InputStream importerInputStream = new ByteArrayInputStream("console.log('0');".getBytes());

    InputStream variation1JavascriptInputStream = new ByteArrayInputStream(
        "console.log('1');".getBytes());
    InputStream variation2JavascriptInputStream = new ByteArrayInputStream(
        "console.log('2');".getBytes());
    InputStream variation3JavascriptInputStream = new ByteArrayInputStream(
        "console.log('3');".getBytes());

    resource = context.create().resource("/component/framework-view");

    cssFolderProperties.put("include", new String[]{"my-js.js"});
    context.create().resource("/component/framework-view/js", cssFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/component/framework-view/js/my-js.js", fileProperties);

    fileJcrContentProperties.put("jcr:data", importerInputStream);
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
    fileJcrContentProperties.put("jcr:data", variation1JavascriptInputStream);
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
    fileJcrContentProperties.put("jcr:data", variation2JavascriptInputStream);
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
    fileJcrContentProperties.put("jcr:data", variation3JavascriptInputStream);
    context.create().resource(
        "/component/framework-view/variations/variation-3/js/my-js.js/jcr:content",
        fileJcrContentProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkView.class);

    assertEquals("" + "console.log('0');\n" + "console.log('1');\n" + "console.log('2');\n"
                 + "console.log('3');\n", componentUiFrameworkView.getOutput(JAVASCRIPT, false));
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

}