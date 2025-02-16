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

package com.google.gson.functional;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public final class StreamingTypeAdaptersTest {
  private Gson miniGson = new GsonBuilder().create();
  private TypeAdapter<Truck> truckAdapter = miniGson.getAdapter(Truck.class);
  private final TypeAdapter<Map<String, Double>> mapAdapter
      = miniGson.getAdapter(new TypeToken<>() {
  });


  @Test
  public void testSerialize() {
    Truck truck = new Truck();
    truck.passengers = Arrays.asList(new Person("Jesse", 29), new Person("Jodie", 29));
    truck.horsePower = 300;

    assertEquals("{'horsePower':300.0,"
        + "'passengers':[{'age':29,'name':'Jesse'},{'age':29,'name':'Jodie'}]}",
        truckAdapter.toJson(truck).replace('\"', '\''));
  }

  @Test
  public void testDeserialize() throws IOException {
    String json = "{'horsePower':300.0,"
        + "'passengers':[{'age':29,'name':'Jesse'},{'age':29,'name':'Jodie'}]}";
    Truck truck = truckAdapter.fromJson(json.replace('\'', '\"'));
    assertEquals(300.0, truck.horsePower);
    assertEquals(Arrays.asList(new Person("Jesse", 29), new Person("Jodie", 29)), truck.passengers);
  }

  @Test
  public void testSerializeNullField() {
    Truck truck = new Truck();
    truck.passengers = null;
    assertEquals("{'horsePower':0.0,'passengers':null}",
        truckAdapter.toJson(truck).replace('\"', '\''));
  }

  @Test
  public void testDeserializeNullField() throws IOException {
    Truck truck = truckAdapter.fromJson("{'horsePower':0.0,'passengers':null}".replace('\'', '\"'));
    assertNull(truck.passengers);
  }

  @Test
  public void testSerializeNullObject() {
    Truck truck = new Truck();
    truck.passengers = List.of((Person) null);
    assertEquals("{'horsePower':0.0,'passengers':[null]}",
        truckAdapter.toJson(truck).replace('\"', '\''));
  }

  @Test
  public void testDeserializeNullObject() throws IOException {
    Truck truck = truckAdapter.fromJson("{'horsePower':0.0,'passengers':[null]}".replace('\'', '\"'));
    assertEquals(List.of((Person) null), truck.passengers);
  }

  @Test
  public void testSerializeWithCustomTypeAdapter() {
    usePersonNameAdapter();
    Truck truck = new Truck();
    truck.passengers = Arrays.asList(new Person("Jesse", 29), new Person("Jodie", 29));
    assertEquals("{'horsePower':0.0,'passengers':['Jesse','Jodie']}",
        truckAdapter.toJson(truck).replace('\"', '\''));
  }

  @Test
  public void testDeserializeWithCustomTypeAdapter() throws IOException {
    usePersonNameAdapter();
    Truck truck = truckAdapter.fromJson("{'horsePower':0.0,'passengers':['Jesse','Jodie']}".replace('\'', '\"'));
    assertEquals(Arrays.asList(new Person("Jesse", -1), new Person("Jodie", -1)), truck.passengers);
  }

  private void usePersonNameAdapter() {
    TypeAdapter<Person> personNameAdapter = new TypeAdapter<>() {
      @Override
      public Person read(JsonReader in) throws IOException {
        String name = in.nextString();
        return new Person(name, -1);
      }

      @Override
      public void write(JsonWriter out, Person value) throws IOException {
        out.value(value.name);
      }
    };
    miniGson = new GsonBuilder().registerTypeAdapter(Person.class, personNameAdapter).create();
    truckAdapter = miniGson.getAdapter(Truck.class);
  }

  @Test
  public void testSerializeMap() {
    Map<String, Double> map = new LinkedHashMap<>();
    map.put("a", 5.0);
    map.put("b", 10.0);
    assertEquals("{'a':5.0,'b':10.0}", mapAdapter.toJson(map).replace('"', '\''));
  }

  @Test
  public void testDeserializeMap() throws IOException {
    Map<String, Double> map = new LinkedHashMap<>();
    map.put("a", 5.0);
    map.put("b", 10.0);
    assertEquals(map, mapAdapter.fromJson("{'a':5.0,'b':10.0}".replace('\'', '\"')));
  }

  @Test
  public void testSerialize1dArray() {
    TypeAdapter<double[]> arrayAdapter = miniGson.getAdapter(new TypeToken<>() {
    });
    assertEquals("[1.0,2.0,3.0]", arrayAdapter.toJson(new double[]{ 1.0, 2.0, 3.0 }));
  }

  @Test
  public void testDeserialize1dArray() throws IOException {
    TypeAdapter<double[]> arrayAdapter = miniGson.getAdapter(new TypeToken<>() {
    });
    double[] array = arrayAdapter.fromJson("[1.0,2.0,3.0]");
    assertArrayEquals(new double[]{1.0, 2.0, 3.0}, array);
  }

  @Test
  public void testSerialize2dArray() {
    TypeAdapter<double[][]> arrayAdapter = miniGson.getAdapter(new TypeToken<>() {
    });
    double[][] array = { {1.0, 2.0 }, { 3.0 } };
    assertEquals("[[1.0,2.0],[3.0]]", arrayAdapter.toJson(array));
  }

  @Test
  public void testDeserialize2dArray() throws IOException {
    TypeAdapter<double[][]> arrayAdapter = miniGson.getAdapter(new TypeToken<>() {
    });
    double[][] array = arrayAdapter.fromJson("[[1.0,2.0],[3.0]]");
    double[][] expected = { {1.0, 2.0 }, { 3.0 } };
    assertTrue(Arrays.deepEquals(expected, array));
  }

  @Test
  public void testNullSafe() {
    TypeAdapter<Person> typeAdapter = new TypeAdapter<>() {
      @Override
      public Person read(JsonReader in) throws IOException {
        String[] values = in.nextString().split(",");
        return new Person(values[0], Integer.parseInt(values[1]));
      }

      public void write(JsonWriter out, Person person) throws IOException {
        out.value(person.name + "," + person.age);
      }
    };
    Gson gson = new GsonBuilder().registerTypeAdapter(
        Person.class, typeAdapter).create();
    Truck truck = new Truck();
    truck.horsePower = 1.0D;
    truck.passengers = new ArrayList<>();
    truck.passengers.add(null);
    truck.passengers.add(new Person("jesse", 30));
    try {
      gson.toJson(truck, Truck.class);
      fail();
    } catch (NullPointerException expected) {}
    String json = "{horsePower:1.0,passengers:[null,'jesse,30']}";
    try {
      gson.fromJson(json, Truck.class);
      fail();
    } catch (JsonSyntaxException expected) {}
    gson = new GsonBuilder().registerTypeAdapter(Person.class, typeAdapter.nullSafe()).create();
    assertEquals("{\"horsePower\":1.0,\"passengers\":[null,\"jesse,30\"]}",
        gson.toJson(truck, Truck.class));
    truck = gson.fromJson(json, Truck.class);
    assertEquals(1.0D, truck.horsePower);
    assertNull(truck.passengers.get(0));
    assertEquals("jesse", truck.passengers.get(1).name);
  }

  @Test
  public void testSerializeRecursive() {
    TypeAdapter<Node> nodeAdapter = miniGson.getAdapter(Node.class);
    Node root = new Node("root");
    root.left = new Node("left");
    root.right = new Node("right");
    assertEquals("{'label':'root',"
        + "'left':{'label':'left','left':null,'right':null},"
        + "'right':{'label':'right','left':null,'right':null}}",
        nodeAdapter.toJson(root).replace('"', '\''));
  }

  @Test
  public void testFromJsonTree() {
    JsonObject truckObject = new JsonObject();
    truckObject.add("horsePower", new JsonPrimitive(300));
    JsonArray passengersArray = new JsonArray();
    JsonObject jesseObject = new JsonObject();
    jesseObject.add("age", new JsonPrimitive(30));
    jesseObject.add("name", new JsonPrimitive("Jesse"));
    passengersArray.add(jesseObject);
    truckObject.add("passengers", passengersArray);

    Truck truck = truckAdapter.fromJsonTree(truckObject);
    assertEquals(300.0, truck.horsePower);
    assertEquals(List.of(new Person("Jesse", 30)), truck.passengers);
  }

  static class Truck {
    double horsePower;
    List<Person> passengers = Collections.emptyList();
  }

  static class Person {
    int age;
    String name;
    Person(String name, int age) {
      this.name = name;
      this.age = age;
    }

    @Override public boolean equals(Object o) {
      return o instanceof Person
          && ((Person) o).name.equals(name)
          && ((Person) o).age == age;
    }
    @Override public int hashCode() {
      return name.hashCode() ^ age;
    }
  }

  static class Node {
    String label;
    Node left;
    Node right;
    Node(String label) {
      this.label = label;
    }
  }
}
