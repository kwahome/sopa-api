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

package io.github.kwahome.structlog4j.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

/**
 * Helper utilities
 *
 * @author kelvin.wahome
 */
@UtilityClass
public class Helpers {
    /**
     * Takes in an array of objects with key-value pairs in alternate indices:
     *
     *      ["key1", "value1", "key2", "value2", "key3", "value3"]
     *
     * Iterates through the array picking the key and values from alternate indices
     * and putting them into a Map<String, Object>
     *
     * @param objectArray "An array of objects forming a key-value pair"
     * @return Map<String, Object>
     */
    public static Map<String, Object> objectArrayToMap(Object[] objectArray) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < objectArray.length; i = i + 2) {
            map.put((String) Arrays.asList(objectArray).get(i),
                    Arrays.asList(objectArray).get(i + 1));
        }
        return map;
    }

    /**
     * Takes in a map of String keys and Object values:
     *
     *      Map<String, Object> map
     *
     * Iterates through the map keySet and values arrays adding the keys & values to
     * alternate indices in an object array to yield key-value pairs e.g:
     *
     *      ["key1", "value1", "key2", "value2", "key3", "value3"]
     *
     * @param map "A map of String keys and Object values"
     * @return Object[]
     */
    public static Object[] mapToObjectArray(Map<String, Object> map) {
        ArrayList<Object> objectArrayList = new ArrayList<>();
        for (int i = 0; i < map.size(); i++) {
            objectArrayList.add(map.keySet().toArray()[i]);
            objectArrayList.add(map.values().toArray()[i]);
        }
        return objectArrayList.toArray();
    }

    /**
     * Merges/concatenates several arrays into one array
     *
     * @param arrays "Object[] arrays"
     * @return Object[]
     */
    public static Object[] mergeObjectArrays(Object[]...arrays) {
        return Stream.of(arrays).flatMap(Stream::of).toArray();
    }
}
