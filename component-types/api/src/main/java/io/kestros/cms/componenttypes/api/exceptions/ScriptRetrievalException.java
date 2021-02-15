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

package io.kestros.cms.componenttypes.api.exceptions;

/**
 * Thrown when HTL Scripts could not be retrieved for a component request.
 */
public class ScriptRetrievalException extends Exception {

  /**
   * Thrown when HTL Scripts could not be retrieved for a component request.
   *
   * @param message Messsage.
   */
  public ScriptRetrievalException(String message) {
    super(message);
  }

}
