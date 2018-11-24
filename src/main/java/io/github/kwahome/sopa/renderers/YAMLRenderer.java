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

package io.github.kwahome.sopa.renderers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import io.github.kwahome.sopa.StructLoggerConfig;
import io.github.kwahome.sopa.interfaces.LogRenderer;

/**
 * Basic YAML renderer.
 *
 * @author Kelvin Wahome
 */
public class YAMLRenderer implements LogRenderer<Map<String, String>> {

    /**
     * ThreadLocal {@link Yaml} instance that can only read and written by the same thread
     */
    private static final ThreadLocal<Yaml> YAML = new ThreadLocal<Yaml>() {
        // for thread safety
        @Override
        protected Yaml initialValue() {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
            return new Yaml(options);
        }
    };

    private static final YAMLRenderer INSTANCE = new YAMLRenderer();

    /**
     * Returns a new {@link YAMLRenderer} instance if it does not exist or the existing instance
     * ig it does
     *
     * @return {@link YAMLRenderer}
     */
    public static YAMLRenderer getInstance() {
        return INSTANCE;
    }

    @Override
    public final Map<String, String> start(Logger logger) {
        return new HashMap<>(10);
    }

    @Override
    public final LogRenderer<Map<String, String>> addMessage(
            Logger logger, Map<String, String> builderObject, String message) {
        builderObject.put("message", message);
        return this;
    }

    @Override
    public final LogRenderer<Map<String, String>> addKeyValue(
            Logger logger, Map<String, String> builderObject, String key, Object value) {
        if ("message".equals(key)) {
            key = "message1";
            logger.warn(String.format("%s key `message` renamed to `%s` to avoid overriding default log message field.",
                    StructLoggerConfig.getSopaLoggerTag(), key));
        }
        builderObject.put(key, String.valueOf(value));
        return this;
    }

    @Override
    public final String end(Logger logger, Map<String, String> builderObject) {
        return YAML.get().dump(builderObject).trim();
    }
}
