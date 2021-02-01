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

package io.kestros.cms.uiframeworks.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.modeltypes.IconResource;
import java.io.IOException;
import javax.annotation.Nonnull;

/**
 * HTML File that contains HTL Templates.
 */
public interface HtlTemplateFile extends IconResource {

  /**
   * Title of the current HTL Template file.  Derived from the file name by replacing `-` with ` `,
   * removing `.html` and capitalizing the first letter of each word.
   *
   * @return Title of the current HTL Template file.
   */
  @Nonnull
  String getTitle();

  /**
   * Content of the current File, as a String.
   *
   * @return Content of the current File, as a String.
   * @throws IOException Thrown when there is an error reading contents of the File.
   */
  @JsonIgnore
  String getFileContent() throws IOException;

  /**
   * File path without the .html extension.
   *
   * @return File path without the .html extension.
   */
  @Nonnull
  String getPathWithoutExtension();
}