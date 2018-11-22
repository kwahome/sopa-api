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

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import io.github.kwahome.sopa.interfaces.LogRenderer;

/**
 * Basic JSON renderer.
 *
 * Formats using Glassfish JSON library as it has minimal dependencies.
 *
 * @author kelvin.wahome
 */
public class JSONRenderer implements LogRenderer<JsonObjectBuilder> {
    private static final String MESSAGE_KEY_NAME = "message";
    private static final String CUSTOM_MESSAGE_KEY_NAME = "custom_message";

    private static final JSONRenderer INSTANCE = new JSONRenderer();
    public static JSONRenderer getInstance() {
        return INSTANCE;
    }

    @Override
    public final JsonObjectBuilder start(org.slf4j.Logger log) {
        return Json.createObjectBuilder();
    }

    @Override
    public final LogRenderer<JsonObjectBuilder> addMessage(
            org.slf4j.Logger log, JsonObjectBuilder jsonObjectBuilder, String message) {
        jsonObjectBuilder.add(MESSAGE_KEY_NAME, message);
        return this;
    }

    @Override
    public final LogRenderer<JsonObjectBuilder> addKeyValue(
            org.slf4j.Logger log, JsonObjectBuilder jsonObjectBuilder, String key, Object value) {
        // avoid overriding the "message" field
        if (key.equals(MESSAGE_KEY_NAME)) {
            key = CUSTOM_MESSAGE_KEY_NAME;
            log.warn(String.format(
                    "Key %s renamed to %s to avoid overriding default JSON message field.",
                    MESSAGE_KEY_NAME, CUSTOM_MESSAGE_KEY_NAME));
        }

        // different methods per type
        if (value == null) {
            jsonObjectBuilder.addNull(key);
        } else if (value instanceof Boolean) {
            jsonObjectBuilder.add(key, (boolean) value);
        } else if (value instanceof Integer || value instanceof Short) {
            jsonObjectBuilder.add(key, (int) value);
        } else if (value instanceof Long) {
            jsonObjectBuilder.add(key, (long) value);
        } else if (value instanceof Double || value instanceof Float) {
            jsonObjectBuilder.add(key, (double) value);
        } else {
            jsonObjectBuilder.add(key, String.valueOf(value));
        }
        return this;
    }

    @Override
    public final String end(org.slf4j.Logger log, JsonObjectBuilder jsonObjectBuilder) {
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
            jsonWriter.writeObject(jsonObjectBuilder.build());
        }
        return stringWriter.toString();
    }
}
