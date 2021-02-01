package models;

import static org.junit.Assert.assertEquals;

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

  @Test
  public void testCompareTo() {
    assertEquals(1, version.compareTo(new Version(0, 0, 1)));
    assertEquals(1, version.compareTo(new Version(0, 1, 1)));
    assertEquals(1, version.compareTo(new Version(1, 1, 1)));
    assertEquals(1, version.compareTo(new Version(1, 2, 1)));
    assertEquals(1, version.compareTo(new Version(1, 2, 2)));
    assertEquals(0, version.compareTo(new Version(1, 2, 3)));
    assertEquals(-1, version.compareTo(new Version(1, 2, 4)));
    assertEquals(-1, version.compareTo(new Version(1, 3, 0)));
    assertEquals(-1, version.compareTo(new Version(2, 0, 0)));
  }
}