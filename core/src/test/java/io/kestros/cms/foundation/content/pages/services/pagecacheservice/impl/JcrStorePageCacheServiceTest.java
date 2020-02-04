package io.kestros.cms.foundation.content.pages.services.pagecacheservice.impl;

import io.kestros.cms.foundation.services.pagecacheservice.impl.JcrFilePageCacheService;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class JcrStorePageCacheServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private JcrFilePageCacheService cacheService;

  @Before
  public void setup() {
    cacheService = new JcrFilePageCacheService();
  }

  @Test
  public void activate() {
  }

  @Test
  public void deactivate() {
  }

  @Test
  public void cachePage() {
  }

  @Test
  public void testCachePage() {
  }

  @Test
  public void flushAllCachedPages() {
  }
}