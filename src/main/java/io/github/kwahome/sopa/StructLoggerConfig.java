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
import java.util.function.Function;

import io.github.kwahome.sopa.interfaces.LogRenderer;
import io.github.kwahome.sopa.interfaces.LoggableObject;
import io.github.kwahome.sopa.renderers.KeyValueRenderer;
import io.github.kwahome.sopa.utils.Helpers;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * sopa configuration class.
 *
 * This class exposes statics that can be accessed without need of an instance.
 *
 * @author Kelvin Wahome
 */
@UtilityClass
public class StructLoggerConfig {
    private static final String SOPA_LOGGER_TAG = "[sopa logger] :";

    private static LogRenderer logRenderer = KeyValueRenderer.getInstance();

    private static Optional<LoggableObject> contextSupplier = Optional.empty();

    // default value formatter returns a toString(), regardless of object type unless null
    private static Function<Object, String> valueRenderer = (value) -> value == null ? "null" : value.toString();

    // default char string to appear between log params
    private static String logEntriesSeparator = ",";

    public static String getSopaLoggerTag() {
        return SOPA_LOGGER_TAG;
    }

    /**
     * {@link LogRenderer} logRenderer setter method.
     *
     * Allows override to the default logRenderer. Should be only done once during application
     * startup from the main thread to avoid any concurrency issues
     *
     * @param logRenderer "Custom logRenderer implementing the {@link LogRenderer} interface"
     */
    public static void setLogRenderer(LogRenderer logRenderer) {
        StructLoggerConfig.logRenderer = logRenderer;
    }

    /**
     * logRenderer getter method.
     */
    public static LogRenderer getLogRenderer() {
        return logRenderer;
    }

    /**
     * {@link Optional} contextSupplier setter method.
     *
     * Allows setting a {@link LoggableObject} POJO or lambda that will be invoked on every log entry
     * to add additional mandatory key-value pairs for shared variables e.g:
     *
     *      environment
     *      hostname
     *      servicename
     *
     * The alternative is to pass them in on every log invocation which is cumbersome.
     *
     * @param contextObject "Lambda that will executed on every log entry."
     */
    public static void setContextSupplier(@NonNull LoggableObject contextObject) {
        StructLoggerConfig.contextSupplier = Optional.of(contextObject);
    }

    /**
     * {@link #setContextSupplier(LoggableObject)} overload allowing use of an array of objects
     *
     * @param params {@link Object[]}
     */
    public static void setContextSupplier(@NonNull Object...params) {
        setContextSupplier(new GenericLoggableObject(params));
    }

    /**
     * {@link #setContextSupplier(LoggableObject)} overload allowing use of a map's key, value entrySet
     *
     * @param map {@link Map <String, Object>}
     */
    public static void setContextSupplier(@NonNull Map<String, Object> map) {
        setContextSupplier(new GenericLoggableObject(Helpers.mapToObjectArray(map)));
    }

    /**
     * {@link Optional} contextSupplier getter method.
     */
    public static Optional<LoggableObject> getContextSupplier() {
        return contextSupplier;
    }

    /**
     * Clears the context supplier (usually for testing purposes only)
     */
    public static void clearContextSupplier() {
        contextSupplier = Optional.empty();
    }

    /**
     * {@link Function <Object, String>} valueRenderer setter method.
     *
     * Allows setting a function that accepts one argument & produces a result e.g.
     * a lambda function, that can perform custom log rendering of any object that
     * is passed in as a value to any key-value entry
     *
     * @param customValueRenderer {@link Function <Object, String>} value renderer lambda
     */
    public static void setValueRenderer(@NonNull Function<Object, String> customValueRenderer) {
        valueRenderer = customValueRenderer;
    }

    /**
     * {@link Function <Object, String>} valueRenderer getter method.
     *
     * @return {@link Function <Object, String>}
     */
    public static Function<Object, String> getValueRenderer() {
        return valueRenderer;
    }

    /**
     * {@link String} logEntriesSeparator setter method.
     *
     * @param logEntriesSeparator "{@link String} appearing between key=value pairs"
     */
    public static void setLogEntriesSeparator(@NonNull String logEntriesSeparator) {
        StructLoggerConfig.logEntriesSeparator = logEntriesSeparator;
    }

    /**
     * {@link String} logEntriesSeparator getter method.
     */
    public static String getLogEntriesSeparator() {
        return logEntriesSeparator;
    }
}
