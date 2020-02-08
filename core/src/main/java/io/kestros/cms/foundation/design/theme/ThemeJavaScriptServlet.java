package io.kestros.cms.foundation.design.theme;

import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.config.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import io.kestros.commons.uilibraries.servlets.BaseJavaScriptServlet;
import javax.servlet.Servlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Servlet to output a Theme's CSS.
 */
@Component(service = Servlet.class,
           property = {"sling.servlet.resourceTypes=kes:Theme",
               "sling.servlet.resourceTypes=kestros/cms/theme", "sling.servlet.extensions=js",
               "sling.servlet.methods=GET",})
public class ThemeJavaScriptServlet extends BaseJavaScriptServlet {

  private static final long serialVersionUID = 7574658580922282342L;

  @SuppressWarnings("unused")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryConfigurationService uiLibraryConfigurationService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCacheService uiLibraryCacheService;

  @Override
  public UiLibraryConfigurationService getUiLibraryConfigurationService() {
    return uiLibraryConfigurationService;
  }

  @Override
  protected UiLibraryCacheService getUiLibraryCacheService() {
    return uiLibraryCacheService;
  }

  @Override
  public Class<? extends UiLibrary> getUiLibraryClass() {
    return Theme.class;
  }
}
