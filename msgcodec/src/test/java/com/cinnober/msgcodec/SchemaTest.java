/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 The MsgCodec Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cinnober.msgcodec;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Collection;

/**
 *
 * @author mikael.brannstrom
 */
public class SchemaTest {

    public SchemaTest() {
    }

    @Test
    public void testEqualsAndHashCode() {
        Schema schema1 = new SchemaBuilder().build(FooMessage.class, BarMessage.class);
        Schema schema2 = new SchemaBuilder().build(FooMessage.class, BarMessage.class);

        assertEquals(schema1, schema2);
        assertEquals(schema1.hashCode(), schema2.hashCode());
        assertNotEquals(schema1.getUID(), schema2.getUID());
    }
    
    @Test
    //This tests the bug where Schema.getDynamicGroups only returned the direct descendants of 
    //the group and not all descendants. Before the bug fix Bar2Message wasn't not be included. 
    public void testMultipleInheritance () {
        Schema schema = new SchemaBuilder().build(FooMessage.class, BarMessage.class, Bar2Message.class);

        Collection<GroupDef> groups = schema.getDynamicGroups("FooMessage");
        for (GroupDef g : groups) {
        	System.out.println(g.getName());
        }
        assertEquals(3, groups.size());
    }
}
