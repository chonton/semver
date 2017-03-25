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
```xml
  <build>
    <dependencies>
      <dependency>
        <groupId>org.honton.chas.semver</groupId>
        <artifactId>vault-url</artifactId>
        <version>0.0.1</version>
      </dependency>
      </dependencies>
  </build>
```
