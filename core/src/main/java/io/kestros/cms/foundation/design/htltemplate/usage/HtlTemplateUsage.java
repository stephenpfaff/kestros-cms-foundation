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

import static io.kestros.cms.foundation.utils.DesignUtils.getHtlTemplateFromUiFramework;

import io.kestros.cms.foundation.componenttypes.frameworkview.ComponentUiFrameworkView;
import io.kestros.cms.foundation.design.htltemplate.HtlTemplate;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;

/**
 * Model for detailing and documenting the usage of and HTL Template within a
 * ComponentUiFrameworkView.
 */
public class HtlTemplateUsage {

  private ComponentUiFrameworkView componentUiFrameworkView;
  private HtlTemplate usedHtlTemplate;
  private String name;
  private String title;
  private List<HtlTemplateParameterUsage> templateParameterUsageList;

  /**
   * Constructs an HtlTemplateUsage object.
   *
   * @param element Element that specified the template call.
   * @param componentUiFrameworkView componentUiFrameworkView that contains the template call.
   */
  public HtlTemplateUsage(Element element, ComponentUiFrameworkView componentUiFrameworkView) {
    setComponentUiFrameworkView(componentUiFrameworkView);

    if (element.attr("data-sly-call").split("@")[0].split("templates.").length > 1) {
      setName(element.attr("data-sly-call").split("@")[0].split("templates.")[1].replaceAll(" ",
          "").replaceAll("}", ""));
      try {
        setUsedHtlTemplate(
            getHtlTemplateFromUiFramework(this.componentUiFrameworkView.getUiFramework(),
                getName()));
        if (usedHtlTemplate != null) {
          setTitle(usedHtlTemplate.getTitle());

          List<HtlTemplateParameterUsage> parameters = new ArrayList<>();
          String parametersArrayString;
          if (element.attr("data-sly-call").contains("@")) {
            parametersArrayString = element.attr("data-sly-call").split("@")[1];
            parametersArrayString = parametersArrayString.replaceAll("}", "");
            for (String parameterString : parametersArrayString.split(",")) {
              String parameterName = parameterString.split("=")[0].replaceAll(" ", "").replaceAll(
                  "\n", "").replaceAll("\t", "");
              try {
                parameters.add(new HtlTemplateParameterUsage(parameterName, "",
                    getUsedHtlTemplate().getTemplateParameter(parameterName),
                    getUsedHtlTemplate()));
              } catch (ResourceNotFoundException e) {
                // todo log.
              }
            }
            setTemplateParameterUsageList(parameters);
          }
        }
      } catch (ResourceNotFoundException e) {
        // todo log.
      }

    }

  }

  /**
   * Name of the called template.
   *
   * @return Name of the called template.
   */
  public String getName() {
    return this.name;
  }

  private void setName(String name) {
    this.name = name;
  }

  /**
   * The HtlTemplate that is being called.
   *
   * @return The HtlTemplate that is being called.
   * @throws ResourceNotFoundException UiFramework could not be retrieved.
   */
  public HtlTemplate getUsedHtlTemplate() throws ResourceNotFoundException {
    return this.usedHtlTemplate;
  }

  private void setUsedHtlTemplate(HtlTemplate usedHtlTemplate) {
    this.usedHtlTemplate = usedHtlTemplate;
  }

  /**
   * ComponentUiFrameworkView that is implementing the HtlTemplate.
   *
   * @return ComponentUiFrameworkView that is implementing the HtlTemplate.
   */
  public ComponentUiFrameworkView getComponentUiFrameworkView() {
    return this.componentUiFrameworkView;
  }

  private void setComponentUiFrameworkView(ComponentUiFrameworkView componentUiFrameworkView) {
    this.componentUiFrameworkView = componentUiFrameworkView;
  }

  /**
   * Parameters that the template usage specifies.
   *
   * @return Parameters that the template usage specifies.
   */
  public List<HtlTemplateParameterUsage> getTemplateParameterUsageList() {
    return templateParameterUsageList;
  }

  private void setTemplateParameterUsageList(
      List<HtlTemplateParameterUsage> templateParameterUsageList) {
    this.templateParameterUsageList = templateParameterUsageList;
  }

  /**
   * Template title.
   *
   * @return Template title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets Template title.
   *
   * @param title Template title.
   */
  public void setTitle(String title) {
    this.title = title;
  }
}
