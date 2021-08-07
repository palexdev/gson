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

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link GsonBuilder}.
 *
 * @author Inderjeet Singh
 */
public class GsonBuilderTest {
  private static final TypeAdapter<Object> NULL_TYPE_ADAPTER = new TypeAdapter<>() {
    @Override
    public void write(JsonWriter out, Object value) {
      throw new AssertionError();
    }

    @Override
    public Object read(JsonReader in) {
      throw new AssertionError();
    }
  };

  @Test
  public void testCreatingMoreThanOnce() {
    GsonBuilder builder = new GsonBuilder();
    builder.create();
    builder.create();
  }

  @Test
  public void testExcludeFieldsWithModifiers() {
    Gson gson = new GsonBuilder()
        .excludeFieldsWithModifiers(Modifier.VOLATILE, Modifier.PRIVATE)
        .create();
    assertEquals("{\"d\":\"d\"}", gson.toJson(new HasModifiers()));
  }

  @Test
  public void testRegisterTypeAdapterForCoreType() {
    Type[] types = {
        byte.class,
        int.class,
        double.class,
        Short.class,
        Long.class,
        String.class,
    };
    for (Type type : types) {
      new GsonBuilder().registerTypeAdapter(type, NULL_TYPE_ADAPTER);
    }
  }

  @SuppressWarnings("unused")
  static class HasModifiers {
    private final String a = "a";
    volatile String b = "b";
    private final String c = "c";
    String d = "d";
  }

  @Test
  public void testTransientFieldExclusion() {
    Gson gson = new GsonBuilder()
        .excludeFieldsWithModifiers()
        .create();
    assertEquals("{\"a\":\"a\"}", gson.toJson(new HasTransients()));
  }

  static class HasTransients {
    transient String a = "a";
  }
}
