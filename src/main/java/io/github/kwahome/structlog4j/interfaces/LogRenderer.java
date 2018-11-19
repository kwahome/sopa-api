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

package io.github.kwahome.structlog4j.interfaces;

import org.slf4j.Logger;

/**
 * Standard renderer (format/encoder) interface.
 *
 * The slf4j logger is passed into every method for any internal error reporting
 * within the log renderer itself.
 *
 * @param <BuilderObject> "Builder object specific to a renderer passed around from start() till end()"
 *
 * @author kelvin.wahome
 */
public interface LogRenderer<BuilderObject> {
    BuilderObject start(org.slf4j.Logger logger);

    /**
     * Adds log message to the log event
     *
     * @param logger Logger instance
     * @param builderObject BuilderObject
     * @param message log message
     * @return LogRenderer
     */
    LogRenderer<BuilderObject> addMessage(
            org.slf4j.Logger logger, BuilderObject builderObject, String message);

    /**
     * Adds formatted key=value pairs to the log event
     *
     * @param logger Logger instance
     * @param builderObject BuilderObject
     * @param key key
     * @param value value
     * @return LogRenderer
     */
    LogRenderer<BuilderObject> addKeyValue(
            org.slf4j.Logger logger, BuilderObject builderObject, String key, Object value);

    /**
     * Returns the formatted log message
     *
     * @param logger Logger instance
     * @param builderObject BuilderObject
     * @return String
     */
    String end(Logger logger, BuilderObject builderObject);
}
