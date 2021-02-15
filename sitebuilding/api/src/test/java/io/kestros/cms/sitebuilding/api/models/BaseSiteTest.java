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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.componenttypes.api.exceptions.ComponentTypeRetrievalException;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.api.services.ComponentTypeRetrievalService;
import io.kestros.cms.sitebuilding.api.services.ThemeProviderService;
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

  private ComponentTypeRetrievalService componentTypeRetrievalService;

  private ComponentType componentType;

  private BaseSite baseSite;

  private Resource resource;

  private Map<String, Object> siteProperties = new HashMap<>();

  private Map<String, Object> siteJcrContentProperties = new HashMap<>();

  private Map<String, Object> pageProperties = new HashMap<>();

  private Map<String, Object> pageContentProperties = new HashMap<>();

  private Map<String, Object> themeProperties = new HashMap<>();

  private Map<String, Object> componentTypeProperties = new HashMap<>();

  private ThemeProviderService themeProviderService = mock(ThemeProviderService.class);

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    componentTypeRetrievalService = mock(ComponentTypeRetrievalService.class);
    componentType = mock(ComponentType.class);

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

    assertEquals(19, baseSite.getAllPages().size());

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
  public void testGetFontAwesomeIcon() throws ComponentTypeRetrievalException {
    context.registerService(ComponentTypeRetrievalService.class, componentTypeRetrievalService);
    when(componentTypeRetrievalService.getComponentType(anyString())).thenReturn(componentType);
    when(componentType.getFontAwesomeIcon()).thenReturn("fa fa-cube");

    resource = context.create().resource("/site", siteProperties);
    siteJcrContentProperties.put("sling:resourceType", "/apps/component-type");
    context.create().resource("/site/jcr:content", siteJcrContentProperties);
    baseSite = resource.adaptTo(BaseSite.class);

    assertEquals("fa fa-sitemap", baseSite.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenInheritedFromComponentType()
      throws ComponentTypeRetrievalException {
    context.registerService(ComponentTypeRetrievalService.class, componentTypeRetrievalService);
    when(componentTypeRetrievalService.getComponentType(anyString())).thenReturn(componentType);

    when(componentType.getFontAwesomeIcon()).thenReturn("icon-class");
    context.create().resource("/apps/component-type", componentTypeProperties);

    resource = context.create().resource("/site", siteProperties);
    siteJcrContentProperties.put("sling:resourceType", "/apps/component-type");
    context.create().resource("/site/jcr:content", siteJcrContentProperties);
    baseSite = resource.adaptTo(BaseSite.class);

    assertEquals("icon-class", baseSite.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenComponentTypeIconIsDefault()
      throws ComponentTypeRetrievalException {
    context.registerService(ComponentTypeRetrievalService.class, componentTypeRetrievalService);
    when(componentTypeRetrievalService.getComponentType(anyString())).thenReturn(componentType);

    when(componentType.getFontAwesomeIcon()).thenReturn("fa fa-cube");
    context.create().resource("/apps/component-type", componentTypeProperties);

    resource = context.create().resource("/site", siteProperties);
    siteJcrContentProperties.put("sling:resourceType", "/apps/component-type");
    context.create().resource("/site/jcr:content", siteJcrContentProperties);
    baseSite = resource.adaptTo(BaseSite.class);

    assertEquals("fa fa-sitemap", baseSite.getFontAwesomeIcon());
  }

}