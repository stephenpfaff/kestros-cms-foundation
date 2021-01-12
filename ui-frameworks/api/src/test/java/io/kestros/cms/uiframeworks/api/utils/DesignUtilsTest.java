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

package io.kestros.cms.uiframeworks.api.utils;

import static org.junit.Assert.assertEquals;

import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DesignUtilsTest {

  @Rule
  public SlingContext context = new SlingContext();

  private Resource resource;

  private UiFramework uiFramework;

  private Map<String, Object> uiFrameworkProperties = new HashMap<>();

  private Map<String, Object> vendorLibrariesProperties = new HashMap<>();

  private Map<String, Object> componentTypeProperties = new HashMap<>();

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    vendorLibrariesProperties.put("jcr:primaryType", "kes:VendorLibrary");
    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
  }

  @Test
  public void testGetVendorLibrariesRootResourceForUiFramework() throws ResourceNotFoundException {
    resource = context.create().resource("/etc/ui-frameworks/ui-framework", uiFrameworkProperties);
    context.create().resource("/etc/vendor-libraries");
    context.create().resource("/libs/kestros/vendor-libraries");

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("/etc/vendor-libraries",
        DesignUtils.getVendorLibrariesRootResourceForUiFramework(uiFramework,
            context.resourceResolver()).getPath());
  }

  @Test
  public void testGetVendorLibrariesRootResourceForUiFrameworkWhenLibs()
      throws ResourceNotFoundException {
    resource = context.create().resource("/libs/kestros/ui-frameworks/ui-framework",
        uiFrameworkProperties);

    context.create().resource("/etc/vendor-libraries");
    context.create().resource("/libs/kestros/vendor-libraries");

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("/libs/kestros/vendor-libraries",
        DesignUtils.getVendorLibrariesRootResourceForUiFramework(uiFramework,
            context.resourceResolver()).getPath());
  }

  @Test
  public void testGetVendorLibrariesRootResourceForUiFrameworkWhenNullUiFramework()
      throws ResourceNotFoundException {
    context.create().resource("/etc/vendor-libraries");
    context.create().resource("/libs/kestros/vendor-libraries");

    assertEquals("/etc/vendor-libraries",
        DesignUtils.getVendorLibrariesRootResourceForUiFramework(null,
            context.resourceResolver()).getPath());
  }

  @Test
  public void testGetVendorLibrariesEtcRootResource() throws ResourceNotFoundException {
    context.create().resource("/etc/vendor-libraries");

    assertEquals("/etc/vendor-libraries",
        DesignUtils.getVendorLibrariesEtcRootResource(context.resourceResolver()).getPath());

  }

  @Test
  public void testGetVendorLibrariesLibsRootResource() throws ResourceNotFoundException {
    context.create().resource("/libs/kestros/vendor-libraries");

    assertEquals("/libs/kestros/vendor-libraries",
        DesignUtils.getVendorLibrariesLibsRootResource(context.resourceResolver()).getPath());
  }

  @Test
  public void testGetAllVendorLibraries() throws ResourceNotFoundException {
    context.create().resource("/etc/vendor-libraries/vendor-library-1", vendorLibrariesProperties);
    context.create().resource("/etc/vendor-libraries/vendor-library-2", vendorLibrariesProperties);
    context.create().resource("/libs/kestros/vendor-libraries/vendor-library-3",
        vendorLibrariesProperties);
    context.create().resource("/libs/kestros/vendor-libraries/vendor-library-4",
        vendorLibrariesProperties);

    assertEquals(4,
        DesignUtils.getAllVendorLibraries(context.resourceResolver(), true, true).size());
    assertEquals("/etc/vendor-libraries/vendor-library-1", DesignUtils.getAllVendorLibraries(
        context.resourceResolver(), true, true).get(0).getPath());
    assertEquals("/etc/vendor-libraries/vendor-library-2", DesignUtils.getAllVendorLibraries(
        context.resourceResolver(), true, true).get(1).getPath());
    assertEquals("/libs/kestros/vendor-libraries/vendor-library-3",
        DesignUtils.getAllVendorLibraries(context.resourceResolver(), true, true).get(2).getPath());
    assertEquals("/libs/kestros/vendor-libraries/vendor-library-4",
        DesignUtils.getAllVendorLibraries(context.resourceResolver(), true, true).get(3).getPath());
  }

  @Test
  public void testGetAllVendorLibrariesWhenExcludingLibs() throws ResourceNotFoundException {
    context.create().resource("/etc/vendor-libraries/vendor-library-1", vendorLibrariesProperties);
    context.create().resource("/etc/vendor-libraries/vendor-library-2", vendorLibrariesProperties);
    context.create().resource("/libs/kestros/vendor-libraries/vendor-library-3",
        vendorLibrariesProperties);
    context.create().resource("/libs/kestros/vendor-libraries/vendor-library-4",
        vendorLibrariesProperties);

    assertEquals(2,
        DesignUtils.getAllVendorLibraries(context.resourceResolver(), true, false).size());
    assertEquals("/etc/vendor-libraries/vendor-library-1", DesignUtils.getAllVendorLibraries(
        context.resourceResolver(), true, true).get(0).getPath());
    assertEquals("/etc/vendor-libraries/vendor-library-2", DesignUtils.getAllVendorLibraries(
        context.resourceResolver(), true, true).get(1).getPath());
  }

  @Test
  public void testGetAllVendorLibrariesWhenExcludingLibsAndDoesNotExist()
      throws ResourceNotFoundException {
    context.create().resource("/libs/kestros/vendor-libraries");

    assertEquals(0, DesignUtils.getAllVendorLibraries(context.resourceResolver(), true, false).size());
  }

  @Test
  public void testGetAllVendorLibrariesWhenExcludingLibsAndRootResourceDoesNotExist()
      throws ResourceNotFoundException {
    assertEquals(0,
        DesignUtils.getAllVendorLibraries(context.resourceResolver(), true, false).size());
  }

  @Test
  public void testGetAllVendorLibrariesWhenExcludingEtc() throws ResourceNotFoundException {
    context.create().resource("/etc/vendor-libraries/vendor-library-1", vendorLibrariesProperties);
    context.create().resource("/etc/vendor-libraries/vendor-library-2", vendorLibrariesProperties);
    context.create().resource("/libs/kestros/vendor-libraries/vendor-library-3",
        vendorLibrariesProperties);
    context.create().resource("/libs/kestros/vendor-libraries/vendor-library-4",
        vendorLibrariesProperties);

    assertEquals(2,
        DesignUtils.getAllVendorLibraries(context.resourceResolver(), false, true).size());
    assertEquals("/libs/kestros/vendor-libraries/vendor-library-3",
        DesignUtils.getAllVendorLibraries(context.resourceResolver(), false, true).get(
            0).getPath());
    assertEquals("/libs/kestros/vendor-libraries/vendor-library-4",
        DesignUtils.getAllVendorLibraries(context.resourceResolver(), false, true).get(
            1).getPath());
  }

  @Test
  public void testGetAllVendorLibrariesWhenExcludingEtcAndDoesNotExist()
      throws ResourceNotFoundException {
    context.create().resource("/etc/vendor-libraries");
    assertEquals(0, DesignUtils.getAllVendorLibraries(context.resourceResolver(), false, true).size());
  }

  @Test
  public void testGetAllVendorLibrariesWhenExcludingEtcAndRootResourceDoesNotExist()
      throws ResourceNotFoundException {
    assertEquals(0, DesignUtils.getAllVendorLibraries(context.resourceResolver(), false, true).size());
  }

  @Test
  public void testGetUiFrameworksEtcRootResource() throws ResourceNotFoundException {
    context.create().resource("/etc/ui-frameworks");
    assertEquals("/etc/ui-frameworks",
        DesignUtils.getUiFrameworksEtcRootResource(context.resourceResolver()).getPath());
  }

  @Test
  public void testGetUiFrameworksLibsRootResource() throws ResourceNotFoundException {
    context.create().resource("/libs/kestros/ui-frameworks");

    assertEquals("/libs/kestros/ui-frameworks",
        DesignUtils.getUiFrameworksLibsRootResource(context.resourceResolver()).getPath());
  }

  @Test
  public void testGetAllUiFrameworks() {
    context.create().resource("/etc/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-2", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework-3", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework-4", uiFrameworkProperties);

    assertEquals(4, DesignUtils.getAllUiFrameworks(context.resourceResolver(), true, true).size());
    assertEquals("/etc/ui-frameworks/ui-framework-1", DesignUtils.getAllUiFrameworks(
        context.resourceResolver(), true, true).get(0).getPath());
    assertEquals("/etc/ui-frameworks/ui-framework-2", DesignUtils.getAllUiFrameworks(
        context.resourceResolver(), true, true).get(1).getPath());
    assertEquals("/libs/kestros/ui-frameworks/ui-framework-3", DesignUtils.getAllUiFrameworks(
        context.resourceResolver(), true, true).get(2).getPath());
    assertEquals("/libs/kestros/ui-frameworks/ui-framework-4", DesignUtils.getAllUiFrameworks(
        context.resourceResolver(), true, true).get(3).getPath());
  }

  @Test
  public void testGetAllUiFrameworksWhenExcludingLibs() {
    context.create().resource("/etc/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-2", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework-3", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework-4", uiFrameworkProperties);

    assertEquals(2, DesignUtils.getAllUiFrameworks(context.resourceResolver(), true, false).size());
    assertEquals("/etc/ui-frameworks/ui-framework-1", DesignUtils.getAllUiFrameworks(
        context.resourceResolver(), true, false).get(0).getPath());
    assertEquals("/etc/ui-frameworks/ui-framework-2", DesignUtils.getAllUiFrameworks(
        context.resourceResolver(), true, false).get(1).getPath());
  }

  @Test
  public void testGetAllUiFrameworksWhenExcludingEtc() {
    context.create().resource("/etc/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-2", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework-3", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework-4", uiFrameworkProperties);

    assertEquals(2, DesignUtils.getAllUiFrameworks(context.resourceResolver(), false, true).size());
    assertEquals("/libs/kestros/ui-frameworks/ui-framework-3", DesignUtils.getAllUiFrameworks(
        context.resourceResolver(), false, true).get(0).getPath());
    assertEquals("/libs/kestros/ui-frameworks/ui-framework-4", DesignUtils.getAllUiFrameworks(
        context.resourceResolver(), false, true).get(1).getPath());
  }

  @Test
  public void testGetUiFramework() throws ResourceNotFoundException {
    context.create().resource("/etc/ui-frameworks/ui-framework", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework", uiFrameworkProperties);

    assertEquals("/etc/ui-frameworks/ui-framework",
        DesignUtils.getUiFramework("ui-framework", true, true,
            context.resourceResolver()).getPath());
  }

  @Test
  public void testGetUiFrameworkWhenExcludeLibs() throws ResourceNotFoundException {
    context.create().resource("/etc/ui-frameworks/ui-framework", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework", uiFrameworkProperties);

    assertEquals("/etc/ui-frameworks/ui-framework",
        DesignUtils.getUiFramework("ui-framework", true, false,
            context.resourceResolver()).getPath());
  }

  @Test
  public void testGetUiFrameworkWhenExcludeLibsAndDoesNotExist() {
    context.create().resource("/etc/ui-frameworks");
    try {
      DesignUtils.getUiFramework("ui-framework", true, false, context.resourceResolver());
    } catch (ResourceNotFoundException e) {
      exception = e;
    }
    assertEquals("Unable to adapt 'ui-framework': Unable to find UiFramework 'ui-framework'.",
        exception.getMessage());
  }

  @Test
  public void testGetUiFrameworkWhenExcludeLibsAndRootResourceDoesNotExist() {
    try {
      DesignUtils.getUiFramework("ui-framework", true, false, context.resourceResolver());
    } catch (ResourceNotFoundException e) {
      exception = e;
    }
    assertEquals("Unable to adapt 'ui-framework': Unable to find UiFramework 'ui-framework'.",
        exception.getMessage());
  }

  @Test
  public void testGetUiFrameworkWhenExcludeEtc() throws ResourceNotFoundException {
    context.create().resource("/etc/ui-frameworks/ui-framework", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework", uiFrameworkProperties);

    assertEquals("/libs/kestros/ui-frameworks/ui-framework",
        DesignUtils.getUiFramework("ui-framework", false, true,
            context.resourceResolver()).getPath());
  }

  @Test
  public void testGetUiFrameworkWhenExcludeEtcAndDoesNotExist() {
    context.create().resource("/libs/kestros/ui-frameworks");
    try {
      DesignUtils.getUiFramework("ui-framework", false, true, context.resourceResolver());
    } catch (ResourceNotFoundException e) {
      exception = e;
    }
    assertEquals("Unable to adapt 'ui-framework': Unable to find UiFramework 'ui-framework'.",
        exception.getMessage());
  }

  @Test
  public void testGetUiFrameworkWhenExcludeEtcAndRootResourceDoesNotExist() {
    try {
      DesignUtils.getUiFramework("ui-framework", false, true, context.resourceResolver());
    } catch (ResourceNotFoundException e) {
      exception = e;
    }
    assertEquals("Unable to adapt 'ui-framework': Unable to find UiFramework 'ui-framework'.",
        exception.getMessage());
  }

  @Test
  public void testGetUiFrameworkWhenExcludeEtcAndLibs() {
    context.create().resource("/etc/ui-frameworks/ui-framework", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework", uiFrameworkProperties);

    try {
      DesignUtils.getUiFramework("ui-framework", false, false, context.resourceResolver());
    } catch (ResourceNotFoundException e) {
      exception = e;
    }
    assertEquals("Unable to adapt 'ui-framework': Unable to find UiFramework 'ui-framework'.",
        exception.getMessage());
  }

  @Test
  public void testGetUiFrameworkByFrameworkCode() throws ResourceNotFoundException {
    uiFrameworkProperties.put("kes:uiFrameworkCode", "framework-1");
    context.create().resource("/etc/ui-frameworks/ui-framework", uiFrameworkProperties);
    uiFrameworkProperties.put("kes:uiFrameworkCode", "framework-2");
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework", uiFrameworkProperties);

    assertEquals("/etc/ui-frameworks/ui-framework",
        DesignUtils.getUiFrameworkByFrameworkCode("framework-1", true, true,
            context.resourceResolver()).getPath());

    assertEquals("/libs/kestros/ui-frameworks/ui-framework",
        DesignUtils.getUiFrameworkByFrameworkCode("framework-2", true, true,
            context.resourceResolver()).getPath());
  }

  @Test
  public void testGetUiFrameworkByFrameworkCodeWhenExcludeLibs() throws ResourceNotFoundException {
    uiFrameworkProperties.put("kes:uiFrameworkCode", "framework-1");
    context.create().resource("/etc/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    uiFrameworkProperties.put("kes:uiFrameworkCode", "framework-1");
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    uiFrameworkProperties.put("kes:uiFrameworkCode", "framework-2");
    context.create().resource("/etc/ui-frameworks/ui-framework-2", uiFrameworkProperties);
    uiFrameworkProperties.put("kes:uiFrameworkCode", "framework-2");
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework-2", uiFrameworkProperties);

    assertEquals("/etc/ui-frameworks/ui-framework-1",
        DesignUtils.getUiFrameworkByFrameworkCode("framework-1", true, false,
            context.resourceResolver()).getPath());

    assertEquals("/etc/ui-frameworks/ui-framework-2",
        DesignUtils.getUiFrameworkByFrameworkCode("framework-2", true, false,
            context.resourceResolver()).getPath());
  }

  @Test
  public void testGetUiFrameworkByFrameworkCodeWhenExcludeEtc() throws ResourceNotFoundException {
    uiFrameworkProperties.put("kes:uiFrameworkCode", "framework-1");
    context.create().resource("/etc/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    uiFrameworkProperties.put("kes:uiFrameworkCode", "framework-1");
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    uiFrameworkProperties.put("kes:uiFrameworkCode", "framework-2");
    context.create().resource("/etc/ui-frameworks/ui-framework-2", uiFrameworkProperties);
    uiFrameworkProperties.put("kes:uiFrameworkCode", "framework-2");
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework-2", uiFrameworkProperties);

    assertEquals("/libs/kestros/ui-frameworks/ui-framework-1",
        DesignUtils.getUiFrameworkByFrameworkCode("framework-1", false, true,
            context.resourceResolver()).getPath());

    assertEquals("/libs/kestros/ui-frameworks/ui-framework-2",
        DesignUtils.getUiFrameworkByFrameworkCode("framework-2", false, true,
            context.resourceResolver()).getPath());
  }

  @Test
  public void testGetUiFrameworkByFrameworkCodeWhenFrameworkDoesNotExist() {
    try {
      DesignUtils.getUiFrameworkByFrameworkCode("framework-1", false, true,
          context.resourceResolver());
    } catch (ResourceNotFoundException e) {
      exception = e;
    }
    assertEquals(
        "Unable to adapt 'framework-1': Unable to find UiFramework matching code 'framework-1'.",
        exception.getMessage());
  }

  //
  //  @Test
  //  public void testGetComponentUiFrameworkView()
  //      throws ChildResourceNotFoundException, ResourceNotFoundException {
  //    resource = context.create().resource("/component-type", componentTypeProperties);
  //    context.create().resource("/component-type/framework");
  //
  //    ComponentType componentType = resource.adaptTo(ComponentType.class);
  //    assertEquals("/component-type/framework",
  //        DesignUtils.getComponentUiFrameworkView("framework", componentType).getPath());
  //  }
  //
  //  @Test
  //  public void testGetComponentUiFrameworkViewWhenComponentOverlaysLibs()
  //      throws ChildResourceNotFoundException, ResourceNotFoundException {
  //    context.create().resource("/libs/component-type", componentTypeProperties);
  //    context.create().resource("/libs/component-type/framework");
  //    resource = context.create().resource("/apps/component-type", componentTypeProperties);
  //
  //    ComponentType componentType = resource.adaptTo(ComponentType.class);
  //
  //    assertEquals("/libs/component-type/framework",
  //        DesignUtils.getComponentUiFrameworkView("framework", componentType).getPath());
  //  }
  //
  //  @Test
  //  public void testGetComponentUiFrameworkViewWhenComponentOverlaysLibsAndViewExistsForBoth()
  //      throws ChildResourceNotFoundException, ResourceNotFoundException {
  //    context.create().resource("/libs/component-type", componentTypeProperties);
  //    context.create().resource("/libs/component-type/framework");
  //    resource = context.create().resource("/apps/component-type", componentTypeProperties);
  //    context.create().resource("/apps/component-type/framework");
  //
  //    ComponentType componentType = resource.adaptTo(ComponentType.class);
  //
  //    assertEquals("/apps/component-type/framework",
  //        DesignUtils.getComponentUiFrameworkView("framework", componentType).getPath());
  //  }
  //
  //  @Test
  //  public void testGetComponentUiFrameworkViewWhenComponentOverlaysLibsTypeIsOverlayed()
  //      throws ChildResourceNotFoundException, ResourceNotFoundException {
  //    resource = context.create().resource("/libs/component-type", componentTypeProperties);
  //    context.create().resource("/apps/component-type", componentTypeProperties);
  //    context.create().resource("/apps/component-type/framework");
  //
  //    ComponentType componentType = resource.adaptTo(ComponentType.class);
  //
  //    assertEquals("/apps/component-type/framework",
  //        DesignUtils.getComponentUiFrameworkView("framework", componentType).getPath());
  //  }
  //
  //  @Test
  //  public void
  //  testGetComponentUiFrameworkViewWhenComponentOverlaysLibsTypeIsOverlayedAndFrameworkExistsForBoth()
  //      throws ChildResourceNotFoundException, ResourceNotFoundException {
  //    resource = context.create().resource("/libs/component-type", componentTypeProperties);
  //    context.create().resource("/libs/component-type/framework");
  //    context.create().resource("/apps/component-type", componentTypeProperties);
  //    context.create().resource("/apps/component-type/framework");
  //
  //    ComponentType componentType = resource.adaptTo(ComponentType.class);
  //
  //    assertEquals("/apps/component-type/framework",
  //        DesignUtils.getComponentUiFrameworkView("framework", componentType).getPath());
  //  }
  //
  //  @Test
  //  public void testGetComponentUiFrameworkViewWhenDoesNotExist() {
  //    resource = context.create().resource("/apps/component-type", componentTypeProperties);
  //
  //    ComponentType componentType = resource.adaptTo(ComponentType.class);
  //
  //    try {
  //      DesignUtils.getComponentUiFrameworkView("framework", componentType);
  //    } catch (ChildResourceNotFoundException e) {
  //      exception = e;
  //    }
  //    assertEquals("Unable to adapt 'framework' under '/apps/component-type': Child not found.",
  //        exception.getMessage());
  //  }
}