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

import com.google.gson.*;
import com.google.gson.common.TestTypes.BagOfPrimitives;
import com.google.gson.common.TestTypes.Nested;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Functional tests for that use JsonParser and related Gson methods
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class JsonParserTest {
  private Gson gson;

  @BeforeEach
  protected void setUp() throws Exception {
    gson = new Gson();
  }

  @Test
  public void testParseInvalidJson() {
    try {
      gson.fromJson("[[]", Object[].class);
      fail();
    } catch (JsonSyntaxException expected) { }
  }

  @Test
  public void testDeserializingCustomTree() {
    JsonObject obj = new JsonObject();
    obj.addProperty("stringValue", "foo");
    obj.addProperty("intValue", 11);
    BagOfPrimitives target = gson.fromJson(obj, BagOfPrimitives.class);
    assertEquals(11, target.intValue);
    assertEquals("foo", target.stringValue);
  }

  @Test
  public void testBadTypeForDeserializingCustomTree() {
    JsonObject obj = new JsonObject();
    obj.addProperty("stringValue", "foo");
    obj.addProperty("intValue", 11);
    JsonArray array = new JsonArray();
    array.add(obj);
    try {
      gson.fromJson(array, BagOfPrimitives.class);
      fail("BagOfPrimitives is not an array");
    } catch (JsonParseException expected) { }
  }

  @Test
  public void testBadFieldTypeForCustomDeserializerCustomTree() {
    JsonArray array = new JsonArray();
    array.add(new JsonPrimitive("blah"));
    JsonObject obj = new JsonObject();
    obj.addProperty("stringValue", "foo");
    obj.addProperty("intValue", 11);
    obj.add("longValue", array);

    try {
      gson.fromJson(obj, BagOfPrimitives.class);
      fail("BagOfPrimitives is not an array");
    } catch (JsonParseException expected) { }
  }

  @Test
  public void testBadFieldTypeForDeserializingCustomTree() {
    JsonArray array = new JsonArray();
    array.add(new JsonPrimitive("blah"));
    JsonObject primitive1 = new JsonObject();
    primitive1.addProperty("string", "foo");
    primitive1.addProperty("intValue", 11);

    JsonObject obj = new JsonObject();
    obj.add("primitive1", primitive1);
    obj.add("primitive2", array);

    try {
      gson.fromJson(obj, Nested.class);
      fail("Nested has field BagOfPrimitives which is not an array");
    } catch (JsonParseException expected) { }
  }

  @Test
  public void testChangingCustomTreeAndDeserializing() {
    StringReader json =
      new StringReader("{'stringValue':'no message','intValue':10,'longValue':20}");
    JsonObject obj = (JsonObject) JsonParser.parseReader(json);
    obj.remove("stringValue");
    obj.addProperty("stringValue", "fooBar");
    BagOfPrimitives target = gson.fromJson(obj, BagOfPrimitives.class);
    assertEquals(10, target.intValue);
    assertEquals(20, target.longValue);
    assertEquals("fooBar", target.stringValue);
  }

  @Test
  public void testExtraCommasInArrays() {
    Type type = new TypeToken<List<String>>() {}.getType();
    assertEquals(Arrays.asList("a", null, "b", null, null), gson.fromJson("[a,,b,,]", type));
    assertEquals(Arrays.asList(null, null), gson.fromJson("[,]", type));
    assertEquals(Arrays.asList("a", null), gson.fromJson("[a,]", type));
  }

  @Test
  public void testExtraCommasInMaps() {
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    try {
      gson.fromJson("{a:b,}", type);
      fail();
    } catch (JsonSyntaxException expected) {
    }
  }
}
