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

import java.io.File;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;

public class VersionExtractorTest {

  @Test
  public void extractVersionBadFile() throws URISyntaxException {
    Assert.assertNull(VersionExtractor.extractVersion(getThisFile()));
  }

  public File getThisFile() throws URISyntaxException {
    String className = getClass().getCanonicalName().replace('.', '/') + ".class";
    return new File(getClass().getClassLoader().getResource(className).toURI().getPath());
  }
}
