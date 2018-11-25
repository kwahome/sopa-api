# sopa-api
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/21b905dc40e542bfbd8477dcc9b0a7ca)](https://app.codacy.com/app/kwahome/sopa-api?utm_source=github.com&utm_medium=referral&utm_content=kwahome/sopa-api&utm_campaign=Badge_Grade_Dashboard)
[![Build Status](https://travis-ci.com/kwahome/sopa-api.svg?branch=master)](https://travis-ci.com/kwahome/sopa-api)
[![codecov](https://codecov.io/gh/kwahome/sopa-api/branch/master/graph/badge.svg)](https://codecov.io/gh/kwahome/sopa-api)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> `Sopa` is hello in Maasai to which you reply `Sopa Oleng` 😀

A configurable, extensible structured logging on top of `slf4j` API that generates easily parsable log messages in desired formats.

Like `slf4j`, on top of whose API it's built, `sopa` is not an actual logging implementation. 
Plugging in your desired logging framework is thus still needed (e.g. java.util.logging, logback, log4j) at deployment time.

It started out as a logging util to add structure to logs in a project at my place of work but I now find myself copying it into all other projects. 
I'm tired of doing it. Now it's a library. ☺️

You are probably wondering why I just couldn't use existing libraries rather than falling into the craze and trope of writing one; and you're probably right in your wondersome thoughts. 
I did too but decided against it like would any other self-appointed benevolent dictator for life who's just starting out so here we are.

My short, not so convincing, poorly put together reason has something to do with being lazy; the good lazy. You see, when tackling the structured logging problem, 
I came across a number of good libraries (built on top of `slf4j` like I wanted) that I must admit I liked eventually borrowing a lot from. 

However, attributable to my laziness, I craved a set of extra features to allow me dump what I needed in my logs with minimal lines of code and zero repetition; 
chief amongst them being the ability to `bind` context to my logger which I would have otherwise had to copy over on every log line.

## Change Log
For a changelog(release notes), see: https://github.com/kwahome/sopa-api/releases

## Overview
Log files are to developers what `Mjöllnir` is to `Thor`. 
They're more often than not the last bastion of hope in figuring out what's what when the devil is in the details in your applications; 
which from my very little experience is almost every other time (whether running in production, performing UATs in a staging environment or in your development environment).

The problem with log files is that they are unstructured text data which makes it hard to query against for any sort of information or perform any useful analytics. 
The goal of structured logging is to bring a more defined format and details to your logging for log files to be machine readable.

Standard logging libraries like Simple Logging Facade for Java (SLF4J) already include a lot of useful information: timestamp, pid, thread, level, loggername, etc. 
We just need to extend this list with attributes specific to our applications.

Standard Java log messages look something like this:

```java
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class MyClass {
    private static final Logger logger = LoggerFactory.getLogger(MyClass.name);
    
    public void myMethod() {
        logger.debug("On the eighth day, God started debugging and he still hasn't finished");
    }
}
```

```
2018-01-27 16:17:58 DEBUG 90413 --- [nio-8080-exec-8] my.package.MyClass  : On the eighth day, God started debugging and he still hasn't finished yet
```

While it's human readable, it's rather quite difficult to parse by code in a log aggregation service, as it is unstructured text.

In it's place, a `key=value` formatted log message like one below is structured and much more friendly to a log aggregation service:

```
2018-01-27 16:17:58 DEBUG 90413 --- [nio-8080-exec-8] my.package.MyClass  : God started debugging, day="eight", status="not finished", bugsFound="7 billion"
```

or in JSON format:

```json
{
    "message": "God started debugging!",
    "status": "not finished", 
    "bugsFound": "7 billion"
}
```

or in YAML format:

```yaml
message: "God started debugging!",
status: "not finished", 
bugsFound: "7 billion"
```

## Adding `sopa` to your build

#### Gradle

```groovy
dependencies {
  compile group: 'io.github.kwahome.sopa', name: 'sopa-api', version: '0.5.0'
}
```
#### Maven

```xml
<dependency>
  <groupId>io.github.kwahome.sopa</groupId>
  <artifactId>sopa-api</artifactId>
  <version>0.5.0</version>
</dependency>
```

## Usage
In place of the standard `slf4j` Logger, to use `sopa` you must instantiate it's Logger as illustrated below:

```java
import io.github.kwahome.sopa.LoggerFactory;
import io.github.kwahome.sopa.Logger;

public class MyClass {
    // instantiating a Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);
}
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
`sopa` is a PnP lib requiring no configuration to get started because it ships with defaults. 
It defaults to a `KeyValueRenderer` to format log messages to the standard `key=value` comma separated pairs.

However, it's also built to be Bring Your Own ... compliant thus has the `StructLoggerConfig` class with static methods to override default behaviour.

Below is an enumeration of configurable properties and how to go about making them fit your wants:

> It's advisable to make global configurations in the main thread of your application to avoid any concurrency issues.

##### a) Default Log Renderer
Log renderer is an instance of a class implementing the `LogRenderer` interface that is used in adding structure to your log messages. 
It establishes the format (structure) you desire in your logs.

`KeyValueRenderer` is set as the  default log renderer formatting log messages to `key=value` pairs.

You can configure a renderer of choice via the `setLogRenderer` setter with the only requirement being that the renderer implements the `LogRenderer` interface; e.g.

To configure the `JSONRenderer` (or `YAMLRenderer` which `sopa` ships with) as the preferred log renderer:

```java
import io.github.kwahome.sopa.renderers.JSONRenderer;
import io.github.kwahome.sopa.StructLoggerConfig;

/**
 * Main application class.
 */
@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) {
        StructLoggerConfig.setLogRenderer(JSONRenderer.getInstance()); // applies for any other renderers implementing LogRenderer
        
        SpringApplication.run(MyApplication.class, args);
    }
}
```
##### b) Default Value Renderer
Value renderer is a function that accepts one argument & produces a result and that formats any object passed in as a value to any key-value entry. e.g:
`(value) -> value == null ? "null" : value.toString();` (which is the default value renderer) returns a `toString()`, regardless of object type unless it's null.

To configure a `valueRenderer` of choice:

```java
import io.github.kwahome.sopa.StructLoggerConfig;

/**
 * Main application class.
 */
@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) {
        Function<Object, String> myValueRenderer = (value) -> value;
        StructLoggerConfig.setValueRenderer(myValueRenderer);
        
        SpringApplication.run(MyApplication.class, args);
    }
}
```

##### c) Global Context Supplier
This is the supplier of application specific key-value pairs that are desired on every log entry (e.g. `host`, `environment`) and that should be bound to the logger class once.

The context supplier is defined as an optional loggable object `Optional<LoggableObject>` and initialized as an empty optional.

To configure your `contextSupplier` use `setContextSupplier`:

```java
import java.net.InetAddress;
import java.net.UnknownHostException;

import io.github.kwahome.sopa.StructLoggerConfig;

/**
 * Main application class.
 */
@SpringBootApplication
public class MyApplication {
    
    private getEnvironment() {
        return System.getenv().get("ENVIRONMENT");
    }
    
    private static InetAddress getHost() {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            inetAddress = null;
        }
        return inetAddress;
    }

    public static void main(String[] args) {
        StructLoggerConfig.setContextSupplier("environment", getEnvironment(), "host", getHost());
        
        SpringApplication.run(MyApplication.class, args);
    }
}
```

`setContextSupplier` setter method is overloaded to also accept a `Map<String, Object>` or a POJO implementing the `LoggableObject` interface.
There's a detailed explanation on their usage(s) in the section about logging not too far below, so please continue reading 😊

##### d) Log Entries Separator
For visual readability, a comma (`,`) is appended between key=value entries in a log message as a default behaviour.
`,` is defined as the `logEntriesSeparator` configuration that can be changed if so wished.

The `logEntriesSeparator` shall only apply for the `KeyValueRenderer` as other renders such as the `JSONRenderer` will separate entries to their specification.

To configure the preferred `logEntriesSeparator` use `setLogEntriesSeparator`:

```java
import io.github.kwahome.sopa.StructLoggerConfig;

