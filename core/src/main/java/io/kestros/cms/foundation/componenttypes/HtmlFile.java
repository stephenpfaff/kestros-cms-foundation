package io.kestros.cms.foundation.componenttypes;

import io.kestros.commons.structuredslingmodels.filetypes.BaseFile;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * HtmlFile Sling Model used for adapting nt:file Resources.
 */
@Model(adaptables = Resource.class,
       resourceType = JcrConstants.NT_FILE)
public class HtmlFile extends BaseFile {

  @Override
  public FileType getFileType() {
    return new HtmlFileType();
  }
}
