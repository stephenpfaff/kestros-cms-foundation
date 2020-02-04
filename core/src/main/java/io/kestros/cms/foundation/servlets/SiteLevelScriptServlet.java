package io.kestros.cms.foundation.servlets;

import static io.kestros.cms.foundation.utils.DesignUtils.getUiFrameworkByFrameworkCode;

import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SiteLevelScriptServlet extends SlingSafeMethodsServlet {

  private static final Logger LOG = LoggerFactory.getLogger(SiteLevelScriptServlet.class);
  private static final long serialVersionUID = -5328841199087488311L;

  @Override
  public void doGet(@Nonnull final SlingHttpServletRequest request,
      @Nonnull final SlingHttpServletResponse response) {
    final String[] selectors = request.getRequestPathInfo().getSelectors();
    if (selectors.length == 2) {
      try {
        final UiFramework uiFramework = getUiFrameworkByFrameworkCode(selectors[0], true, false,
            request.getResourceResolver());

        response.setContentType(getScriptType().getOutputContentType());
        response.setStatus(200);
        response.getWriter().write(
            uiFramework.getTheme(selectors[1]).getOutput(getScriptType(), false));
      } catch (final ResourceNotFoundException | IOException | ChildResourceNotFoundException | InvalidResourceTypeException exception) {
        LOG.error("Unable to render site level {} response for {}. {}", getScriptType().getName(),
            request.getResource().getPath(), exception.getMessage());
        response.setStatus(400);
      }
    } else {
      LOG.debug(
          "Unable to render site level css response for request {}. Does not contain exactly two "
          + "selectors", request.getContextPath());
      response.setStatus(400);
    }
  }

  public abstract ScriptType getScriptType();

}
