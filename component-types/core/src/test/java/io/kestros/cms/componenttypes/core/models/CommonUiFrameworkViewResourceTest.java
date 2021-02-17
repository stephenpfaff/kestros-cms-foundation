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

package io.kestros.cms.componenttypes.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CommonUiFrameworkViewResourceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private CommonUiFrameworkViewResource commonView;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    commonView = new CommonUiFrameworkViewResource();
  }

  @Test
  public void testGetTitle() {
    assertEquals("Common", commonView.getTitle());
  }

  @Test
  public void testGetFontAwesomeIcon() {
    assertEquals("fas fa-palette", commonView.getFontAwesomeIcon());
  }

  @Test
  public void testGetManagingResourceType() {
    assertNull(commonView.getManagingResourceType());
  }

  @Test
  public void testIsDeprecated() {
    assertFalse(commonView.isDeprecated());
  }

  @Test
  public void testGetVersion() throws VersionFormatException {
    assertNull(commonView.getVersion());
  }

  @Test
  public void testGetRootResource() throws NoValidAncestorException {
    assertNull(commonView.getRootResource());
  }
}