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

package io.kestros.cms.uiframeworks.api.services;

import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import java.io.IOException;
import java.util.List;

/**
 * Compiles a {@link UiFramework} HTL Template files into a single file.
 */
public interface HtlTemplateCompilationService extends ManagedService {

  /**
   * Compiles multiple HTL Template file content into a single string.
   *
   * @param htlTemplateFileList List of HTL Template Files
   * @return Compiled HTL Template file content.
   * @throws IOException IOException
   */
  String getCompiledHtlTemplateFileOutput(List<HtlTemplateFile> htlTemplateFileList)
      throws IOException;

}
