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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.kestros.cms.componenttypes.api.exceptions.InvalidCommonUiFrameworkException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidScriptException;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ComponentTypeTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ComponentType componentType;

  private UiFramework uiFramework;

  private Resource resource;

  private List<ComponentType> allComponentTypes = new ArrayList<>();

  private Map<String, Object> componentProperties = new HashMap<>();
  private Map<String, Object> componentFrameworkViewProperties = new HashMap<>();
  private Map<String, Object> dialogProperties = new HashMap<>();
  private Map<String, Object> frameworkProperties = new HashMap<>();
  private Map<String, Object> uiFrameworkProperties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> fileJcrContentProperties = new HashMap<>();

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    componentProperties.put("jcr:primaryType", "kes:ComponentType");

    dialogProperties.put("jcr:primaryType", "kes:Dialog");

    componentFrameworkViewProperties.put("jcr:primaryType", "nt:folder");

    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    frameworkProperties.put("sling:resourceType", "kestros/cms/ui-framework");
    fileProperties.put("jcr:primaryType", "nt:file");
    fileJcrContentProperties.put("jcr:mimeType", "text/html");
  }

  @Test
  public void testGetPropertiesWhenInheritedFromLibs() {
    resource = context.create().resource("/apps/component-type");
    componentProperties.put("property", "/libs");
    context.create().resource("/libs/component-type", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/libs", componentType.getProperties().get("property"));
  }

  @Test
  public void testGetPropertiesWhenAppsIsComponentType() {
    componentProperties.put("property", "/apps");
    resource = context.create().resource("/apps/component-type", componentProperties);
    componentProperties.put("property", "/libs");
    context.create().resource("/libs/component-type", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/apps", componentType.getProperties().get("property"));
  }

  @Test
  public void testGetPropertiesWhenModelAdaptionException() {
    Map<String, Object> properties = new HashMap<>();
    properties.put("property", "/apps");
    resource = context.create().resource("/apps/component-type", properties);
    componentProperties.put("property", "/libs");
    context.create().resource("/libs/component-type", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/libs", componentType.getProperties().get("property"));
  }

  @Test
  public void testGetComponentGroup() throws Exception {
    componentProperties.put("componentGroup", "My Group");

    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("My Group", componentType.getComponentGroup());
  }

  @Test
  public void testGetComponentSuperType() throws Exception {
    componentProperties.put("sling:resourceSuperType", "components/super-type");
    resource = context.create().resource("/apps/component", componentProperties);
    context.create().resource("/apps/components/super-type", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("components/super-type", componentType.getResourceSuperType());
    assertEquals("/apps/components/super-type", componentType.getComponentSuperType().getPath());
  }

  @Test
  public void testGetComponentSuperTypeWhenOnlyLibsResourceExists() throws Exception {
    componentProperties.put("sling:resourceSuperType", "components/super-type");
    resource = context.create().resource("/apps/component", componentProperties);
    context.create().resource("/libs/components/super-type", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("components/super-type", componentType.getResourceSuperType());
    assertEquals("/libs/components/super-type", componentType.getComponentSuperType().getPath());
  }

  @Test
  public void testGetComponentSuperTypeWhenDoesNotExist() {
    componentProperties.put("sling:resourceSuperType", "components/super-type");
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    try {
      componentType.getComponentSuperType();
    } catch (InvalidComponentTypeException e) {
      exception = e;
    }
    assertEquals(
        "Unable to adapt 'components/super-type' to ComponentType for resource /apps/component. "
        + "SuperTyped resource not found.", exception.getMessage());
  }

  @Test
  public void testGetComponentSuperTypeWhenAppsIsPassedAndDoesNotExistAndLibsDoes() {
    componentProperties.put("sling:resourceSuperType", "/apps/components/super-type");
    resource = context.create().resource("/apps/component", componentProperties);
    context.create().resource("/libs/components/super-type", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/apps/components/super-type", componentType.getResourceSuperType());
    try {
      componentType.getComponentSuperType().getPath();
    } catch (InvalidComponentTypeException e) {
      exception = e;
    }
    assertEquals("Unable to adapt '/apps/components/super-type' to ComponentType for resource "
                 + "/apps/component. SuperTyped resource not found.", exception.getMessage());
  }

  @Test
  public void testGetCommonUiFrameworkView() throws InvalidCommonUiFrameworkException {
    resource = context.create().resource("/apps/component", componentProperties);

    context.create().resource("/apps/component/common", componentFrameworkViewProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/apps/component/common", componentType.getCommonUiFrameworkView().getPath());
    assertEquals("/apps/component/common", componentType.getCommonUiFrameworkView().getPath());
  }

  @Test
  public void testGetCommonUiFrameworkViewWhenLibsHasCommonView()
      throws InvalidCommonUiFrameworkException {

    context.create().resource("/libs/component", componentProperties);
    context.create().resource("/libs/component/common", componentFrameworkViewProperties);
    resource = context.create().resource("/apps/component");

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/libs/component/common", componentType.getCommonUiFrameworkView().getPath());
  }

  @Test
  public void testGetCommonUiFrameworkViewWhenAppsOverridesButHasNoCommonView()
      throws InvalidCommonUiFrameworkException {

    context.create().resource("/libs/component", componentProperties);
    context.create().resource("/libs/component/common", componentFrameworkViewProperties);
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/libs/component/common", componentType.getCommonUiFrameworkView().getPath());
  }

  @Test
  public void testGetCommonUiFrameworkViewWhenCommonViewIsMissing() {
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    try {
      componentType.getCommonUiFrameworkView().getPath();
    } catch (InvalidCommonUiFrameworkException e) {
      exception = e;
    }
    assertEquals("Unable to retrieve 'common' ComponentUiFrameworkView for '/apps/component'.",
        exception.getMessage());
  }

  @Test
  public void testIsBypassUiFrameworks() {
    componentProperties.put("bypassUiFrameworks", true);
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertTrue(componentType.isBypassUiFrameworks());
  }

  @Test
  public void testIsBypassUiFrameworksWhenKestrosComponent() {
    componentProperties.put("bypassUiFrameworks", false);
    resource = context.create().resource("/libs/kestros/components/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertTrue(componentType.isBypassUiFrameworks());
  }

  @Test
  public void testIsBypassUiFrameworksWhenFalse() {
    componentProperties.put("bypassUiFrameworks", false);
    resource = context.create().resource("/apps/kestros/components/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertFalse(componentType.isBypassUiFrameworks());
  }

  @Test
  public void testGetAllowedComponentTypeGroupsNames() {
    componentProperties.put("allowedComponentTypes",
        new String[]{"group:Group 1", "group:Group 2", "/content/component-type-1",
            "/content/component-type-2"});
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals(2, componentType.getAllowedComponentTypeGroupNames().size());
    assertEquals("Group 1", componentType.getAllowedComponentTypeGroupNames().get(0));
    assertEquals("Group 2", componentType.getAllowedComponentTypeGroupNames().get(1));
  }

  @Test
  public void testGetAllowedComponentTypePaths() {
    componentProperties.put("allowedComponentTypes",
        new String[]{"group:Group 1", "group:Group 2", "/content/component-type-1",
            "/content/component-type-2"});
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals(2, componentType.getAllowedComponentTypePaths().size());
    assertEquals("/content/component-type-1", componentType.getAllowedComponentTypePaths().get(0));
    assertEquals("/content/component-type-2", componentType.getAllowedComponentTypePaths().get(1));
  }

  @Test
  public void testGetUiFrameworkViews() throws Exception {
    resource = context.create().resource("/apps/component", componentProperties);

    context.create().resource("/apps/component/framework-1", componentFrameworkViewProperties);
    context.create().resource("/apps/component/framework-2", componentFrameworkViewProperties);

    context.create().resource("/etc/ui-frameworks/framework-1", frameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework-2", frameworkProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals(2, componentType.getUiFrameworkViews().size());
  }

  @Test
  public void testGetUiFrameworkViewsWhenViewIsSlingFolder() throws Exception {
    resource = context.create().resource("/apps/component", componentProperties);

    componentFrameworkViewProperties.put("jcr:primaryType", "sling:Folder");
    context.create().resource("/apps/component/framework-1", componentFrameworkViewProperties);
    context.create().resource("/apps/component/framework-2", componentFrameworkViewProperties);

    context.create().resource("/etc/ui-frameworks/framework-1", frameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework-2", frameworkProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals(2, componentType.getUiFrameworkViews().size());
  }

  @Test
  public void testGetUiFrameworkViewsWhenHasCommon() throws Exception {
    resource = context.create().resource("/apps/component", componentProperties);

    context.create().resource("/apps/component/common", componentFrameworkViewProperties);
    context.create().resource("/apps/component/framework-1", componentFrameworkViewProperties);
    context.create().resource("/apps/component/framework-2", componentFrameworkViewProperties);

    context.create().resource("/etc/ui-frameworks/framework-1", frameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework-2", frameworkProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals(3, componentType.getUiFrameworkViews().size());
  }

  @Test
  public void testGetUiFrameworkViewsWhenAppsAddsNewViews() throws Exception {
    resource = context.create().resource("/libs/component", componentProperties);
    context.create().resource("/apps/component");

    context.create().resource("/apps/component/common", componentFrameworkViewProperties);
    context.create().resource("/apps/component/framework-1", componentFrameworkViewProperties);
    context.create().resource("/apps/component/framework-2", componentFrameworkViewProperties);

    context.create().resource("/etc/ui-frameworks/framework-1", frameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework-2", frameworkProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals(3, componentType.getUiFrameworkViews().size());
  }

  @Test
  public void testGetUiFrameworkViewsWhenExcludedFrameworks() throws Exception {
    componentProperties.put("excludedUiFrameworks", new String[]{"excluded-1", "excluded-2"});

    resource = context.create().resource("/apps/component", componentProperties);

    context.create().resource("/apps/component/framework-1", componentFrameworkViewProperties);
    context.create().resource("/apps/component/framework-2", componentFrameworkViewProperties);
    context.create().resource("/apps/component/excluded-1", componentFrameworkViewProperties);
    context.create().resource("/apps/component/excluded-2", componentFrameworkViewProperties);

    frameworkProperties.put("uiFrameworkCode", "framework-1");
    context.create().resource("/etc/ui-frameworks/framework-1", frameworkProperties);

    frameworkProperties.put("uiFrameworkCode", "framework-2");
    context.create().resource("/etc/ui-frameworks/framework-2", frameworkProperties);

    frameworkProperties.put("uiFrameworkCode", "excluded-1");
    context.create().resource("/etc/ui-frameworks/excluded-1", frameworkProperties);

    frameworkProperties.put("uiFrameworkCode", "excluded-2");
    context.create().resource("/etc/ui-frameworks/excluded-2", frameworkProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals(2, componentType.getUiFrameworkViews().size());
  }


  @Test
  public void testGetFrameworkScriptRootResource() throws Exception {
    resource = context.create().resource("/apps/component", componentProperties);
    context.create().resource("/apps/component/framework-1", componentFrameworkViewProperties);

    UiFramework framework = context.create().resource("/etc/ui-frameworks/framework-1",
        frameworkProperties).adaptTo(UiFramework.class);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/apps/component/framework-1",
        componentType.getComponentUiFrameworkView(framework).getPath());
  }

  @Test
  public void testGetAllowedComponentTypeGroups() {
    componentProperties.put("componentGroup", "Group 1");
    ComponentType componentType1 = context.create().resource("/apps/component-type-1",
        componentProperties).adaptTo(ComponentType.class);
    ComponentType componentType2 = context.create().resource("/apps/component-type-2",
        componentProperties).adaptTo(ComponentType.class);

    componentProperties.put("componentGroup", "Group 2");
    ComponentType componentType3 = context.create().resource("/apps/component-type-3",
        componentProperties).adaptTo(ComponentType.class);
    ComponentType componentType4 = context.create().resource("/apps/component-type-4",
        componentProperties).adaptTo(ComponentType.class);
    allComponentTypes.add(componentType1);
    allComponentTypes.add(componentType2);
    allComponentTypes.add(componentType3);
    allComponentTypes.add(componentType4);

    //    componentProperties.put("allowedComponentTypes",
    //        new String[]{"group:Group 1", "group:Group 2", "/content/component-type-1",
    //            "/content/component-type-2"});
    componentProperties.put("componentGroup", "Group 1");
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals(2, componentType.getAllowedComponentTypeGroups().size());
    assertEquals("Group 1", componentType.getAllowedComponentTypeGroups().get(0).getTitle());
    assertEquals("Group 2", componentType.getAllowedComponentTypeGroups().get(1).getTitle());
    assertEquals(3, componentType.getAllowedComponentTypeGroups().get(
        0).getComponentTypes().size());
    assertEquals(2, componentType.getAllowedComponentTypeGroups().get(
        1).getComponentTypes().size());
  }

  @Test
  public void testGetAllowedComponentTypeGroupsWhenAllowedComponentTypePaths() {
    componentProperties.put("componentGroup", "Group 1");
    ComponentType componentType1 = context.create().resource("/apps/component-type-1",
        componentProperties).adaptTo(ComponentType.class);
    ComponentType componentType2 = context.create().resource("/apps/component-type-2",
        componentProperties).adaptTo(ComponentType.class);

    componentProperties.put("componentGroup", "Group 2");
    ComponentType componentType3 = context.create().resource("/apps/component-type-3",
        componentProperties).adaptTo(ComponentType.class);
    ComponentType componentType4 = context.create().resource("/apps/component-type-4",
        componentProperties).adaptTo(ComponentType.class);
    allComponentTypes.add(componentType1);
    allComponentTypes.add(componentType2);
    allComponentTypes.add(componentType3);
    allComponentTypes.add(componentType4);

    //    componentProperties.put("allowedComponentTypes",
    //        new String[]{"group:Group 1", "group:Group 2", "/content/component-type-1",
    //            "/content/component-type-2"});
    componentProperties.put("allowedComponentTypes", "/apps/component-type-1");
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals(1, componentType.getAllowedComponentTypeGroups().size());
    assertEquals("Group 1", componentType.getAllowedComponentTypeGroups().get(0).getTitle());
    assertEquals(1, componentType.getAllowedComponentTypeGroups().get(
        0).getComponentTypes().size());
  }

  @Test
  public void testGetMissingUiFrameworkCodesWhenHasAll() {
    resource = context.create().resource("/apps/component", componentProperties);

    context.create().resource("/apps/component/framework-1", componentFrameworkViewProperties);
    context.create().resource("/apps/component/framework-2", componentFrameworkViewProperties);

    frameworkProperties.put("uiFrameworkCode", "framework-1");
    context.create().resource("/etc/ui-frameworks/framework-1", frameworkProperties);

    frameworkProperties.put("uiFrameworkCode", "framework-2");
    context.create().resource("/etc/ui-frameworks/framework-2", frameworkProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals(0, componentType.getMissingUiFrameworkCodes().size());
  }

  @Test
  public void testGetMissingUiFrameworkCodesWhenMissingViews() {
    resource = context.create().resource("/apps/component", componentProperties);

    context.create().resource("/apps/component/framework-1", componentFrameworkViewProperties);

    frameworkProperties.put("kes:uiFrameworkCode", "framework-1");
    context.create().resource("/etc/ui-frameworks/framework-1", frameworkProperties);

    frameworkProperties.put("kes:uiFrameworkCode", "framework-2");
    context.create().resource("/etc/ui-frameworks/framework-2", frameworkProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals(1, componentType.getMissingUiFrameworkCodes().size());
    assertEquals("framework-2", componentType.getMissingUiFrameworkCodes().get(0));
  }

  @Test
  public void testGetScript() throws InvalidScriptException, InvalidCommonUiFrameworkException {
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    uiFrameworkProperties.put("frameworkCode", "ui-framework");
    Resource uiFrameworkResource = context.create().resource("/ui-framework",
        uiFrameworkProperties);
    uiFramework = uiFrameworkResource.adaptTo(UiFramework.class);
    resource = context.create().resource("/apps/component", componentProperties);
    context.create().resource("/apps/component/ui-framework");
    context.create().resource("/apps/component/ui-framework/content.html", fileProperties);
    context.create().resource("/apps/component/ui-framework/content.html/jcr:content",
        fileJcrContentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/apps/component/ui-framework/content.html",
        componentType.getScript("content.html", uiFramework).getPath());
  }

  @Test
  public void testGetScriptWhenInheritedFromSupertype()
      throws InvalidScriptException, InvalidCommonUiFrameworkException {
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    uiFrameworkProperties.put("frameworkCode", "ui-framework");
    Resource uiFrameworkResource = context.create().resource("/ui-framework",
        uiFrameworkProperties);
    uiFramework = uiFrameworkResource.adaptTo(UiFramework.class);

    context.create().resource("/apps/supertype", componentProperties);
    context.create().resource("/apps/supertype/ui-framework");
    context.create().resource("/apps/supertype/ui-framework/content.html", fileProperties);
    context.create().resource("/apps/supertype/ui-framework/content.html/jcr:content",
        fileJcrContentProperties);

    componentProperties.put("sling:resourceSuperType", "/apps/supertype");
    resource = context.create().resource("/apps/component", componentProperties);
    context.create().resource("/apps/component/ui-framework");

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/apps/supertype/ui-framework/content.html",
        componentType.getScript("content.html", uiFramework).getPath());
  }

  @Test
  public void testGetScriptWhenInheritedFromSupertypeAndUiFrameworkViewIsMissing()
      throws InvalidScriptException, InvalidCommonUiFrameworkException {
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    uiFrameworkProperties.put("frameworkCode", "ui-framework");
    Resource uiFrameworkResource = context.create().resource("/ui-framework",
        uiFrameworkProperties);
    uiFramework = uiFrameworkResource.adaptTo(UiFramework.class);

    context.create().resource("/apps/supertype", componentProperties);
    context.create().resource("/apps/supertype/ui-framework");
    context.create().resource("/apps/supertype/ui-framework/content.html", fileProperties);
    context.create().resource("/apps/supertype/ui-framework/content.html/jcr:content",
        fileJcrContentProperties);

    componentProperties.put("sling:resourceSuperType", "/apps/supertype");
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/apps/supertype/ui-framework/content.html",
        componentType.getScript("content.html", uiFramework).getPath());
  }


  @Test
  public void testGetScriptWhenInheritedFromSupertypeAndInheritedViewIsInvalid()
      throws InvalidScriptException, InvalidCommonUiFrameworkException {
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    uiFrameworkProperties.put("frameworkCode", "ui-framework");
    Resource uiFrameworkResource = context.create().resource("/ui-framework",
        uiFrameworkProperties);
    uiFramework = uiFrameworkResource.adaptTo(UiFramework.class);

    context.create().resource("/apps/supertype", componentProperties);
    context.create().resource("/apps/supertype/ui-framework");
    context.create().resource("/apps/supertype/ui-framework/content.html", fileProperties);

    componentProperties.put("sling:resourceSuperType", "/apps/supertype");
    resource = context.create().resource("/apps/component", componentProperties);
    context.create().resource("/apps/component/common/content.html", fileProperties);
    context.create().resource("/apps/component/common/content.html/jcr:content",
        fileJcrContentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/apps/component/common/content.html",
        componentType.getScript("content.html", uiFramework).getPath());
  }

  @Test
  public void testGetScriptWhenCommonViewIsInherited()
      throws InvalidScriptException, InvalidCommonUiFrameworkException {
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    uiFrameworkProperties.put("frameworkCode", "ui-framework");
    Resource uiFrameworkResource = context.create().resource("/ui-framework",
        uiFrameworkProperties);
    uiFramework = uiFrameworkResource.adaptTo(UiFramework.class);

    context.create().resource("/apps/supertype", componentProperties);
    context.create().resource("/apps/supertype/ui-framework");
    context.create().resource("/apps/supertype/ui-framework/content.html", fileProperties);
    context.create().resource("/apps/supertype/common/content.html", fileProperties);
    context.create().resource("/apps/supertype/common/content.html/jcr:content",
        fileJcrContentProperties);

    componentProperties.put("sling:resourceSuperType", "/apps/supertype");
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);
    assertEquals("/apps/supertype/common/content.html",
        componentType.getScript("content.html", uiFramework).getPath());
  }

  @Test
  public void testGetScriptWhenFallsBackToCommon()
      throws InvalidScriptException, InvalidCommonUiFrameworkException {
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    uiFrameworkProperties.put("frameworkCode", "ui-framework");
    Resource uiFrameworkResource = context.create().resource("/ui-framework",
        uiFrameworkProperties);
    uiFramework = uiFrameworkResource.adaptTo(UiFramework.class);
    resource = context.create().resource("/apps/component", componentProperties);
    context.create().resource("/apps/component/common");
    context.create().resource("/apps/component/common/content.html", fileProperties);
    context.create().resource("/apps/component/common/content.html/jcr:content",
        fileJcrContentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/apps/component/common/content.html",
        componentType.getScript("content.html", uiFramework).getPath());
  }

  @Test
  public void testGetScriptWhenUiFrameworkIsNull()
      throws InvalidScriptException, InvalidCommonUiFrameworkException {
    resource = context.create().resource("/apps/component", componentProperties);
    context.create().resource("/apps/component/common");
    context.create().resource("/apps/component/common/content.html", fileProperties);
    context.create().resource("/apps/component/common/content.html/jcr:content",
        fileJcrContentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("/apps/component/common/content.html",
        componentType.getScript("content.html", null).getPath());
  }


  @Test
  public void testGetFontAwesomeIcon() {
    componentProperties.put("fontAwesomeIcon", "icon-class");
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("icon-class", componentType.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenNotConfigured() {
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("fa fa-cube", componentType.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenInheritedFromSupertype() {
    Map<String, Object> supertypeProperties = new HashMap<>();
    supertypeProperties.putAll(componentProperties);
    supertypeProperties.put("fontAwesomeIcon", "supertype-icon");
    context.create().resource("/apps/supertype", supertypeProperties);

    componentProperties.put("sling:resourceSuperType", "/apps/supertype");
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("supertype-icon", componentType.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenSupertypeIsInvalid() {
    Map<String, Object> supertypeProperties = new HashMap<>();
    supertypeProperties.put("fontAwesomeIcon", "supertype-icon");
    context.create().resource("/apps/supertype", supertypeProperties);

    componentProperties.put("sling:resourceSuperType", "/apps/supertype");
    resource = context.create().resource("/apps/component", componentProperties);

    componentType = resource.adaptTo(ComponentType.class);

    assertEquals("fa fa-cube", componentType.getFontAwesomeIcon());
  }

  @Test
  public void testGetExcludedComponentTypeGroups() {
    componentProperties.put("excludedComponentTypes",
        new String[]{"/component-1", "/component-2", "group:group-1", "group:group-2",});
    resource = context.create().resource("/component", componentProperties);
    componentType = resource.adaptTo(ComponentType.class);
    assertEquals(2, componentType.getExcludedComponentTypeGroups().size());
    assertEquals("group-1", componentType.getExcludedComponentTypeGroups().get(0));
    assertEquals("group-2", componentType.getExcludedComponentTypeGroups().get(1));
  }

  @Test
  public void testGetExcludedComponentTypePaths() {
    componentProperties.put("excludedComponentTypes", new String[]{"/component-1", "/component-2"});
    resource = context.create().resource("/component", componentProperties);
    componentType = resource.adaptTo(ComponentType.class);
    assertEquals(2, componentType.getExcludedComponentTypePaths().size());
    assertEquals("/component-1", componentType.getExcludedComponentTypePaths().get(0));
    assertEquals("/component-2", componentType.getExcludedComponentTypePaths().get(1));
  }

  @Test
  public void testGetExcludedComponentTypePathsWhenIncludesGroup() {
    componentProperties.put("excludedComponentTypes",
        new String[]{"/component-1", "/component-2", "group:group-1"});
    resource = context.create().resource("/component", componentProperties);
    componentType = resource.adaptTo(ComponentType.class);
    assertEquals(2, componentType.getExcludedComponentTypePaths().size());
    assertEquals("/component-1", componentType.getExcludedComponentTypePaths().get(0));
    assertEquals("/component-2", componentType.getExcludedComponentTypePaths().get(1));
  }

}