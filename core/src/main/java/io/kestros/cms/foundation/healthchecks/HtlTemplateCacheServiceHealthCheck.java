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

package io.kestros.cms.foundation.healthchecks;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getAllDescendantsOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;

import io.kestros.cms.foundation.componenttypes.HtmlFile;
import io.kestros.cms.foundation.services.cache.htltemplate.HtlTemplateCacheService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import org.apache.felix.hc.annotation.Async;
import org.apache.felix.hc.annotation.HealthCheckMBean;
import org.apache.felix.hc.annotation.HealthCheckService;
import org.apache.felix.hc.annotation.ResultTTL;
import org.apache.felix.hc.annotation.Sticky;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.felix.hc.api.HealthCheck;
import org.apache.felix.hc.api.Result;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Health check which checks whether the HtlTemplateCacheService is active and running properly.
 */
@Component
@HealthCheckService(name = "HtlTemplateCacheService Check",
                    tags = {"kestros", "cms-foundation"})
@Async(intervalInSec = 60)
@ResultTTL(resultCacheTtlInMs = 10000)
@HealthCheckMBean(name = "HtlTemplateCacheServiceHealthCheck")
@Sticky(keepNonOkResultsStickyForSec = 10)
public class HtlTemplateCacheServiceHealthCheck implements HealthCheck {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private HtlTemplateCacheService htlTemplateCacheService;

  @Override
  public Result execute() {
    FormattingResultLog log = new FormattingResultLog();
    if (htlTemplateCacheService == null) {
      log.critical("HtlTemplateCacheService is not registered.");
    } else {
      if (htlTemplateCacheService.getServiceResourceResolver() == null) {
        log.critical("HtlTemplateCacheService has null Service ResourceResolver.");
      }
      try {
        if (getAllDescendantsOfType(getCacheRootResource(), HtmlFile.class).size() == 0) {
          log.critical("HtlTemplateCacheService has no cached compilation files.");
        }
      } catch (ResourceNotFoundException e) {
        log.critical("Root template cache folder not found.");
      }
    }
    log.info("HtlTemplateCacheService is registered and running properly.");
    return new Result(log);
  }

  private BaseResource getCacheRootResource() throws ResourceNotFoundException {
    return getResourceAsBaseResource(htlTemplateCacheService.getServiceCacheRootPath(),
        htlTemplateCacheService.getServiceResourceResolver());
  }
}


