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

package com.cinnober.msgcodec.xml;

import com.cinnober.msgcodec.MsgCodecInstantiationException;
import com.cinnober.msgcodec.Schema;
import com.cinnober.msgcodec.MsgCodecFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Factory for XmlCodec.
 * 
 * @author mikael.brannstrom
 */
public class XmlCodecFactory implements MsgCodecFactory {

    private final Schema schema;

    /**
     * Create an XML codec factory.
     * 
     * @param schema the schema to be used by all codec instances, not null.
     */
    public XmlCodecFactory(Schema schema) {
        if (!schema.isBound()) {
            throw new IllegalArgumentException("Schema must be bound");
        }
        this.schema = schema;
    }

    @Override
    public XmlCodec createCodec() throws MsgCodecInstantiationException {
        try {
            return new XmlCodec(schema);
        } catch (ParserConfigurationException | SAXException e) {
            throw new MsgCodecInstantiationException("Could not create codec", e);
        }
    }
    
}
