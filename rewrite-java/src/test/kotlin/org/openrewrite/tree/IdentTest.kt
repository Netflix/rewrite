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
package org.openrewrite.tree

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrewrite.Parser
import org.openrewrite.asClass
import org.openrewrite.fields

open class IdentTest : Parser() {
    
    @Test
    fun referToField() {
        val a = parse("""
            public class A {
                Integer n = 0;
                Integer m = n;
            }
        """)
        
        val ident = a.fields(1..1)[0].vars[0].initializer as J.Ident
        assertEquals("n", ident.simpleName)
        assertEquals("java.lang.Integer", ident.type.asClass()?.fullyQualifiedName)
    }
}