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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class InheritedContentAreaTest {

  @Rule
  public SlingContext context = new SlingContext();

  private InheritedContentArea inheritedContentArea;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  private Map<String, Object> contentAreaProperties = new HashMap<>();

  private Map<String, Object> pageProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    properties.put("sling:resourceType", "kestros/commons/components/inherited-content-area");
    contentAreaProperties.put("sling:resourceType", "kestros/commons/components/content-area");
    pageProperties.put("jcr:primaryType", "kes:Page");
  }

  @Test
  public void testIsAllowComponentsBefore() {
    properties.put("allowComponentsBefore", "true");
    resource = context.create().resource("/inherited-content-area", properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);

    assertTrue(inheritedContentArea.isAllowComponentsBefore());
  }

  @Test
  public void testIsAllowComponentsBeforeWhenEmpty() {
    resource = context.create().resource("/inherited-content-area", properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);

    assertFalse(inheritedContentArea.isAllowComponentsBefore());
  }

  @Test
  public void testIsAllowComponentsAfter() {
    properties.put("allowComponentsAfter", "true");
    resource = context.create().resource("/inherited-content-area", properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);

    assertTrue(inheritedContentArea.isAllowComponentsAfter());
  }

  @Test
  public void testIsAllowComponentsAfterWhenEmpty() {
    resource = context.create().resource("/inherited-content-area", properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);

    assertFalse(inheritedContentArea.isAllowComponentsAfter());
  }

  @Test
  public void testIsReset() {
    properties.put("reset", "true");
    resource = context.create().resource("/inherited-content-area", properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);

    assertTrue(inheritedContentArea.isReset());
  }

  @Test
  public void testIsResetWhenEmpty() {
    resource = context.create().resource("/inherited-content-area", properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);

    assertFalse(inheritedContentArea.isReset());
  }

  @Test
  public void testIsRootLevelContentArea() {
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content/inherited-content-area", properties);
    context.create().resource("/page/child", pageProperties);

    properties.put("reset", "true");
    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);

    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);

    assertTrue(inheritedContentArea.isRootLevelContentArea());
  }

  @Test
  public void testIsRootLevelContentAreaWhenReset() {
    properties.put("reset", "true");
    resource = context.create().resource("/inherited-content-area", properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);

    assertTrue(inheritedContentArea.isRootLevelContentArea());
  }

  @Test
  public void testIsRootLevelContentAreaWhenResetWhenHasParentInheritance() {
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content/inherited-content-area", properties);
    context.create().resource("/page/child", pageProperties);

    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);

    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);

    assertFalse(inheritedContentArea.isRootLevelContentArea());
  }

  @Test
  public void testGetBeforeContentArea() {
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content/inherited-content-area", properties);
    context.create().resource("/page/child", pageProperties);

    properties.put("allowComponentsBefore", "true");
    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);
    context.create().resource("/page/child/jcr:content/inherited-content-area/before",
        contentAreaProperties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertEquals("/page/child/jcr:content/inherited-content-area/before",
        inheritedContentArea.getBeforeContentArea().getPath());
  }

  @Test
  public void testGetBeforeContentAreaWhenDoesNotAllowComponentsBefore() {
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content/inherited-content-area", properties);
    context.create().resource("/page/child", pageProperties);

    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);
    context.create().resource("/page/child/jcr:content/inherited-content-area/before",
        contentAreaProperties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertNull(inheritedContentArea.getBeforeContentArea());
  }

  @Test
  public void testGetBeforeContentAreaWhenRootLevel() {
    context.create().resource("/page/child", pageProperties);

    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);
    context.create().resource("/page/child/jcr:content/inherited-content-area/before",
        contentAreaProperties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertNull(inheritedContentArea.getBeforeContentArea());
  }

  @Test
  public void testGetBeforeContentAreaWhenMissing() {
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content/inherited-content-area", properties);
    context.create().resource("/page/child", pageProperties);

    properties.put("allowComponentsBefore", "true");
    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertNull(inheritedContentArea.getBeforeContentArea());
  }


  @Test
  public void testGetAfterContentArea() {
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content/inherited-content-area", properties);
    context.create().resource("/page/child", pageProperties);

    properties.put("allowComponentsAfter", "true");
    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);
    context.create().resource("/page/child/jcr:content/inherited-content-area/after",
        contentAreaProperties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertEquals("/page/child/jcr:content/inherited-content-area/after",
        inheritedContentArea.getAfterContentArea().getPath());
  }

  @Test
  public void testGetAfterContentAreaWhenDoesNotAllowComponentsAfter() {
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content/inherited-content-area", properties);
    context.create().resource("/page/child", pageProperties);

    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);
    context.create().resource("/page/child/jcr:content/inherited-content-area/after",
        contentAreaProperties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertNull(inheritedContentArea.getAfterContentArea());
  }

  @Test
  public void testGetAfterContentAreaWhenRootLevel() {
    context.create().resource("/page/child", pageProperties);

    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);
    context.create().resource("/page/child/jcr:content/inherited-content-area/after",
        contentAreaProperties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertNull(inheritedContentArea.getAfterContentArea());
  }

  @Test
  public void testGetAfterContentAreaWhenMissing() {
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content/inherited-content-area", properties);
    context.create().resource("/page/child", pageProperties);

    properties.put("allowComponentsAfter", "true");
    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);

    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);

    assertNull(inheritedContentArea.getAfterContentArea());
  }

  @Test
  public void testGetInheritedFromContentArea() {
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content/inherited-content-area", properties);
    context.create().resource("/page/child", pageProperties);
    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertEquals("/page/jcr:content/inherited-content-area",
        inheritedContentArea.getInheritedFromContentArea().getPath());
  }

  @Test
  public void testGetInheritedFromContentAreaWhenInheritedFromMultipleLevelsUp() {
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content/inherited-content-area", properties);
    context.create().resource("/page/page", pageProperties);
    context.create().resource("/page/page/page", pageProperties);
    context.create().resource("/page/page/page/page/", pageProperties);
    context.create().resource("/page/page/page/page/child", pageProperties);
    resource = context.create().resource(
        "/page/page/page/page/child/jcr:content/inherited-content-area", properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertEquals("/page/jcr:content/inherited-content-area",
        inheritedContentArea.getInheritedFromContentArea().getPath());
  }

  @Test
  public void testGetInheritedFromContentAreaWhenRootLevel() {
    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertNull("/page/jcr:content/inherited-content-area",
        inheritedContentArea.getInheritedFromContentArea());
  }

  @Test
  public void testGetInheritedFromContentAreaWhenReset() {
    properties.put("reset", true);
    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertNull("/page/jcr:content/inherited-content-area",
        inheritedContentArea.getInheritedFromContentArea());
  }

  @Test
  public void testGetInheritedFromContentAreaWhenNoContainingPage() {
    properties.put("reset", true);
    resource = context.create().resource("/inherited-content-area", properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertNull("/page/jcr:content/inherited-content-area",
        inheritedContentArea.getInheritedFromContentArea());
  }

  @Test
  public void testGetInheritedFromContentAreaWhenInheritedContentAreaIsInvalid() {
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content/inherited-content-area", contentAreaProperties);
    context.create().resource("/page/child", pageProperties);
    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertNull(inheritedContentArea.getInheritedFromContentArea());
  }

  @Test
  public void testGetInheritedFromContentAreaWhenInheritedContentAreaIsMissing() {
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/child", pageProperties);
    resource = context.create().resource("/page/child/jcr:content/inherited-content-area",
        properties);
    inheritedContentArea = resource.adaptTo(InheritedContentArea.class);
    assertNull(inheritedContentArea.getInheritedFromContentArea());
  }

}