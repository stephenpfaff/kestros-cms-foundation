package io.kestros.cms.foundation.content.components.parentcomponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ParentComponentEditContextTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ParentComponentEditContext parentComponentEditContext;

  private Resource resource;

  private Map<String, Object> uiFrameworkProperties = new HashMap<>();

  private Map<String, Object> themeProperties = new HashMap<>();

  private Exception exception = null;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
  }

  @Test
  public void testIsEditMode() {
    context.request().setAttribute("editMode", true);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertTrue(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenFalse() {
    context.request().setAttribute("editMode", false);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenStringTrue() {
    context.request().setAttribute("editMode", "true");
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertTrue(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenStringFalse() {
    context.request().setAttribute("editMode", "false");
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenNull() {
    context.request().setAttribute("editMode", null);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenAttributeNotFound() {
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testGetEditTheme() throws InvalidThemeException {
    themeProperties.put("jcr:primaryType", "kes:Theme");
    context.create().resource("/libs/kestros/ui-frameworks/kestros-editor-include",
        uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/kestros-editor-include/themes/default",
        themeProperties);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertEquals("/libs/kestros/ui-frameworks/kestros-editor-include/themes/default",
        parentComponentEditContext.getEditTheme().getPath());
  }

  @Test
  public void testGetEditThemeWhenInvalidResourceType() {
    context.create().resource("/libs/kestros/ui-frameworks/kestros-editor-include/themes/default",
        themeProperties).adaptTo(Theme.class);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    try {
      assertEquals("/libs/kestros/ui-frameworks/kestros-editor-include/themes/default",
          parentComponentEditContext.getEditTheme().getPath());
    } catch (InvalidThemeException e) {
      exception = e;
    }
    assertEquals("Unable to retrieve theme 'default' under UiFramework "
                 + "'/libs/kestros/ui-frameworks/kestros-editor-include'. Unable to adapt "
                 + "'/libs/kestros/ui-frameworks/kestros-editor-include' to UiFramework: Invalid "
                 + "resource " + "type.", exception.getMessage());
  }

  @Test
  public void testGetEditThemeWhenResourceNotFound() {
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    try {
      assertEquals("/libs/kestros/ui-frameworks/kestros-editor-include/themes/default",
          parentComponentEditContext.getEditTheme().getPath());
    } catch (InvalidThemeException e) {
      exception = e;
    }
    assertEquals("Unable to retrieve theme 'default' under UiFramework "
                 + "'/libs/kestros/ui-frameworks/kestros-editor-include'. Unable to adapt "
                 + "'/libs/kestros/ui-frameworks/kestros-editor-include': Resource not found.",
        exception.getMessage());
  }
}