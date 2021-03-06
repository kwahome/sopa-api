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

import io.github.kwahome.sopa.interfaces.LoggableObject;

/**
 * Generic loggable object implementation.
 *
 * For use in dynamically adding logger contexts
 *
 * @author Kelvin Wahome
 */
public class GenericLoggableObject implements LoggableObject {
    private Object[] loggableObjectArray = new Object[]{};

    GenericLoggableObject(){}

    /**
     * Constructor
     *
     * @param loggableObjectArray {@link Object[]}
     */
    public GenericLoggableObject(Object[] loggableObjectArray) {
        this.loggableObjectArray = loggableObjectArray;
    }

    /**
     * Getter
     *
     * @return {@link Object[]}
     */
    public Object[] getLoggableObjectArray() {
        return loggableObjectArray;
    }

    /**
     * Setter
     *
     * @param loggableObjectArray {@link Object[]}
     */
    public void setLoggableObjectArray(Object[] loggableObjectArray) {
        this.loggableObjectArray = loggableObjectArray;
    }

    /**
     * {@link LoggableObject} loggableObject method implementation.
     *
     * @return {@link Object[]}
     */
    @Override
    public Object[] loggableObject() {
        return getLoggableObjectArray();
    }
}
