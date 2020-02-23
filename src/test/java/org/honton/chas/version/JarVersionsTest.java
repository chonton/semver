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

import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;

public class JarVersionsTest {

  @Test
  public void testExtractMavenJars() {
    Map<String, String> actual = new JarVersions(getClass().getClassLoader()).getDependentJars();
    Assert.assertEquals(
        "3.0.1+com.google.code.findbugs-annotations", actual.get("annotations-3.0.1.jar"));
    Assert.assertEquals("3.0.1+com.google.code.findbugs-jsr305", actual.get("jsr305-3.0.1.jar"));
    Assert.assertEquals("1.7.30+org.slf4j-slf4j-api", actual.get("slf4j-api-1.7.30.jar"));
  }

  @Test
  public void testRemovePrincipal() {
    JarVersions jarVersions = new JarVersions(getClass().getClassLoader());

    SemVer semVer = jarVersions.removePrincipal(Logger.class);
    Assert.assertEquals("1.7.30+org.slf4j-slf4j-api", semVer.getVersion());
    Assert.assertFalse(jarVersions.getDependentJars().containsKey("slf4j-api-1.7.30.jar"));
  }
}
