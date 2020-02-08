package io.kestros.cms.foundation.componenttypes;

import io.kestros.commons.structuredslingmodels.filetypes.BaseFile;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import java.util.Collections;
import java.util.List;

/**
 * FileType implementation for HTML files.
 */
public class HtmlFileType implements FileType {

  @Override
  public String getExtension() {
    return "html";
  }

  @Override
  public String getOutputContentType() {
    return "text/html";
  }

  @Override
  public List<String> getReadableContentTypes() {
    return Collections.singletonList("text/html");
  }

  @Override
  public String getName() {
    return "html";
  }

  @Override
  public <T extends BaseFile> Class<T> getFileModelClass() {
    return (Class<T>) HtmlFile.class;
  }


}
