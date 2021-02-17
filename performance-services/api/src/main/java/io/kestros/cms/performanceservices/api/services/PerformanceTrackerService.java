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

package io.kestros.cms.performanceservices.api.services;

import io.kestros.commons.osgiserviceutils.services.ManagedService;

/**
 * Starts and stops performance tracking for services.
 */
public interface PerformanceTrackerService extends ManagedService {

  /**
   * Starts tracking a method's performance.
   *
   * @param service Service.
   * @param methodName Tracked method name.
   * @return Tracker ID.
   */
  String startRequestTracking(ManagedService service, String methodName);

  /**
   * End performance tracking for a method.
   *
   * @param trackerId Tracker ID.
   */
  void endRequestTracking(String trackerId);


}
