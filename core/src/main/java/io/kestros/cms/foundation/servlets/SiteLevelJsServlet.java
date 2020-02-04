package io.kestros.cms.foundation.servlets;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;

import io.kestros.commons.uilibraries.filetypes.ScriptType;
import javax.servlet.Servlet;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.service.component.annotations.Component;

@Component(service = {Servlet.class},
           property = {"sling.servlet.resourceTypes=kes:Site", "sling.servlet.extensions=js",
               "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class SiteLevelJsServlet extends SiteLevelScriptServlet {

  private static final long serialVersionUID = -372985760220947749L;

  @Override
  public ScriptType getScriptType() {
    return JAVASCRIPT;
  }
}
