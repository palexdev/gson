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

package com.google.gson;

import com.google.gson.internal.Excluder;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for GsonBuilder.EXCLUDE_INNER_CLASSES.
 *
 * @author Joel Leitch
 */
public class InnerClassExclusionStrategyTest {
  public InnerClass innerClass = new InnerClass();
  public StaticNestedClass staticNestedClass = new StaticNestedClass();
  private final Excluder excluder = Excluder.DEFAULT.disableInnerClassSerialization();

  @Test
  public void testExcludeInnerClassObject() {
    Class<?> clazz = innerClass.getClass();
    assertTrue(excluder.excludeClass(clazz, true));
  }

  @Test
  public void testExcludeInnerClassField() throws Exception {
    Field f = getClass().getField("innerClass");
    assertTrue(excluder.excludeField(f, true));
  }

  @Test
  public void testIncludeStaticNestedClassObject() {
    Class<?> clazz = staticNestedClass.getClass();
    assertFalse(excluder.excludeClass(clazz, true));
  }

  @Test
  public void testIncludeStaticNestedClassField() throws Exception {
    Field f = getClass().getField("staticNestedClass");
    assertFalse(excluder.excludeField(f, true));
  }

  static class InnerClass { }
  static class StaticNestedClass { }
}
