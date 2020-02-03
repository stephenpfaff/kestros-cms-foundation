package io.kestros.cms.foundation.design.theme;

import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ThemeValidationServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ThemeValidationService validationService;

  private Theme theme;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();
  private Map<String, Object> uiFrameworkProperties = new HashMap<>();

  @Before
  public void setUp() {
    context.addModelsForPackage("com.slingware");
    validationService = new ThemeValidationService();
    validationService = spy(validationService);

    properties.put("jcr:primaryType", "kes:Theme");
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
  }

  @Test
  public void testIsChildOfAUiFramework() {
    context.create().resource("/ui-framework", uiFrameworkProperties);

    resource = context.create().resource("/ui-framework/themes/theme", properties);

    theme = resource.adaptTo(Theme.class);

    when(validationService.getModel()).thenReturn(theme);

    assertTrue(validationService.isChildOfAUiFramework().isValid());
    assertEquals("Lives in UiFramework's 'themes' folder.",
        validationService.isChildOfAUiFramework().getMessage());
    assertEquals(ERROR,
        validationService.isChildOfAUiFramework().getType());
  }

  @Test
  public void testIsChildOfAUiFrameworkWhenNoChildOfUiFramework() {
    resource = context.create().resource("/themes/theme", properties);

    theme = resource.adaptTo(Theme.class);

    doReturn(theme).when(validationService).getModel();

    assertFalse(validationService.isChildOfAUiFramework().isValid());
  }

  @Test
  public void testIsChildOfAUiFrameworkWhenNoInThemesFolder() {
    properties.put("kes:uiFrameworkCode", "code");
    context.create().resource("/ui-framework", uiFrameworkProperties);

    resource = context.create().resource("/ui-framework/not-themes/theme", properties);

    theme = resource.adaptTo(Theme.class);

    when(validationService.getModel()).thenReturn(theme);

    assertFalse(validationService.isChildOfAUiFramework().isValid());
  }

}