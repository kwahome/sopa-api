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
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.kwahome.sopa.interfaces.LoggableObject;
import io.github.kwahome.sopa.renderers.JSONRenderer;
import io.github.kwahome.sopa.renderers.KeyValueRenderer;
import io.github.kwahome.sopa.utils.Helpers;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;

/**
 * @author Kelvin Wahome
 */
public class StructLoggerConfigTests {
    private StructLogger logger;
    private TestLogger slf4jLogger;

    @Before
    public void setUp() {
        // reset to defaults
        StructLoggerConfig.setLogRenderer(KeyValueRenderer.getInstance());
        StructLoggerConfig.setValueRenderer((value) -> value == null ? "null" : value.toString());
        StructLoggerConfig.clearContextSupplier();
        StructLoggerConfig.setLogEntriesSeparator(",");

        logger = (StructLogger) LoggerFactory.getLogger(StructLoggerKeyValueTests.class);
        slf4jLogger = (TestLogger) logger.getSlf4jLogger();
    }

    @After
    public void tearDown() {
        // reset to defaults
        StructLoggerConfig.setLogRenderer(KeyValueRenderer.getInstance());
        StructLoggerConfig.setValueRenderer((value) -> value == null ? "null" : value.toString());
        StructLoggerConfig.clearContextSupplier();
        StructLoggerConfig.setLogEntriesSeparator(",");
        slf4jLogger.clear();
    }

    @Test
    public void logRendererTest() {
        StructLoggerConfig.setLogRenderer(JSONRenderer.getInstance());
        Assert.assertThat(StructLoggerConfig.getLogRenderer(), is(JSONRenderer.getInstance()));

        // assert it's in use in logging
        String message = "Houston! We have a problem!";
        logger.info(message);
        LoggingEvent expectedLoggingEvent = LoggingEvent.info(
                String.format("{\"%s\":\"%s\"}", "message", message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void valueRendererTest() {
        Function<Object, String> valueRenderer = (value) -> value.getClass().getName();
        StructLoggerConfig.setValueRenderer(valueRenderer);
        Assert.assertThat(StructLoggerConfig.getValueRenderer(), is(valueRenderer));

        // assert it's in use in logging
        Object value = new Object();
        String message = "Houston! We have a problem!";
        logger.info(message, "key1", value);
        LoggingEvent expectedLoggingEvent = LoggingEvent.info(
                String.format("%s, %s=%s", message, "key1", value.getClass().getName()));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void contextSupplierTest() {
        Object[] context = new Object[]{"environment", "test", "host", "localhost"};
        StructLoggerConfig.setContextSupplier("environment", "test", "host", "localhost");
        Assert.assertTrue(StructLoggerConfig.getContextSupplier().isPresent());
        StructLoggerConfig.getContextSupplier().ifPresent(
                setContext -> Assert.assertThat(setContext.loggableObject(), is(context))
        );

        // assert it's in use in logging
        String message = "Houston! We have a problem!";
        logger.info(message, "key1", "value1");
        LoggingEvent expectedLoggingEvent = LoggingEvent.info(String.format("%s, %s=%s, %s=%s, %s=%s",
                message, "key1", "value1", context[0], context[1], context[2], context[3]));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events
        StructLoggerConfig.clearContextSupplier(); // clear existing context

        // assert LoggableObject passed in is set
        LoggableObject loggableObject = new GenericLoggableObject(
                new Object[]{"environment", "test", "host", "localhost"});
        StructLoggerConfig.setContextSupplier(context);
        Assert.assertTrue(StructLoggerConfig.getContextSupplier().isPresent());
        StructLoggerConfig.getContextSupplier().ifPresent(
                setContext -> Assert.assertThat(setContext.loggableObject(), is(loggableObject.loggableObject()))
        );

        // assert it's in use in logging
        logger.info(message, "key1", "value1");
        expectedLoggingEvent = LoggingEvent.info(String.format("%s, %s=%s, %s=%s, %s=%s",
                message, "key1", "value1", context[0], context[1], context[2], context[3]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));

        slf4jLogger.clear(); // clear previous log events
        StructLoggerConfig.clearContextSupplier(); // clear existing context

        // assert Map<String, Object> passed in is set
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("environment", "test");
        contextMap.put("host", "localhost");
        StructLoggerConfig.setContextSupplier(contextMap);
        Assert.assertTrue(StructLoggerConfig.getContextSupplier().isPresent());
        StructLoggerConfig.getContextSupplier().ifPresent(
                setContext -> Assert.assertThat(setContext.loggableObject(), is(Helpers.mapToObjectArray(contextMap)))
        );

        // assert it's in use in logging
        logger.info(message, "key1", "value1");
        expectedLoggingEvent = LoggingEvent.info(String.format("%s, %s=%s, %s=%s, %s=%s",
                message, "key1", "value1", context[0], context[1], context[2], context[3]));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }

    @Test
    public void logEntriesSeparatorTest() {
        String separator = ";";
        StructLoggerConfig.setLogEntriesSeparator(separator);
        Assert.assertThat(StructLoggerConfig.getLogEntriesSeparator(), is(separator));

        // assert it's in use in logging
        String message = "Houston! We have a problem!";
        logger.info(message, "key1", "value1", "key2", "value2");
        LoggingEvent expectedLoggingEvent = LoggingEvent.info(
                String.format("%s%s %s=%s%s %s=%s", message, separator, "key1", "value1", separator, "key2", "value2"));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(actualLoggingEvent, is(expectedLoggingEvent));
    }
}
