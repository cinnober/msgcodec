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
package com.cinnober.msgcodec.messages;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.cinnober.msgcodec.Schema;

/**
 * @author mikael.brannstrom
 *
 */
public class MetaProtocolTest {

    /** Test that the schema can be built (does not throw any exceptions). */
    @Test
    public void testSchema() {
        Schema schema = MetaProtocol.getSchema();
        String schemaString = schema.toString();
        System.out.println(schemaString);

        // convert to meta messages and back
        MetaSchema metaSchema = schema.toMessage();
        Schema schema2 = metaSchema.toSchema();
        String schemaString2 = schema2.toString();
        System.out.println(schemaString2);

        assertEquals(schemaString, schemaString2);
    }

}
