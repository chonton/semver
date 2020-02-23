# Semver
This project contains:
1. SemVer - a class to parse and compare [Semantic Version](semver.org) specification strings.
2. JarVersions - finds all jars used by a ClassLoader.
3. VersionExtractor - extracts metadata from maven built jars.
4. VersionMatcher - is a version supported by one of the supplied versions.

### Requirements
* Minimum of Java 8

## Maven Coordinates
To include semver in your maven build, use the following fragment in your pom.

** **Not yet released to Maven Central** **

```xml
<build>
  <dependencies>
    <dependency>
      <groupId>org.honton.chas.version</groupId>
      <artifactId>semver</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>
    </dependencies>
</build>
```
