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
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Functional tests for Json serialization and deserialization of collections.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class CollectionTest {
    private Gson gson;

    @BeforeEach
    protected void setUp() throws Exception {
        gson = new Gson();
    }

    @Test
    public void testTopLevelCollectionOfIntegersSerialization() {
        Collection<Integer> target = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Type targetType = new TypeToken<Collection<Integer>>() {
        }.getType();
        String json = gson.toJson(target, targetType);
        assertEquals("[1,2,3,4,5,6,7,8,9]", json);
    }

    @Test
    public void testTopLevelCollectionOfIntegersDeserialization() {
        String json = "[0,1,2,3,4,5,6,7,8,9]";
        Type collectionType = new TypeToken<Collection<Integer>>() {
        }.getType();
        Collection<Integer> target = gson.fromJson(json, collectionType);
        int[] expected = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        assertArrayEquals(expected, toIntArray(target));
    }

    @Test
    public void testTopLevelListOfIntegerCollectionsDeserialization() {
        String json = "[[1,2,3],[4,5,6],[7,8,9]]";
        Type collectionType = new TypeToken<Collection<Collection<Integer>>>() {
        }.getType();
        List<Collection<Integer>> target = gson.fromJson(json, collectionType);
        int[][] expected = new int[3][3];
        for (int i = 0; i < 3; ++i) {
            int start = (3 * i) + 1;
            for (int j = 0; j < 3; ++j) {
                expected[i][j] = start + j;
            }
        }

        for (int i = 0; i < 3; i++) {
            assertArrayEquals(expected[i], toIntArray(target.get(i)));
        }
    }

    @Test
    public void testLinkedListSerialization() {
        List<String> list = new LinkedList<>();
        list.add("a1");
        list.add("a2");
        Type linkedListType = new TypeToken<LinkedList<String>>() {
        }.getType();
        String json = gson.toJson(list, linkedListType);
        assertTrue(json.contains("a1"));
        assertTrue(json.contains("a2"));
    }

    @Test
    public void testLinkedListDeserialization() {
        String json = "['a1','a2']";
        Type linkedListType = new TypeToken<LinkedList<String>>() {
        }.getType();
        List<String> list = gson.fromJson(json, linkedListType);
        assertEquals("a1", list.get(0));
        assertEquals("a2", list.get(1));
    }

    @Test
    public void testQueueSerialization() {
        Queue<String> queue = new LinkedList<>();
        queue.add("a1");
        queue.add("a2");
        Type queueType = new TypeToken<Queue<String>>() {
        }.getType();
        String json = gson.toJson(queue, queueType);
        assertTrue(json.contains("a1"));
        assertTrue(json.contains("a2"));
    }

    @Test
    public void testQueueDeserialization() {
        String json = "['a1','a2']";
        Type queueType = new TypeToken<Queue<String>>() {
        }.getType();
        Queue<String> queue = gson.fromJson(json, queueType);
        assertEquals("a1", queue.element());
        queue.remove();
        assertEquals("a2", queue.element());
    }

    @Test
    public void testPriorityQueue() {
        Type type = new TypeToken<PriorityQueue<Integer>>() {
        }.getType();
        PriorityQueue<Integer> queue = gson.fromJson("[10, 20, 22]", type);
        assertEquals(3, queue.size());
        String json = gson.toJson(queue);
        assertEquals(10, queue.remove().intValue());
        assertEquals(20, queue.remove().intValue());
        assertEquals(22, queue.remove().intValue());
        assertEquals("[10,20,22]", json);
    }

    @Test
    public void testVector() {
        Type type = new TypeToken<Vector<Integer>>() {
        }.getType();
        Vector<Integer> target = gson.fromJson("[10, 20, 31]", type);
        assertEquals(3, target.size());
        assertEquals(10, target.get(0).intValue());
        assertEquals(20, target.get(1).intValue());
        assertEquals(31, target.get(2).intValue());
        String json = gson.toJson(target);
        assertEquals("[10,20,31]", json);
    }

    @Test
    public void testStack() {
        Type type = new TypeToken<Stack<Integer>>() {
        }.getType();
        Stack<Integer> target = gson.fromJson("[11, 13, 17]", type);
        assertEquals(3, target.size());
        String json = gson.toJson(target);
        assertEquals(17, target.pop().intValue());
        assertEquals(13, target.pop().intValue());
        assertEquals(11, target.pop().intValue());
        assertEquals("[11,13,17]", json);
    }

    @Test
    public void testNullsInListSerialization() {
        List<String> list = new ArrayList<>();
        list.add("foo");
        list.add(null);
        list.add("bar");
        String expected = "[\"foo\",null,\"bar\"]";
        Type typeOfList = new TypeToken<List<String>>() {
        }.getType();
        String json = gson.toJson(list, typeOfList);
        assertEquals(expected, json);
    }

    @Test
    public void testNullsInListDeserialization() {
        List<String> expected = new ArrayList<>();
        expected.add("foo");
        expected.add(null);
        expected.add("bar");
        String json = "[\"foo\",null,\"bar\"]";
        Type expectedType = new TypeToken<List<String>>() {
        }.getType();
        List<String> target = gson.fromJson(json, expectedType);
        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(expected.get(i), target.get(i));
        }
    }

    @Test
    public void testCollectionOfObjectSerialization() {
        List<Object> target = new ArrayList<>();
        target.add("Hello");
        target.add("World");
        assertEquals("[\"Hello\",\"World\"]", gson.toJson(target));

        Type type = new TypeToken<List<Object>>() {
        }.getType();
        assertEquals("[\"Hello\",\"World\"]", gson.toJson(target, type));
    }

    @Test
    public void testCollectionOfObjectWithNullSerialization() {
        List<Object> target = new ArrayList<>();
        target.add("Hello");
        target.add(null);
        target.add("World");
        assertEquals("[\"Hello\",null,\"World\"]", gson.toJson(target));

        Type type = new TypeToken<List<Object>>() {
        }.getType();
        assertEquals("[\"Hello\",null,\"World\"]", gson.toJson(target, type));
    }

    @Test
    public void testCollectionOfStringsSerialization() {
        List<String> target = new ArrayList<>();
        target.add("Hello");
        target.add("World");
        assertEquals("[\"Hello\",\"World\"]", gson.toJson(target));
    }

    @Test
    public void testCollectionOfBagOfPrimitivesSerialization() {
        List<BagOfPrimitives> target = new ArrayList<>();
        BagOfPrimitives objA = new BagOfPrimitives(3L, 1, true, "blah");
        BagOfPrimitives objB = new BagOfPrimitives(2L, 6, false, "blahB");
        target.add(objA);
        target.add(objB);

        String result = gson.toJson(target);
        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("]"));
        for (BagOfPrimitives obj : target) {
            assertTrue(result.contains(obj.getExpectedJson()));
        }
    }

    @Test
    public void testCollectionOfStringsDeserialization() {
        String json = "[\"Hello\",\"World\"]";
        Type collectionType = new TypeToken<Collection<String>>() {
        }.getType();
        Collection<String> target = gson.fromJson(json, collectionType);

        assertTrue(target.contains("Hello"));
        assertTrue(target.contains("World"));
    }

    @Test
    public void testRawCollectionOfIntegersSerialization() {
        Collection<Integer> target = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals("[1,2,3,4,5,6,7,8,9]", gson.toJson(target));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testRawCollectionSerialization() {
        BagOfPrimitives bag1 = new BagOfPrimitives();
        Collection target = Arrays.asList(bag1, bag1);
        String json = gson.toJson(target);
        assertTrue(json.contains(bag1.getExpectedJson()));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testRawCollectionDeserializationNotAllowed() {
        String json = "[0,1,2,3,4,5,6,7,8,9]";
        Collection integers = gson.fromJson(json, Collection.class);
        // JsonReader converts numbers to double by default, so we need a floating point comparison
        assertEquals(Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L), integers);

        json = "[\"Hello\", \"World\"]";
        Collection strings = gson.fromJson(json, Collection.class);
        assertTrue(strings.contains("Hello"));
        assertTrue(strings.contains("World"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testRawCollectionOfBagOfPrimitivesNotAllowed() {
        BagOfPrimitives bag = new BagOfPrimitives(10, 20, false, "stringValue");
        String json = '[' + bag.getExpectedJson() + ',' + bag.getExpectedJson() + ']';
        Collection target = gson.fromJson(json, Collection.class);
        assertEquals(2, target.size());
        for (Object bag1 : target) {
            // Gson 2.0 converts raw objects into maps
            Map<String, Object> values = (Map<String, Object>) bag1;
            assertTrue(values.containsValue(10L));
            assertTrue(values.containsValue(20L));
            assertTrue(values.containsValue("stringValue"));
        }
    }

    @Test
    public void testWildcardPrimitiveCollectionSerialization() {
        Collection<? extends Integer> target = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Type collectionType = new TypeToken<Collection<? extends Integer>>() {
        }.getType();
        String json = gson.toJson(target, collectionType);
        assertEquals("[1,2,3,4,5,6,7,8,9]", json);

        json = gson.toJson(target);
        assertEquals("[1,2,3,4,5,6,7,8,9]", json);
    }

    @Test
    public void testWildcardPrimitiveCollectionDeserialization() {
        String json = "[1,2,3,4,5,6,7,8,9]";
        Type collectionType = new TypeToken<Collection<? extends Integer>>() {
        }.getType();
        Collection<? extends Integer> target = gson.fromJson(json, collectionType);
        assertEquals(9, target.size());
        assertTrue(target.contains(1));
        assertTrue(target.contains(9));
    }

    @Test
    public void testWildcardCollectionField() {
        Collection<BagOfPrimitives> collection = new ArrayList<>();
        BagOfPrimitives objA = new BagOfPrimitives(3L, 1, true, "blah");
        BagOfPrimitives objB = new BagOfPrimitives(2L, 6, false, "blahB");
        collection.add(objA);
        collection.add(objB);

        ObjectWithWildcardCollection target = new ObjectWithWildcardCollection(collection);
        String json = gson.toJson(target);
        assertTrue(json.contains(objA.getExpectedJson()));
        assertTrue(json.contains(objB.getExpectedJson()));

        target = gson.fromJson(json, ObjectWithWildcardCollection.class);
        Collection<? extends BagOfPrimitives> deserializedCollection = target.getCollection();
        assertEquals(2, deserializedCollection.size());
        assertTrue(deserializedCollection.contains(objA));
        assertTrue(deserializedCollection.contains(objB));
    }

    @Test
    public void testFieldIsArrayList() {
        HasArrayListField object = new HasArrayListField();
        object.longs.add(1L);
        object.longs.add(3L);
        String json = gson.toJson(object, HasArrayListField.class);
        assertEquals("{\"longs\":[1,3]}", json);
        HasArrayListField copy = gson.fromJson("{\"longs\":[1,3]}", HasArrayListField.class);
        assertEquals(Arrays.asList(1L, 3L), copy.longs);
    }

    @Test
    public void testUserCollectionTypeAdapter() {
        Type listOfString = new TypeToken<List<String>>() {
        }.getType();
        Object stringListSerializer = (JsonSerializer<List<String>>) (src, typeOfSrc, context) -> new JsonPrimitive(src.get(0) + ";" + src.get(1));
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(listOfString, stringListSerializer)
                .create();
        assertEquals("\"ab;cd\"", gson.toJson(Arrays.asList("ab", "cd"), listOfString));
    }

    static class HasArrayListField {
        ArrayList<Long> longs = new ArrayList<>();
    }

    @SuppressWarnings("rawtypes")
    private static int[] toIntArray(Collection collection) {
        int[] ints = new int[collection.size()];
        int i = 0;
        for (Iterator iterator = collection.iterator(); iterator.hasNext(); ++i) {
            Object obj = iterator.next();
            if (obj instanceof Integer) {
                ints[i] = (Integer) obj;
            } else if (obj instanceof Long) {
                ints[i] = ((Long) obj).intValue();
            }
        }
        return ints;
    }

    private static class ObjectWithWildcardCollection {
        private final Collection<? extends BagOfPrimitives> collection;

        public ObjectWithWildcardCollection(Collection<? extends BagOfPrimitives> collection) {
            this.collection = collection;
        }

        public Collection<? extends BagOfPrimitives> getCollection() {
            return collection;
        }
    }

    private static class Entry {
        int value;

        Entry(int value) {
            this.value = value;
        }
    }

    @Test
    public void testSetSerialization() {
        Set<Entry> set = new HashSet<>();
        set.add(new Entry(1));
        set.add(new Entry(2));
        String json = gson.toJson(set);
        assertTrue(json.contains("1"));
        assertTrue(json.contains("2"));
    }

    @Test
    public void testSetDeserialization() {
        String json = "[{value:1},{value:2}]";
        Type type = new TypeToken<Set<Entry>>() {
        }.getType();
        Set<Entry> set = gson.fromJson(json, type);
        assertEquals(2, set.size());
        for (Entry entry : set) {
            assertTrue(entry.value == 1 || entry.value == 2);
        }
    }

    private static class BigClass {
        private final Map<String, ? extends List<SmallClass>> inBig;

        private BigClass(Map<String, ? extends List<SmallClass>> inBig) {
            this.inBig = inBig;
        }
    }

    private static class SmallClass {
        private final String inSmall;

        private SmallClass(String inSmall) {
            this.inSmall = inSmall;
        }
    }

    @Test
    public void testIssue1107() {
        String json = "{\n" +
                "  \"inBig\": {\n" +
                "    \"key\": [\n" +
                "      { \"inSmall\": \"hello\" }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        BigClass bigClass = new Gson().fromJson(json, BigClass.class);
        SmallClass small = bigClass.inBig.get("key").get(0);
        assertNotNull(small);
        assertEquals("hello", small.inSmall);
    }

}
