/*
 * MIT License
 *
 * Copyright (c) 2018 Kelvin Wahome
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.kwahome.sopa;

import java.util.HashMap;
import java.util.Map;

import io.github.kwahome.sopa.interfaces.LoggableObject;
import io.github.kwahome.sopa.renderers.KeyValueRenderer;
import io.github.kwahome.sopa.utils.Helpers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;

import static org.hamcrest.CoreMatchers.is;
import static uk.org.lidalia.slf4jtest.LoggingEvent.*;

/**
 * Testing sopa's API.
 *
 * @author kelvin.wahome
 */
public class StructLoggerTests {
    private StructLogger logger;
    private TestLogger slf4jLogger;

    @Before
    public void setUp() {
        StructLoggerConfig.clearContextSupplier();
        StructLoggerConfig.setLogRenderer(KeyValueRenderer.getInstance());
        StructLoggerConfig.setLogEntriesSeparator(",");

        logger = (StructLogger) LoggerFactory.getLogger(StructLoggerTests.class);
        slf4jLogger = (TestLogger) logger.getSlf4jLogger();
    }

    @After
    public void tearDown() {
        slf4jLogger.clear();
    }

    @Test
    public void loggingAtErrorTest() {
        String message = "Houston! We have a problem!";
        logger.error(message);
        LoggingEvent expectedLoggingEvent = error(message);
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.error(message, params);
        expectedLoggingEvent = error(String.format("%s, %s=%s", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.error(message, loggableObject);
        params = loggableObject.loggableObject();
        expectedLoggingEvent = error(String.format("%s, %s=%s", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert hashmap will work as well
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", "value2");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("key3", "value1");
        map2.put("key4", "value2");
        LoggableObject object = new GenericLoggableObject(new Object[]{"map", map1});
        // test that a map associated with a key is not interpreted as a value to the key
        // rather than a map object with key, values to be logged
        logger.error(message, map1, object, map2, "myMap", map2);
        params = Helpers.mergeObjectArrays(
                Helpers.mapToObjectArray(map1), object.loggableObject(), Helpers.mapToObjectArray(map2));
        expectedLoggingEvent = error(String.format("%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], params[3], params[4], String.format( "\"%s\"", params[5]),
                params[6], params[7], params[8], params[9], "myMap", String.format( "\"%s\"", map2)));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggingAtWarnTest() {
        String message = "Consider this a warning!";
        logger.warn(message);
        LoggingEvent expectedLoggingEvent = warn(message);
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.warn(message, params);
        expectedLoggingEvent = warn(String.format("%s, %s=%s", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.warn(message, loggableObject);
        params = loggableObject.loggableObject();
        expectedLoggingEvent = warn(String.format("%s, %s=%s", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert hashmap will work as well
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", "value2");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("key3", "value1");
        map2.put("key4", "value2");
        LoggableObject object = new GenericLoggableObject(new Object[]{"map", map1});
        // test that a map associated with a key is not interpreted as a value to the key
        // rather than a map object with key, values to be logged
        logger.warn(message, map1, object, map2, "myMap", map2);
        params = Helpers.mergeObjectArrays(
                Helpers.mapToObjectArray(map1), object.loggableObject(), Helpers.mapToObjectArray(map2));
        expectedLoggingEvent = warn(String.format("%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], params[3], params[4], String.format( "\"%s\"", params[5]),
                params[6], params[7], params[8], params[9], "myMap", String.format( "\"%s\"", map2)));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggingAtInfoTest() {
        String message = "Hello World!";
        logger.info(message);
        LoggingEvent expectedLoggingEvent = info(message);
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.info(message, params);
        expectedLoggingEvent = info(String.format("%s, %s=%s", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.info(message, loggableObject);
        params = loggableObject.loggableObject();
        expectedLoggingEvent = info(String.format("%s, %s=%s", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert hashmap will work as well
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", "value2");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("key3", "value1");
        map2.put("key4", "value2");
        LoggableObject object = new GenericLoggableObject(new Object[]{"map", map1});
        // test that a map associated with a key is not interpreted as a value to the key
        // rather than a map object with key, values to be logged
        logger.info(message, map1, object, map2, "myMap", map2);
        params = Helpers.mergeObjectArrays(
                Helpers.mapToObjectArray(map1), object.loggableObject(), Helpers.mapToObjectArray(map2));
        expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], params[3], params[4], String.format( "\"%s\"", params[5]),
                params[6], params[7], params[8], params[9], "myMap", String.format( "\"%s\"", map2)));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggingAtDebugTest() {
        String message = "On the eight day, God started debugging!";
        logger.debug(message);
        LoggingEvent expectedLoggingEvent = debug(message);
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.debug(message, params);
        expectedLoggingEvent = debug(String.format("%s, %s=%s", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.debug(message, loggableObject);
        params = loggableObject.loggableObject();
        expectedLoggingEvent = debug(String.format("%s, %s=%s", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert hashmap will work as well
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", "value2");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("key3", "value1");
        map2.put("key4", "value2");
        LoggableObject object = new GenericLoggableObject(new Object[]{"map", map1});
        // test that a map associated with a key is not interpreted as a value to the key
        // rather than a map object with key, values to be logged
        logger.debug(message, map1, object, map2, "myMap", map2);
        params = Helpers.mergeObjectArrays(
                Helpers.mapToObjectArray(map1), object.loggableObject(), Helpers.mapToObjectArray(map2));
        expectedLoggingEvent = debug(String.format("%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], params[3], params[4], String.format( "\"%s\"", params[5]),
                params[6], params[7], params[8], params[9], "myMap", String.format( "\"%s\"", map2)));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggingAtTraceTest() {
        String message = "Tracer round!";
        logger.trace(message);
        LoggingEvent expectedLoggingEvent = trace(message);
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.trace(message, params);
        expectedLoggingEvent = trace(String.format("%s, %s=%s", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.trace(message, loggableObject);
        params = loggableObject.loggableObject();
        expectedLoggingEvent = trace(String.format("%s, %s=%s", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert hashmap will work as well
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", "value2");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("key3", "value1");
        map2.put("key4", "value2");
        LoggableObject object = new GenericLoggableObject(new Object[]{"map", map1});
        // test that a map associated with a key is not interpreted as a value to the key
        // rather than a map object with key, values to be logged
        logger.trace(message, map1, object, map2, "myMap", map2);
        params = Helpers.mergeObjectArrays(
                Helpers.mapToObjectArray(map1), object.loggableObject(), Helpers.mapToObjectArray(map2));
        expectedLoggingEvent = trace(String.format("%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], params[3], params[4], String.format( "\"%s\"", params[5]),
                params[6], params[7], params[8], params[9], "myMap", String.format( "\"%s\"", map2)));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggingMultiKeyValueTest() {
        String message = "Hello World!";
        Object[] params = new Object[]{"key1", "value1", "key2", "value number two", "key3", null};
        logger.info(message, params);
        LoggingEvent expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], String.format("\"%s\"", params[3]),  params[4], params[5]));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggingLoggableObjectTest() {
        String message = "Hello World!";
        Object[] params = new Object[]{"key1", "value1", "key2", "value number two", "key3", null};
        LoggableObject loggableObject = new GenericLoggableObject(params);
        logger.info(message, loggableObject);
        params = loggableObject.loggableObject();
        LoggingEvent expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], String.format("\"%s\"", params[3]),  params[4], params[5]));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggingMultipleLoggableObjectTest() {
        String message = "Hello World!";
        LoggableObject loggableObject1 = new GenericLoggableObject(new Object[]{"key1", "value1"});
        LoggableObject loggableObject2 = new GenericLoggableObject(new Object[]{"key2", "value number two"});
        LoggableObject loggableObject3 = new GenericLoggableObject(new Object[]{"key3", null});

        logger.info(message, loggableObject1, loggableObject2, loggableObject3);
        // merge all objects into one
        Object[] params = Helpers.mergeObjectArrays(
                loggableObject1.loggableObject(), loggableObject2.loggableObject(), loggableObject3.loggableObject());
        LoggingEvent expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], String.format("\"%s\"", params[3]),  params[4], params[5]));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggingHashMapKeyValuesTest() {
        String message = "Hello World!";
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        logger.info(message, map);
        Object[] params = Helpers.mapToObjectArray(map);
        LoggingEvent expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], params[3]));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

    }

    @Test
    public void loggingMultipleHashMapKeyValuesTest() {
        String message = "Hello World!";
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", "value2");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("key1", true);
        map2.put("key2", map1);
        logger.info(message, map1, map2);
        Object[] params = Helpers.mergeObjectArrays(Helpers.mapToObjectArray(map1), Helpers.mapToObjectArray(map2));
        LoggingEvent expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s, %s=%s, %s=%s", message, params[0],
                params[1], params[2], params[3], params[4], params[5], params[6], String.format( "\"%s\"", params[7])));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggingMixedAllowedLoggableObjectsTest() {
        // test that a map associated with a key is not interpreted as a value to the key
        // rather than a map object with key, values to be logged
        String message = "Hello World!";
        // assert hashmap will work as well
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", "value2");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("key3", "value1");
        map2.put("key4", "value2");
        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"map", map1});
        logger.info(message, map1, loggableObject, map2, "myMap", map2, "test", 1.234);
        Object[] params = Helpers.mergeObjectArrays(
                Helpers.mapToObjectArray(map1), loggableObject.loggableObject(), Helpers.mapToObjectArray(map2));
        LoggingEvent expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], params[3], params[4], String.format( "\"%s\"", params[5]),
                params[6], params[7], params[8], params[9], "myMap", String.format( "\"%s\"", map2), "test", 1.234));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggableObjectPassedInAsValue() {
        String message = "Hello World!";
        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key1", "value1"});
        logger.info(message, "loggableObject", loggableObject, "loggableObject", loggableObject);

        LoggingEvent expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s", message,
                "loggableObject", loggableObject, "loggableObject", loggableObject));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void logNewContextBindingTest() {
        String message = "Hello World!";
        Object[] context = new Object[]{"key1", "value1", "key2", "value2"};
        logger.newBind(context);
        logger.info(message);
        LoggingEvent expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s",
                message, context[0], context[1], context[2], context[3]));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert all log events will have bound context
        Object[] params = new Object[]{"key3", null, "key4", true};
        logger.info(message, params);
        // concatenation order matters for assert equal
        // the log params are added before the context
        params = Helpers.mergeObjectArrays(params, context);
        expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert newBind() overwrites existing context
        params = new Object[]{"key3", null, "key4", true};
        logger.newBind(params);
        logger.info(message);
        expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], params[3]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert loggable object instances work as well
        Map<String, Object> map = new HashMap<>();
        map.put("test", new Object());
        LoggableObject loggableObject = new GenericLoggableObject(
                new Object[]{"loggable", true, "integer", 1, "object", map});
        logger.newBind(loggableObject);
        logger.info(message);
        params = loggableObject.loggableObject();
        expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], params[3], params[4], params[5]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert hashmap will work as well
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("key1", "value1");
        contextMap.put("key2", "value2");
        LoggableObject contextObject = new GenericLoggableObject(new Object[]{"map", contextMap});
        logger.newBind(contextMap, contextObject, contextMap);
        logger.info(message);
        params = Helpers.mergeObjectArrays(Helpers.mapToObjectArray(contextMap), contextObject.loggableObject());
        expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], params[3], params[4],String.format( "\"%s\"", params[5])));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void logContextBindUpdatesTest() {
        String message = "Hello World!";
        Object[] context = new Object[]{"key1", "value1", "key2", "value2"};
        logger.bind(context);
        logger.info(message);
        LoggingEvent expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s",
                message, context[0], context[1], context[2], context[3]));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert bind updates existing context
        Object[] params = new Object[]{"key3", "value3"};
        logger.bind(params);
        logger.info(message);
        // concatenation order matters for assert equal
        // new bind context added at the end of existing
        params = Helpers.mergeObjectArrays(context, params);
        expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], params[3], params[4], params[5]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert loggable object instances work as well
        Map<String, Object> map = new HashMap<>();
        map.put("test", new Object());
        LoggableObject loggableObject = new GenericLoggableObject(
                new Object[]{"integer", 1, "object", map});
        logger.bind(loggableObject);
        logger.info(message);
        // concatenation order matters for assert equal
        // new bind context added at the end of existing
        params = Helpers.mergeObjectArrays(params, loggableObject.loggableObject());
        expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s", message, params[0],
                params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert hashmap will work as well
        logger.newBind(); // reset bound context for easier assert
        Map<String, Object> contextMap1 = new HashMap<>();
        contextMap1.put("key1", "value1");
        contextMap1.put("key2", "value2");
        Map<String, Object> contextMap2 = new HashMap<>();
        contextMap2.put("key3", "value1");
        contextMap2.put("key4", "value1");
        LoggableObject contextObject = new GenericLoggableObject(new Object[]{"map", contextMap1});
        logger.bind(contextMap1, contextObject, contextMap2);
        logger.info(message);
        params = Helpers.mergeObjectArrays(Helpers.mapToObjectArray(contextMap1), contextObject.loggableObject(),
                Helpers.mapToObjectArray(contextMap2));
        expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s",
                message, params[0], params[1], params[2], params[3], params[4], String.format("\"%s\"", params[5]),
                params[6], params[7], params[8], params[9]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void logContextUnbindTest() {
        String message = "Hello World!";
        Object[] context = new Object[]{"key1", "value1", "key2", "value2"};
        logger.bind(context);
        logger.info(message);
        LoggingEvent expectedLoggingEvent = info(String.format("%s, %s=%s, %s=%s", message,
                context[0], context[1], context[2], context[3]));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] unbind = new Object[]{"key2", "value2"};
        logger.unbind(unbind);
        logger.info(message);
        expectedLoggingEvent = info(String.format("%s, %s=%s", message, context[0], context[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        logger.unbind(new GenericLoggableObject(new Object[]{"key1", "value1"}));
        logger.info(message);
        expectedLoggingEvent = info(message);
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        // assert hashmap will work as well
        Map<String, Object> contextMap1 = new HashMap<>();
        contextMap1.put("key1", "value1");
        contextMap1.put("key2", "value2");
        Map<String, Object> contextMap2 = new HashMap<>();
        contextMap2.put("key3", "value1");
        contextMap2.put("key4", "value1");
        LoggableObject contextObject = new GenericLoggableObject(new Object[]{"map", contextMap1});
        logger.newBind(contextMap1, contextObject, contextMap2); // bind context before unbinding
        logger.unbind(contextMap1, contextObject, contextMap2);
        logger.info(message);
        expectedLoggingEvent = info(message);
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void contextSupplierAddedTest() {
        LoggableObject globalContext = new GenericLoggableObject(new Object[]{"environment", "development"});
        StructLoggerConfig.setContextSupplier(globalContext);
        String message = "Hello World!";
        logger.info(message);
        Object[] context = globalContext.loggableObject();
        LoggingEvent expectedLoggingEvent = info(String.format("%s, %s=%s", message, context[0], context[1]));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert not overwritten by bind context
        Object[] params = new Object[]{"environment", "test"};
        logger.bind(params);
        logger.info(message);
        String warningMessage = String.format("%s key `%s` ignored because it exists in the global context with " +
                "value `%s` which takes precedence.", StructLoggerConfig.getStructLog4jTag(), params[0],
                globalContext.loggableObject()[1]);
        expectedLoggingEvent = info(String.format("%s, %s=%s", message, context[0], context[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(1);
        LoggingEvent structlogWarningLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertEquals(warningMessage, structlogWarningLoggingEvent.getMessage());
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void throwableLoggingTest() {
        String message = "Oh boy!";
        Throwable throwable = new RuntimeException(message);
        logger.error(message, throwable);
        LoggingEvent expectedLoggingEvent = error(
                throwable, String.format("%s, %s=%s", message, "errorMessage", String.format("\"%s\"", message)));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertTrue(actualLoggingEvent.getThrowable().isPresent());
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void throwableLoggedDespiteOrderTest() {
        String message = "Oh boy!";
        Throwable throwable = new RuntimeException(message);
        logger.error(message, "key1", "value1", "key2", "value number two", "key3", null, throwable);

        LoggingEvent expectedLoggingEvent = error(throwable, String.format("%s, %s=%s, %s=%s, %s=%s, %s=%s", message,
                "key1", "value1", "key2", String.format("\"%s\"", "value number two"), "key3",  null, "errorMessage",
                String.format("\"%s\"", message)));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        logger.error(message, throwable, "key1", "value1", "key2", "value number two", "key3", null);

        expectedLoggingEvent = error(throwable, String.format("%s, %s=%s, %s=%s, %s=%s, %s=%s", message, "errorMessage",
                String.format("\"%s\"", message), "key1", "value1", "key2", String.format("\"%s\"", "value number two"),
                "key3",  null));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertTrue(actualLoggingEvent.getThrowable().isPresent());
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        logger.error(message, "key1", "value1", throwable, "key2", "value number two", "key3", null);

        expectedLoggingEvent = error(throwable, String.format("%s, %s=%s, %s=%s, %s=%s, %s=%s", message,
                "key1", "value1", "errorMessage", String.format("\"%s\"", message), "key2",
                String.format("\"%s\"", "value number two"), "key3",  null));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertTrue(actualLoggingEvent.getThrowable().isPresent());
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void throwableRootCauseLoggedCorrectlyTest() {
        String rootCauseMessage = "This is the root cause of the error";
        String message = "Oh boy!";
        Throwable rootCause = new RuntimeException(rootCauseMessage);
        Throwable throwable = new RuntimeException(message, rootCause);

        logger.error(message, throwable);
        LoggingEvent expectedLoggingEvent = error(throwable,
                String.format("%s, %s=%s", message, "errorMessage", String.format("\"%s\"", rootCauseMessage)));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertTrue(actualLoggingEvent.getThrowable().isPresent());
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    /*
    * failure case test scenarios
    *
    */
    @Test
    public void oddNumberParamsTest() {
        String message = "Hello World!";
        Object[] params = new Object[]{"key1", "value1", "key2"};
        logger.info(message, params);

        String warningMessage = String.format("%s odd number of parameters (%s) passed in. " +
                        "The value pair for key `%s` not found thus it has been ignored.",
                        StructLoggerConfig.getStructLog4jTag(), params.length, params[params.length - 1]);

        LoggingEvent expectedLoggingEvent = info(String.format("%s, %s=%s", message, params[0], params[1]));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(1);
        LoggingEvent structlogWarningLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertEquals(warningMessage, structlogWarningLoggingEvent.getMessage());
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void nonStringKeyPassedIn() {
        String message = "Hello World!";
        Object[] params = new Object[]{1, "value1"};
        logger.info(message, params);

        String warningMessage = String.format("%s key `%s` expected to be of type String but `%s` passed in.",
                StructLoggerConfig.getStructLog4jTag(), params[0], params[0].getClass().getName());

        LoggingEvent expectedLoggingEvent = info(message);
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(1);
        LoggingEvent structlogWarningLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertEquals(warningMessage, structlogWarningLoggingEvent.getMessage());
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{1, "value1"});
        logger.info(message, loggableObject);

        params = loggableObject.loggableObject();
        warningMessage = String.format("%s key `%s` expected to be of type String but `%s` passed in from " +
                "%s.loggableObject()", StructLoggerConfig.getStructLog4jTag(), params[0], params[0].getClass().getName(),
                loggableObject.getClass().getName());

        expectedLoggingEvent = info(message);
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(1);
        structlogWarningLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertEquals(warningMessage, structlogWarningLoggingEvent.getMessage());
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void keyWithSpacesPassedIn() {
        String message = "Hello World!";
        Object[] params = new Object[]{"invalid key", "value1"};
        logger.info(message, params);

        String warningMessage = String.format("%s key `%s` with spaces passed in.",
                StructLoggerConfig.getStructLog4jTag(), params[0]);

        LoggingEvent expectedLoggingEvent = info(message);
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(1);
        LoggingEvent structlogWarningLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertEquals(warningMessage, structlogWarningLoggingEvent.getMessage());
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"invalid key", "value1"});
        logger.info(message, loggableObject);

        warningMessage = String.format("%s key `%s` with spaces passed in from %s.loggableObject()",
                StructLoggerConfig.getStructLog4jTag(), params[0], loggableObject.getClass().getName());

        expectedLoggingEvent = info(message);
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(1);
        structlogWarningLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertEquals(warningMessage, structlogWarningLoggingEvent.getMessage());
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }
}
