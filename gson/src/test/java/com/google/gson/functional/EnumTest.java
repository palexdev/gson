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
import com.google.gson.annotations.SerializedName;
import com.google.gson.common.MoreAsserts;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Functional tests for Java 5.0 enums.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class EnumTest {

  private Gson gson;

  @BeforeEach
  protected void setUp() throws Exception {
    gson = new Gson();
  }

  @Test
  public void testTopLevelEnumSerialization() {
    String result = gson.toJson(MyEnum.VALUE1);
    assertEquals('"' + MyEnum.VALUE1.toString() + '"', result);
  }

  @Test
  public void testTopLevelEnumDeserialization() {
    MyEnum result = gson.fromJson('"' + MyEnum.VALUE1.toString() + '"', MyEnum.class);
    assertEquals(MyEnum.VALUE1, result);
  }

  @Test
  public void testCollectionOfEnumsSerialization() {
    Type type = new TypeToken<Collection<MyEnum>>() {}.getType();
    Collection<MyEnum> target = new ArrayList<>();
    target.add(MyEnum.VALUE1);
    target.add(MyEnum.VALUE2);
    String expectedJson = "[\"VALUE1\",\"VALUE2\"]";
    String actualJson = gson.toJson(target);
    assertEquals(expectedJson, actualJson);
    actualJson = gson.toJson(target, type);
    assertEquals(expectedJson, actualJson);
  }

  @Test
  public void testCollectionOfEnumsDeserialization() {
    Type type = new TypeToken<Collection<MyEnum>>() {}.getType();
    String json = "[\"VALUE1\",\"VALUE2\"]";
    Collection<MyEnum> target = gson.fromJson(json, type);
    MoreAsserts.assertContains(target, MyEnum.VALUE1);
    MoreAsserts.assertContains(target, MyEnum.VALUE2);
  }

  @Test
  public void testClassWithEnumFieldSerialization() {
    ClassWithEnumFields target = new ClassWithEnumFields();
    assertEquals(target.getExpectedJson(), gson.toJson(target));
  }

  @Test
  public void testClassWithEnumFieldDeserialization() {
    String json = "{value1:'VALUE1',value2:'VALUE2'}";
    ClassWithEnumFields target = gson.fromJson(json, ClassWithEnumFields.class);
    assertEquals(MyEnum.VALUE1,target.value1);
    assertEquals(MyEnum.VALUE2,target.value2);
  }

  private enum MyEnum {
    VALUE1, VALUE2
  }

  private static class ClassWithEnumFields {
    private final MyEnum value1 = MyEnum.VALUE1;
    private final MyEnum value2 = MyEnum.VALUE2;
    public String getExpectedJson() {
      return "{\"value1\":\"" + value1 + "\",\"value2\":\"" + value2 + "\"}";
    }
  }

  /**
   * Test for issue 226.
   */
  @Test
  public void testEnumSubclass() {
    assertNotSame(Roshambo.class, Roshambo.ROCK.getClass());
    assertEquals("\"ROCK\"", gson.toJson(Roshambo.ROCK));
    assertEquals("[\"ROCK\",\"PAPER\",\"SCISSORS\"]", gson.toJson(EnumSet.allOf(Roshambo.class)));
    assertEquals(Roshambo.ROCK, gson.fromJson("\"ROCK\"", Roshambo.class));
    assertEquals(EnumSet.allOf(Roshambo.class),
        gson.fromJson("[\"ROCK\",\"PAPER\",\"SCISSORS\"]", new TypeToken<Set<Roshambo>>() {}.getType()));
  }

  @Test
  public void testEnumSubclassWithRegisteredTypeAdapter() {
    gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(Roshambo.class, new MyEnumTypeAdapter())
        .create();
    assertNotSame(Roshambo.class, Roshambo.ROCK.getClass());
    assertEquals("\"123ROCK\"", gson.toJson(Roshambo.ROCK));
    assertEquals("[\"123ROCK\",\"123PAPER\",\"123SCISSORS\"]", gson.toJson(EnumSet.allOf(Roshambo.class)));
    assertEquals(Roshambo.ROCK, gson.fromJson("\"123ROCK\"", Roshambo.class));
    assertEquals(EnumSet.allOf(Roshambo.class),
        gson.fromJson("[\"123ROCK\",\"123PAPER\",\"123SCISSORS\"]", new TypeToken<Set<Roshambo>>() {}.getType()));
  }

  @Test
  public void testEnumSubclassAsParameterizedType() {
    Collection<Roshambo> list = new ArrayList<>();
    list.add(Roshambo.ROCK);
    list.add(Roshambo.PAPER);

    String json = gson.toJson(list);
    assertEquals("[\"ROCK\",\"PAPER\"]", json);

    Type collectionType = new TypeToken<Collection<Roshambo>>() {}.getType();
    Collection<Roshambo> actualJsonList = gson.fromJson(json, collectionType);
    MoreAsserts.assertContains(actualJsonList, Roshambo.ROCK);
    MoreAsserts.assertContains(actualJsonList, Roshambo.PAPER);
  }
  @Test

  public void testEnumCaseMapping() {
    assertEquals(Gender.MALE, gson.fromJson("\"boy\"", Gender.class));
    assertEquals("\"boy\"", gson.toJson(Gender.MALE, Gender.class));
  }

  @Test
  public void testEnumSet() {
    EnumSet<Roshambo> foo = EnumSet.of(Roshambo.ROCK, Roshambo.PAPER);
    String json = gson.toJson(foo);
    Type type = new TypeToken<EnumSet<Roshambo>>() {}.getType();
    EnumSet<Roshambo> bar = gson.fromJson(json, type);
    assertTrue(bar.contains(Roshambo.ROCK));
    assertTrue(bar.contains(Roshambo.PAPER));
    assertFalse(bar.contains(Roshambo.SCISSORS));
  }

  public enum Roshambo {
    ROCK {
      @Override Roshambo defeats() {
        return SCISSORS;
      }
    },
    PAPER {
      @Override Roshambo defeats() {
        return ROCK;
      }
    },
    SCISSORS {
      @Override Roshambo defeats() {
        return PAPER;
      }
    };

    abstract Roshambo defeats();
  }

  private static class MyEnumTypeAdapter
      implements JsonSerializer<Roshambo>, JsonDeserializer<Roshambo> {
    @Override public JsonElement serialize(Roshambo src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive("123" + src.name());
    }

    @Override public Roshambo deserialize(JsonElement json, Type classOfT, JsonDeserializationContext context)
        throws JsonParseException {
      return Roshambo.valueOf(json.getAsString().substring(3));
    }
  }

  public enum Gender {
    @SerializedName("boy")
    MALE,

    @SerializedName("girl")
    FEMALE
  }

  @Test
  public void testEnumClassWithFields() {
	  assertEquals("\"RED\"", gson.toJson(Color.RED));
	  assertEquals("red", gson.fromJson("RED", Color.class).value);
  }

  public enum Color {
	  RED("red", 1), BLUE("blue", 2), GREEN("green", 3);
	  String value;
	  int index;
	  Color(String value, int index) {
		  this.value = value;
		  this.index = index;
	  }
  }
}
