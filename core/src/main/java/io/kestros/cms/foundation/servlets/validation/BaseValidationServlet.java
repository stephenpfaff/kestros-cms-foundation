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

package io.kestros.cms.foundation.servlets.validation;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsClosestType;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kestros.cms.foundation.services.cache.validation.ValidationCacheService;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.BaseSlingModel;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Baseline servlet logic to provides basic or detailed validation messages for a given resource.
 * Attempts to match the requested to resource to the closest matching Sling Model that extends
 * {@link BaseSlingModel} in order to determine
 * {@link io.kestros.commons.structuredslingmodels.validation.ModelValidator}s
 * that will be processed.
 */
public abstract class BaseValidationServlet extends SlingAllMethodsServlet {

  private static final Logger LOG = LoggerFactory.getLogger(BaseValidationServlet.class);
  private static final long serialVersionUID = 8033781512563632444L;

  /**
   * Whether the validation servlet retrieves detailed validation. (false = basic).
   *
   * @return Whether the validation servlet retrieves detailed validation.
   */
  public abstract Boolean isDetailed();

  /**
   * Performs validation on the given model.
   *
   * @param model Model to validate.
   */
  public abstract void doValidation(BaseSlingModel model);

  /**
   * {@link ModelFactory} used to determine the closest matching Sling Model type for the given
   * resource.
   *
   * @return {@link ModelFactory} used to determine the closest matching Sling Model type for the
   *     given resource.
   */
  public abstract ModelFactory getModelFactory();

  /**
   * Validation Cache Service.
   *
   * @return Validation Cache Service.
   */
  public abstract ValidationCacheService getValidationCacheService();


  @Override
  protected void doGet(@Nonnull final SlingHttpServletRequest request,
      @Nonnull final SlingHttpServletResponse response) throws IOException {
    final Resource resource = request.getResource();
    try {
      final BaseResource model = getResourceAsClosestType(resource, getModelFactory());
      final Map<String, Object> validationMap = new HashMap<>();

      if (getValidationCacheService() != null) {
        try {
          validationMap.put("errorMessages",
              getValidationCacheService().getCachedErrorMessages(resource, model.getClass(),
                  isDetailed()));

          validationMap.put("warningMessages",
              getValidationCacheService().getCachedErrorMessages(resource, model.getClass(),
                  isDetailed()));
        } catch (CacheRetrievalException e) {
          LOG.debug(e.getMessage());
        }
      }

      if (validationMap.isEmpty()) {
        doValidation(model);
        validationMap.put("errorMessages", model.getErrorMessages());
        validationMap.put("warningMessages", model.getWarningMessages());

        if (getValidationCacheService() != null) {
          getValidationCacheService().cacheValidationResults(model, isDetailed());
        }
      }

      final ObjectMapper mapper = new ObjectMapper();
      final String json = mapper.writeValueAsString(validationMap);
      response.getWriter().write(json);
    } catch (final InvalidResourceTypeException exception) {
      response.setStatus(400);
      LOG.warn(
          "Unable to build basic validation endpoint for {}, no matching resourceType could be "
          + "found.", resource.getPath());
    }
  }

}
