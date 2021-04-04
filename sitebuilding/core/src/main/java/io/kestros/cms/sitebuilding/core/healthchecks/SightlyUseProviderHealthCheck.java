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

package io.kestros.cms.sitebuilding.core.healthchecks;

import io.kestros.commons.osgiserviceutils.utils.OsgiServiceUtils;
import java.util.List;
import org.apache.felix.hc.annotation.Async;
import org.apache.felix.hc.annotation.HealthCheckMBean;
import org.apache.felix.hc.annotation.HealthCheckService;
import org.apache.felix.hc.annotation.ResultTTL;
import org.apache.felix.hc.annotation.Sticky;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.felix.hc.api.HealthCheck;
import org.apache.felix.hc.api.Result;
import org.apache.sling.scripting.sightly.use.UseProvider;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * Checks Sightly Render Context health.
 */
@Component
@HealthCheckService(name = "Sightly UseProvider Health Check",
                    tags = {"kestros", "cms-foundation", "site-building"})
@Async(intervalInSec = 60)
@ResultTTL(resultCacheTtlInMs = 10000)
@HealthCheckMBean(name = "SightlyUseProviderHealthCheck")
@Sticky(keepNonOkResultsStickyForSec = 10)
public class SightlyUseProviderHealthCheck implements HealthCheck {

  private ComponentContext componentContext;

  /**
   * Activates component.
   *
   * @param componentContext ComponentContext.
   */
  @Activate
  public void activate(ComponentContext componentContext) {
    this.componentContext = componentContext;
  }

  @Override
  public Result execute() {
    FormattingResultLog log = new FormattingResultLog();
    log.info("Running sightly UseProvider.");
    List<UseProvider> userProviderList = OsgiServiceUtils.getAllOsgiServicesOfType(componentContext,
        UseProvider.class);
    log.debug(String.format("%s UseProviders found", userProviderList.size()));
    if (userProviderList.isEmpty()) {
      log.critical("No HTL UseProviders found.");
    } else if (userProviderList.size() < 6) {
      log.critical("UseProviders do not match expected.");
    }
    return new Result(log);
  }

}
