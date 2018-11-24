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

import java.util.Map;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import io.github.kwahome.sopa.interfaces.LogRenderer;
import io.github.kwahome.sopa.interfaces.LoggableObject;
import io.github.kwahome.sopa.interfaces.Logger;
import io.github.kwahome.sopa.utils.Helpers;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


/**
 * Concrete implementation of the Logger interface
 *
 * @author Kelvin Wahome
 */
@RequiredArgsConstructor
public class StructLogger implements Logger {
    private final org.slf4j.Logger slf4jLogger;

    private Optional<LoggableObject> instanceBoundContext = Optional.empty();

    StructLogger(String name) {
        slf4jLogger = LoggerFactory.getLogger(name);
    }

    StructLogger(Class<?> source) {
        slf4jLogger = LoggerFactory.getLogger(source);
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

    public org.slf4j.Logger getSlf4jLogger() {
        return slf4jLogger;
    }

    /**
     * Returns a {@link LoggableObject} from the optional {@link #instanceBoundContext}.
     * If the {@link Optional} is empty, an empty {@link GenericLoggableObject} is returned.
     *
     * @return {@link Optional}
     */
    private LoggableObject getLoggableInstanceBoundContext() {
        LoggableObject loggableObject = new GenericLoggableObject();
        if (instanceBoundContext.isPresent()) {
            loggableObject = instanceBoundContext.get();
        }
        return loggableObject;
    }

    /**
     * {@link #instanceBoundContext} setter method.
     *
     * @param instanceBoundContext {@link LoggableObject}
     */
    private void setInstanceBoundContext(LoggableObject instanceBoundContext) {
        this.instanceBoundContext = Optional.of(instanceBoundContext);
    }

    /**
     * Returns a {@link LoggableObject} from the {@link StructLoggerConfig} contextSupplier.
     *
     * If the optional is empty, an empty {@link GenericLoggableObject} is returned.
     *
     * @return Optional
     */
    private LoggableObject getLoggableGlobalContextSupplier() {
        LoggableObject loggableObject = new GenericLoggableObject();
        if (StructLoggerConfig.getContextSupplier().isPresent()) {
            loggableObject = StructLoggerConfig.getContextSupplier().get();
        }
        return loggableObject;
    }

    /**
     * Binds passed context to {@link Logger} instance. Existing context will be overwritten.
     *
     * Takes a list of key-value pairs at alternate positions e.g:
     *
     *      [key1, value1, key2, value2]
     *
     * @param params "list of key-value pairs"
     */
    @Override
    public void newBind(Object...params) {
        instanceBoundContext = Optional.empty();
        setInstanceBoundContext(new GenericLoggableObject(addParamsToBoundContext(params)));
    }

    /**
     * Binds passed context to {@link Logger} instance while preserving existing context
     * by adding new params onto the already bound context.
     *
     * Takes a list of key-value pairs at alternate positions e.g:
     *
     *      [key1, value1, key2, value2]
     *
     * @param params "list of key-value pairs"
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
     * Removes passed context from {@link Logger} instance bound context.
     *
     * Takes a list of key-value pairs at alternate positions e.g:
     *
     *      [key1, value1, key2, value2]
     *
     * @param params "list of key-value pairs"
     */
    @Override
    public void unbind(Object...params) {
        instanceBoundContext = Optional.of(new GenericLoggableObject(removeItemFromBoundContext(params)));
    }

    /**
     * Adds passed log context params to context bound to the logger instance.
     *
     * The {@link Object}[] instanceBoundContext and the array of params are converted into a
     * {@link Map}<{@link String}, {@link Object}> to guarantee that keys are not duplicated.
     *
     * @param params "array of objects"
     * @return {@link Object}[]
     */
    private Object[] addParamsToBoundContext(Object...params) {
        Map<String, Object> globalLoggerContext = Helpers.objectArrayToMap(
                getLoggableGlobalContextSupplier().loggableObject());
        Map<String, Object> stringObjectMap = Helpers.objectArrayToMap(
                getLoggableInstanceBoundContext().loggableObject());
        boolean proceed = true;
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof LoggableObject) {
                LoggableObject loggableObject = (LoggableObject) param;
                stringObjectMap.putAll(Helpers.objectArrayToMap(loggableObject.loggableObject()));
            } else if (param instanceof Map &&
                    (i % 2 == 0 || (i % 2 != 0 && !validateKey(params[i - 1], null, false)))) {
                stringObjectMap.putAll((Map<String, Object>) param);
            } else if (proceed) {
                // dynamic key-value pairs being passed in
                // process the key-value pairs only if no errors were encountered and order can is reliably correct
                // move on to the next field automatically and assume it's the value
                i++;
                if (i < params.length) {
                    if (proceed = validateKey(param, null, true)) {
                        String key = (String) param;
                        // check if key in global context in which case the
                        // global context values takes precedence & we don't want duplication
                        if (!globalLoggerContext.containsKey(key)) {
                            stringObjectMap.put(key, params[i]);
                        } else {
                            slf4jLogger.warn(
                                    String.format("%s key `%s` ignored because it exists in the global context with " +
                                    "value `%s` which takes precedence.", StructLoggerConfig.getSopaLoggerTag(), key,
                                    globalLoggerContext.get(key)));
                        }
                    }
                } else {
                    slf4jLogger.warn(String.format("%s odd number of parameters (%s) passed in. " +
                            "The value pair for key `%s` not found thus it has been ignored.",
                            StructLoggerConfig.getSopaLoggerTag(), params.length, param));
                }
            }
        }
        return Helpers.mapToObjectArray(stringObjectMap);
    }

    /**
     * Removes passed log context params to context bound to the logger instance
     *
     * @param params "array of objects"
     * @return Object[]
     */
    private Object[] removeItemFromBoundContext(Object...params) {
        Map<String, Object> stringObjectMap = Helpers.objectArrayToMap(
                getLoggableInstanceBoundContext().loggableObject());
        boolean proceed = true;
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof LoggableObject) {
                LoggableObject loggableObject = (LoggableObject) param;
                stringObjectMap.entrySet().removeAll(
                        Helpers.objectArrayToMap(loggableObject.loggableObject()).entrySet());
            } else if (param instanceof Map &&
                    (i % 2 == 0 || (i % 2 != 0 && !validateKey(params[i - 1], null, false)))) {
                stringObjectMap.entrySet().removeAll(((Map<String, Object>) param).entrySet());
            } else if (proceed) {
                // process the key-value pairs only if no errors were encountered and order can is reliably correct
                // next field is construed to be the value
                i++;
                if (i < params.length) {
                    // check if key in global context in which case the
                    // global context values takes precedence
                    if (proceed = validateKey(param, null, true)) {
                        String key = (String) param;
                        stringObjectMap.remove(key, params[i]);
                    }
                } else {
                    slf4jLogger.warn(String.format("%s odd number of parameters (%s) passed in. " +
                                    "The value pair for key `%s` not found thus it has been ignored.",
                            StructLoggerConfig.getSopaLoggerTag(), params.length, param));
                }
            }
        }
        return Helpers.mapToObjectArray(stringObjectMap);
    }

    /**
     * Handle {@link LoggableObject} implementations
     *
     * @param logRenderer "{@link LogRenderer <T>} implementation"
     * @param builderObject "{@link Object} builder"
     * @param loggableObject "{@link LoggableObject}"
     */
    private void handleLoggableObject(
            LogRenderer<Object> logRenderer, Object builderObject, @NonNull LoggableObject loggableObject) {
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
                    StructLoggerConfig.getSopaLoggerTag(), params.getClass().getName(), params.length, params[size]));

        }
        for (int i = 0; i < size; i = i + 2) {
            handleKeyValue(logRenderer, builderObject, params[i], params[i + 1], loggableObject);
        }
    }

    /**
     * Common logic for handling/rendering a key-value pair.
     *
     * Returns true/false which is dependent on success or not thereof
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
        boolean valid = validateKey(keyObject, loggableSourceObject, true);
        if (valid) {
            String key = (String) keyObject;
            logRenderer.addKeyValue(slf4jLogger, builderObject, key, value);
        }
        return valid;
    }

    /**
     * Handle passed in {@link Map <String, Object>} objects containing key, value loggable entries
     * to be iterated over as key-value pairs
     *
     * @param logRenderer {@link LogRenderer <T>}
     * @param builderObject {@link Object}
     * @param map {@link Map <String, Object>}
     */
    private void handleMap(LogRenderer<Object> logRenderer, Object builderObject, @NonNull Map<String, Object> map) {
        Object[] mapKeySet = map.keySet().toArray();
        Object[] mapValues = map.values().toArray();
        for (int i = 0; i < map.size(); i++) {
            handleKeyValue(logRenderer, builderObject, mapKeySet[i], mapValues[i], null);
        }
    }

    /**
     * Validates a paseed in key object by checking if it's a string that does not contain spaces in which
     * case true is returned and false otherwise.
     *
     * boolean warningLog tells this method whether to write a warning log or not.
     * Some usages are retrospective while deciding whether a key for a value has already been processed
     * thus logging may not be desired always.
     *
     * @param keyObject {@link Object} key object
     * @param loggableSourceObject {@link LoggableObject} loggable object that's the source of the key
     * @param warningLog {@link boolean}
     * @return boolean
     */
    private boolean validateKey(@NonNull Object keyObject, LoggableObject loggableSourceObject, boolean warningLog) {
        boolean valid = false;
        // key must be a String
        if (keyObject instanceof String) {
            String key = (String) keyObject;
            if (!key.contains(" ")) {
                // key is a String & has no spaces thus it's valid
                valid = true;
            } else if (warningLog) {
                if (loggableSourceObject == null) {
                    slf4jLogger.warn(String.format("%s key `%s` with spaces passed in.",
                            StructLoggerConfig.getSopaLoggerTag(), key));
                } else {
                    slf4jLogger.warn(String.format("%s key `%s` with spaces passed in from %s.loggableObject()",
                            StructLoggerConfig.getSopaLoggerTag(), key, loggableSourceObject.getClass().getName()));
                }
            }
        } else if (warningLog) {
            if (loggableSourceObject == null) {
                slf4jLogger.warn(String.format("%s key `%s` expected to be of type String but `%s` passed in.",
                        StructLoggerConfig.getSopaLoggerTag(), keyObject, keyObject.getClass().getName()));
            } else {
                slf4jLogger.warn(String.format(
                        "%s key `%s` expected to be of type String but `%s` passed in from %s.loggableObject()",
                        StructLoggerConfig.getSopaLoggerTag(), keyObject, keyObject.getClass().getName(),
                        loggableSourceObject.getClass().getName()));
            }

        }
        return valid;
    }

    /**
     * Common method to handle structured logging.
     * It delegates logging of different levels to the {@link #slf4jLogger} logger.
     *
     * @param level "{@link Level}"
     * @param message "{@link String} message"
     * @param params "{@link Object}[] containing key-value pairs at alternate indices"
     */
    private void log(Level level, @NonNull String message, Object...params) {
        try {
            Throwable throwable = null;
            LogRenderer<Object> logRenderer = StructLoggerConfig.getLogRenderer();
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
                } else if (param instanceof Map &&
                        (i % 2 == 0 || (i % 2 != 0 && !validateKey(params[i - 1], null, false)))) {
                    handleMap(logRenderer, builderObject, (Map<String, Object>) param);
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
                                StructLoggerConfig.getSopaLoggerTag(), params.length, param));
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
            slf4jLogger.error(String.format(
                    "%s unexpected logger error `%s`.", StructLoggerConfig.getSopaLoggerTag(), ex.getMessage()), ex);
        }
    }

    /**
     * {@link #log(Level, String, Object...)} overload that calls {@link #slf4jLogger} method
     * handling the {@link Level} passed with a formatted structured message string and a
     * {@link Throwable} if any
     *
     * @param level "{@link Level} level"
     * @param structuredMessage "String message"
     * @param err "{@link Throwable} error"
     */
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
     * Recursively goes down the {@link Throwable} hierarchy to find the actual error message at the
     * root of the stack trace.
     *
     * @param throwable "{@link Throwable}"
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
