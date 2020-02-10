/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.rewrite.tree.visitor.refactor.op

import com.netflix.rewrite.assertRefactored
import com.netflix.rewrite.parse.OpenJdkParser
import com.netflix.rewrite.parse.Parser
import org.junit.Test

open class ChangeMethodTargetToVariableTest : Parser by OpenJdkParser() {

    @Test
    fun refactorExplicitStaticToVariable() {
        val a = """
            package a;
            public class A {
               public void foo() {}
            }
        """.trimIndent()

        val b = """
            package b;
            public class B {
               public static void foo() {}
            }
        """.trimIndent()

        val c = """
            import a.*;
            import b.B;
            public class C {
               A a;
               public void test() {
                   B.foo();
               }
            }
        """.trimIndent()

        val cu = parse(c, a, b)
        val fixed = cu.refactor().apply {
            val f = cu.classes[0].findFields("a.A")[0]
            changeMethodTarget(cu.findMethodCalls("b.B foo()"), f.vars[0])
        }.fix()

        assertRefactored(fixed, """
            import a.*;
            public class C {
               A a;
               public void test() {
                   a.foo();
               }
            }
        """)
    }

    @Test
    fun refactorStaticImportToVariable() {
        val a = """
            package a;
            public class A {
               public void foo() {}
            }
        """.trimIndent()

        val b = """
            package b;
            public class B {
               public static void foo() {}
            }
        """.trimIndent()

        val c = """
            import a.*;
            import static b.B.*;
            public class C {
               A a;
               public void test() {
                   foo();
               }
            }
        """.trimIndent()

        val cu = parse(c, a, b)

        val fixed = cu.refactor().apply {
            val f = cu.classes[0].findFields("a.A")[0]
            changeMethodTarget(cu.findMethodCalls("b.B foo()"), f.vars[0])
        }.fix()

        assertRefactored(fixed, """
            import a.*;
            public class C {
               A a;
               public void test() {
                   a.foo();
               }
            }
        """)
    }
}