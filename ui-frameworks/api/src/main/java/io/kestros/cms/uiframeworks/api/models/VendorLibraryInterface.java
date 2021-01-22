package io.kestros.cms.uiframeworks.api.models;

import io.kestros.cms.modeltypes.IconResource;
import io.kestros.cms.modeltypes.ThirdPartyResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import java.util.List;

public interface VendorLibraryInterface extends ThirdPartyResource, IconResource {

  List<VendorLibraryInterface> getDependencies();

  List<HtlTemplateFile> getTemplateFiles();

  List<String> getIncludedCdnJsScripts();

  List<String> getIncludedCdnCssScripts();

  List<BaseResource> getExternalizedFiles();

  List<String> getExternalizedFilesProperty();

}
