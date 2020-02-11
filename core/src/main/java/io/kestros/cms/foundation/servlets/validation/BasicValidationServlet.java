package io.kestros.cms.foundation.servlets.validation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.commons.structuredslingmodels.BaseSlingModel;
import javax.servlet.Servlet;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Servlet that provides basic validation messages for a given resource.
 *
 * Attempts to match the requested to resource to the closest matching Sling Model that extends
 * {@link BaseSlingModel} in order to determine
 * {@link io.kestros.commons.structuredslingmodels.validation.ModelValidator}s
 * that will be processed.
 */
@Component(service = {Servlet.class},
           property = {"sling.servlet.resourceTypes=sling/servlet/default",
               "sling.servlet.selectors=basic-validation", "sling.servlet.extensions=json",
               "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class BasicValidationServlet extends BaseValidationServlet {

  private static final long serialVersionUID = -6880653266429090069L;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference
  private transient ModelFactory modelFactory;

  @Override
  public void doValidation(final BaseSlingModel model) {
    model.validate();
  }

  @Override
  public ModelFactory getModelFactory() {
    return modelFactory;
  }
}
