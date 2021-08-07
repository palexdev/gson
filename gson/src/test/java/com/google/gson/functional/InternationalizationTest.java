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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Functional tests for internationalized strings.
 *
 * @author Inderjeet Singh
 */
public class InternationalizationTest {
  private Gson gson;

  @BeforeEach
  protected void setUp() throws Exception {
    gson = new Gson();
  }

  /*
  public void testStringsWithRawChineseCharactersSerialization() throws Exception {
    String target = "好好好";
    String json = gson.toJson(target);
    String expected = "\"\\u597d\\u597d\\u597d\"";
    assertEquals(expected, json);
  }
  */

  @Test
  public void testStringsWithRawChineseCharactersDeserialization() {
    String expected = "好好好";
    String json = "\"" + expected + "\"";
    String actual = gson.fromJson(json, String.class);
    assertEquals(expected, actual);
  }

  @Test
  public void testStringsWithUnicodeChineseCharactersSerialization() {
    String target = "\u597d\u597d\u597d";
    String json = gson.toJson(target);
    String expected = "\"\u597d\u597d\u597d\"";
    assertEquals(expected, json);
  }

  @Test
  public void testStringsWithUnicodeChineseCharactersDeserialization() {
    String expected = "\u597d\u597d\u597d";
    String json = "\"" + expected + "\"";
    String actual = gson.fromJson(json, String.class);
    assertEquals(expected, actual);
  }

  @Test
  public void testStringsWithUnicodeChineseCharactersEscapedDeserialization() {
    String actual = gson.fromJson("'\\u597d\\u597d\\u597d'", String.class);
    assertEquals("\u597d\u597d\u597d", actual);
  }
}
