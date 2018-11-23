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

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.kwahome.sopa.utils.Helpers;

/**
 * Testing helper utilities
 *
 * @author kelvin.wahome
 */
public class HelperUtilsTests {
    private Object[] objectArray;
    private Map<String, Object> map = new HashMap<>();

    @Before
    public void setUp() {
        objectArray = new Object[]{"key1", "value1", "key2", "value2", "key3", "value3"};
        // would iterate over but since that is the logic being tested
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
    }

    @After
    public void tearDown() {
        objectArray = new Object[]{};
    }

    @Test
    public void mapToObjectArrayTest() {
        Assert.assertThat(objectArray, is(Helpers.mapToObjectArray(map)));
    }

    @Test
    public void objectArrayToMapTest() {
        Assert.assertThat(map, is(Helpers.objectArrayToMap(objectArray)));
    }

    @Test
    public void mergeObjectArraysTest() {
        Object[] objectArray1 = new Object[]{"key1", "value1", "key2", "value2", "key3", "value3"};
        Object[] objectArray2 = new Object[]{"key4", "value4"};
        Object[] mergedObjectArray = new Object[]{
                "key1", "value1", "key2", "value2", "key3", "value3", "key4", "value4"};

        Assert.assertThat(mergedObjectArray, is(Helpers.mergeObjectArrays(objectArray1, objectArray2)));

    }
}
