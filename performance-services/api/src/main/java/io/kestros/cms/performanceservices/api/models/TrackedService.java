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

package io.kestros.cms.performanceservices.api.models;

import java.util.List;

/**
 * Tracked Service interface.
 */
public interface TrackedService {

  /**
   * Name of tracked service.
   *
   * @return Name of tracked service.
   */
  String getName();

  /**
   * Retrieves or creates a TrackedMethod object.
   *
   * @param name Method name.
   * @return Retrieves or creates a TrackedMethod object.
   */
  TrackedMethod getOrCreateTrackedMethod(String name);

  /**
   * All currently tracked methods.
   *
   * @return All currently tracked methods.
   */
  List<TrackedMethod> getAllTrackedMethods();

  /**
   * Starts tracking a specified method.
   *
   * @param methodName Method name.
   * @param id Unique request tracker ID.
   */
  void startRequestTracking(String methodName, String id);

  /**
   * Starts tracking for a specified method, and unique request ID.
   *
   * @param methodName Method name.
   * @param id Tracker ID.
   */
  void endRequestTracking(String methodName, String id);

}
