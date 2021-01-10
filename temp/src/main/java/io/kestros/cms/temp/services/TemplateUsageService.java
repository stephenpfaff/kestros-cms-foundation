package io.kestros.cms.temp.services;

import io.kestros.cms.componenttypes.api.exceptions.InvalidScriptException;
import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.filetypes.HtmlFile;
import io.kestros.commons.structuredslingmodels.utils.FileModelUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TemplateUsageService {

//
//  /**
//   * The name of all templates a specified ComponentUiFrameworkView attempts to call.
//   *
//   * @param componentUiFrameworkView View to retrieve called template names from.
//   * @return The name of all templates a specified ComponentUiFrameworkView attempts to call.
//   * @throws InvalidScriptException Failed to find a valid content.html script.
//   * @throws IOException Failed to read view's content.html script.
//   */
//  @Nonnull
//  public static List<String> getTemplateNamesAComponentViewAttemptsToCall(
//      @Nonnull ComponentUiFrameworkView componentUiFrameworkView)
//      throws InvalidScriptException, IOException {
//    List<String> templateNameList = new ArrayList<>();
//    for (HtmlFile htlScriptFile : FileModelUtils.getChildrenOfFileType(componentUiFrameworkView,
//        HtmlFile.class)) {
//
//      final Document contentScriptDocument = Jsoup.parse(htlScriptFile.getFileContent());
//      contentScriptDocument.outputSettings().outline(true);
//      contentScriptDocument.outputSettings().prettyPrint(false);
//
//      for (Element element : contentScriptDocument.body().getElementsByAttributeStarting(
//          "data-sly-call")) {
//        if (element.hasAttr("data-sly-call")) {
//          String templateName = element.attr("data-sly-call").split("@")[0];
//          templateName = templateName.split("templates.")[1];
//          templateName = templateName.replaceAll(" ", "");
//          templateNameList.add(templateName);
//        }
//      }
//    }
//
//    return templateNameList;
//  }

}
