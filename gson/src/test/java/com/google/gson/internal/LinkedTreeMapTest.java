/*
 * Copyright (C) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.internal;

import com.google.gson.common.MoreAsserts;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public final class LinkedTreeMapTest {

  @Test
  public void testIterationOrder() {
    LinkedTreeMap<String, String> map = new LinkedTreeMap<>();
    map.put("a", "android");
    map.put("c", "cola");
    map.put("b", "bbq");
    assertIterationOrder(map.keySet(), "a", "c", "b");
    assertIterationOrder(map.values(), "android", "cola", "bbq");
  }

  @Test
  public void testRemoveRootDoesNotDoubleUnlink() {
    LinkedTreeMap<String, String> map = new LinkedTreeMap<>();
    map.put("a", "android");
    map.put("c", "cola");
    map.put("b", "bbq");
    Iterator<Map.Entry<String,String>> it = map.entrySet().iterator();
    it.next();
    it.next();
    it.next();
    it.remove();
    assertIterationOrder(map.keySet(), "a", "c");
  }

  @Test
  public void testPutNullKeyFails() {
    LinkedTreeMap<String, String> map = new LinkedTreeMap<>();
    try {
      map.put(null, "android");
      fail();
    } catch (NullPointerException expected) {
    }
  }

  @Test
  public void testPutNonComparableKeyFails() {
    LinkedTreeMap<Object, String> map = new LinkedTreeMap<>();
    try {
      map.put(new Object(), "android");
      fail();
    } catch (ClassCastException expected) {}
  }

  @Test
  public void testContainsNonComparableKeyReturnsFalse() {
    LinkedTreeMap<String, String> map = new LinkedTreeMap<>();
    map.put("a", "android");
    assertFalse(map.containsKey(new Object()));
  }

  @Test
  public void testContainsNullKeyIsAlwaysFalse() {
    LinkedTreeMap<String, String> map = new LinkedTreeMap<>();
    map.put("a", "android");
    assertFalse(map.containsKey(null));
  }

  @Test
  public void testPutOverrides() {
    LinkedTreeMap<String, String> map = new LinkedTreeMap<>();
    assertNull(map.put("d", "donut"));
    assertNull(map.put("e", "eclair"));
    assertNull(map.put("f", "froyo"));
    assertEquals(3, map.size());

    assertEquals("donut", map.get("d"));
    assertEquals("donut", map.put("d", "done"));
    assertEquals(3, map.size());
  }

  @Test
  public void testEmptyStringValues() {
    LinkedTreeMap<String, String> map = new LinkedTreeMap<>();
    map.put("a", "");
    assertTrue(map.containsKey("a"));
    assertEquals("", map.get("a"));
  }

  @Test
  public void testLargeSetOfRandomKeys() {
    Random random = new Random(1367593214724L);
    LinkedTreeMap<String, String> map = new LinkedTreeMap<>();
    String[] keys = new String[1000];
    for (int i = 0; i < keys.length; i++) {
      keys[i] = Integer.toString(Math.abs(random.nextInt()), 36) + "-" + i;
      map.put(keys[i], "" + i);
    }

    for (int i = 0; i < keys.length; i++) {
      String key = keys[i];
      assertTrue(map.containsKey(key));
      assertEquals("" + i, map.get(key));
    }
  }

  @Test
  public void testClear() {
    LinkedTreeMap<String, String> map = new LinkedTreeMap<>();
    map.put("a", "android");
    map.put("c", "cola");
    map.put("b", "bbq");
    map.clear();
    assertIterationOrder(map.keySet());
    assertEquals(0, map.size());
  }

  @Test
  public void testEqualsAndHashCode() {
    LinkedTreeMap<String, Integer> map1 = new LinkedTreeMap<>();
    map1.put("A", 1);
    map1.put("B", 2);
    map1.put("C", 3);
    map1.put("D", 4);

    LinkedTreeMap<String, Integer> map2 = new LinkedTreeMap<>();
    map2.put("C", 3);
    map2.put("B", 2);
    map2.put("D", 4);
    map2.put("A", 1);

    MoreAsserts.assertEqualsAndHashCode(map1, map2);
  }

  private <T> void assertIterationOrder(Iterable<T> actual, T... expected) {
    ArrayList<T> actualList = new ArrayList<>();
    for (T t : actual) {
      actualList.add(t);
    }
    assertEquals(Arrays.asList(expected), actualList);
  }
}
