package io.kestros.cms.foundation.services.headlessrender.impl;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseHeadlessRenderServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BaseHeadlessRenderService headlessRenderService;

  private Resource resource;

  private Map<String, Object> pageProperties = new HashMap<>();

  private Map<String, Object> pageJcrContentProperties = new HashMap<>();

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    context.addModelsForPackage("io.kestros");

    pageProperties.put("jcr:primaryType", "kes:Page");
    pageJcrContentProperties.put("jcr:primaryType", "nt:unstructured");
    pageJcrContentProperties.put("jcr:title", "Title");

    headlessRenderService = new BaseHeadlessRenderService();
  }

  @Test
  public void testRenderHeadlessResponse() throws IOException, InvalidResourceTypeException {
    resource = context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", pageJcrContentProperties);

    context.request().setResource(resource);
    headlessRenderService.renderHeadlessResponse(context.request(), context.response());

    ObjectMapper mapper = new ObjectMapper();

    Map output = mapper.readValue(context.response().getOutputAsString(), Map.class);

    assertEquals(200, context.response().getStatus());
    assertEquals(10, output.size());
    assertEquals("kes:Page", output.get("resourceType"));
  }

  @Test
  public void testRenderHeadlessResponseWhenSite()
      throws IOException, InvalidResourceTypeException {
    pageProperties.put("jcr:primaryType", "kes:Site");
    resource = context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", pageJcrContentProperties);

    context.request().setResource(resource);
    headlessRenderService.renderHeadlessResponse(context.request(), context.response());

    ObjectMapper mapper = new ObjectMapper();

    Map output = mapper.readValue(context.response().getOutputAsString(), Map.class);

    assertEquals(200, context.response().getStatus());
    assertEquals(10, output.size());
    assertEquals("kes:Site", output.get("resourceType"));
  }

  @Test
  public void testRenderHeadlessResponseWhenInvalidRequest() {
    pageProperties.put("jcr:primaryType", "kes:ComponentType");
    resource = context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", pageJcrContentProperties);

    context.request().setResource(resource);
    try {
      headlessRenderService.renderHeadlessResponse(context.request(), context.response());
    } catch (IOException e) {
    } catch (InvalidResourceTypeException e) {
      exception = e;
    }
    assertEquals(
        "Unable to adapt '/page' to BaseContentPage: Unable to adapt to BaseContentPage or Site "
        + "while building headless response.", exception.getMessage());

    assertEquals(400, context.response().getStatus());
    assertEquals("", context.response().getOutputAsString());
  }
}