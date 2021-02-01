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

package io.kestros.cms.uiframeworks.refactored.models;

import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.uilibraries.api.models.ScriptFile;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import java.util.List;
import org.apache.sling.api.resource.Resource;

/**
 * Wrapper object for when a UiFramework inherits Themes for previous versions.
 */
public class VirtualTheme implements Theme {

  private Theme theme;
  private UiFramework uiFramework;

  /**
   * Constructor.
   * @param theme Theme.
   * @param uiFramework UiFramework.
   */
  public VirtualTheme(Theme theme, UiFramework uiFramework) {
    this.theme = theme;
    this.uiFramework = uiFramework;
  }

  @Override
  public UiFramework getUiFramework() {
    return uiFramework;
  }

  @Override
  public String getFontAwesomeIcon() {
    return theme.getFontAwesomeIcon();
  }

  @Override
  public Resource getResource() {
    return theme.getResource();
  }

  @Override
  public String getTitle() {
    return theme.getTitle();
  }

  @Override
  public String getDescription() {
    return theme.getDescription();
  }

  @Override
  public String getName() {
    return theme.getName();
  }

  @Override
  public String getPath() {
    return String.format("%s/themes/%s", this.uiFramework.getPath(), getName());
  }

  @Override
  public String getCssPath() {
    return String.format("%s.css", getPath());
  }

  @Override
  public String getJsPath() {
    return String.format("%s.js", getPath());
  }

  @Override
  public List<String> getIncludedFileNames(ScriptType scriptType) {
    return theme.getIncludedFileNames(scriptType);
  }

  @Override
  public <T extends ScriptFile> List<T> getScriptFiles(List<ScriptType> scriptTypes,
      String folderName) {
    return theme.getScriptFiles(scriptTypes, folderName);
  }
}
