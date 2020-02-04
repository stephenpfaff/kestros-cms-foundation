package io.kestros.cms.foundation.services.themeprovider;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;

public interface ThemeProviderService {

  Theme getThemeForPage(BaseContentPage page)
      throws ResourceNotFoundException, InvalidThemeException;

  Theme getThemeForComponent(BaseComponent component)
      throws ResourceNotFoundException, InvalidThemeException;
}
