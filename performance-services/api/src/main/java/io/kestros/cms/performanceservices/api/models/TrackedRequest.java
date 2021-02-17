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

/**
 * Performance Tracked Request.
 */
public interface TrackedRequest {

  /**
   * Method request start time.
   *
   * @return Method request start time.
   */
  long startTime();

  /**
   * Method request end time.
   *
   * @return Method request end time.
   */
  long endTime();

  /**
   * Method request duration.
   *
   * @return Method request duration.
   */
  long duration();

  /**
   * Whether the request has completed.
   *
   * @return Whether the request has completed.
   */
  boolean isComplete();

  /**
   * Completes tracking.
   *
   * @param endTime End time.
   */
  void complete(long endTime);

}
