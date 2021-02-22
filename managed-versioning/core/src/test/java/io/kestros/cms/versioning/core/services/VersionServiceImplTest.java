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

package io.kestros.cms.versioning.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.models.VersionableResource;
import io.kestros.cms.versioning.core.utils.VersionResourceSorter;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class VersionServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private VersionServiceImpl versionService;

  private VersionableResource versionable;

  private Resource resource;

  private Map<String, Object> versionableProperties = new HashMap<>();
  private Map<String, Object> versionProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    versionService = new VersionServiceImpl();

    context.registerInjectActivateService(versionService);

    versionableProperties.put("sling:resourceType", "versionable");
    versionProperties.put("sling:resourceType", "version");

    resource = context.create().resource("/content/versionable", versionableProperties);
    context.create().resource("/content/versionable/versions/2.0.0", versionProperties);
    context.create().resource("/content/versionable/versions/0.0.2", versionProperties);
    context.create().resource("/content/versionable/versions/1.1.1", versionProperties);
    context.create().resource("/content/versionable/versions/0.0.1", versionProperties);
    context.create().resource("/content/versionable/versions/0.1.0", versionProperties);
    context.create().resource("/content/versionable/versions/1.0.0", versionProperties);
  }

  @Test
  public void getCurrentVersion() throws VersionFormatException {
    versionable = resource.adaptTo(SampleVersionable.class);
    assertNotNull(versionService.getCurrentVersion(versionable));
    assertEquals("2.0.0", versionService.getCurrentVersion(versionable).getName());
  }

  @Test
  public void getVersionResource() throws VersionRetrievalException {
    versionable = resource.adaptTo(SampleVersionable.class);
    assertNotNull(versionService.getVersionResource(versionable, "1.0.0"));
    assertEquals("1.0.0", versionService.getVersionResource(versionable, "1.0.0").getName());
  }

  @Test
  public void getVersionHistory() {
    versionable = resource.adaptTo(SampleVersionable.class);

    assertNotNull(versionService.getVersionHistory(versionable));
    assertEquals(6, versionService.getVersionHistory(versionable).size());

    assertEquals("0.0.1", versionService.getVersionHistory(versionable).get(0).getName());
    assertEquals("0.0.2", versionService.getVersionHistory(versionable).get(1).getName());
    assertEquals("0.1.0", versionService.getVersionHistory(versionable).get(2).getName());
    assertEquals("1.0.0", versionService.getVersionHistory(versionable).get(3).getName());
    assertEquals("1.1.1", versionService.getVersionHistory(versionable).get(4).getName());
    assertEquals("2.0.0", versionService.getVersionHistory(versionable).get(5).getName());
  }

  @Test
  public void getVersionsFolderResource() throws ChildResourceNotFoundException {
    versionable = resource.adaptTo(SampleVersionable.class);
    assertNotNull(versionService.getVersionsFolderResource(versionable));
    assertEquals("/content/versionable/versions",
        versionService.getVersionsFolderResource(versionable).getPath());
  }

  @Test
  public void testGetPreviousVersion()
      throws ChildResourceNotFoundException, NoValidAncestorException {
    resource = context.create().resource("/content/versionable/versions/0.1.3", versionProperties);
    SampleVersion version = resource.adaptTo(SampleVersion.class);
    assertEquals("/content/versionable/versions/0.1.0",
        versionService.getPreviousVersion(version).getPath());
  }

  @Test
  public void testGetPreviousVersionWhenMinorVersionIsHigher()
      throws ChildResourceNotFoundException, NoValidAncestorException {
    resource = context.create().resource("/content/versionable/versions/0.2.3", versionProperties);
    SampleVersion version = resource.adaptTo(SampleVersion.class);
    assertEquals("/content/versionable/versions/0.1.0",
        versionService.getPreviousVersion(version).getPath());
  }

  @Test
  public void testGetPreviousVersionWhenMajorVersionIsHigher()
      throws ChildResourceNotFoundException, NoValidAncestorException {
    resource = context.create().resource("/content/versionable/versions/3.2.3", versionProperties);
    SampleVersion version = resource.adaptTo(SampleVersion.class);
    assertEquals("/content/versionable/versions/2.0.0",
        versionService.getPreviousVersion(version).getPath());
  }

  @Test
  public void testGetClosestVersion()
      throws ChildResourceNotFoundException, NoValidAncestorException, VersionRetrievalException {
    context.create().resource("/content/versionable/versions/1.2.3", versionProperties);
    context.create().resource("/content/versionable/versions/2.4.2", versionProperties);
    context.create().resource("/content/versionable/versions/3.0.0", versionProperties);

    versionable = resource.adaptTo(SampleVersionable.class);

    assertEquals("/content/versionable/versions/3.0.0",
        versionService.getClosestVersion(versionable, "3.0.1").getPath());
    assertEquals("/content/versionable/versions/3.0.0",
        versionService.getClosestVersion(versionable, "3.0.0").getPath());
    assertEquals("/content/versionable/versions/1.2.3",
        versionService.getClosestVersion(versionable, "1.2.3").getPath());
    assertNull(        versionService.getClosestVersion(versionable, "0.0.0"));
  }

  @Test
  public void testVersionComparator() {
    VersionResourceSorter versionResourceSorter = new VersionResourceSorter();
    SampleVersion version1 = context.create().resource("/content/versionable/versions/0.0.5",
        versionProperties).adaptTo(SampleVersion.class);
    SampleVersion version2 = context.create().resource("/content/versionable/versions/0.0.6",
        versionProperties).adaptTo(SampleVersion.class);
    SampleVersion version3 = context.create().resource("/content/versionable/versions/0.2.1",
        versionProperties).adaptTo(SampleVersion.class);
    SampleVersion version4 = context.create().resource("/content/versionable/versions/0.3.1",
        versionProperties).adaptTo(SampleVersion.class);
    SampleVersion version5 = context.create().resource("/content/versionable/versions/4.1.6",
        versionProperties).adaptTo(SampleVersion.class);

    assertEquals(-1, versionResourceSorter.compare(version1, version2));
    assertEquals(-1, versionResourceSorter.compare(version1, version3));
    assertEquals(-1, versionResourceSorter.compare(version1, version4));
    assertEquals(-1, versionResourceSorter.compare(version1, version5));

    assertEquals(1, versionResourceSorter.compare(version2, version1));
    assertEquals(-1, versionResourceSorter.compare(version2, version3));
    assertEquals(-1, versionResourceSorter.compare(version2, version4));
    assertEquals(-1, versionResourceSorter.compare(version2, version5));

    assertEquals(1, versionResourceSorter.compare(version3, version1));
    assertEquals(1, versionResourceSorter.compare(version3, version2));
    assertEquals(-1, versionResourceSorter.compare(version3, version4));
    assertEquals(-1, versionResourceSorter.compare(version3, version5));

    assertEquals(1, versionResourceSorter.compare(version4, version1));
    assertEquals(1, versionResourceSorter.compare(version4, version2));
    assertEquals(1, versionResourceSorter.compare(version4, version3));
    assertEquals(-1, versionResourceSorter.compare(version4, version5));

    assertEquals(1, versionResourceSorter.compare(version5, version4));
    assertEquals(1, versionResourceSorter.compare(version5, version3));
    assertEquals(1, versionResourceSorter.compare(version5, version2));
    assertEquals(1, versionResourceSorter.compare(version5, version1));
  }
}