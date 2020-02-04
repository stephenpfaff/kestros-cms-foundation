package io.kestros.cms.foundation.services.themeprovider;

import static io.kestros.cms.foundation.design.DesignConstants.PN_THEME_PATH;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true,
           service = ThemeProviderService.class,
           property = "service.ranking:Integer=1")
public class BaseThemeProviderService implements ThemeProviderService {

  private static final Logger LOG = LoggerFactory.getLogger(BaseThemeProviderService.class);

  @Override
  public Theme getThemeForPage(final BaseContentPage page)
      throws ResourceNotFoundException, InvalidThemeException {
    String themePath = StringUtils.EMPTY;
    try {
      if (page != null) {
        themePath = page.getProperties().get(PN_THEME_PATH, StringUtils.EMPTY);
        return getResourceAsType(themePath, page.getResourceResolver(), Theme.class);
      }
    } catch (final ResourceNotFoundException exception) {
      try {
        return getThemeForPage(page.getParent());
      } catch (final NoParentResourceException exception1) {
        LOG.error(
            "Unable to inherit Theme for resource {}. No ancestor with a valid Theme could be "
            + "found.", page.getPath());
      }
    } catch (final InvalidResourceTypeException e) {
      throw new InvalidThemeException(themePath,
          "Could not adapt to Theme. Resource must have jcr:primaryType 'kes:Theme'.");
    }
    throw new ResourceNotFoundException(themePath, "Theme reference resource missing or invalid.");
  }

  @Override
  @Nonnull
  public Theme getThemeForComponent(final BaseComponent component)
      throws InvalidThemeException, ResourceNotFoundException {
    if (component != null) {
      try {
        return getThemeForPage(component.getContainingPage());
      } catch (final NoValidAncestorException exception) {
        return getThemeFromFirstAncestor(component);
      }
    }
    throw new IllegalStateException();
  }

  @Nonnull
  private Theme getThemeFromFirstAncestor(final BaseResource resource)
      throws InvalidThemeException, ResourceNotFoundException {
    String themePath = StringUtils.EMPTY;
    BaseResource parentResource = resource;
    while (StringUtils.EMPTY.equals(themePath)) {
      themePath = parentResource.getProperty("kes:theme", StringUtils.EMPTY);
      try {
        parentResource = parentResource.getParent();
      } catch (final NoParentResourceException exception) {
        throw new InvalidThemeException(themePath,
            "No ancestor resource with configured Theme found.");
      }
    }
    try {
      return getResourceAsType(themePath, resource.getResourceResolver(), Theme.class);
    } catch (final InvalidResourceTypeException exception) {
      throw new InvalidThemeException(themePath, exception.getMessage());
    }
  }

}
