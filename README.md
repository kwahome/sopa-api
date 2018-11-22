# sopa-api
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/21b905dc40e542bfbd8477dcc9b0a7ca)](https://app.codacy.com/app/kwahome/sopa-api?utm_source=github.com&utm_medium=referral&utm_content=kwahome/sopa-api&utm_campaign=Badge_Grade_Dashboard)
[![Build Status](https://travis-ci.com/kwahome/sopa-api.svg?branch=master)](https://travis-ci.com/kwahome/sopa-api)
[![codecov](https://codecov.io/gh/kwahome/sopa-api/branch/master/graph/badge.svg)](https://codecov.io/gh/kwahome/sopa-api)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> `Sopa` is hello in Maasai to which you reply `Sopa Oleng` :-)

A configurable, extensible structured logging on top of `slf4j` API to generate easily parsable log messages in desired formats.

Like `slf4j` on top of whose API it's built, `sopa` is not an actual logging implementation. You still need to plug in your desired logging framework (e.g. java.util.logging, logback, log4j) at deployment time.

## Adding to `sopa` to your build

### Gradle

```groovy
dependencies {
  compile group: 'io.github.kwahome.sopa', name: 'sopa-api', version: '0.4.0'
  
  // optional json
  compile group: 'io.github.kwahome.sopa', name: 'sopa-json', version: '0.1.0'
  
  // optional yaml
  compile group: 'io.github.kwahome.sopa', name: 'sopa-yaml', version: '0.1.0'
}
```
### Maven

```xml
<dependency>
  <groupId>io.github.kwahome.sopa</groupId>
  <artifactId>sopa-api</artifactId>
  <version>0.4.0</version>
</dependency>
```

## Overview

## Usage
IN place of the standard SLF4J Logger, to use structlog4j you must instantiate the Logger:

```java
import io.github.kwahome.sopa.interfaces.Logger;
import io.github.kwahome.sopa.LoggerFactory;

private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);
```

The `Logger` interface offers basic methods for the different logging levels and binding context to a logger:

```java
public interface Logger {
    void error(String message, Object...params);
    void warn(String message, Object...params);
    void info(String message, Object...params);
    void debug(String message, Object...params);
    void trace(String message, Object...params);
    
    void newBind(Object...params);
    void bind(Object...params);
    void unbind(Object...params);
}

```

## Contributing
Please read [CONTRIBUTING.md](https://github.com/kwahome/sopa-api/blob/master/CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](https://github.com/kwahome/sopa-api/blob/master/CODE_OF_CONDUCT.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning
We use [SemVer](https://semver.org/) for versioning. For the versions available, see the tags on this repository.

## License
This software is licensed under the MIT License. See the [LICENSE](https://github.com/kwahome/sopa-api/blob/master/LICENSE) file in the top distribution directory for the full license text.
