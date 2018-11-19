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

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import io.github.kwahome.structlog4j.interfaces.LogRenderer;
import io.github.kwahome.structlog4j.interfaces.LoggableObject;
import io.github.kwahome.structlog4j.interfaces.Logger;
import io.github.kwahome.structlog4j.utils.Helpers;

/**
 * Concrete implementation of the Logger interface
 *
 * @author kelvin.wahome
 */
@RequiredArgsConstructor
public class StructLogger implements Logger {
    private final org.slf4j.Logger slf4jLogger;

    private Optional<LoggableObject> instanceBoundContext = Optional.empty();

    StructLogger(String name) {
        slf4jLogger = org.slf4j.LoggerFactory.getLogger(name);
    }

    StructLogger(Class<?> source) {
        slf4jLogger = LoggerFactory.getLogger(source);
    }

    public org.slf4j.Logger getSlf4jLogger() {
        return slf4jLogger;
    }

    /**
     * Returns a LoggableObject from the optional instanceBoundContext.
     *
     * If the optional is empty, an empty GenericLoggableObject is returned.
     *
     * @return Optional
     */
    private LoggableObject getLoggableInstanceBoundContext() {
        LoggableObject loggableObject = new GenericLoggableObject();
        if (instanceBoundContext.isPresent()) {
            loggableObject = instanceBoundContext.get();
        }
        return loggableObject;
    }

    private void setInstanceBoundContext(LoggableObject instanceBoundContext) {
        this.instanceBoundContext = Optional.of(instanceBoundContext);
    }

    /**
     * Returns a LoggableObject from the optional StructLog4JConfig contextSupplier.
     *
     * If the optional is empty, an empty GenericLoggableObject is returned.
     *
     * @return Optional
     */
    private LoggableObject getLoggableGlobalContextSupplier() {
        LoggableObject loggableObject = new GenericLoggableObject();
        if (StructLog4JConfig.getContextSupplier().isPresent()) {
            loggableObject = StructLog4JConfig.getContextSupplier().get();
        }
        return loggableObject;
    }

    /**
     * Binds passed context to Logger instance. Existing context will be overwritten.
     *
     * Takes a list of key-value pairs at alternate positions e.g:
     *
     *      [key1, value1, key2, value2]
     *
     * @param params "list of key/value pairs"
     */
    @Override
    public void newBind(Object...params) {
        instanceBoundContext = Optional.empty();
        setInstanceBoundContext(new GenericLoggableObject(addParamsToBoundContext(params)));
    }

    /**
     * Binds passed context to Logger instance. Existing context will be overwritten.
     *
     * Takes an instance of a class implementing the LoggableObject interface.
     *
     * @param loggableObject "LoggableObject object"
     */
    @Override
    public void newBind(LoggableObject loggableObject) {
        newBind(loggableObject.loggableObject());
    }

    /**
     * Binds passed context to Logger instance while preserving existing context
     * by adding new params onto the already bound context.
     *
     * Takes a list of key-value pairs at alternate positions e.g:
     *
     *      [key1, value1, key2, value2]
     *
     * @param params "list of key/value pairs"
     */
    @Override
    public void bind(Object...params) {
        if (!instanceBoundContext.isPresent()) {
            newBind(params);
        } else {
            setInstanceBoundContext(new GenericLoggableObject(addParamsToBoundContext(params)));
        }
    }

    /**
     * Binds passed context to Logger instance while preserving existing context
     * by adding new params onto the already bound context.
     *
     * Takes an instance of a class implementing the LoggableObject interface.
     *
     * @param loggableObject "LoggableObject object"
     */
    @Override
    public void bind(LoggableObject loggableObject) {
        if (!instanceBoundContext.isPresent()) {
            newBind(loggableObject);
        } else {
            bind(loggableObject.loggableObject());
        }

    }

    /**
     * Removes passed context from Logger instance bound context.
     *
     * Takes a list of key-value pairs at alternate positions e.g:
     *
     *      [key1, value1, key2, value2]
     *
     * @param params "list of key/value pairs"
     */
    @Override
    public void unbind(Object...params) {
        instanceBoundContext = Optional.of(new GenericLoggableObject(removeItemFromBoundContext(params)));
    }

