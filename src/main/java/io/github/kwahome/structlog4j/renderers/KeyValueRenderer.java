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

package io.github.kwahome.structlog4j.renderers;

import org.slf4j.Logger;

import io.github.kwahome.structlog4j.interfaces.LogRenderer;

/**
 * Standard key-value pair renderer that formats log messages as:
 *
 *      message, key1=value1, key2=value2, key3=value3
 *
 * Used as the default out of the box LogRenderer
 *
 * @author kelvin.wahome
 */
public class KeyValueRenderer implements LogRenderer<StringBuilder> {
    private static final KeyValueRenderer INSTANCE = new KeyValueRenderer();

    /**
     * Returns a new KeyValueRenderer instance if it does not exist or the existing instance
     * ig it does
     *
     * @return KeyValueRenderer
     */
    public static KeyValueRenderer getInstance() {
        return INSTANCE;
    }

    /**
     * ThreadLocal StringBuilder instance that can only read and written by the same thread
     */
    private final ThreadLocal<StringBuilder> threadLocalStringBuilder = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder();
        }

        @Override
        public StringBuilder get() {
            super.get().setLength(0);
            return super.get();
        }
    };

    @Override
    public StringBuilder start(Logger logger) {
        return threadLocalStringBuilder.get();
    }

    @Override
    public LogRenderer<StringBuilder> addMessage(Logger logger, StringBuilder stringBuilder, String message) {
        stringBuilder.append(message);
        return this;
    }

    @Override
    public LogRenderer<StringBuilder> addKeyValue(Logger logger, StringBuilder stringBuilder, String key, Object val) {
        stringBuilder.append(",").append(" ").append(key).append("=");
        String value = String.valueOf(val);
        value = value.replace("\"", "\\\"");
        if (!value.contains(" ")) {
            // no spaces in the value, no need to surround it with quotes
            stringBuilder.append(value);
        } else {
            stringBuilder.append("\"").append(value).append("\"");
        }
        return this;
    }

    @Override
    public String end(Logger logger, StringBuilder stringBuilder) {
        return stringBuilder.toString();
    }
}
