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

import static org.hamcrest.CoreMatchers.is;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.kwahome.sopa.interfaces.LoggableObject;
import io.github.kwahome.sopa.renderers.JSONRenderer;
import io.github.kwahome.sopa.utils.Helpers;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;

/**
 * @author Kelvin Wahome
 */
public class JSONRendererTests {
    private StructLogger logger;
    private TestLogger slf4jLogger;

    @Before
    public void setUp() {
        StructLoggerConfig.clearContextSupplier();
        StructLoggerConfig.setLogRenderer(JSONRenderer.getInstance());

        logger = (StructLogger) LoggerFactory.getLogger(StructLoggerKeyValueTests.class);
        slf4jLogger = (TestLogger) logger.getSlf4jLogger();
    }

    @After
    public void tearDown() {
        slf4jLogger.clear();
    }

    @Test
    public void loggingAtErrorTest() {
        String message = "Houston! We have a problem!";
        Throwable throwable = new RuntimeException(message);
        logger.error(message, throwable);
        LoggingEvent expectedLoggingEvent = LoggingEvent.error(throwable, String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}",
                "message", message, "errorMessage", message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.error(message, params);
        expectedLoggingEvent = LoggingEvent.error(
                String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", "message", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.error(message, loggableObject);
        params = loggableObject.loggableObject();
        expectedLoggingEvent = LoggingEvent.error(
                String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", "message", message, params[0], params[1]));
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
        expectedLoggingEvent = LoggingEvent.error(String.format(
                "{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\"}",
                "message", message, params[0], params[1], params[2], params[3], params[4], String.format("%s",
                params[5]), params[6], params[7], params[8], params[9], "myMap", String.format("%s", map2)));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggingAtWarnTest() {
        String message = "Consider this a warning!";
        logger.warn(message);
        LoggingEvent expectedLoggingEvent = LoggingEvent.warn(
                String.format("{\"%s\":\"%s\"}", "message", message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.warn(message, params);
        expectedLoggingEvent = LoggingEvent.warn(
                String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", "message", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.warn(message, loggableObject);
        params = loggableObject.loggableObject();
        expectedLoggingEvent = LoggingEvent.warn(
                String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", "message", message, params[0], params[1]));
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
        expectedLoggingEvent = LoggingEvent.warn(String.format(
                "{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\"}",
                "message", message, params[0], params[1], params[2], params[3], params[4], String.format("%s",
                        params[5]), params[6], params[7], params[8], params[9], "myMap", String.format("%s", map2)));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggingAtInfoTest() {
        String message = "Hello World!";
        logger.info(message);
        LoggingEvent expectedLoggingEvent = LoggingEvent.info(
                String.format("{\"%s\":\"%s\"}", "message", message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.info(message, params);
        expectedLoggingEvent = LoggingEvent.info(
                String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", "message", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.info(message, loggableObject);
        params = loggableObject.loggableObject();
        expectedLoggingEvent = LoggingEvent.info(
                String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", "message", message, params[0], params[1]));
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
        expectedLoggingEvent = LoggingEvent.info(String.format(
                "{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\"}",
                "message", message, params[0], params[1], params[2], params[3], params[4], String.format("%s",
                        params[5]), params[6], params[7], params[8], params[9], "myMap", String.format("%s", map2)));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggingAtDebugTest() {
        String message = "On the eighth day, God started debugging!";
        logger.debug(message);
        LoggingEvent expectedLoggingEvent = LoggingEvent.debug(
                String.format("{\"%s\":\"%s\"}", "message", message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.debug(message, params);
        expectedLoggingEvent = LoggingEvent.debug(
                String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", "message", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.debug(message, loggableObject);
        params = loggableObject.loggableObject();
        expectedLoggingEvent = LoggingEvent.debug(
                String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", "message", message, params[0], params[1]));
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
        expectedLoggingEvent = LoggingEvent.debug(String.format(
                "{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\"}",
                "message", message, params[0], params[1], params[2], params[3], params[4], String.format("%s",
                        params[5]), params[6], params[7], params[8], params[9], "myMap", String.format("%s", map2)));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void loggingAtTraceTest() {
        String message = "Tracer round!";
        logger.trace(message);
        LoggingEvent expectedLoggingEvent = LoggingEvent.trace(
                String.format("{\"%s\":\"%s\"}", "message", message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.trace(message, params);
        expectedLoggingEvent = LoggingEvent.trace(
                String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", "message", message, params[0], params[1]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.trace(message, loggableObject);
        params = loggableObject.loggableObject();
        expectedLoggingEvent = LoggingEvent.trace(
                String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", "message", message, params[0], params[1]));
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
        expectedLoggingEvent = LoggingEvent.trace(String.format(
                "{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\"}",
                "message", message, params[0], params[1], params[2], params[3], params[4], String.format("%s",
                        params[5]), params[6], params[7], params[8], params[9], "myMap", String.format("%s", map2)));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }
}
