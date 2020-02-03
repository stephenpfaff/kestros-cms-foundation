package io.kestros.cms.foundation.design.htltemplate;

import io.kestros.cms.foundation.componenttypes.HtmlFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTML File that contains HTL Templates.
 */
@Model(adaptables = Resource.class,
       resourceType = "nt:file")
public class HtlTemplateFile extends HtmlFile {

  private static final Logger LOG = LoggerFactory.getLogger(HtlTemplateFile.class);
  public static final String EXTENSION_HTML = ".html";

  /**
   * Title of the current HTL Template file.  Derived from the file name by replacing `-` with ` `,
   * removing `.html` and capitalizing the first letter of each word.
   *
   * @return Title of the current HTL Template file.
   */
  @Nonnull
  @Override
  public String getTitle() {
    String title = getName();

    title = title.replaceAll("-", " ");
    title = title.replaceAll(EXTENSION_HTML, "");

    return WordUtils.capitalize(title);
  }

  /**
   * All HTL Templates in the current files.
   *
   * @return All HTL Templates in the current files.
   */
  public List<HtlTemplate> getTemplates() {
    List<HtlTemplate> templates = new ArrayList<>();
    try {
      Document templateFile = Jsoup.parse(getOutput());
      templateFile.outputSettings().outline(true);
      templateFile.outputSettings().prettyPrint(false);
      for (Node node : templateFile.child(0).childNodes().get(1).childNodes()) {
        if (StringUtils.isNotBlank(node.toString())) {
          HtlTemplate template = new HtlTemplate(node, getPath());
          if (template.getName() != null) {
            templates.add(template);
          }
        }
      }
    } catch (IOException exception) {
      LOG.warn(
          "Unable to get HtlTemplates for HTL Template compilation file {} due to IOException. {}",
          getPath(), exception.getMessage());
    }
    return templates;
  }

}
