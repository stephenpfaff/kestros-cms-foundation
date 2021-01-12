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

package io.kestros.cms.sitebuilding.api.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ContentAreaTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ContentArea contentArea;

  private Map<String, Object> properties = new HashMap<>();

  @Before
  public void setUp() {
    context.addModelsForPackage("io.kestros");
  }

  @Test
  public void testInitialize() {
    SyntheticResource syntheticResource = new SyntheticResource(context.resourceResolver(),
        "/content-area", "");

    assertNull(context.resourceResolver().getResource("/content-area"));

    contentArea = syntheticResource.adaptTo(ContentArea.class);

    assertNotNull(context.resourceResolver().getResource("/content-area"));
    assertEquals("/content-area", contentArea.getPath());

  }

  @Test
  public void testInitializeWhenResourcesDoesExist() throws PersistenceException {
    properties.put("jcr:primaryType", "nt:unstructured");
    properties.put("sling:resourceType", "component/content-area");
    Resource resource = context.create().resource("/content-area", properties);

    assertNotNull(context.resourceResolver().getResource("/content-area"));

    contentArea = resource.adaptTo(ContentArea.class);
    context.resourceResolver().delete(resource);
    assertNull(context.resourceResolver().getResource("/content-area"));

    contentArea.initialize();
    assertNotNull(context.resourceResolver().getResource("/content-area"));
    assertEquals("/content-area", contentArea.getPath());

    assertEquals("component/content-area", contentArea.getResourceType());
    assertEquals("component/content-area", resource.getResourceType());
    assertEquals("component/content-area", contentArea.getSlingResourceType());
  }

  @Test
  public void testGetRelativePath() {
    Resource resource = context.create().resource("/page/jcr:content/component/parent/content-area",
        properties);

    contentArea = resource.adaptTo(ContentArea.class);

    assertEquals("component/parent/content-area", contentArea.getRelativePath());
  }

}