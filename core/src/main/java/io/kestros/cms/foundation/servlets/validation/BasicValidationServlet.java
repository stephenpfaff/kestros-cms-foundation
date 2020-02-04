package io.kestros.cms.foundation.servlets.validation;

import io.kestros.commons.structuredslingmodels.BaseSlingModel;
import javax.servlet.Servlet;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = {Servlet.class},
           property = {"sling.servlet.resourceTypes=sling/servlet/default",
               "sling.servlet.selectors=basic-validation", "sling.servlet.extensions=json",
               "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class BasicValidationServlet extends BaseValidationServlet {

  private static final long serialVersionUID = -6880653266429090069L;

  @Reference
  private ModelFactory modelFactory;

  @Override
  public void doValidation(final BaseSlingModel model) {
    model.validate();
  }

  @Override
  public ModelFactory getModelFactory() {
    return modelFactory;
  }
}
