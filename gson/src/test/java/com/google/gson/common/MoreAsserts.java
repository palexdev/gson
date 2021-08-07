/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.common;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class MoreAsserts {

  /**
   * Asserts that the specified {@code value} is not present in {@code collection}
   * @param collection the collection to look into
   * @param value the value that needs to be checked for presence
   */
  public static <T> void assertContains(Collection<T> collection, T value) {
    for (T entry : collection) {
      if (entry.equals(value)) {
        return;
      }
    }
    fail(value + " not present in " + collection);
  }

  public static void assertEqualsAndHashCode(Object a, Object b) {
    assertEquals(a, b);
    assertEquals(b, a);
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(null, a);
    assertNotEquals(a, new Object());
  }
}
