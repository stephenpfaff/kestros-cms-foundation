package io.kestros.cms.uiframeworks.api.models;

import static io.kestros.cms.uiframeworks.api.utils.DesignUtils.getAllVendorLibraries;

import io.kestros.cms.modeltypes.VersionResource;
import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.cms.versioning.api.models.Version;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.cms.versioning.api.models.VersionableResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import io.kestros.commons.uilibraries.UiLibrary;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VendorLibraryVersion extends UiLibrary
    implements VendorLibraryInterface, VersionResource {

  private static final Logger LOG = LoggerFactory.getLogger(VendorLibraryVersion.class);
  

  @Override
  public List<HtlTemplateFile> getTemplateFiles() {
    return null;
  }

  @Override
  public List<String> getIncludedCdnJsScripts() {
    return null;
  }

  @Override
  public List<String> getIncludedCdnCssScripts() {
    return null;
  }

  @Override
  public List<BaseResource> getExternalizedFiles() {
    return null;
  }

  @Override
  public List<String> getExternalizedFilesProperty() {
    return null;
  }

  @Override
  public String getFontAwesomeIcon() {
    return null;
  }

  @Override
  public String getDocumentationUrl() {
    return null;
  }

  @Override
  public VersionableResource getRootResource() throws NoValidAncestorException {
    return null;
  }

}
