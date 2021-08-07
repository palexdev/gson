/*
 * Copyright (C) 2018 The Gson authors
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
package com.google.gson.internal.reflect;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.security.Permission;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UnsafeReflectionAccessor}
 *
 * @author Inderjeet Singh
 */
public class UnsafeReflectionAccessorTest {

  @Test
  public void testMakeAccessibleWithUnsafe() throws Exception {
    UnsafeReflectionAccessor accessor = new UnsafeReflectionAccessor();
    Field field = ClassWithPrivateFinalFields.class.getDeclaredField("a");
    try {
      boolean success = accessor.makeAccessibleWithUnsafe(field);
      assertTrue(success);
    } catch (Exception e) {
      fail("Unsafe didn't work on the JDK");
    }
  }

  @Test
  public void testMakeAccessibleWithRestrictiveSecurityManager() throws Exception {
    final Permission accessDeclaredMembers = new RuntimePermission("accessDeclaredMembers");
    final SecurityManager original = System.getSecurityManager();
    SecurityManager restrictiveManager = new SecurityManager() {
      @Override
      public void checkPermission(Permission perm) {
        if (accessDeclaredMembers.equals(perm)) {
          throw new SecurityException("nope");
        }
      }
    };
    System.setSecurityManager(restrictiveManager);

    try {
      UnsafeReflectionAccessor accessor = new UnsafeReflectionAccessor();
      Field field = ClassWithPrivateFinalFields.class.getDeclaredField("a");
      assertFalse(accessor.makeAccessibleWithUnsafe(field), "override field should have been inaccessible");
      accessor.makeAccessible(field);
    } finally {
      System.setSecurityManager(original);
    }
  }

  @SuppressWarnings("unused")
  private static final class ClassWithPrivateFinalFields {
    private final String a;
    public ClassWithPrivateFinalFields(String a) {
      this.a = a;
    }
  }
}