/**
 * Main application class.
 */
@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) {
        StructLoggerConfig.setLogEntriesSeparator(";");
        
        SpringApplication.run(MyApplication.class, args);
    }
}
```

### Logging key-value pairs

##### a) Object array of ["key", "value'] pairs
The APIs exposed for logging at different levels take in a string message & an array of object params which is the building block of passing in key=value pairs to the logger.

Pass in key-value pairs as object array params following the convention key on the left, value on the right 
(i.e. `["key1", "value1", "key2", "value2"]`) which should yield pairs `key1=value` and `key2=value2`; 
thus keys are on even indices, values on odd indices

All keys must be strings, but the values can be of any type, e.g:

```java
public class MyClass {

    public void myMethod() {
        LOGGER.info("start", 
                    "user", MyClass.getUser(),
                    "requestId", MyClass.getRequestId());
        
        // Oh! The indentation is for readability & cognition 😎
    }
}
```

which would result in a log message:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, user=johndoe@gmail.com, requestId=xyz123dgew
```

Since the key-value pairs passed in as array items with the key and value at alternative indices, 
I must admit it can get out of hand and be a bit confusing because an odd number of items in the array would indicate a dangling key-value pair. 
But hey, there are no named arguments in most strongly typed languages like `java` so we can only make the most out of what's available.

