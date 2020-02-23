/**
 * Apache 2.0 Licensed. See the LICENSE file distributed with this work for additional information
 * regarding copyright ownership. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.
 */
package org.honton.chas.version;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

/** Match semantic versions. */
public class VersionMatcher {

  private final List<SemVer> expectedVersions;

  /**
   * Create a matcher with expectations. At least one of the supportVersion or
   * additionalSupportedVersions must be matched
   *
   * @param supportedVersion The version that is supported.
   * @param additionalSupportedVersions The additional versions that are supported.
   */
  public VersionMatcher(@NonNull String supportedVersion, String... additionalSupportedVersions) {
    expectedVersions = new ArrayList<>(1 + additionalSupportedVersions.length);
    expectedVersions.add(SemVer.valueOf(supportedVersion));
    for (String additionalSupportedVersion : additionalSupportedVersions) {
      expectedVersions.add(SemVer.valueOf(additionalSupportedVersion));
    }
  }

  /**
   * Find a matching supported version.
   *
   * @param actual The version to match.
   * @return The matching supported version, or null.
   */
  public SemVer findMatchingVersion(SemVer actual) {
    return actual.findSupported(expectedVersions);
  }

  /**
   * An error message when the version supplied is not one of the acceptable versions. This method
   * an error message when no supportedVersions are matched, or null if a supportedVersion is
   * matched.
   *
   * @param actual The actual version
   * @return null, if the actual version matches one of the expected versions; otherwise an error
   *     message.
   */
  public String getErrorMessage(String actual) {
    return getErrorMessage(SemVer.valueOf(actual));
  }

  /**
   * An error message when the version supplied is not one of the acceptable versions.
   *
   * @param actual The actual version
   * @return null, if the actual version matches one of the expected versions; otherwise an error
   *     message.
   */
  public String getErrorMessage(SemVer actual) {
    if (findMatchingVersion(actual) != null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (SemVer expected : expectedVersions) {
      if (sb.length() == 0) {
        sb.append(actual).append(" does not support ");
        if (expectedVersions.size() > 1) {
          sb.append("any of ");
        }
      } else {
        sb.append(", ");
      }
      sb.append(expected.getVersion());
    }
    return sb.toString();
  }
}
