/*
 * Copyright (C) 2009 Google Inc.
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
import com.google.gson.common.TestTypes.Base;
import com.google.gson.common.TestTypes.ClassWithBaseField;
import com.google.gson.common.TestTypes.Sub;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Functional Test exercising custom serialization only. When test applies to both
 * serialization and deserialization then add it to CustomTypeAdapterTest.
 *
 * @author Inderjeet Singh
 */
public class InstanceCreatorTest {

  @Test
  public void testInstanceCreatorReturnsBaseType() {
    Gson gson = new GsonBuilder()
      .registerTypeAdapter(Base.class, (InstanceCreator<Base>) type -> new Base())
      .create();
    String json = "{baseName:'BaseRevised',subName:'Sub'}";
    Base base = gson.fromJson(json, Base.class);
    assertEquals("BaseRevised", base.baseName);
  }

  @Test
  public void testInstanceCreatorReturnsSubTypeForTopLevelObject() {
    Gson gson = new GsonBuilder()
    .registerTypeAdapter(Base.class, (InstanceCreator<Base>) type -> new Sub())
    .create();

    String json = "{baseName:'Base',subName:'SubRevised'}";
    Base base = gson.fromJson(json, Base.class);
    assertTrue(base instanceof Sub);

    Sub sub = (Sub) base;
    assertNotEquals("SubRevised", sub.subName);
    assertEquals(Sub.SUB_NAME, sub.subName);
  }

  @Test
  public void testInstanceCreatorReturnsSubTypeForField() {
    Gson gson = new GsonBuilder()
    .registerTypeAdapter(Base.class, (InstanceCreator<Base>) type -> new Sub())
    .create();
    String json = "{base:{baseName:'Base',subName:'SubRevised'}}";
    ClassWithBaseField target = gson.fromJson(json, ClassWithBaseField.class);
    assertTrue(target.base instanceof Sub);
    assertEquals(Sub.SUB_NAME, ((Sub)target.base).subName);
  }

  // This regressed in Gson 2.0 and 2.1
  @Test
  public void testInstanceCreatorForCollectionType() {
    class SubArrayList<T> extends ArrayList<T> {}
    InstanceCreator<List<String>> listCreator = type -> new SubArrayList<>();
    Type listOfStringType = new TypeToken<List<String>>() {}.getType();
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(listOfStringType, listCreator)
        .create();
    List<String> list = gson.fromJson("[\"a\"]", listOfStringType);
    assertEquals(SubArrayList.class, list.getClass());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public void testInstanceCreatorForParametrizedType() {
    class SubTreeSet<T> extends TreeSet<T> {}
    InstanceCreator<SortedSet> sortedSetCreator = type -> new SubTreeSet();
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(SortedSet.class, sortedSetCreator)
        .create();

    Type sortedSetType = new TypeToken<SortedSet<String>>() {}.getType();
    SortedSet<String> set = gson.fromJson("[\"a\"]", sortedSetType);
    assertEquals(set.first(), "a");
    assertEquals(SubTreeSet.class, set.getClass());

    set = gson.fromJson("[\"b\"]", SortedSet.class);
    assertEquals(set.first(), "b");
    assertEquals(SubTreeSet.class, set.getClass());
  }
}
