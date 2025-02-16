/*
 * Copyright (C) 2015 Google Inc.
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

package com.google.gson.functional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Functional test for Json serialization and deserialization for classes in java.util.concurrent.atomic
 */
public class JavaUtilConcurrentAtomicTest {
  private Gson gson;

  @BeforeEach
  protected void setUp() throws Exception {
    gson = new Gson();
  }

  @Test
  public void testAtomicBoolean() {
    AtomicBoolean target = gson.fromJson("true", AtomicBoolean.class);
    assertTrue(target.get());
    String json = gson.toJson(target);
    assertEquals("true", json);
  }

  @Test
  public void testAtomicInteger() {
    AtomicInteger target = gson.fromJson("10", AtomicInteger.class);
    assertEquals(10, target.get());
    String json = gson.toJson(target);
    assertEquals("10", json);
  }

  @Test
  public void testAtomicLong() {
    AtomicLong target = gson.fromJson("10", AtomicLong.class);
    assertEquals(10, target.get());
    String json = gson.toJson(target);
    assertEquals("10", json);
  }

  @Test
  public void testAtomicLongWithStringSerializationPolicy() {
    Gson gson = new GsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .create();
    AtomicLongHolder target = gson.fromJson("{'value':'10'}", AtomicLongHolder.class);
    assertEquals(10, target.value.get());
    String json = gson.toJson(target);
    assertEquals("{\"value\":\"10\"}", json);
  }

  @Test
  public void testAtomicIntegerArray() {
    AtomicIntegerArray target = gson.fromJson("[10, 13, 14]", AtomicIntegerArray.class);
    assertEquals(3, target.length());
    assertEquals(10, target.get(0));
    assertEquals(13, target.get(1));
    assertEquals(14, target.get(2));
    String json = gson.toJson(target);
    assertEquals("[10,13,14]", json);
  }

  @Test
  public void testAtomicLongArray() {
    AtomicLongArray target = gson.fromJson("[10, 13, 14]", AtomicLongArray.class);
    assertEquals(3, target.length());
    assertEquals(10, target.get(0));
    assertEquals(13, target.get(1));
    assertEquals(14, target.get(2));
    String json = gson.toJson(target);
    assertEquals("[10,13,14]", json);
  }

  @Test
  public void testAtomicLongArrayWithStringSerializationPolicy() {
    Gson gson = new GsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .create();
    AtomicLongArray target = gson.fromJson("['10', '13', '14']", AtomicLongArray.class);
    assertEquals(3, target.length());
    assertEquals(10, target.get(0));
    assertEquals(13, target.get(1));
    assertEquals(14, target.get(2));
    String json = gson.toJson(target);
    assertEquals("[\"10\",\"13\",\"14\"]", json);
  }

  private static class AtomicLongHolder {
    AtomicLong value;
  }
}
