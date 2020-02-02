package io.kestros.cms.foundation.utils;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

import io.kestros.commons.structuredslingmodels.BaseResource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class JcrPropertyUtilsTest {

  @Rule
  public SlingContext context = new SlingContext();
  private BaseResource baseResource;
  private Resource resource;
  private Map<String, Object> properties = new HashMap<>();

  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void testGetRelativeDate() {
    properties.put("kes:lastModified", new Date().getTime());
    resource = context.create().resource("/resource", properties);
    baseResource = resource.adaptTo(BaseResource.class);

    assertEquals("Just now",
        JcrPropertyUtils.getRelativeDate(baseResource, "kes:lastModified").getTimeAgo());
  }

  @Test
  public void testGetRelativeDateWhenEmpty() {
    properties.put("kes:lastModified", "");
    resource = context.create().resource("/resource", properties);
    baseResource = resource.adaptTo(BaseResource.class);

    assertNull(JcrPropertyUtils.getRelativeDate(baseResource, "kes:lastModified"));
  }

  @Test
  public void testGetRelativeDatePropertyMissing() {
    properties.put("kes:lastModified", "invalid");
    resource = context.create().resource("/resource", properties);
    baseResource = resource.adaptTo(BaseResource.class);

    assertNull(JcrPropertyUtils.getRelativeDate(baseResource, "kes:lastModified"));
  }

  @Test
  public void testGetRelativeDateWhenInvalidDate() {
    resource = context.create().resource("/resource", properties);
    baseResource = resource.adaptTo(BaseResource.class);

    assertNull(JcrPropertyUtils.getRelativeDate(baseResource, "kes:lastModified"));

  }

  @Test
  public void testGetRelativeDateWhenPassingResource() {
    properties.put("kes:lastModified", new Date().getTime());
    resource = context.create().resource("/resource", properties);

    assertEquals("Just now",
        JcrPropertyUtils.getRelativeDate(resource, "kes:lastModified").getTimeAgo());
  }
}