##### b) `Map<String, Object>` objects
From the above rant, I decided (read was persuaded) to add in support for `Map<String, Object>` objects that offer better `key=value` management.

Thus the example above would become:
```java
public class MyClass {

    public void myMethod() {
        Map<String, Object> map = new HashMap<>();
        map.put("user", MyClass.getUser());
        map.put("requestId", MyClass.getRequestId());
        LOGGER.info("start", map);
        
        // you can pass in any number of Map<String, Object> objects 
        // since it's all based on an array of objects and they'll all be logged
    }
}
```

which would result in a log message:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, user=johndoe@gmail.com, requestId=xyz123dgew
```

Granted you have more logging oriented lines of code, but you'll never miss a key=value pair or have to count through the items you're passing in. 😃 

#####  c) Objects implementing the `LoggableObject` interface
Any plain old java object in your code can implement the `LoggableObject` interface which allows you to make any class loggable, e.g:

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
        // returns an object array with the treasured key-value pairs
        // similar to toString() but for sopa key-value pairs
        return new Object[]{"userName", getUserName(), "requestId", getRequestId()};
    }
}
```

Then you can just pass in the object instance directly, without the need to specify any key-value pairs, e.g:

```java
public class MyClass {

    public void myMethod() {
        LOGGER.info("start", new MyClass());
        // you can pass in any number of such POJOs
        // since it's all based on an array of objects and they'll all be logged
    }
}
```

which would result in a log message:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, user=johndoe@gmail.com, requestId=xyz123dgew
```

#####  d) Mixing up the alternatives
Though not advisable (because of the confusion that will ensue many light years later when you are older, probably wiser & looking at your code with disgust), 
it is possible to pass in a mix of `["key", "value"]` pairs, `Map<String, Object>` objects & `LoggableObject` objects in one call to the logging APIs.

What's more is that `Map<String, Object>` or a `LoggableObject` object passed in as values to keys in a pair are not iterated over but rather logged as values of the respective keys.

The wizard of `sopa` handling it all beneath, without being overly presumptuous, is endowed with enough level of wit to discern and tell them apart.

Example:
```java
public class MyClass {

