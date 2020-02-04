package io.kestros.cms.foundation.servlets;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;

import io.kestros.commons.uilibraries.filetypes.ScriptType;
import javax.servlet.Servlet;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.service.component.annotations.Component;

@Component(service = {Servlet.class},
           property = {"sling.servlet.resourceTypes=kes:Site", "sling.servlet.extensions=css",
               "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class SiteLevelCssServlet extends SiteLevelScriptServlet {

  private static final long serialVersionUID = 6383115665070503260L;

  @Override
  public ScriptType getScriptType() {
    return CSS;
  }
}
