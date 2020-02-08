package io.kestros.cms.foundation.design.theme;

import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.config.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import io.kestros.commons.uilibraries.servlets.BaseCssServlet;
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
               "sling.servlet.resourceTypes=kestros/cms/theme", "sling.servlet.extensions=css",
               "sling.servlet.methods=GET",})
public class ThemeCssServlet extends BaseCssServlet {

  private static final long serialVersionUID = 9115879784266249170L;

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