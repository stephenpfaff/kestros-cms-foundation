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

package io.kestros.cms.foundation.content.sites;

import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;

import io.kestros.cms.foundation.content.pages.BaseContentPageValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;

/**
 * ModelValidationService for {@link BaseSite} models.
 */
public class BaseSiteValidationService extends BaseContentPageValidationService {

  @Override
  public BaseSite getModel() {
    return (BaseSite) getGenericModel();
  }

  ModelValidator hasPages() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return !getModel().getChildPages().isEmpty();
      }

      @Override
      public String getMessage() {
        return "Site has pages.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }
}
