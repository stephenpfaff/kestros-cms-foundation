package io.kestros.cms.foundation.services.themeprovider;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;

/**
 * Provides Themes for {@link BaseContentPage} and {@link BaseComponent} instances.
 */
public interface ThemeProviderService {

  /**
   * Retrieves the {@link Theme} for a page.
   *
   * @param page Page to retrieve the Theme for.
   * @return The {@link Theme} for a page.
   * @throws ResourceNotFoundException Expected Theme Resource was not found.
   * @throws InvalidThemeException Theme Resource was found, but could not be adatped to Theme.
   */
  Theme getThemeForPage(BaseContentPage page)
      throws ResourceNotFoundException, InvalidThemeException;

  /**
   * Retrieves the {@link Theme} for a Component.
   *
   * @param component Component to retrieve the Theme for.
   * @return The {@link Theme} for a component.
   * @throws ResourceNotFoundException Expected Theme Resource was not found.
   * @throws InvalidThemeException Theme Resource was found, but could not be adatped to Theme.
   */
  Theme getThemeForComponent(BaseComponent component)
      throws ResourceNotFoundException, InvalidThemeException;
}
