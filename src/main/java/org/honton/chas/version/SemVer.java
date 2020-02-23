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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A semantic version mostly following the rules at <a href="https://semver.org">semver.org</a>. The
 * major.minor.patch numbering scheme is loosened. If minor or patch is not supplied, the minor and
 * patch attributes will be -1.
 */
@Getter
@EqualsAndHashCode(of = "version")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SemVer {

  private static final Pattern SEM_VER_EXACT =
      Pattern.compile(
          '^'
              + "(((0|[1-9][0-9]*)(\\.(0|[1-9][0-9]*))*)"
              + "(-([0-9A-Za-z-\\.]*))?)"
              + "(\\+([0-9A-Za-z-\\.]*))?"
              + '$');
  private static final int ORDINAL_GROUP = 2;
  private static final int PRE_RELEASE_GROUP = 7;
  private static final int META_GROUP = 9;

  private static final Pattern PRE_RELEASE = Pattern.compile("(([1-9]\\d*)|([0-9A-Za-z-]+))\\.?");
  private static final int LEXICAL_GROUP = 1;
  private static final int NUMERIC_GROUP = 2;

  /** The version as given in the constructor */
  private final String version;
  /** The ordinals of the version */
  private final int[] ordinals;
  /**
   * The pre-release qualifier. Any suffix starting with a minus. i.e. SNAPSHOT of 1.2.3-SNAPSHOT
   */
  private final String preRelease;
  /**
   * Any version metadata. The suffix starting with a plus. i.e. exp.sha.5114f85 of
   * 1.2.3+exp.sha.5114f85
   */
  private final String metadata;

  /**
   * Create an instance from the string specification
   *
   * @param version The string
   * @return A SemVer; or null, if version is null
   * @throws IllegalArgumentException when version is not a semantic version
   */
  public static SemVer valueOf(@Nullable String version) {
    if (version == null) {
      return null;
    }
    Matcher matcher = SEM_VER_EXACT.matcher(version);
    if (!matcher.find()) {
      throw new IllegalArgumentException(version + " is not a proper semantic version");
    }
    return new SemVer(
        matcher.group(0),
        getOrdinals(matcher),
        matcher.group(PRE_RELEASE_GROUP),
        matcher.group(META_GROUP));
  }

  private static int[] getOrdinals(Matcher matcher) {
    String group = matcher.group(ORDINAL_GROUP);
    String[] segments = group.split("\\.");
    int[] ordinals = new int[segments.length];
    for (int i = 0; i < segments.length; ++i) {
      ordinals[i] = Integer.parseInt(segments[i]);
    }
    return ordinals;
  }

  /** The version as given in the constructor */
  public String getVersion() {
    return version;
  }

  /** The major version. i.e. X of X.Y.Z */
  public int getMajor() {
    return ordinals[0];
  }

  /** The minor version. i.e. Y of X.Y.Z */
  public int getMinor() {
    return ordinals.length > 1 ? ordinals[1] : -1;
  }

  /** The patch version. i.e. Z of X.Y.Z */
  public int getPatch() {
    return ordinals.length > 2 ? ordinals[2] : -1;
  }

  @Override
  public String toString() {
    return version;
  }

  /**
   * Does this version support the expected version?
   *
   * @param expected The desired version
   * @return true, if this version supports the expected version
   */
  public boolean isSupported(@NonNull SemVer expected) {

    if (ordinals[0] != expected.ordinals[0]) {
      return false;
    }

    for (int i = 1; i < ordinals.length; ++i) {
      if (i == expected.ordinals.length) {
        return true;
      }
      int diff = ordinals[i] - expected.ordinals[i];
      if (diff != 0) {
        return diff > 0;
      }
    }
    if (ordinals.length < expected.ordinals.length) {
      return false;
    }

    if (preRelease == null || expected.preRelease == null) {
      return preRelease == null;
    }
    return preReleaseCompare(expected);
  }

  // pre-release comparisons...
  private boolean preReleaseCompare(SemVer expected) {

    Matcher a = PRE_RELEASE.matcher(preRelease);
    Matcher e = PRE_RELEASE.matcher(expected.preRelease);
    while (a.find()) {
      if (!e.find()) {
        return true;
      }

      int numberCmp = numberCompare(a.group(NUMERIC_GROUP), e.group(NUMERIC_GROUP));
      if (numberCmp != 0) {
        return numberCmp > 0;
      }

      int lexicalCmp = a.group(LEXICAL_GROUP).compareTo(e.group(LEXICAL_GROUP));
      if (lexicalCmp != 0) {
        return lexicalCmp > 0;
      }
    }
    return !e.find();
  }

  /*
   * Compare a segment numerically
   * return 0, if leftField and rightField are equal; >0, if leftField>rightField; <0, if leftField<rightField
   */
  private static int numberCompare(String leftField, String rightField) {
    if (leftField != null) {
      if (rightField == null) {
        // leftField is number and rightField is not.  Numbers are ordered earlier than alpha fields
        return -1;
      }
      // both are numbers, compare numerically
      return Integer.parseInt(leftField) - Integer.parseInt(rightField);
    } else if (rightField != null) {
      // rightField is number and leftField is not.
      // Numbers are ordered earlier than alpha fields
      return 1;
    }
    // both are not numbers
    return 0;
  }

  /**
   * Find the first expected version by this version.
   *
   * @param expectedVersions The desired versions
   * @return The expectedVersion this version supports; or, null if no expectedVersion is supported.
   */
  public SemVer findSupported(@Nonnull Iterable<SemVer> expectedVersions) {
    for (SemVer semVer : expectedVersions) {
      if (isSupported(semVer)) {
        return semVer;
      }
    }
    return null;
  }
}
