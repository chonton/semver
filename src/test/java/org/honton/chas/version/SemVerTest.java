/**
 * Apache 2.0 Licensed.  See the LICENSE file distributed with this work for additional information
 * regarding copyright ownership. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */
package org.honton.chas.version;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class SemVerTest {

  @Test
  public void majorGreater() {
    Assert.assertFalse(SemVer.valueOf("2.0.0").isSupported(SemVer.valueOf("1.0.0")));
    Assert.assertFalse(SemVer.valueOf("1.0.0").isSupported(SemVer.valueOf("2.0.0")));
  }

  private static void assertSemVerAttributes(SemVer semVer, int major, int minor, int patch, String preRelease, String metaData) {
    Assert.assertEquals(major, semVer.getMajor());
    Assert.assertEquals(minor, semVer.getMinor());
    Assert.assertEquals(patch, semVer.getPatch());
    Assert.assertEquals(preRelease, semVer.getPreRelease());
    Assert.assertEquals(metaData, semVer.getMetadata());
  }

  @Test
  public void extendingSemVer() {
    assertSemVerAttributes(SemVer.valueOf("2"), 2, -1, -1, null, null);
    assertSemVerAttributes(SemVer.valueOf("2-alpha"), 2, -1, -1, "alpha", null);
    assertSemVerAttributes(SemVer.valueOf("2-alpha+meta"), 2, -1, -1, "alpha", "meta");
    assertSemVerAttributes(SemVer.valueOf("3.2"), 3, 2, -1, null, null);
    assertSemVerAttributes(SemVer.valueOf("4.5.6"), 4, 5, 6, null, null);
    assertSemVerAttributes(SemVer.valueOf("4.5.6.7-rc+m"), 4, 5, 6, "rc", "m");
  }

  @Test
  public void extendingSemVerCompare() {
    Assert.assertTrue(SemVer.valueOf("1.0.1").isSupported(SemVer.valueOf("1.0")));
    Assert.assertFalse(SemVer.valueOf("1.0.0").isSupported(SemVer.valueOf("1.0.0.1")));
  }

  @Test
  public void ignoreMeta() {
    Assert.assertTrue(SemVer.valueOf("2.0.0+other").isSupported(SemVer.valueOf("2.0.0+metadata")));
    Assert.assertTrue(SemVer.valueOf("2.0.0-alpha+other").isSupported(SemVer.valueOf("2.0.0-alpha+metadata")));
  }

  @Test
  public void nullVersion() {
    Assert.assertNull(SemVer.valueOf(null));
  }

  @Test(expected = IllegalArgumentException.class)
  public void improperVersion() {
    SemVer.valueOf("version");
  }

  @Test(expected = NullPointerException.class)
  public void nullIsSupported() {
    SemVer.valueOf("2.0.0-alpha+other").isSupported(null);
  }

  private final String[] ORDERED = {
    "1.0.0-alpha",
    "1.0.0-alpha.1",
    "1.0.0-alpha.beta",
    "1.0.0-beta",
    "1.0.0-beta.2",
    "1.0.0-beta.11",
    "1.0.0-rc.1",
    "1.0.0",
    "1.9.0",
    "1.9.1",
    "1.10.0",
    "1.11.0"
  };

  @Test
  public void testAscending() {
    SemVer prior = SemVer.valueOf("1.0.0-a");
    for (String order : ORDERED) {
      SemVer next = SemVer.valueOf(order);
      Assert.assertTrue(next + " == " + next, next.isSupported(next));
      Assert.assertTrue(next + " > " + prior, next.isSupported(prior));
      Assert.assertFalse(next + " < " + prior, prior.isSupported(next));
      prior = next;
    }
  }

  @Test
  public void failSingle() {
    Assert.assertNull(SemVer.valueOf("1.11.0").findSupported(Collections.singletonList(SemVer.valueOf("2.3.1"))));
  }

  @Test
  public void failDual() {
    Assert.assertNull(SemVer.valueOf("3.3.1").findSupported(Arrays.asList(SemVer.valueOf("1.11.0"), SemVer.valueOf("2.0.0"))));
  }

  @Test
  public void passSingle() {
    SemVer expected = SemVer.valueOf("2.3.1");
    Assert.assertEquals(expected, SemVer.valueOf("2.3.2").findSupported(Collections.singletonList(expected)));
  }

  @Test
  public void passDual() {
    SemVer expected = SemVer.valueOf("2.0.0");
    Assert.assertEquals(expected, SemVer.valueOf("2.1.0").findSupported(Arrays.asList(SemVer.valueOf("1.11.0"), expected)));
  }
}
