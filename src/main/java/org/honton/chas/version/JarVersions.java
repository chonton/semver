/**
 * Apache 2.0 Licensed.  See the LICENSE file distributed with this work for additional information
 * regarding copyright ownership. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */
package org.honton.chas.version;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Information about the jars in this process.
 * The constructor's ClassLoader parameter is the ClassLoader that will be queried for MANIFEST.MF files.
 */
@Slf4j
@RequiredArgsConstructor
public class JarVersions {

  @Getter
  private final Map<String, String> dependentJars;

  public JarVersions(ClassLoader classLoader) {
    dependentJars = extractAllJars(classLoader);
  }

  /**
   * Remove the principal jar from the list of dependentJars.
   *
   * @param mainClass The principal class which is contained in the principal jar.
   * @return The semantic version of the principal class.  This may be null if the jar cannot be found.
   */
  public SemVer removePrincipal(Class mainClass) {
    String location = getLocation(mainClass);
    String jarVer = dependentJars.remove(location);
    return SemVer.valueOf(jarVer);
  }

  private static Map<String, String> extractAllJars(ClassLoader classLoader) {
    Map<String, String> versions = new TreeMap<>();
    try {
      for (Enumeration<URL> jarLocations = classLoader.getResources("META-INF/MANIFEST.MF");
        jarLocations.hasMoreElements(); ) {
        URI jarLocation = toURI(jarLocations.nextElement());
        extractJarVersion(versions, jarLocation);
      }
    } catch (IOException e) {
      log.debug("No jar information extractable from classLoader", e);
    }
    return versions;
  }

  private static void extractJarVersion(Map<String, String> versions, URI jarLocation) {
    if ("jar".equals(jarLocation.getScheme())) {
      jarLocation = extractJarLocation(jarLocation);
    }
    File file = createFile(jarLocation);
    String version = VersionExtractor.extractVersion(file);
    if (version != null) {
      versions.put(file.getName(), version);
    }
  }

  // N.B SneakyThrows hides the URISyntaxException that cannot happen
  // since jar schema URI must be well formed
  @SneakyThrows
  private static URI extractJarLocation(URI jarLocation) {
    String part = jarLocation.getRawSchemeSpecificPart();
    return new URI(part.substring(0, part.indexOf('!')));
  }

  @SuppressWarnings("findsecbugs:PATH_TRAVERSAL_IN")
  private static File createFile(URI uri) {
    return new File(uri.getPath());
  }

  private static String getLocation(Class cls) {
    URL location = cls.getProtectionDomain().getCodeSource().getLocation();
    return createFile(toURI(location)).getName();
  }

  // N.B SneakyThrows hides the URISyntaxException that cannot happen
  // since URL from ClassLoader or protectionDomain.codeSource.location must be well formed
  @SneakyThrows
  private static URI toURI(URL url) {
    return url.toURI();
  }
}
