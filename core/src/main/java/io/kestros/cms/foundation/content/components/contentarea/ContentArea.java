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

package io.kestros.cms.foundation.content.components.contentarea;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Component used for containing other components.  When referenced in a script without an existing
 * resource, it will attempt to create itself, and assign the proper sling:resourceType property
 * value.
 */
@KestrosModel(docPaths = {
    "/content/guide-articles/kestros-cms/site-building/defining-content-areas"})
@Model(adaptables = Resource.class,
       resourceType = "kestros/commons/components/content-area")
public class ContentArea extends BaseComponent {

  /**
   * The relative path of the ContentArea. Does not include jcr:content.
   *
   * @return The relative path of the ContentArea. Does not include jcr:content.
   */
  @KestrosProperty(description = "The relative path of the ContentArea. Does not include "
                                 + "jcr:content.")
  public String getRelativePath() {
    String path = getPath();
    String jcrContentRelativePath = "/" + JcrConstants.JCR_CONTENT + "/";
    if (path.contains(jcrContentRelativePath)) {
      path = path.split(jcrContentRelativePath)[1];
    }
    return path;
  }
}