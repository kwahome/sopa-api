# sopa-api
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/21b905dc40e542bfbd8477dcc9b0a7ca)](https://app.codacy.com/app/kwahome/sopa-api?utm_source=github.com&utm_medium=referral&utm_content=kwahome/sopa-api&utm_campaign=Badge_Grade_Dashboard)
[![Build Status](https://travis-ci.com/kwahome/sopa-api.svg?branch=master)](https://travis-ci.com/kwahome/sopa-api)
[![codecov](https://codecov.io/gh/kwahome/sopa-api/branch/master/graph/badge.svg)](https://codecov.io/gh/kwahome/sopa-api)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> `Sopa` is hello in Maasai to which you reply `Sopa Oleng` 😀

A configurable, extensible structured logging on top of `slf4j` API to generate easily parsable log messages in desired formats.

Like `slf4j` on top of whose API it's built, `sopa` is not an actual logging implementation. You still need to plug in your desired logging framework (e.g. java.util.logging, logback, log4j) at deployment time.

It started out as a logging util component to add structure to logs in a project at my place of work but I now find myself copy it into all other Java projects. I'm tired of doing it. Now it's a library. ☺️

You might be wondering why I just couldn't use existing libraries rather than falling into the craze and trope of writing one. And you are probably right in your wondersome thoughts. I did too but decided against it like would any other self-appointed benevolent dictator for life who's just starting out so here we are.

My short, not so convincing, poorly put together reason has something to do with being lazy; the good lazy. You see, when tackling the structured logging problem, I came across a number of good libraries (built on top of `slf4j` like I wanted) that I must admit I liked eventually borrowing a lot from. 

However, attributable to my laziness, I craved a set of extra features to allow me dump what I needed in my logs with minimal lines of code and minimal to zero repetition; chief amongst them being the ability to `bind` context to my logger; context which I would have had to repeat over on every log line.

## Overview
Log files are to developers what Mjöllnir is to Thor. 
They are more often than not the last bastion of hope in figuring out what's what when the devil is in the details in your applications; which from my very little experience is almost every other time (whether running in production, performing UATs in a staging environment or in your development environment).

The problem with log files is that they are unstructured text data which makes it hard to query against for any sort of information or perform any useful analytics. The goal of structured logging is to bring a more defined format and details to your logging for log files to be machine readable.

Standard logging libraries like Simple Logging Facade for Java (SLF4J) already include a lot of useful information: timestamp, pid, thread, level, loggername, etc. We just need to extend this list with attributes specific to our applications.

Standard Java log messages look something like this:

```java
logger.debug("On the eighth day, God started debugging and he still hasn't finished yet");
```

```
2018-01-27 16:17:58 DEBUG 90413 --- [nio-8080-exec-8] my.package.MyClass  : On the eighth day, God started debugging and he still hasn't finished yet
```

While it's human readable, it's rather quite difficult to parse in a log aggregation service, as it's unstructured text.

In it's place, a log message like one below is structured and much more friendly to our log aggregation service:

```
2018-01-27 16:17:58 DEBUG 90413 --- [nio-8080-exec-8] my.package.MyClass  : God started debugging, day="eight", status="not finished", bugsFound="7 billion"
```

or as JSON:
```json
{
    "message": "God started debugging!",
    "status": "not finished", 
    "bugsFound": "7 billion"
}
```

or as YAML:
```yaml
message: "God started debugging!",
status: "not finished", 
bugsFound: "7 billion"
```

## Adding `sopa` to your build

#### Gradle

```groovy
dependencies {
  compile group: 'io.github.kwahome.sopa', name: 'sopa-api', version: '0.1.0'
}
```
#### Maven

```xml
<dependency>
  <groupId>io.github.kwahome.sopa</groupId>
  <artifactId>sopa-api</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Usage
In place of the standard `slf4j` Logger, to use `sopa` you must instantiate it's Logger as illustrated below:

```java
private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);
```

The `Logger` interface offers the same `slf4j` logging APIs and an additional ones for use in binding context to a logger:

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
### Configuration
`sopa` is a PnP library requiring no configuration to get started on because it ships with defaults. It defaults to a `KeyValueRenderer` to format log messages to the standard `key=value` comma separated pairs.

The library is also built to be Bring Your Own ... compliant thus has `StructLoggerConfig` class with static methods to override default behaviour such as the renderer in use.

Below is an enumeration of configurable properties and how to go about it:

> It's advisable to make global configurations in the main thread of your application to avoid any concurrency issues.

##### a) Default Log Renderer
Log renderer is an instance of a class implementing `LogRenderer` that will be used in adding structure to your log messages. 
It proscribes the format (structure) you desire in your logs.

`KeyValueRenderer` is set as the  default `logRenderer` formatting log messages to `key=value` pairs.

You can configure the renderer the logger will use via the `setLogRenderer` setter with the only requirement being that the renderer passed in implements the `LogRenderer` interface; e.g.

To configure the `JSONRenderer` (or `YAMLRenderer` which `sopa` ships with) as the preferred log renderer:

```java
import io.github.kwahome.sopa.renderers.JSONRenderer;
import io.github.kwahome.sopa.StructLoggerConfig;

/**
 * Main application class.
 */
@SpringBootApplication
public class MyApplication {
    
    StructLoggerConfig.setLogRenderer(JSONRenderer.getInstance()); // applies for any other renderers implementing LogRenderer

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```
##### b) Default Value Renderer
Value Renderer is a function that accepts one argument & produces a result that can perform custom log rendering of any object that is passed in as a value to any key-value entry. e.g:
`(value) -> value == null ? "null" : value.toString();` which is the `defaultValueRenderer` that returns a `toString()`, regardless of object type unless it's null.

To configure a `valueRenderer` of choice:

```java
import io.github.kwahome.sopa.StructLoggerConfig;

/**
 * Main application class.
 */
@SpringBootApplication
public class MyApplication {
    
    Function<Object, String> myValueRenderer = (value) -> value;
    
    StructLoggerConfig.setValueRenderer(myValueRenderer);

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

##### c) Global Context Supplier
This is the supplier of application specific key-value pairs that are desired on every log entry (e.g. host, environment) and that should be bound to the logger class once.

The context supplier is defined as an optional loggable object `Optional<LoggableObject>` and initialized as an empty optional.

To configure your `contextSupplier` use `setContextSupplier`:

```java
import io.github.kwahome.sopa.StructLoggerConfig;

/**
 * Main application class.
 */
@SpringBootApplication
public class MyApplication {
    
    StructLoggerConfig.setContextSupplier("environment", getEnvironment(), "host", getHost());

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

`setContextSupplier` setter method is overloaded to also accept a `Map<String, Object>` and a POJO implementing the `LoggableObject` interface.
There's a detailed explanation on their usage in the section about logging below, so please continue reading 😊

##### d) Log Entries Separator
For visual readability, `,` is appended by default between key=value entries in a log message.
`,` is defined as the `logEntriesSeparator` configuration which can be changed if so desired.

The `logEntriesSeparator` will only apply for the `KeyValueRenderer` as other renders such as the `JSONRenderer` will separate entries according to their specification.

To configure the preferred `logEntriesSeparator` use `setLogEntriesSeparator`:

```java
import io.github.kwahome.sopa.StructLoggerConfig;

/**
 * Main application class.
 */
@SpringBootApplication
public class MyApplication {
    
    StructLoggerConfig.setLogEntriesSeparator(";");

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

### Logging key-value pairs

##### a) Object array of ["key", "value'] pairs
The APIs exposed for logging at different levels take in a string message and an array of object params which is the building block of passing in key=value pairs to the logger.

Pass in key-value pairs as parameters following the convention key on the left, value on the right (i.e. `["key1", "value1", "key2", "value2"]`) which should yield pairs `key1=value` and `key2=value2`.

All keys must be strings, but the values can be anything, e.g:

```java
LOGGER.info("start", 
            "user", MyClass.getUser(),
            "requestId", MyClass.getRequestId());

// Oh! The indentation is for readability & cognition 😎
```
which would result in a log event:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, user=johndoe@gmail.com, requestId=xyz123dgew
```

Since the key-value pairs are based on passing in an array with the key and value at alternative indices, I must admit it can get out of hand and be a bit confusing because an odd number of items in the array would indicate a dangling key-value pair. 
But hey, there are no named arguments in most strongly typed languages like `java` so we can only make the most out of what's available.

##### b) `Map<String, Object>` objects
From the above rant, I decided (read was persuaded) to add in support for `Map<String, Object>` objects that offer better `key=value` management.

Thus the example above would become:
```java
Map<String, Object> map = new HashMap<>();
map.put("user", MyClass.getUser());
map.put("requestId", MyClass.getRequestId())l
LOGGER.info("start", map);

// you can pass in any number of Map<String, Object> objects 
// since it's all based on an array of objects and they'll all be logged
```
which would result in a log event:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, user=johndoe@gmail.com, requestId=xyz123dgew
```

Granted you have more lines of code just for logging, but you'll never miss a key=value pair or have to count through the items you're passing in. 😃 

#####  c) Objects implementing the `LoggableObject` interface
If you so desire, any POJO in your code can implement the `LoggableObject` interface which allows you to make any class loggable, e.g:

```java
public class MyClass implements LoggableObject {

    private String userName;
    private String requestId;
    
    public String getUserName() {
        return userName;
    }
    
    public String getRequestId() {
        return requestId;
    }

    @Override
    public Object[] loggableObject() {
        return new Object[]{"userName", getUserName(), "requestId", getRequestId()};
    }
}
```

Then you can just pass in the object instance directly, without the need to specify any key-value pairs, e.g:

```java
LOGGER.info("start", new MyClass());
// you can pass in any number of such POJOs
// since it's all based on an array of objects and they'll all be logged
```

which would result in a log event:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, user=johndoe@gmail.com, requestId=xyz123dgew
```

#####  d) Mixing up the alternatives
Though not advisable (because of the confusion that will ensue many light years later when you are older, probably wiser and looking at your code with disgust), it is possible to pass in a mix of `["key", "value"]` pairs, `Map<String, Object>` objects and `LoggableObject` objects in one call to the logging APIs.

What's more is that `Map<String, Object>` or a `LoggableObject` object passed in as values to keys in a pair are not iterated over but rather logged as values of the respective keys.

The wizard of `sopa` handling it all beneath, without being overly presumptuous, is endowed with enough level of wit to discern and tell them apart.

Example:
```java
LoggableObject loggableObject = new MyClass(); // using MyClass from above

Map<String, Object> map = new HashMap<>();
map.put("age", 20);
map.put("gender", "female");

LOGGER.info("start", map, "myMap", map, loggableObject, "myLoggableObject", loggableObject);
```

which would result in a log event:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, age=20, gender=female, myMap="{age=20, gender=female}", user=johndoe@gmail.com, requestId=xyz123dgew, loggableObject=my.package.MyClass@17c386de
```

### Logging Exceptions
Unlike in `slf4j`, there's no separate API for use in logging a `Throwable`. 

Instead, just pass in the exception(s) as a parameter (order is not important) and it's root cause message will be extracted and logged under the key `errorMessage`. 
The entire exception stack trace will also be appended to the log message.

Example:
```java
try {
    // some terrorirs code (read bugs) here
    // it blows up, and now the world is not a safe place anymore (but has it ever been?) 😬
    // you get the drift
} catch (Exception e) {
    // but gladly it's all caught in the act
    // so we decide to log it for the helpless mortal in a dark basement 
    // with a cup of coffee who's just about to buy a bar going like "WTF is going on?"
    
    LOGGER.error("oops!",
        "user", MyClass.getUser(),
        "requestId", MyClass.getRequestId(),
        e);
    
    // Oh again! The indentation is for readability & cognition 😎
}
```

which would result in a log event like:

```
2018-01-27 16:17:58 ERROR 90413 --- [nio-8080-exec-8] my.package.MyClass  : oops!, user=johndoe@gmail.com, requestId=xyz123dgew, errorMessage="May the force be with you!",
...followed by the regular scary full stack trace of the exception...
```

In the above example, the exception will still get logged even if the `Throwable` object was passed in as the first param to the `error` API with only the order of entries in the log message being a wee bit different.

The same behaviour can be observed on all other logging level APIs but perhaps you'll most likely never log an exception on any other level apart from `error` because why would you?

### Logger Context
To make logging less painful and more powerful, `sopa` allows you to bind, re-binding and unbind key-value pairs to your loggers to ensure they are present in every following logging call without having to repeat them over and over.

Two types of logger contexts exist:

##### 1. Global Context
This is application specific key-value pairs that are desired on every log entry (e.g. `host` or `environment`) and would usually be bound to the logger class once.
This is the context set using `setContextSupplier` as described in the configuration section earlier.

Example (using a `Map`; the earlier example used `key-value` params):

```java
import io.github.kwahome.sopa.StructLoggerConfig;

/**
 * Main application class.
 */
@SpringBootApplication
public class MyApplication {
    Map<String, Object> globalLoggerContext = new HashMap<>();
    globalLoggerContext.put("environment", getEnvironment())
    globalLoggerContext.put("host", getHost())
    
    StructLoggerConfig.setContextSupplier(globalLoggerContext);

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

Any call to the logger will bear the set global context.

It's strongly advisable to do this in the mean thread for the same reasons highlighted in the renderer section above.

##### 2. Instance Bound Context
Similar to the global context, `sopa` allows you to bind key-value pairs that appear on every log event generated by a `Logger` instance to avoid duplicating cross-cutting concerns on all calls to the logging APIs.

The `Logger` interface exposes `newBind(Object...params)`, `bind(Object...params)` and `unbind(Object...params)` methods that accept in `["key", "value"]` pairs, `Map<String, Object>` objects or `LoggableObject` objects for the purpose of binding and clearing logger context.

> `newBind(Object...params)` allows you to bind new context and overwrite any existing

> `bind(Object...params)` allows you to update bound context

> `unbind(Object...params)` allows you to removing key-values from bound context

Examples:

`newBind()`
```java
private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);

public class MyClass {
    MyClass() {
        
    }
    
    public void myMethod() {
        Map<String, Object> context = new HashMap<>();
        context.put("user", MyClass.getUser());
        context.put("requestId", MyClass.getRequestId());
        LOGGER.newBind(context); // you can pass in key-value pairs in an array or a LoggableObject. Or a mix of those options
        
        // some code that does something extra-ordinary goes here
        // some more code (or bugs)
        LOGGER.info("start");
        
        // some other code
        // and more where that came from
        LOGGER.info("end");
    }
}
```
which would result in a log event:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, user=johndoe@gmail.com, requestId=xyz123dgew

2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : end, user=johndoe@gmail.com, requestId=xyz123dgew
```

`bind()`

```java
private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);

public class MyClass {
    MyClass() {
        
    }
    
    public void myMethod() {
        Map<String, Object> context = new HashMap<>();
        context.put("user", getUser());
        context.put("requestId", getRequestId());
        LOGGER.newBind(context); // you can pass in key-value pairs in an array or a LoggableObject. Or a mix of those options
        // some code that does something extra-ordinary goes here
        LOGGER.info("received");
        
        // several quantum leaps of code later
        // you hit some new info that is of significance
        LOGGER.bind("age", getAge(), "gender", getGender());
        
        // some more code (or bugs)
        LOGGER.info("start");
        
        // some other code
        // and more where that came from
        LOGGER.info("end");
    }
}
```
which would result in a log event:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : received, user=johndoe@gmail.com, requestId=xyz123dgew

2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, user=johndoe@gmail.com, requestId=xyz123dgew, age=20, gender=Female

2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : end, user=johndoe@gmail.com, requestId=xyz123dgew, age=20, gender=Female
```

`unbind()`

```java
private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);

public class MyClass {
    MyClass() {
        
    }
    
    public void myMethod() {
        Map<String, Object> context = new HashMap<>();
        context.put("user", getUser());
        context.put("requestId", getRequestId());
        LOGGER.newBind(context); // you can pass in key-value pairs in an array or a LoggableObject. Or a mix of those options
        // some code that does something extra-ordinary goes here
        LOGGER.info("received");
        //...
        // several quantum leaps of code later
        // you hit some new info that is of significance
        LOGGER.bind("age", getAge(), "gender", getGender());
        //...
        // some more code (or bugs)
        LOGGER.info("start");
       // ...
        // some other code
        // and more where that came from
        // ...
        // decide you don't need some of the bound info in logs from this section
        LOGGER.unbind("age", getAge());
        LOGGER.info("end");
    }
}
```
which would result in a log event:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : received, user=johndoe@gmail.com, requestId=xyz123dgew

2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, user=johndoe@gmail.com, requestId=xyz123dgew, age=20, gender=Female

2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : end, user=johndoe@gmail.com, requestId=xyz123dgew, gender=Female
```

## Contributing
Please read [CONTRIBUTING.md](https://github.com/kwahome/sopa-api/blob/master/CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](https://github.com/kwahome/sopa-api/blob/master/CODE_OF_CONDUCT.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning
We use [SemVer](https://semver.org/) for versioning. For the versions available, see the tags on this repository.

## License
This software is licensed under the MIT License. See the [LICENSE](https://github.com/kwahome/sopa-api/blob/master/LICENSE) file in the top distribution directory for the full license text.
