package io.kestros.cms.temp.utils;

public class HtlTemplateUsageUtils {
//
  //  /**
  //   * All template usages within a specified ComponentUiFrameworkView.
  //   *
  //   * @param componentUiFrameworkView view to find templates usages from.
  //   * @return All template usages within a specified ComponentUiFrameworkView.
  //   * @throws InvalidScriptException Failed to find a valid content.html script.
  //   * @throws IOException Failed to read view's content.html script.
  //   */
  //  public static List<HtlTemplateUsage> getHtlTemplateUsageList(
  //      @Nonnull ComponentUiFrameworkView componentUiFrameworkView)
  //      throws InvalidScriptException, IOException {
  //    List<HtlTemplateUsage> templateNameList = new ArrayList<>();
  //    for (HtmlFile htlScriptFile : FileModelUtils.getChildrenOfFileType(componentUiFrameworkView,
  //        HtmlFile.class)) {
  //
  //      if (htlScriptFile != null) {
  //        final Document contentScriptDocument = Jsoup.parse(htlScriptFile.getFileContent());
  //        contentScriptDocument.outputSettings().outline(true);
  //        contentScriptDocument.outputSettings().prettyPrint(false);
  //
  //        for (Element element : contentScriptDocument.body().getElementsByAttributeStarting(
  //            "data-sly-call")) {
  //          HtlTemplateUsage templateUsage = new HtlTemplateUsage(element,
  //          componentUiFrameworkView);
  //          if (templateUsage.getName() != null) {
  //            templateNameList.add(templateUsage);
  //          }
  //        }
  //      }
  //    }
  //
  //    return templateNameList;
  //  }

}
