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

import io.kestros.cms.uiframeworks.api.exceptions.HtlTemplateFileRetrievalException;
import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.models.VendorLibrary;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import java.util.List;

/**
 * Retrieves HTL Template files.
 */
public interface HtlTemplateFileRetrievalService extends ManagedService {

  /**
   * Retrieves HTL Template files for a given library.
   *
   * @param uiFramework UiFramework to retrieve HTL Templates for.
   * @return Retrieves HTL Template files for a given library.
   * @throws HtlTemplateFileRetrievalException Failed to retrieve HTL Template files/
   */
  List<HtlTemplateFile> getHtlTemplatesFromUiFramework(UiFramework uiFramework)
      throws HtlTemplateFileRetrievalException;

  /**
   * Retrieves HTL Template files for a given library.
   *
   * @param vendorLibrary VendorLibrary to retrieve HTL Templates for.
   * @return Retrieves HTL Template files for a given library.
   * @throws HtlTemplateFileRetrievalException Failed to retrieve HTL Template files/
   */
  List<HtlTemplateFile> getHtlTemplatesFromVendorLibrary(VendorLibrary vendorLibrary)
      throws HtlTemplateFileRetrievalException;
}