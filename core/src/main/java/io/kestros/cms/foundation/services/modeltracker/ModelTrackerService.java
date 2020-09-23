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

package io.kestros.cms.foundation.services.modeltracker;

import java.util.List;

/**
 * Service used for tracking Sling {@link org.apache.sling.models.annotations.Model}s registered to
 * the Sling instance.
 */
public interface ModelTrackerService {

  /**
   * List of all Sling Models registered to the instance.
   *
   * @return List of all Sling Models registered to the instance.
   */
  List<ModelDocumentationDescription> getAllRegisteredSlingModels();

  /**
   * First matching Model for a given resourceType.
   *
   * @param resourceType ResourceType to find the registering class for.
   * @return First matching Model for a given resourceType.
   */
  Class getModelClassForResourceType(String resourceType);

  /**
   * List of all Sling Models a specified resourceType is registered to.
   *
   * @param resourceType ResourceType to look up.
   * @return List of all Sling Models a specified resourceType is registered to.
   */
  List<ModelDocumentationDescription> getAllClassesRegisteredToAResourceType(String resourceType);

}
