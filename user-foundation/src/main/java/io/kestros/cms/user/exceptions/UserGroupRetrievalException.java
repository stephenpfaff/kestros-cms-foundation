/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.kestros.cms.user.exceptions;

/**
 * Exception thrown when an expected {@link io.kestros.cms.user.group.KestrosUserGroup} could not be
 * retrieved.
 */
public class UserGroupRetrievalException extends Exception {

  protected UserGroupRetrievalException() {
  }

  /**
   * Exception thrown when an expected {@link io.kestros.cms.user.group.KestrosUserGroup} could not
   * be retrieved.
   *
   * @param groupId Expected group that could not be retrieved.
   * @param message Cause message.
   */
  public UserGroupRetrievalException(final String groupId, final String message) {
    super("Unable to retrieve group " + groupId + ". " + message);
  }
}
