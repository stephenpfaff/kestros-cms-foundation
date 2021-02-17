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

package io.kestros.cms.uiframeworks.core.healthchecks;

import static org.junit.Assert.assertEquals;

import io.kestros.cms.uiframeworks.core.eventlisteners.HtlTemplateCachePurgeEventListener;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HtlTemplateCachePurgeEventListenerHealthCheckTest {

  @Rule
  public SlingContext context = new SlingContext();

  private HtlTemplateCachePurgeEventListenerHealthCheck healthCheck;
  private HtlTemplateCachePurgeEventListener service;

  @Before
  public void setUp() throws Exception {
    healthCheck = new HtlTemplateCachePurgeEventListenerHealthCheck();
    service = new HtlTemplateCachePurgeEventListener();

    context.registerService(HtlTemplateCachePurgeEventListener.class, service);
    context.registerInjectActivateService(healthCheck);
  }

  @Test
  public void testGetCacheService() {
    assertEquals(service, healthCheck.getCacheService());
  }

  @Test
  public void testGetServiceName() {
    assertEquals("HTL Template Cache Purge Event Listener", healthCheck.getServiceName());
  }
}