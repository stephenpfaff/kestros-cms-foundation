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

package io.kestros.cms.foundation.design.htltemplate.usage;

import io.kestros.cms.foundation.design.htltemplate.HtlTemplate;
import io.kestros.cms.foundation.design.htltemplate.HtlTemplateParameter;

/**
 * Model for detailing and documenting the specified parameters within an {@link HtlTemplateUsage}
 * instance.
 */
public class HtlTemplateParameterUsage {

  private String parameterName;
  private String value;
  private HtlTemplateParameter parameter;
  private HtlTemplate template;

  /**
   * Constructs and HtlTemplateParameterUsage object.
   *
   * @param parameterName Name of the specified parameter.
   * @param value Value passed to the template.
   * @param parameter Associated HtlTemplateParameter
   * @param template The template the parameter is being passed to.
   */
  public HtlTemplateParameterUsage(String parameterName, String value,
      HtlTemplateParameter parameter, HtlTemplate template) {
    this.parameterName = parameterName;
    this.value = value;
    this.parameter = parameter;
    this.template = template;
  }

  /**
   * Name of the specified parameter.
   *
   * @return Name of the specified parameter.
   */
  public String getName() {
    return parameterName;
  }

  /**
   * Value passed to the template.
   *
   * @return Value passed to the template.
   */
  public String getValue() {
    return value;
  }

  /**
   * The associated HtlTemplateParameter.
   *
   * @return The associated HtlTemplateParameter.
   */
  public HtlTemplateParameter getParameter() {
    return parameter;
  }

  /**
   * The template the parameter is being passed to.
   *
   * @return The template the parameter is being passed to.
   */
  public HtlTemplate getTemplate() {
    return template;
  }

}
