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

package com.cinnober.msgcodec.blink;

import com.cinnober.msgcodec.Schema;
import java.util.WeakHashMap;

/**
 *
 * @author mikael.brannstrom
 */
class GeneratedCodecClassLoader extends ClassLoader {
    private static final GeneratedCodecClassLoader instance = new GeneratedCodecClassLoader();

    public static GeneratedCodecClassLoader getInstance() {
        return instance;
    }

    private final CompactByteCodeGenerator compactCodeGenerator;
    private final NativeByteCodeGenerator nativeCodeGenerator;
    private final WeakHashMap<Object, Class<GeneratedCompactCodec>> codecClassesBySchemaUID = new WeakHashMap<>();
    private final WeakHashMap<Object, Class<GeneratedNativeCodec>> nativeCodecClassesBySchemaUID = new WeakHashMap<>();
    private int nextClassSuffix = 0;
    
    private GeneratedCodecClassLoader() {
        super(GeneratedCodecClassLoader.class.getClassLoader());
        compactCodeGenerator = new CompactByteCodeGenerator();
        nativeCodeGenerator = new NativeByteCodeGenerator();
    }

    public Class<GeneratedCompactCodec> getGeneratedCodecClass(Schema schema) {
        synchronized (this) {
            final Object uid = schema.getUID();
            Class<GeneratedCompactCodec> codecClass = codecClassesBySchemaUID.get(uid);
            if (codecClass == null && !codecClassesBySchemaUID.containsKey(uid)) {
                codecClass = generateCodecClass(schema, nextClassSuffix++);
                codecClassesBySchemaUID.put(uid, codecClass);
            }
            return codecClass;
        }
    }

    public Class<GeneratedNativeCodec> getGeneratedNativeCodecClass(Schema schema) {
        synchronized (this) {
            final Object uid = schema.getUID();
            Class<GeneratedNativeCodec> codecClass = nativeCodecClassesBySchemaUID.get(uid);
            if (codecClass == null && !nativeCodecClassesBySchemaUID.containsKey(uid)) {
                codecClass = generateNativeCodecClass(schema, nextClassSuffix++);
                nativeCodecClassesBySchemaUID.put(uid, codecClass);
            }
            return codecClass;
        }
    }

    @SuppressWarnings("unchecked")
    private Class<GeneratedCompactCodec> generateCodecClass(Schema schema, int suffix) {
        String generatedClassName = compactCodeGenerator.getGeneratedClassName(suffix);
        byte[] generatedClassBytes = compactCodeGenerator.generateClass(schema, suffix);
        Class<?> generatedClass = defineClass(generatedClassName, generatedClassBytes, 0, generatedClassBytes.length);
        return (Class<GeneratedCompactCodec>) generatedClass;
    }

    @SuppressWarnings("unchecked")
    private Class<GeneratedNativeCodec> generateNativeCodecClass(Schema schema, int suffix) {
        String generatedClassName = nativeCodeGenerator.getGeneratedClassName(suffix);
        byte[] generatedClassBytes = nativeCodeGenerator.generateClass(schema, suffix);
        Class<?> generatedClass = defineClass(generatedClassName, generatedClassBytes, 0, generatedClassBytes.length);
        return (Class<GeneratedNativeCodec>) generatedClass;
    }
    
}