    public void myMethod() {
       LoggableObject loggableObject = new MyClass(); // using MyClass from above
       
       Map<String, Object> map = new HashMap<>();
       map.put("age", 20);
       map.put("gender", "female");
       
       LOGGER.info("start", map, "myMap", map, loggableObject, "myLoggableObject", loggableObject);
    }
}
```

which would result in a log message:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, age=20, gender=female, myMap="{age=20, gender=female}", user=johndoe@gmail.com, requestId=xyz123dgew, loggableObject=my.package.MyClass@17c386de
```

### Logging Exceptions
Unlike in `slf4j`, there is no separate API for use in logging a `Throwable`. 

Instead, just pass in the exception(s) as a parameter (order is not important) and it's root cause message will be extracted and logged under the key `errorMessage`. 
The entire exception stack trace will also be appended to the log message.

Example:
```java
public class MyClass {

    public void myMethod() {
       try {
           System.out.println();
           // some terrorist code (read bugs) here
           // it blows up 💥
           // and now the world is not a safe place anymore 
           // but has it ever been? 😬
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
    }
}
```

which would result in a log event like:

```
2018-01-27 16:17:58 ERROR 90413 --- [nio-8080-exec-8] my.package.MyClass  : oops!, user=johndoe@gmail.com, requestId=xyz123dgew, errorMessage="May the force be with you!",
...followed by the regular scary full stack trace of the exception...
```

In the above example, the exception will still get logged even if the `Throwable` object was passed in in another position with only the order of entries in the log message being a wee bit different.

The same behaviour can be observed on all other logging level APIs but perhaps you'll most likely never log an exception on any other level apart from `error` because why would you?

### Logger Context
To make logging less painful and more powerful, `sopa` allows you to bind, re-binding and unbind key-value pairs to your loggers to ensure they are present in every following logging call without having to repeat them over and over.

Two types of logger contexts exist:

##### 1. Global Context
This is application specific key-value pairs that are desired on every log message (e.g. `host` or `environment`) and would usually be bound to the logger class once.
This is the context set using `setContextSupplier` as described in the configuration section earlier.

Example (using a `Map`; the earlier example used `key-value` params):

```java
import io.github.kwahome.sopa.StructLoggerConfig;

/**
 * Main application class.
 */
@SpringBootApplication
public class MyApplication {
    
    private getEnvironment() {
        return System.getenv().get("ENVIRONMENT");
    }
    
    private static InetAddress getHost() {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            inetAddress = null;
        }
        return inetAddress;
    }

    public static void main(String[] args) {
        Map<String, Object> globalLoggerContext = new HashMap<>();
        globalLoggerContext.put("environment", getEnvironment());
        globalLoggerContext.put("host", getHost());
        StructLoggerConfig.setContextSupplier(globalLoggerContext);
        
        SpringApplication.run(MyApplication.class, args);
    }
}
```

Any call to the logger will bear the set global context.

It's strongly advisable to do this in the mean thread.

##### 2. Instance Bound Context
Similar to global context, `sopa` allows you to bind key-value pairs that appear on every log message generated by a `Logger` instance 
to avoid duplicating cross-cutting concerns on all calls to the logging APIs.

The `Logger` interface exposes **`newBind(Object...params)`**, **`bind(Object...params)`** and **`unbind(Object...params)`** methods that 
accept `["key", "value"]` pairs, `Map<String, Object>` objects or `LoggableObject` objects for the purpose of binding & clearing logger context.

> **`newBind(Object...params)`** allows you to bind new context and overwrite any existing

> **`bind(Object...params)`** allows you to update bound context

> **`unbind(Object...params)`** allows you to remove key-values from bound context

Examples:

a) **`newBind()`**

```java

public class MyClass {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);
    
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
which would result in log messages:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, user=johndoe@gmail.com, requestId=xyz123dgew

2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : end, user=johndoe@gmail.com, requestId=xyz123dgew
```

b) **`bind()`**

