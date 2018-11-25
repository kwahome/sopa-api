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

package io.github.kwahome.sopa.interfaces;

/**
 * Core standard structured logger interface
 *
 * @author Kelvin Wahome
 */
public interface Logger {
    // slf4j wrapped APIs

    /**
     * Log a message at the ERROR {@link org.slf4j.event.Level}.
     *
     * @param message {@link String} message
     * @param params {@link Object []} params
     */
    void error(String message, Object...params);

    /**
     * Log a message at the WARN {@link org.slf4j.event.Level}.
     *
     * @param message {@link String} message
     * @param params {@link Object []} params
     */
    void warn(String message, Object...params);

    /**
     * Log a message at the INFO {@link org.slf4j.event.Level}.
     *
     * @param message {@link String} message
     * @param params {@link Object []} params
     */
    void info(String message, Object...params);

    /**
     * Log a message at the DEBUG {@link org.slf4j.event.Level}.
     *
     * @param message {@link String} message
     * @param params {@link Object []} params
     */
    void debug(String message, Object...params);

    /**
     * Log a message at the TRACE {@link org.slf4j.event.Level}.
     *
     * @param message {@link String} message
     * @param params {@link Object []} params
     */
    void trace(String message, Object...params);

    // logging level checks

    /**
     * Is the logger instance enabled for the ERROR {@link org.slf4j.event.Level}
     * @return boolean
     */
    boolean isErrorEnabled();

    /**
     * Is the logger instance enabled for the WARN {@link org.slf4j.event.Level}
     * @return boolean
     */
    boolean isWarnEnabled();

    /**
     * Is the logger instance enabled for the INFO {@link org.slf4j.event.Level}
     * @return boolean
     */
    boolean isInfoEnabled();

    /**
     * Is the logger instance enabled for the DEBUG {@link org.slf4j.event.Level}
     * @return boolean
     */
    boolean isDebugEnabled();

    /**
     * Is the logger instance enabled for the TRACE {@link org.slf4j.event.Level}
     * @return boolean
     */
    boolean isTraceEnabled();

    // context bind handling

    /**
     * Bind new context to the {@link Logger} instance & overwrite any existing.
     *
     * @param params {@link Object []} params
     */
    void newBind(Object...params);

    /**
     * Update context bound to the {@link Logger} instance.
     *
     * @param params {@link Object []} params
     */
    void bind(Object...params);

    /**
     * Remove from context bound to the {@link Logger} instance.
     *
     * @param params {@link Object []} params
     */
    void unbind(Object...params);
}
