package models;

import static org.junit.Assert.*;

import io.kestros.cms.versioning.api.models.Version;
import org.junit.Before;
import org.junit.Test;

public class VersionTest {

  private Version version;

  @Before
  public void setUp() throws Exception {
    version = new Version(1, 2, 3);
  }

  @Test
  public void getFormatted() {
    assertEquals("1.2.3", version.getFormatted());
  }

  @Test
  public void getMajorVersion() {
    assertEquals(1, version.getMajorVersion().intValue());
  }

  @Test
  public void getMinorVersion() {
    assertEquals(2, version.getMinorVersion().intValue());
  }

  @Test
  public void getPatchVersion() {
    assertEquals(3, version.getPatchVersion().intValue());
  }
}