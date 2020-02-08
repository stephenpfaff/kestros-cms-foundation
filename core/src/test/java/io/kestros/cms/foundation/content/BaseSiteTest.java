package io.kestros.cms.foundation.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import io.kestros.cms.foundation.content.sites.BaseSite;
import io.kestros.cms.foundation.services.themeprovider.BaseThemeProviderService;
import io.kestros.cms.foundation.services.themeprovider.ThemeProviderService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseSiteTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BaseSite baseSite;

  private Resource resource;

  private Map<String, Object> siteProperties = new HashMap<>();

  private Map<String, Object> siteJcrContentProperties = new HashMap<>();

  private Map<String, Object> pageProperties = new HashMap<>();

  private Map<String, Object> pageContentProperties = new HashMap<>();

  private Map<String, Object> themeProperties = new HashMap<>();

  private Map<String, Object> componentTypeProperties = new HashMap<>();

  private ThemeProviderService themeProviderService = new BaseThemeProviderService();

  private Exception exception;

  // TODO make theme provider service optional
  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    context.registerService(ThemeProviderService.class, themeProviderService);

    pageProperties.put("jcr:primaryType", "kes:Page");
    siteProperties.put("jcr:primaryType", "kes:Site");
    themeProperties.put("jcr:primaryType", "kes:Theme");

    resource = context.create().resource("/content/site", siteProperties);
    context.create().resource("/content/site/jcr:content");

    context.create().resource("/etc/themes/theme-1", themeProperties);
    context.create().resource("/etc/themes/theme-2", themeProperties);
    context.create().resource("/etc/themes/theme-3", themeProperties);
    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
  }

  @Test
  public void testInitializeWhenWrongResourceType() {
    resource = context.create().resource("/content/invalid-site");

    try {
      baseSite = SlingModelUtils.adaptTo(resource, BaseSite.class);
    } catch (InvalidResourceTypeException e) {
      exception = e;
    }
    assertEquals("Unable to adapt '/content/invalid-site' to BaseSite: Invalid resource type.",
        exception.getMessage());
  }

  @Test
  public void testGetAllPages() throws Exception {
    context.create().resource("/content/site/page-1", pageProperties);
    context.create().resource("/content/site/page-1/child-1", pageProperties);
    context.create().resource("/content/site/page-1/child-1/grand-child-1", pageProperties);
    context.create().resource("/content/site/page-1/child-1/grand-child-2", pageProperties);
    context.create().resource("/content/site/page-1/child-1/grand-child-3", pageProperties);
    context.create().resource("/content/site/page-1/child-2", pageProperties);
    context.create().resource("/content/site/page-1/child-2/grand-child-1", pageProperties);
    context.create().resource("/content/site/page-1/child-2/grand-child-2", pageProperties);
    context.create().resource("/content/site/page-1/child-2/grand-child-3", pageProperties);
    context.create().resource("/content/site/page-2", pageProperties);
    context.create().resource("/content/site/page-2/child-1", pageProperties);
    context.create().resource("/content/site/page-2/child-1/grand-child-1", pageProperties);
    context.create().resource("/content/site/page-2/child-1/grand-child-2", pageProperties);
    context.create().resource("/content/site/page-2/child-1/grand-child-3", pageProperties);
    context.create().resource("/content/site/page-2/child-2", pageProperties);
    context.create().resource("/content/site/page-2/child-2/grand-child-1", pageProperties);
    context.create().resource("/content/site/page-2/child-2/grand-child-2", pageProperties);
    context.create().resource("/content/site/page-2/child-2/grand-child-3", pageProperties);

    context.create().resource("/content/site/page-INVALID");

    baseSite = resource.adaptTo(BaseSite.class);

    baseSite.doDetailedValidation();

    assertEquals(19, baseSite.getAllPages().size());

    assertFalse(baseSite.getErrorMessages().contains("Site has no pages."));

    assertEquals(2, baseSite.getChildPages().size());
  }

  @Test
  public void testGetLastModified() {
    Date date1 = new Date(1);
    Date date2 = new Date(2);
    Date date3 = new Date(3);

    siteProperties.put("jcr:lastModified", date2);
    resource = context.create().resource("/site", siteProperties);

    context.create().resource("/site/page-1", pageProperties);
    pageContentProperties.put("jcr:lastModified", date1);
    context.create().resource("/site/page-1/jcr:content", pageContentProperties);

    context.create().resource("/site/page-3", pageProperties);
    pageContentProperties.put("jcr:lastModified", date3);
    context.create().resource("/site/page-3/jcr:content", pageContentProperties);

    context.create().resource("/site/page-2", pageProperties);
    pageContentProperties.put("jcr:lastModified", date2);
    context.create().resource("/site/page-2/jcr:content", pageContentProperties);

    baseSite = resource.adaptTo(BaseSite.class);

    assertEquals(new Date(2).getTime(), baseSite.getLastModifiedDate().getTime());
    assertEquals(new Date(3).getTime(), baseSite.getAncestorPageLastModifiedDate().getTime());
  }

  @Test
  public void testGetFontAwesomeIcon() {
    resource = context.create().resource("/site", siteProperties);
    siteJcrContentProperties.put("sling:resourceType", "/apps/component-type");
    context.create().resource("/site/jcr:content", siteJcrContentProperties);
    baseSite = resource.adaptTo(BaseSite.class);

    assertEquals("fa fa-sitemap", baseSite.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenInheritedFromComponentType() {
    componentTypeProperties.put("fontAwesomeIcon", "icon-class");
    context.create().resource("/apps/component-type", componentTypeProperties);

    resource = context.create().resource("/site", siteProperties);
    siteJcrContentProperties.put("sling:resourceType", "/apps/component-type");
    context.create().resource("/site/jcr:content", siteJcrContentProperties);
    baseSite = resource.adaptTo(BaseSite.class);

    assertEquals("icon-class", baseSite.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenComponentTypeIconIsDefault() {
    componentTypeProperties.put("fontAwesomeIcon", "fa fa-cube");
    context.create().resource("/apps/component-type", componentTypeProperties);

    resource = context.create().resource("/site", siteProperties);
    siteJcrContentProperties.put("sling:resourceType", "/apps/component-type");
    context.create().resource("/site/jcr:content", siteJcrContentProperties);
    baseSite = resource.adaptTo(BaseSite.class);

    assertEquals("fa fa-sitemap", baseSite.getFontAwesomeIcon());
  }

}