```java
public class MyClass {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);
    
    public void myMethod() {
        Map<String, Object> context = new HashMap<>();
        context.put("user", getUser());
        context.put("requestId", getRequestId());
        LOGGER.newBind(context); // you can pass in key-value pairs in an array or a LoggableObject. Or a mix of those options
       // some code that does something extra-ordinary goes here
        LOGGER.info("received");
        // some more code (or bugs)
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
which would result in log messages:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : received, user=johndoe@gmail.com, requestId=xyz123dgew

2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, user=johndoe@gmail.com, requestId=xyz123dgew, age=20, gender=Female

2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : end, user=johndoe@gmail.com, requestId=xyz123dgew, age=20, gender=Female
```

c) **`unbind()`**

```java
public class MyClass {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);
    
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
        // some other code that you don't remember putting in
        // but you decide you don't need some of the bound info in logs from this section
        LOGGER.unbind("age", getAge());
        LOGGER.info("end");
    }
}
```

which would result in log messages:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : received, user=johndoe@gmail.com, requestId=xyz123dgew

2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, user=johndoe@gmail.com, requestId=xyz123dgew, age=20, gender=Female

2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : end, user=johndoe@gmail.com, requestId=xyz123dgew, gender=Female
```

### Helper Utils
`sopa` has a static class `Helpers` in the utils with methods useful in converting a `Map<String, Object>` into an `Object[]` and the converse.
They are used internally in converting passed in objects back and forth into which is the clever trick behind supporting logging params alternatives.

They can be helpful to you as well especially while implementing the `LoggableObject` in your class. e.g:

`Car.java`

```java
import io.kwahome.github.sopa.interfaces.LoggableObject;
import io.kwahome.github.sopa.utils.Helpers;


public class Car implements LoggableObject {
    private String make;
    private String model;
    private String engineCapacity;
    
    Car(String make, String model, int capacity) {
        this.make = make;
        this.model = model;
        this.capacity = capacity;
    }
    
    public String getMake() {
        return make;
    }
    
    public String getModel() {
        return model;
    }
    
    public int getEngineCapacity() {
        return engineCapacity;
    }
    
    @Override
    public Object[] loggableObject() {
        /*
        * rather than:
        * 
        * return Object[]{"make", getMake(), 
        *                 "model", getModel(), 
        *                 "engineCapacity", getEngineCapacity()}
        *                 
        * use a map and convert it to Object[]            
        * */
        Map<String, Object> carLoggableContext = new HashMap<>();
        carLoggableContext.put("make", getMake());
        carLoggableContext.put("model", getModel());
        carLoggableContext.put("engineCapacity", getEngineCapacity());
        
        return Helpers.mapToObjectArray(carLoggableContext);
    }
}
```

`MyClass.java`
```java
import java.util.Date;


public class MyClass {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);
    
    private Car myCar = new Car("mercedes", "s-65", "2000cc");
    
    public void myMethod() {
        LOGGER.bind(myCar); // bind with myCar loggable object
        
        LOGGER.info("start", "time", new Date());
        // ...
        // some code to keep the engine running
        // ...
        LOGGER.info("stop", "time", new Date());
    }
}
```

which would result in log messages:

```
2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : start, make=mercedes, model=s-65, capacity=200cc, time="Sun Nov 25 09:43:17 EAT 2018"

2018-01-27 16:17:58 INFO 90413 --- [nio-8080-exec-8] my.package.MyClass  : stop, make=mercedes, model=s-65, capacity=200cc, time="Sun Nov 25 11:43:17 EAT 2018"
``` 

## Contributing
Please read [CONTRIBUTING.md](https://github.com/kwahome/sopa-api/blob/master/CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](https://github.com/kwahome/sopa-api/blob/master/CODE_OF_CONDUCT.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning
We use [SemVer](https://semver.org/) for versioning. For the versions available, see the tags on this repository.

## License
This software is licensed under the MIT License. See the [LICENSE](https://github.com/kwahome/sopa-api/blob/master/LICENSE) file in the top distribution directory for the full license text.
