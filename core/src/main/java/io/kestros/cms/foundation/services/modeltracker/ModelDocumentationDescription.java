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

package io.kestros.cms.foundation.services.modeltracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sling.models.annotations.Model;
import org.osgi.framework.Bundle;

/**
 * Object which provides details of a Sling {@link Model} Class. Used for validation and
 * documentation.
 */
public class ModelDocumentationDescription {

  private final String adaptable;
  private final List<String> adapters;
  private final String condition;
  private final Bundle bundle;
  private Class modelClass;
  private final List<String> resourceTypes = new ArrayList<>();
  private final boolean deprecated;

  /**
   * ModelDocumentationDescription constructor.
   *
   * @param bundle Bundle.
   * @param modelClass Model Class.
   * @param adaptable Adaptable.
   * @param adapters Adapters.
   * @param condition Model Condition.
   * @param deprecated Whether the model is deprecated.
   */
  public ModelDocumentationDescription(final Bundle bundle, final Class modelClass,
      final String adaptable, final String[] adapters, final String condition, boolean deprecated) {
    this.adaptable = adaptable;
    this.adapters = Arrays.asList(adapters);
    this.condition = condition;
    this.bundle = bundle;
    this.deprecated = deprecated;
    this.modelClass = modelClass;
    Model annotation = ((Model) modelClass.getAnnotation(Model.class));
    this.resourceTypes.addAll(Arrays.asList(annotation.resourceType()));
  }

  /**
   * Model Class.
   *
   * @return Model Class.
   */
  public Class getModelClass() {
    return this.modelClass;
  }

  /**
   * ResourceTypes registered to the Model.
   *
   * @return ResourceTypes registered to the Model.
   */
  public List<String> getResourceTypes() {
    return this.resourceTypes;
  }

  /**
   * Adaptable.
   *
   * @return Adaptable.
   */
  public String getAdaptable() {
    return adaptable;
  }

  /**
   * Adapters.
   *
   * @return Adapters.
   */
  public List<String> getAdapters() {
    return adapters;
  }

  /**
   * Model conditional.
   *
   * @return Model conditional.
   */
  public String getCondition() {
    return condition;
  }

  /**
   * Parent bundle.
   *
   * @return Parent bundle.
   */
  public Bundle getBundle() {
    return bundle;
  }

  /**
   * Whether the model is deprecated.
   *
   * @return Whether the model is deprecated.
   */
  public boolean isDeprecated() {
    return deprecated;
  }
}