    /**
     * Removes passed context from Logger instance bound context.
     *
     * Takes an instance of a class implementing the LoggableObject interface.
     *
     * @param loggableObject "LoggableObject object"
     */
    @Override
    public void unbind(LoggableObject loggableObject) {
        unbind(loggableObject.loggableObject());
    }

    private Object[] addParamsToBoundContext(Object...params) {
        ArrayList<Object> objectArrayList = new ArrayList<>();
        Map<String, Object> globalLoggerContext = Helpers.objectArrayToMap(
                getLoggableGlobalContextSupplier().loggableObject());
        Map<String, Object> stringObjectMap = Helpers.objectArrayToMap(
                getLoggableInstanceBoundContext().loggableObject());
        int paramsSize = params.length;
        if (paramsSize % 2 != 0) {
            // odd number of params provided meaning the last key-value pair
            // does not have a matching value
            // ignore it and log to warn of the ignored value
            paramsSize = paramsSize - 1;
            slf4jLogger.warn(String.format("%s odd number of parameters (%s) passed in. " +
                    "The value pair for key `%s` not found thus it has been ignored.",
                    StructLog4JConfig.getStructLog4jTag(), params.length, params[paramsSize]));
        }
        for (int i = 0; i < paramsSize; i = i + 2) {
            // check if key in global context in which case the
            // global context values takes precedence
            String key = (String) params[i];
            Object value = params[i + 1];
            if (!globalLoggerContext.containsKey(key)) {
                stringObjectMap.put(key, value);
            } else {
                slf4jLogger.warn(String.format("%s key `%s` ignored because it exists in the global context with " +
                        "value `%s` which takes precedence.", StructLog4JConfig.getStructLog4jTag(), key,
                        globalLoggerContext.get(key)));
            }
        }
        for (int i = 0; i < stringObjectMap.keySet().size(); i++) {
            objectArrayList.add(stringObjectMap.keySet().toArray()[i]);
            objectArrayList.add(stringObjectMap.values().toArray()[i]);
        }
        return objectArrayList.toArray();
    }

    private Object[] removeItemFromBoundContext(Object...params) {
        Map<String, Object> stringObjectMap = Helpers.objectArrayToMap(
                getLoggableInstanceBoundContext().loggableObject());
        int paramsSize = params.length;
        if (paramsSize % 2 != 0) {
            // odd number of params provided meaning the last key-value pair
            // does not have a matching value
            // ignore it and log to warn of the ignored value
            paramsSize = paramsSize - 1;
            slf4jLogger.warn(String.format("%s odd number of parameters (%s) passed in. " +
                    "The value pair for key `%s` not found thus it has been ignored.",
                    StructLog4JConfig.getStructLog4jTag(), params.length, params[paramsSize]));
        }
        for (int i = 0; i < paramsSize; i = i + 2) {
            String key = (String) params[i];
            Object value = params[i + 1];
            stringObjectMap.remove(key, value);
        }
        return Helpers.mapToObjectArray(stringObjectMap);
    }

    @Override
    public void error(String message, Object... params) {
        if (slf4jLogger.isErrorEnabled()) {
            log(Level.ERROR, message, params);
        }
    }

    @Override
    public void warn(String message, Object... params) {
        if (slf4jLogger.isWarnEnabled()) {
            log(Level.WARN, message, params);
        }
    }

    @Override
    public void info(String message, Object... params) {
        if (slf4jLogger.isInfoEnabled()) {
            log(Level.INFO, message, params);
        }
    }

    @Override
    public void debug(String message, Object... params) {
        if (slf4jLogger.isDebugEnabled()) {
            log(Level.DEBUG, message, params);
        }
    }

