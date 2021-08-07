/*
 * Copyright (C) 2011 Google Inc.
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

package com.google.gson;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ObjectTypeAdapterTest {
    private final Gson gson = new GsonBuilder().create();
    private final TypeAdapter<Object> adapter = gson.getAdapter(Object.class);

    @Test
    public void testDeserialize() throws Exception {
        Map<?, ?> map = (Map<?, ?>) adapter.fromJson("{\"a\":5,\"b\":[1,2,null],\"c\":{\"x\":\"y\"}," +
                "\"d\":9223372036854775807}");
        assertEquals(5L, map.get("a"));
        assertEquals(Arrays.asList(1L, 2L, null), map.get("b"));
        assertEquals(Collections.singletonMap("x", "y"), map.get("c"));
        assertEquals(Long.MAX_VALUE, map.get("d"));
        assertEquals(4, map.size());
    }

    @Test
    public void testSerialize() {
        Object object = new RuntimeType();
        assertEquals("{'a':5,'b':[1,2,null]}", adapter.toJson(object).replace("\"", "'"));
    }

    @Test
    public void testSerializeNullValue() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", null);
        assertEquals("{'a':null}", adapter.toJson(map).replace('"', '\''));
    }

    @Test
    public void testDeserializeNullValue() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", null);
        assertEquals(map, adapter.fromJson("{\"a\":null}"));
    }

    @Test
    public void testSerializeObject() {
        assertEquals("{}", adapter.toJson(new Object()));
    }

    @SuppressWarnings("unused")
    private static class RuntimeType {
        Object a = 5;
        Object b = Arrays.asList(1, 2, null);
    }
}
