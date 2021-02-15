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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * Tracked method instance.
 */
public interface TrackedMethod {

  /**
   * Service the method belongs to.
   *
   * @return Service the method belongs to.
   */
  @JsonIgnore
  TrackedService getTrackedService();

  /**
   * Method name.
   *
   * @return Method name.
   */
  String getName();

  /**
   * All tracked request data.
   *
   * @return All tracked request data.
   */
  List<TrackedRequest> getAllTrackedRequests();

  /**
   * Retrieves a specified tracked request.
   *
   * @param requestId Request tracker ID.
   * @return A specified tracked request.
   */
  TrackedRequest getTrackedRequest(String requestId);

  /**
   * Total duration of all completed requests.
   *
   * @return Total duration of all completed requests.
   */
  long getTotalDuration();

  /**
   * Average duration of all completed requests.
   *
   * @return Average duration of all completed requests.
   */
  long getAverageDuration();

  /**
   * Minimum request duration.
   *
   * @return Minimum request duration.
   */
  long getMinimumDuration();

  /**
   * Maximum request duration.
   *
   * @return Maximum request duration.
   */
  long getMaximumDuration();

  /**
   * Creates a new tracked request.
   *
   * @param requestId Tracker ID.
   * @param trackedRequest Tracked request object.
   */
  void trackRequest(String requestId, TrackedRequest trackedRequest);

}
