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

public class VersionMatcherTest {

  @Test
  public void findMatch() {
    VersionMatcher versionMatcher = new VersionMatcher("1.11.0");
    Assert.assertEquals(SemVer.valueOf("1.11.0"), versionMatcher.findMatchingVersion(SemVer.valueOf("1.11.1")));
  }

  @Test
  public void findSecondMatch() {
    VersionMatcher versionMatcher = new VersionMatcher("1.11.0", "2.0.0");
    Assert.assertEquals(SemVer.valueOf("2.0.0"), versionMatcher.findMatchingVersion(SemVer.valueOf("2.3.1")));
  }

  private void assertErrorMessage(String error, String actual, String supportedVersion, String... additional) {
    VersionMatcher versionMatcher = new VersionMatcher(supportedVersion, additional);
    Assert.assertEquals(error, versionMatcher.getErrorMessage(SemVer.valueOf(actual)));
  }

  @Test
  public void nullOnMatch() {
    assertErrorMessage(null, "1.11.1", "1.11.0");
  }

  @Test
  public void nullOnSecondMatch() {
    assertErrorMessage(null, "2.3.1", "1.11.0", "2.0.0");
  }

  @Test
  public void messageOnSingleFailure() {
    assertErrorMessage("2.3.1 does not support 1.11.0", "2.3.1", "1.11.0");
  }

  @Test
  public void messageOnDualFailure() {
    assertErrorMessage("3.3.1 does not support any of 1.11.0, 2.0.0", "3.3.1", "1.11.0", "2.0.0");
  }

  @Test(expected = NullPointerException.class)
  public void throwOnNull() {
    assertErrorMessage("", "1.2.3", null, "1.2.3");
  }

  @Test(expected = NullPointerException.class)
  public void throwOnNullArray() {
    assertErrorMessage("", "1.2.3", "1.2.3", (String[]) null);
  }
}
