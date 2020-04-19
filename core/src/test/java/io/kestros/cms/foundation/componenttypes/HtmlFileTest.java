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

package io.kestros.cms.foundation.componenttypes;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class HtmlFileTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testGetFileType() throws Exception {
    HtmlFile htmlFile = new HtmlFile();

    assertEquals("html", htmlFile.getFileType().getExtension());
    assertEquals("html", htmlFile.getFileType().getName());
    assertEquals("text/html", htmlFile.getFileType().getOutputContentType());
    assertEquals(1, htmlFile.getFileType().getReadableContentTypes().size());
    assertEquals("text/html", htmlFile.getFileType().getReadableContentTypes().get(0));
  }
}