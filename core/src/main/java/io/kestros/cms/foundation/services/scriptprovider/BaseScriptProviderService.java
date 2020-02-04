package io.kestros.cms.foundation.services.scriptprovider;

import io.kestros.cms.foundation.content.components.parentcomponent.ParentComponent;
import io.kestros.cms.foundation.exceptions.InvalidScriptException;
import io.kestros.commons.osgiserviceutils.services.BaseServiceResolverService;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(immediate = true,
           service = ScriptProviderService.class,
           property = "service.ranking:Integer=1")
public class BaseScriptProviderService extends BaseServiceResolverService
    implements ScriptProviderService {

  // TODO make this unique to this service.
  public static final String KESTROS_HTL_TEMPLATE_CACHE_PURGE_SERVICE_USER
      = "kestros-htl-template-cache-purge";

  private static final Logger LOG = LoggerFactory.getLogger(BaseScriptProviderService.class);

  @Reference
  ResourceResolverFactory resourceResolverFactory;

  @Override
  protected String getServiceUserName() {
    return KESTROS_HTL_TEMPLATE_CACHE_PURGE_SERVICE_USER;
  }

  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

  /**
   * Retrieves the path to a specified script name, resolved proper to the ComponentUiFramework
   * view.
   *
   * @param parentComponent Component to retrieve the script for.
   * @param scriptName Script to retrieve.
   * @return The path to a specified script.
   * @throws InvalidScriptException The script was not found, or could not be adapt to *
   *     HtmlFile.
   */
  public String getScriptPath(final ParentComponent parentComponent, final String scriptName)
      throws InvalidScriptException {
    LOG.trace("Getting Script Path {}", scriptName);
    try {
      return parentComponent.getComponentType().getScript(scriptName,
          parentComponent.getTheme().getUiFramework()).getPath();
    } catch (final Exception exception) {
      try {
        return parentComponent.getComponentType().getScript(scriptName, null).getPath();
      } catch (final ModelAdaptionException exception1) {
        throw new InvalidScriptException(scriptName, exception.getMessage());
      }
    }
  }
}
