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

package io.github.kwahome.structlog4j;

import java.util.HashMap;
import java.util.Map;

import io.github.kwahome.structlog4j.interfaces.LoggableObject;
import io.github.kwahome.structlog4j.renderers.KeyValueRenderer;
import io.github.kwahome.structlog4j.utils.Helpers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;

import static org.hamcrest.CoreMatchers.is;
import static uk.org.lidalia.slf4jtest.LoggingEvent.*;

/**
 * Testing structlog4j's API.
 *
 * @author kelvin.wahome
 */
public class StructLoggerTests {
    private StructLogger logger;
    private TestLogger slf4jLogger;

    @Before
    public void setUp() {
        StructLog4JConfig.clearContextSupplier();
        StructLog4JConfig.setLogRenderer(KeyValueRenderer.getInstance());
        StructLog4JConfig.setLogEntriesSeparator(",");

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
        LoggingEvent expectedLoggingEvent = error(TestUtils.formatAsStructLogEntry(message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        Object[] params = new Object[]{"key1", "value1"};
        logger.error(message, params);
        expectedLoggingEvent = error(TestUtils.formatAsStructLogEntry(message, params));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(1);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.error(message, loggableObject);
        expectedLoggingEvent = error(TestUtils.formatAsStructLogEntry(message, loggableObject.loggableObject()));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(2);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void loggingAtWarnTest() {
        String message = "Consider this a warning!";
        logger.warn(message);
        LoggingEvent expectedLoggingEvent = warn(TestUtils.formatAsStructLogEntry(message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.warn(message, params);
        expectedLoggingEvent = warn(TestUtils.formatAsStructLogEntry(message, params));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.warn(message, loggableObject);
        expectedLoggingEvent = warn(TestUtils.formatAsStructLogEntry(message, loggableObject.loggableObject()));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void loggingAtInfoTest() {
        String message = "Hello World!";
        logger.info(message);
        LoggingEvent expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.info(message, params);
        expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, params));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.info(message, loggableObject);
        expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, loggableObject.loggableObject()));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void loggingAtDebugTest() {
        String message = "On the eight day, God started debugging!";
        logger.debug(message);
        LoggingEvent expectedLoggingEvent = debug(TestUtils.formatAsStructLogEntry(message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.debug(message, params);
        expectedLoggingEvent = debug(TestUtils.formatAsStructLogEntry(message, params));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.debug(message, loggableObject);
        expectedLoggingEvent = debug(TestUtils.formatAsStructLogEntry(message, loggableObject.loggableObject()));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void loggingAtTraceTest() {
        String message = "Tracer round!";
        logger.trace(message);
        LoggingEvent expectedLoggingEvent = trace(TestUtils.formatAsStructLogEntry(message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] params = new Object[]{"key1", "value1"};
        logger.trace(message, params);
        expectedLoggingEvent = trace(TestUtils.formatAsStructLogEntry(message, params));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"key2", "value2"});
        logger.trace(message, loggableObject);
        expectedLoggingEvent = trace(TestUtils.formatAsStructLogEntry(message, loggableObject.loggableObject()));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void loggingMultiKeyValueTest() {
        String message = "Hello World!";
        Object[] params = new Object[]{"key1", "value1", "key2", "value number two", "key3", null};
        logger.info(message, params);
        LoggingEvent expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, params));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void loggingLoggableObjectTest() {
        String message = "Hello World!";
        Object[] params = new Object[]{"key1", "value1", "key2", "value number two", "key3", null};
        LoggableObject loggableObject = new GenericLoggableObject(params);
        logger.info(message, loggableObject);
        LoggingEvent expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, params));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void loggingMultipleLoggableObjectTest() {
        String message = "Hello World!";
        LoggableObject loggableObject1 = new GenericLoggableObject(new Object[]{"key1", "value1"});
        LoggableObject loggableObject2 = new GenericLoggableObject(new Object[]{"key2", "value number two"});
        LoggableObject loggableObject3 = new GenericLoggableObject(new Object[]{"key3", null});

        logger.info(message, loggableObject1, loggableObject2, loggableObject3);
        // merge all objects into one
        Object[] allParams = Helpers.mergeObjectArrays(
                loggableObject1.loggableObject(), loggableObject2.loggableObject(), loggableObject3.loggableObject());
        LoggingEvent expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, allParams));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void logNewContextBindingTest() {
        String message = "Hello World!";
        Object[] context = new Object[]{"key1", "value1", "key2", "value2"};
        logger.newBind(context);
        logger.info(message);
        LoggingEvent expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, context));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert all log events will have bound context
        Object[] params = new Object[]{"key3", null, "key4", true};
        logger.info(message, params);
        // concatenation order matters for assert equal
        // the log params are added before the context
        Object[] allParams = Helpers.mergeObjectArrays(params, context);
        expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, allParams));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert newBind() overwrites existing context
        logger.newBind(params);
        logger.info(message);
        expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, params));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert loggable object instances work as well
        Map<String, Object> map = new HashMap<>();
        map.put("test", new Object());
        LoggableObject loggableObject = new GenericLoggableObject(
                new Object[]{"loggable", true, "integer", 1, "object", map});
        logger.newBind(loggableObject);
        logger.info(message);
        expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, loggableObject.loggableObject()));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

    }

    @Test
    public void logContextBindUpdatesTest() {
        String message = "Hello World!";
        Object[] context = new Object[]{"key1", "value1", "key2", "value2"};
        logger.bind(context);
        logger.info(message);
        LoggingEvent expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, context));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert bind updates existing context
        Object[] params = new Object[]{"key3", "value3"};
        logger.bind(params);
        logger.info(message);
        // concatenation order matters for assert equal
        // new bind context added at the end of existing
        Object[] allParams = Helpers.mergeObjectArrays(context, params);
        expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, allParams));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

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
        allParams = Helpers.mergeObjectArrays(allParams, loggableObject.loggableObject());
        expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, allParams));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void logContextUnbindTest() {
        String message = "Hello World!";
        Object[] context = new Object[]{"key1", "value1", "key2", "value2"};
        logger.bind(context);
        logger.info(message);
        LoggingEvent expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, context));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        Object[] unbind = new Object[]{"key2", "value2"};
        logger.unbind(unbind);
        logger.info(message);
        expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, "key1", "value1"));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        logger.unbind(new GenericLoggableObject(new Object[]{"key1", "value1"}));
        logger.info(message);
        expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void contextSupplierAddedTest() {
        LoggableObject globalContext = new GenericLoggableObject(new Object[]{"environment", "development"});
        StructLog4JConfig.setContextSupplier(globalContext);
        String message = "Hello World!";
        logger.info(message);
        LoggingEvent expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(
                message, globalContext.loggableObject()));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        // assert not overwritten by bind context
        Object[] params = new Object[]{"environment", "test"};
        logger.bind(params);
        logger.info(message);
        String warningMessage = String.format("%s key `%s` ignored because it exists in the global context with " +
                "value `%s` which takes precedence.", StructLog4JConfig.getStructLog4jTag(), params[0],
                globalContext.loggableObject()[1]);
        expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, globalContext.loggableObject()));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(1);
        LoggingEvent structlogWarningLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertEquals(warningMessage, structlogWarningLoggingEvent.getMessage());
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void throwableLoggingTest() {
        String message = "Oh boy!";
        Throwable throwable = new RuntimeException(message);
        logger.error(message, throwable);
        LoggingEvent expectedLoggingEvent = error(
                throwable, TestUtils.formatAsStructLogEntry(message, "errorMessage", message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertTrue(actualLoggingEvent.getThrowable().isPresent());
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void throwableLoggedDespiteOrderTest() {
        String message = "Oh boy!";
        Throwable throwable = new RuntimeException(message);
        logger.error(message, "key1", "value1", "key2", "value number two", "key3", null, throwable);

        LoggingEvent expectedLoggingEvent = error(throwable, TestUtils.formatAsStructLogEntry(
                message, "key1", "value1", "key2", "value number two", "key3", null, "errorMessage", message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        logger.error(message, throwable, "key1", "value1", "key2", "value number two", "key3", null);

        expectedLoggingEvent = error(throwable, TestUtils.formatAsStructLogEntry(
                message, "errorMessage", message, "key1", "value1", "key2", "value number two", "key3", null));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertTrue(actualLoggingEvent.getThrowable().isPresent());
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        logger.error(message, "key1", "value1", throwable, "key2", "value number two", "key3", null);

        expectedLoggingEvent = error(throwable, TestUtils.formatAsStructLogEntry(
                message, "key1", "value1", "errorMessage", message, "key2", "value number two", "key3", null));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertTrue(actualLoggingEvent.getThrowable().isPresent());
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void throwableRootCauseLoggedCorrectlyTest() {
        String rootCauseMessage = "This is the root cause of the error";
        String message = "Oh boy!";
        Throwable rootCause = new RuntimeException(rootCauseMessage);
        Throwable throwable = new RuntimeException(message, rootCause);

        logger.error(message, throwable);
        LoggingEvent expectedLoggingEvent = error(
                throwable, TestUtils.formatAsStructLogEntry(message, "errorMessage", rootCauseMessage));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertTrue(actualLoggingEvent.getThrowable().isPresent());
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    /*failure case tests*/
    @Test
    public void oddNumberParamsTest() {
        String message = "Hello World!";
        Object[] params = new Object[]{"key1", "value1", "key2"};
        logger.info(message, params);

        String warningMessage = String.format("%s odd number of parameters (%s) passed in. " +
                        "The value pair for key `%s` not found thus it has been ignored.",
                        StructLog4JConfig.getStructLog4jTag(), params.length, params[params.length - 1]);

        LoggingEvent expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message, params));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(1);
        LoggingEvent structlogWarningLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertEquals(warningMessage, structlogWarningLoggingEvent.getMessage());
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void nonStringKeyPassedIn() {
        String message = "Hello World!";
        Object[] params = new Object[]{1, "value1"};
        logger.info(message, params);

        String warningMessage = String.format("%s key `%s` expected to be of type String but `%s` passed in.",
                StructLog4JConfig.getStructLog4jTag(), params[0], params[0].getClass().getName());

        LoggingEvent expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(1);
        LoggingEvent structlogWarningLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertEquals(warningMessage, structlogWarningLoggingEvent.getMessage());
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{1, "value1"});
        logger.info(message, loggableObject);

        params = loggableObject.loggableObject();
        warningMessage = String.format("%s key `%s` expected to be of type String but `%s` passed in from " +
                "%s.loggableObject()", StructLog4JConfig.getStructLog4jTag(), params[0], params[0].getClass().getName(),
                loggableObject.getClass().getName());

        expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(1);
        structlogWarningLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertEquals(warningMessage, structlogWarningLoggingEvent.getMessage());
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }

    @Test
    public void keyWithSpacesPassedIn() {
        String message = "Hello World!";
        Object[] params = new Object[]{"invalid key", "value1"};
        logger.info(message, params);

        String warningMessage = String.format("%s key `%s` with spaces passed in.",
                StructLog4JConfig.getStructLog4jTag(), params[0]);

        LoggingEvent expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message));
        LoggingEvent actualLoggingEvent = slf4jLogger.getLoggingEvents().get(1);
        LoggingEvent structlogWarningLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertEquals(warningMessage, structlogWarningLoggingEvent.getMessage());
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));

        slf4jLogger.clear(); // clear previous log events

        LoggableObject loggableObject = new GenericLoggableObject(new Object[]{"invalid key", "value1"});
        logger.info(message, loggableObject);

        warningMessage = String.format("%s key `%s` with spaces passed in from %s.loggableObject()",
                StructLog4JConfig.getStructLog4jTag(), params[0], loggableObject.getClass().getName());

        expectedLoggingEvent = info(TestUtils.formatAsStructLogEntry(message));
        actualLoggingEvent = slf4jLogger.getLoggingEvents().get(1);
        structlogWarningLoggingEvent = slf4jLogger.getLoggingEvents().get(0);
        Assert.assertEquals(warningMessage, structlogWarningLoggingEvent.getMessage());
        Assert.assertThat(expectedLoggingEvent, is(actualLoggingEvent));
    }
}
