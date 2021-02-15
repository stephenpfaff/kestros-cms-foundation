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

import io.kestros.cms.performanceservices.api.models.TrackedRequest;
import io.kestros.cms.performanceservices.api.models.TrackedService;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import java.util.List;

/**
 * Manages Performance tracking data.
 */
public interface PerformanceTrackerManagementService extends ManagedService {

  /**
   * All services that have tracking data.
   *
   * @return All services that have tracking data.
   */
  List<TrackedService> getTrackedServices();

  /**
   * Starts request tracking for a specified method.
   *
   * @param service Service.
   * @param methodName Method name.
   * @return Tracker ID.
   */
  String startRequestTracking(ManagedService service, String methodName);

  /**
   * Ends request tracking for a specified method.
   *
   * @param requestId Tracker ID.
   */
  void endRequestTracking(String requestId);

  /**
   * Retrieves a tracked request, base on the request ID.
   *
   * @param requestId Tracker ID.
   * @return Tracked request, base on the request ID.
   */
  TrackedRequest getTrackedRequest(String requestId);

  /**
   * Creates a unique request token.
   *
   * @param startTime Start time.
   * @return Unique request token.
   */
  String getUniqueRequestToken(long startTime);


}
