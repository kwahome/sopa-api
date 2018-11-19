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

/**
 * Test utilities
 *
 * @author kelvin.wahome
 */
public class TestUtils {
    /**
     * Tries to format to the key-value structlogger standard for testing purposes.
     *
     * @param message "String message"
     * @param params "Array of objects"
     * @return String
     */
    public static String formatAsStructLogEntry(String message, Object...params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(message);
        int size = params.length;
        size = (size % 2 != 0 ) ? size - 1 : size; // to take care of odd params as structlogger does
        for(int i = 0; i < size; i = i + 2) {
            String key = (String) params[i];
            String value = params[i + 1] == null ? "null" : params[i + 1].toString();
            stringBuilder.append(StructLog4JConfig.getLogEntriesSeparator()).append(" ").append(key).append("=");
            if (!value.contains(" ")) {
                // no spaces in the value, no need to surround it with quotes
                stringBuilder.append(value);
            } else {
                stringBuilder.append("\"").append(value).append("\"");
            }
        }
        return stringBuilder.toString();
    }
}
