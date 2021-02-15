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

package io.kestros.cms.componenttypes.core.healthchecks;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.componenttypes.api.services.ComponentTypeCacheService;
import io.kestros.commons.osgiserviceutils.healthchecks.BaseManagedServiceHealthCheck;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import org.apache.felix.hc.annotation.Async;
import org.apache.felix.hc.annotation.HealthCheckMBean;
import org.apache.felix.hc.annotation.HealthCheckService;
import org.apache.felix.hc.annotation.ResultTTL;
import org.apache.felix.hc.annotation.Sticky;
import org.apache.felix.hc.api.HealthCheck;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Health Check for {@link ComponentTypeCacheService}.
 */
@SuppressFBWarnings("RI_REDUNDANT_INTERFACES")
@Component
@HealthCheckService(name = "Component Type Cache Service Check",
                    tags = {"kestros", "component-types"})
@Async(intervalInSec = 300)
@ResultTTL(resultCacheTtlInMs = 10000)
@HealthCheckMBean(name = "ComponentTypeCacheServiceHealthCheck")
@Sticky(keepNonOkResultsStickyForSec = 300)
public class ComponentTypeCacheServiceHealthCheck extends BaseManagedServiceHealthCheck
    implements HealthCheck {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ComponentTypeCacheService componentTypeCacheService;

  @Override
  public ManagedService getCacheService() {
    return componentTypeCacheService;
  }

  @Override
  public String getServiceName() {
    return "Component Type Cache Service";
  }
}
