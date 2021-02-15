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

package models;

import static org.junit.Assert.assertEquals;

import io.kestros.cms.versioning.api.models.Version;
import org.junit.Before;
import org.junit.Test;

public class VersionTest {

  private Version version;

  @Before
  public void setUp() throws Exception {
    version = new Version(1, 2, 3);
  }

  @Test
  public void getFormatted() {
    assertEquals("1.2.3", version.getFormatted());
  }

  @Test
  public void getMajorVersion() {
    assertEquals(1, version.getMajorVersion().intValue());
  }

  @Test
  public void getMinorVersion() {
    assertEquals(2, version.getMinorVersion().intValue());
  }

  @Test
  public void getPatchVersion() {
    assertEquals(3, version.getPatchVersion().intValue());
  }

  @Test
  public void testCompareTo() {
    assertEquals(1, version.compareTo(new Version(0, 0, 1)));
    assertEquals(1, version.compareTo(new Version(0, 1, 1)));
    assertEquals(1, version.compareTo(new Version(1, 1, 1)));
    assertEquals(1, version.compareTo(new Version(1, 2, 1)));
    assertEquals(1, version.compareTo(new Version(1, 2, 2)));
    assertEquals(0, version.compareTo(new Version(1, 2, 3)));
    assertEquals(-1, version.compareTo(new Version(1, 2, 4)));
    assertEquals(-1, version.compareTo(new Version(1, 3, 0)));
    assertEquals(-1, version.compareTo(new Version(2, 0, 0)));
  }
}