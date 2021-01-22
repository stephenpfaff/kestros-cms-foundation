package io.kestros.cms.versioning.core.services;

import static org.junit.Assert.*;

import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.cms.versioning.api.models.VersionableResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class VersionServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private VersionServiceImpl versionService;

  private VersionableResource versionable;

  private Resource resource;

  private Map<String, Object> versionableProperties = new HashMap<>();
  private Map<String, Object> versionProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    versionService = new VersionServiceImpl();

    context.registerInjectActivateService(versionService);

    versionableProperties.put("sling:resourceType", "versionable");
    versionProperties.put("sling:resourceType", "version");

    resource = context.create().resource("/content/versionable", versionableProperties);
    context.create().resource("/content/versionable/versions/0.0.1", versionProperties);
    context.create().resource("/content/versionable/versions/0.1.0", versionProperties);
    context.create().resource("/content/versionable/versions/1.0.0", versionProperties);
    context.create().resource("/content/versionable/versions/1.1.1", versionProperties);
    context.create().resource("/content/versionable/versions/2.0.0", versionProperties);
  }

  @Test
  public void getCurrentVersion() throws VersionFormatException {
    versionable = resource.adaptTo(SampleVersionable.class);
    assertNotNull(versionService.getCurrentVersion(versionable));
    assertEquals("2.0.0", versionService.getCurrentVersion(versionable).getName());
  }

  @Test
  public void getVersionResource() throws VersionRetrievalException {
    versionable = resource.adaptTo(SampleVersionable.class);
    assertNotNull(versionService.getVersionResource(versionable, "1.0.0"));
    assertEquals("1.0.0", versionService.getVersionResource(versionable, "1.0.0").getName());
  }

  @Test
  public void getVersionHistory() {
    versionable = resource.adaptTo(SampleVersionable.class);
    assertNotNull(versionService.getVersionHistory(versionable));
    assertEquals(5, versionService.getVersionHistory(versionable).size());
  }

  @Test
  public void getVersionsFolderResource() throws ChildResourceNotFoundException {
    versionable = resource.adaptTo(SampleVersionable.class);
    assertNotNull(versionService.getVersionsFolderResource(versionable));
    assertEquals("/content/versionable/versions",
        versionService.getVersionsFolderResource(versionable).getPath());
  }
}