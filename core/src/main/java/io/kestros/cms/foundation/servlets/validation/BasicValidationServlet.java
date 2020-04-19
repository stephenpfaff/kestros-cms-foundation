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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.foundation.services.cache.validation.ValidationCacheService;
import io.kestros.commons.structuredslingmodels.BaseSlingModel;
import javax.annotation.Nullable;
import javax.servlet.Servlet;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Servlet that provides basic validation messages for a given resource. Attempts to match the
 * requested to resource to the closest matching Sling Model that extends {@link BaseSlingModel} in
 * order to determine {@link io.kestros.commons.structuredslingmodels.validation.ModelValidator}s
 * that will be processed.
 */
@Component(service = {Servlet.class},
           property = {"sling.servlet.resourceTypes=sling/servlet/default",
               "sling.servlet.selectors=basic-validation", "sling.servlet.extensions=json",
               "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class BasicValidationServlet extends BaseValidationServlet {

  private static final long serialVersionUID = -6880653266429090069L;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference
  private transient ModelFactory modelFactory;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ValidationCacheService validationCacheService;

  @Override
  public void doValidation(final BaseSlingModel model) {
    model.validate();
  }

  @Override
  public ModelFactory getModelFactory() {
    return modelFactory;
  }

  @Override
  public Boolean isDetailed() {
    return false;
  }

  @Override
  @Nullable
  public ValidationCacheService getValidationCacheService() {
    return validationCacheService;
  }
}