    @Override
    public void trace(String message, Object... params) {
        if (slf4jLogger.isTraceEnabled()) {
            log(Level.TRACE, message, params);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return slf4jLogger.isErrorEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return slf4jLogger.isWarnEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return slf4jLogger.isInfoEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return slf4jLogger.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return slf4jLogger.isTraceEnabled();
    }

    /**
     * Common method to handle structured logging. It delegates logging of different levels to the slf4j logger.
     *
     * @param level "logging level"
     * @param message "log message"
     * @param params "Object array containing key-value pairs at alternate indices"
     */
    private void log(Level level, String message, Object...params) {
        try {
            message = message == null ? "" : message; // just in case...
            Throwable throwable = null;
            LogRenderer<Object> logRenderer = StructLog4JConfig.getLogRenderer();
            Object builderObject = logRenderer.start(slf4jLogger);
            logRenderer.addMessage(slf4jLogger, builderObject, message);
            boolean processKeyValues = true; // set to false in case of errors thus cannot rely on the order any more
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof LoggableObject) {
                    handleLoggableObject(logRenderer, builderObject, (LoggableObject) param);
                } else if (param instanceof Throwable) {
                    // exceptions are not logged directly (unless they implement LoggableObject)
                    // they will get passed separate as exceptions to the base slf4j API
                    throwable = (Throwable) param;
                    // also log the error explicitly as a separate key-value pair for easy parsing
                    logRenderer.addKeyValue(
                            slf4jLogger, builderObject, "errorMessage", getCauseErrorMessage(throwable));
                } else if (processKeyValues) {
                    // dynamic key-value pairs being passed in
                    // process the key-value pairs only if no errors were encountered and order can is reliably correct
                    // move on to the next field automatically and assume it's the value
                    i++;
                    if (i < params.length) {
                        // error encountered in a key, stop processing other key-value pairs
                        processKeyValues = handleKeyValue(logRenderer, builderObject, param, params[i], null);
                    } else {
                        slf4jLogger.warn(String.format("%s odd number of parameters (%s) passed in. " +
                                        "The value pair for key `%s` not found thus it has been ignored.",
                                StructLog4JConfig.getStructLog4jTag(), params.length, param));
                    }
                }
            }
            // add logger instance bound context
            handleLoggableObject(logRenderer, builderObject, getLoggableInstanceBoundContext());
            // add mandatory context, if specified
            handleLoggableObject(logRenderer, builderObject, getLoggableGlobalContextSupplier());
            // actual logging via slf4j
            log(level, logRenderer.end(slf4jLogger, builderObject), throwable);
        } catch (Exception ex) {
            // should never happen, a logging library has no right to generate exceptions :-)
            slf4jLogger.error(String.format(
                    "%s unexpected logger error `%s`.", StructLog4JConfig.getStructLog4jTag(), ex.getMessage()), ex);
        }
    }

    /**
     * Handle LoggableObject implementations
     *
     * @param logRenderer "LogRenderer implementation"
     * @param builderObject "Object builder"
     * @param loggableObject "Loggable object"
     */
    private void handleLoggableObject(LogRenderer<Object> logRenderer, Object builderObject,
                                      LoggableObject loggableObject) {
        //sanity checks
        if (loggableObject.loggableObject() == null) {
            slf4jLogger.warn(String.format("%s `null` returned from %s.loggableObject()",
                    StructLog4JConfig.getStructLog4jTag(), loggableObject.getClass().getName()));
            return;
        }
        Object[] params = loggableObject.loggableObject();
        int size = params.length;
        if (size % 2 != 0) {
            /*
             * Validates that the object array can be iterated over in pairs i.e. each key has a value.
             *
             * If the length of the object array is odd, the last item in the array (item at the last index)
             * is disregarded with the assumption it's the key with the missing value since iteration starts
             * at index 0.
             */
            size = size - 1;
            slf4jLogger.warn(String.format("%s odd number of parameters (%s) returned from %s.loggableObject(). " +
                    "The value pair for key `%s` not found thus it has been ignored.",
                    StructLog4JConfig.getStructLog4jTag(), params.getClass().getName(), params.length, params[size]));

        }
        for (int i = 0; i < size; i = i + 2) {
            handleKeyValue(logRenderer, builderObject, params[i], params[i + 1], loggableObject);
        }
    }

    /**
     * Common logic for handling keys.
     *
     * Returns true/false depending on whether it was successful or not
     *
     * @param logRenderer "LogRenderer implementation"
     * @param builderObject "Object builder"
     * @param keyObject "key"
     * @param value "value"
     * @param loggableSourceObject "Loggable source object"
     * @return boolean
     */
    private boolean handleKeyValue(LogRenderer<Object> logRenderer, Object builderObject, Object keyObject,
                                   Object value, LoggableObject loggableSourceObject) {
        boolean handled = true;
        // key must be a String
        if (keyObject instanceof String) {
            String key = (String) keyObject;
            if (!key.contains(" ")) {
                logRenderer.addKeyValue(slf4jLogger, builderObject, key, value);
            } else {
                if (loggableSourceObject == null) {
                    slf4jLogger.warn(String.format("%s key `%s` with spaces passed in.",
                            StructLog4JConfig.getStructLog4jTag(), key));
                } else {
                    slf4jLogger.warn(String.format("%s key `%s` with spaces passed in from %s.loggableObject()",
                            StructLog4JConfig.getStructLog4jTag(), key, loggableSourceObject.getClass().getName()));
                }
                handled = false;
            }

        } else {
            // a non-String key was passed
            if (loggableSourceObject == null) {
                slf4jLogger.warn(String.format("%s key `%s` expected to be of type String but `%s` passed in.",
                        StructLog4JConfig.getStructLog4jTag(), keyObject,
                        keyObject != null ? keyObject.getClass().getName() : "null"));
            } else {
                slf4jLogger.warn(String.format(
                        "%s key `%s` expected to be of type String but `%s` passed in from %s.loggableObject()",
                        StructLog4JConfig.getStructLog4jTag(), keyObject,
                        keyObject != null ? keyObject.getClass().getName() : "null",
                        loggableSourceObject.getClass().getName()));
            }
            handled = false;
        }
        return handled;
    }

    private void log(Level level, String structuredMessage, Throwable err) {
        switch (level) {
            case ERROR:
                logAtError(structuredMessage, err);
                break;
            case WARN:
                logAtWarn(structuredMessage, err);
                break;
            case INFO:
                logAtInfo(structuredMessage, err);
                break;
            case DEBUG:
                logAtDebug(structuredMessage, err);
                break;
            case TRACE:
                logAtTrace(structuredMessage, err);
                break;
            default:
                // nothing to do
        }
    }

    private void logAtError(String structuredMessage, Throwable err) {
        if (err == null) {
            slf4jLogger.error(structuredMessage);
        } else {
            slf4jLogger.error(structuredMessage, err);
        }
    }

    private void logAtWarn(String structuredMessage, Throwable err) {
        if (err == null) {
            slf4jLogger.warn(structuredMessage);
        } else {
            slf4jLogger.warn(structuredMessage, err);
        }
    }

    private void logAtInfo(String structuredMessage, Throwable err) {
        if (err == null) {
            slf4jLogger.info(structuredMessage);
        } else {
            slf4jLogger.info(structuredMessage, err);
        }
    }

    private void logAtDebug(String structuredMessage, Throwable err) {
        if (err == null) {
            slf4jLogger.debug(structuredMessage);
        } else {
            slf4jLogger.debug(structuredMessage, err);
        }
    }

    private void logAtTrace(String structuredMessage, Throwable err) {
        if (err == null) {
            slf4jLogger.trace(structuredMessage);
        } else {
            slf4jLogger.trace(structuredMessage, err);
        }
    }

    /**
     * Goes down the exception hierarchy to find the actual error message at the
     * root of the entire stack trace.
     *
     * @param throwable "Throwable"
     * @return String
     */
    private String getCauseErrorMessage(Throwable throwable) {
        if (throwable.getCause() == null) {
            return throwable.getMessage();
        } else {
            return getCauseErrorMessage(throwable.getCause());
        }
    }
}
