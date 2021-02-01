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

package io.kestros.cms.uiframeworks.refactored;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DesignConstantsTest {

  @Test
  public void testValues() {
    assertEquals("kes:uiFrameworkCode", DesignConstants.PN_UI_FRAMEWORK_CODE);
    assertEquals("kes:Theme", DesignConstants.THEME_PRIMARY_TYPE);
    assertEquals("/etc/vendor-libraries", DesignConstants.VENDOR_LIBRARIES_ETC_ROOT_PATH);
    assertEquals("/libs/kestros/vendor-libraries", DesignConstants.VENDOR_LIBRARIES_LIBS_ROOT_PATH);
    assertEquals("/etc/ui-frameworks", DesignConstants.UI_FRAMEWORKS_ETC_ROOT_PATH);
    assertEquals("/libs/kestros/ui-frameworks", DesignConstants.UI_FRAMEWORKS_LIBS_ROOT_PATH);
    assertEquals("themes", DesignConstants.NN_THEMES);
    assertEquals("variations", DesignConstants.NN_VARIATIONS);
    assertEquals("kes:theme", DesignConstants.PN_THEME_PATH);
    assertEquals("kes:uiFrameworkCode", DesignConstants.PN_UI_FRAMEWORK_CODE);
    assertEquals("kes:vendorLibraries", DesignConstants.PN_VENDOR_LIBRARIES);
  }
}