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

package com.google.gson.functional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.annotations.Expose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the regarding functional "@Expose" type tests.
 *
 * @author Joel Leitch
 */
public class ExposeFieldsTest {

  private Gson gson;

  @BeforeEach
  protected void setUp() throws Exception {
    gson = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .registerTypeAdapter(SomeInterface.class, new SomeInterfaceInstanceCreator())
        .create();
  }

  @Test
  public void testNullExposeFieldSerialization() {
    ClassWithExposedFields object = new ClassWithExposedFields(null, 1);
    String json = gson.toJson(object);

    assertEquals(object.getExpectedJson(), json);
  }

  @Test
  public void testArrayWithOneNullExposeFieldObjectSerialization() {
    ClassWithExposedFields object1 = new ClassWithExposedFields(1, 1);
    ClassWithExposedFields object2 = new ClassWithExposedFields(null, 1);
    ClassWithExposedFields object3 = new ClassWithExposedFields(2, 2);
    ClassWithExposedFields[] objects = { object1, object2, object3 };

    String json = gson.toJson(objects);
    String expected = new StringBuilder()
        .append('[').append(object1.getExpectedJson()).append(',')
        .append(object2.getExpectedJson()).append(',')
        .append(object3.getExpectedJson()).append(']')
        .toString();

    assertEquals(expected, json);
  }

  @Test
  public void testExposeAnnotationSerialization() {
    ClassWithExposedFields target = new ClassWithExposedFields(1, 2);
    assertEquals(target.getExpectedJson(), gson.toJson(target));
  }

  @Test
  public void testExposeAnnotationDeserialization() {
    String json = "{a:3,b:4,d:20.0}";
    ClassWithExposedFields target = gson.fromJson(json, ClassWithExposedFields.class);

    assertEquals(3, (int) target.a);
    assertNull(target.b);
    assertNotEquals(20, target.d, 0.0);
  }

  @Test
  public void testNoExposedFieldSerialization() {
    ClassWithNoExposedFields obj = new ClassWithNoExposedFields();
    String json = gson.toJson(obj);

    assertEquals("{}", json);
  }

  @Test
  public void testNoExposedFieldDeserialization() {
    String json = "{a:4,b:5}";
    ClassWithNoExposedFields obj = gson.fromJson(json, ClassWithNoExposedFields.class);

    assertEquals(0, obj.a);
    assertEquals(1, obj.b);
  }

  @Test
  public void testExposedInterfaceFieldSerialization() {
    String expected = "{\"interfaceField\":{}}";
    ClassWithInterfaceField target = new ClassWithInterfaceField(new SomeObject());
    String actual = gson.toJson(target);
    
    assertEquals(expected, actual);
  }

  @Test
  public void testExposedInterfaceFieldDeserialization() {
    String json = "{\"interfaceField\":{}}";
    ClassWithInterfaceField obj = gson.fromJson(json, ClassWithInterfaceField.class);

    assertNotNull(obj.interfaceField);
  }

  private static class ClassWithExposedFields {
    @Expose private final Integer a;
    private final Integer b;
    @Expose(serialize = false) final long c;
    @Expose(deserialize = false) final double d;
    @Expose(serialize = false, deserialize = false) final char e;

    public ClassWithExposedFields(Integer a, Integer b) {
      this(a, b, 1L, 2.0, 'a');
    }
    public ClassWithExposedFields(Integer a, Integer b, long c, double d, char e) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
      this.e = e;
    }

    public String getExpectedJson() {
      StringBuilder sb = new StringBuilder("{");
      if (a != null) {
        sb.append("\"a\":").append(a).append(",");
      }
      sb.append("\"d\":").append(d);
      sb.append("}");
      return sb.toString();
    }
  }

  private static class ClassWithNoExposedFields {
    private final int a = 0;
    private final int b = 1;
  }
  
  private interface SomeInterface {
    // Empty interface
  }
  
  private static class SomeObject implements SomeInterface {
    // Do nothing
  }
  
  private static class SomeInterfaceInstanceCreator implements InstanceCreator<SomeInterface> {
    @Override public SomeInterface createInstance(Type type) {
      return new SomeObject();
    }
  }
  
  private static class ClassWithInterfaceField {
    @Expose
    private final SomeInterface interfaceField;

    public ClassWithInterfaceField(SomeInterface interfaceField) {
      this.interfaceField = interfaceField;
    }
  }  
}
