package io.kestros.cms.temp.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.temp.models.htl.HtlTemplate;
import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.cms.uiframeworks.api.models.Theme;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

public class HtlTemplateFileService {


//  /**
//   * All HTL Templates associated to the current UiFramework.
//   *
//   * @return All HTL Templates associated to the current UiFramework.
//   */
//  @Nonnull
//  @JsonIgnore
//  public List<HtlTemplate> getTemplates(Theme theme) {
//    final List<HtlTemplate> templates = new ArrayList<>();
//
//    for (final HtlTemplateFile htlTemplateFile : theme.getTemplateFiles()) {
//      templates.addAll(htlTemplateFile.getTemplates());
//    }
//
//    return templates;
//  }



  //  /**
  //   * List of HTL Templates associated to the current Vendor Library.
  //   *
  //   * @return List of HTL Templates associated to the current Vendor Library.
  //   */
  //  public List<HtlTemplate> getTemplates(UiFramework uiFramework) {
  //    final List<HtlTemplate> templates = new ArrayList<>();
  //
  //    for (final HtlTemplateFile htlTemplateFile : getTemplateFiles()) {
  //      templates.addAll(htlTemplateFile.getTemplates());
  //    }
  //
  //    return templates;
  //  }




  /**
   * All HTL Templates in the current files.
   *
   * @return All HTL Templates in the current files.
   */
  public List<HtlTemplate> getTemplates(HtlTemplateFile htlTemplateFile) {
    final List<HtlTemplate> templates = new ArrayList<>();
    try {
      final Document templateFile = Jsoup.parse(htlTemplateFile.getFileContent());
      templateFile.outputSettings().outline(true);
      templateFile.outputSettings().prettyPrint(false);
      for (final Node node : templateFile.child(0).childNodes().get(1).childNodes()) {
        if (StringUtils.isNotBlank(node.toString())) {
          final HtlTemplate template = new HtlTemplate(node, htlTemplateFile.getPath());
          if (template.getName() != null) {
            templates.add(template);
          }
        }
      }
    } catch (final IOException exception) {
      // todo log.
      //        LOG.warn(
      //            "Unable to get HtlTemplates for HTL Template compilation file {} due to
      //            IOException. {}",
      //            getPath(), exception.getMessage());
    }
    return templates;
  }

  /**
   * Retrieve a specified HTL Template.
   *
   * @param name Template name.
   * @return A specified HTL Template.
   */
  @Nullable
  public HtlTemplate getTemplate(String name, HtlTemplateFile htlTemplateFile) {
    for (HtlTemplate htlTemplate : getTemplates(htlTemplateFile)) {
      if (htlTemplate.getName().equals(name)) {
        return htlTemplate;
      }
    }
    return null;
  }

}
