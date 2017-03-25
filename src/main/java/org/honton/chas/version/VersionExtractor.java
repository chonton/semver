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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

/**
 * Extract version information from Maven built jars
 */
@Slf4j
class VersionExtractor {

  private VersionExtractor() {
    // utility class
  }

  /**
   * Extract the version and metadata from the contents of a jar.
   * The version and metadata is found in the maven generated pom.properties file.
   * The metadata formated as <i>groupId</i>-<i>artifactId</i>.
   *
   * @param file The file which must be a jar
   * @return null, if version information cannot be extracted from the jar
   */
  static @Nullable
  String extractVersion(File file) {
    try (JarFile jarFile = new JarFile(file)) {
      return readVersion(jarFile);
    } catch (IOException e) {
      log.warn("Unable to read " + file.getName(), e);
      return null;
    }
  }

  /**
   * Read the version from the jar.  Look for both server-version and pom.properties
   * @return A SemVer string
   */
  private static @Nullable
  String readVersion(JarFile jarFile) throws IOException {
    GroupArtifactVersion pomMeta = readPomMeta(jarFile);
    return pomMeta != null ? pomMeta.withMeta() : null;
  }

  /**
   * Read the GAV built into all maven created jars
   * @return Null or the GAV
   */
  private static @Nullable
  GroupArtifactVersion readPomMeta(JarFile jarFile) throws IOException {
    for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
      JarEntry jarEntry = entries.nextElement();
      String name = jarEntry.getName();
      if (name.startsWith("META-INF/") && name.endsWith("/pom.properties")) {
        return readJarEntry(jarFile, jarEntry, GroupArtifactVersion::new);
      }
    }
    return null;
  }

  private static <T> T readJarEntry(JarFile jarFile, JarEntry jarEntry, Function<Properties, T> extractor) throws IOException {
    try (InputStream is = jarFile.getInputStream(jarEntry)) {
      Properties properties = new Properties();
      properties.load(is);
      return extractor.apply(properties);
    }
  }

  /**
   * convenience immutable class to hold group, artifact, and version
   */
  private static class GroupArtifactVersion {
    private final String groupId;
    private final String artifactId;
    private final String version;

    GroupArtifactVersion(Properties properties) {
      this.groupId = properties.getProperty("groupId");
      this.artifactId = properties.getProperty("artifactId");
      this.version = properties.getProperty("version");
    }

    String withMeta() {
      return version + '+' + groupId + '-' + artifactId;
    }
  }
}
