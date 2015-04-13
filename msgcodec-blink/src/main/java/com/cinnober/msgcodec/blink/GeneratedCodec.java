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

import com.cinnober.msgcodec.DecodeException;
import com.cinnober.msgcodec.GroupDef;
import com.cinnober.msgcodec.util.LimitInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.CodingErrorAction;
import javax.xml.ws.handler.MessageContext;

/**
 * Base class for a dynamically generated codec for a specific dictionary.
 * There is also a sub class which does not use dynamically generated byte code; {@link InstructionCodec}.
 * 
 * A GeneratedCodec sub class represents a dictionary.
 * A GeneratedCodec instance is tied to a specific {@link BlinkCodec} instance which holds the encode buffers
 * and any max binary size settings.
 * 
 * @author mikael brannstrom
 */
public abstract class GeneratedCodec { // PENDING: This should be package private

    /** Reference to the blink codec. */
    protected final BlinkCodec codec;

    /**
     * Constructor.
     * The constructor of the subclass should have the signature <code>(BlinkCodec, ProtocolDictionary)</code>.
     * 
     * @param codec the blink codec, not null.
     */
    public GeneratedCodec(BlinkCodec codec) {
        this.codec = codec;
    }
    
    /**
     * Write a static group and its group id.
     * Method to be generated in a sub class using <b>switch</b> construct
     * (similar what is generated for a switch on String) based on the group type.
     * 
     * @param out where to write to, not null
     * @param group the group to write, not null.
     * @throws IOException if the underlying stream throws an exception.
     * @throws IllegalArgumentException if an illegal value is encountered, e.g. missing required field value.
     */
    protected abstract void writeStaticGroupWithId(OutputStream out, Object group) 
            throws IOException, IllegalArgumentException;
    
    /**
     * Read a static group.
     * Method to be generated in a sub class using <b>switch</b> based on group id.
     * 
     * @param groupId the group id
     * @param in where to read from, not null.
     * @return the decoded group, not null.
     * @throws IOException if the underlying stream throws an exception.
     * @throws DecodeException if the group could not be decoded.
     */
    protected abstract Object readStaticGroup(int groupId, LimitInputStream in) throws IOException, DecodeException;

    /**
     * Write a dynamic group to the specified output stream.
     * @param out where to write to, not null.
     * @param group the group to encode, not null
     * @throws IOException if the underlying stream throws an exception.
     */
    public void writeDynamicGroup(OutputStream out, Object group) throws IOException, IllegalArgumentException {
        OutputStream out2 = codec.preambleBegin();
        writeStaticGroupWithId(out2, group);
        codec.preambleEnd(out);
    }

    /**
     * Write a nullable dynamic group to the specified output stream.
     * @param out where to write to, or null.
     * @param group the group to encode, not null
     * @throws IOException if the underlying stream throws an exception.
     */
    public void writeDynamicGroupNull(OutputStream out, Object group) throws IOException {
        if (group == null) {
            BlinkOutput.writeNull(out);
        } else {
            writeDynamicGroup(out, group);
        }
    }
    
    /** 
     * Read a dynamic group.
     * @param in the stream to read from.
     * @return the group, not null.
     * @throws IOException if the underlying stream throws an exception.
     */
    public Object readDynamicGroup(LimitInputStream in) throws IOException {
        int size = BlinkInput.readUInt32(in);
        return readDynamicGroup(size, in);
    }
    /** 
     * Read a nullable dynamic group.
     * @param in the stream to read from.
     * @return the group, or null.
     * @throws IOException if the underlying stream throws an exception.
     */
    public Object readDynamicGroupNull(LimitInputStream in) throws IOException {
        Integer sizeObj = BlinkInput.readUInt32Null(in);
        if (sizeObj == null) {
            return null;
        }
        int size = sizeObj.intValue();
        return readDynamicGroup(size, in);
    }

    private Object readDynamicGroup(int size, LimitInputStream in) throws IOException {
        int limit = in.limit();
        try {
            if (limit >= 0) {
                if (size > limit) {
                    // there is already a limit that is smaller than this message size
                    throw new DecodeException("Dynamic group size preamble (" + size +
                            ") goes beyond current stream limit (" + limit + ").");
                } else {
                    limit -= size;
                    in.limit(size);
                }
            }
            int groupId = BlinkInput.readUInt32(in);
            Object group;
            try {
                group = readStaticGroup(groupId, in);
            } catch (Exception e) {
                GroupDef groupDef = codec.getDictionary().getGroup(groupId);
                if (groupDef != null) {
                    throw new GroupDecodeException(groupDef.getName(), e);
                } else {
                    throw e;
                }
            }
            in.skip(in.limit());
            return group;
        } finally {
            in.limit(limit); // restore old limit
        }
    }

    // --- UTILITY METHODS FOR CREATING EXCEPTIONS ---

    /**
     * Create an encode exception when a required value is missing.
     * 
     * @param valueName the name of the value, e.g. a field name, not null.
     * @return the exception to be thrown.
     */
    protected static IllegalArgumentException missingRequiredValue(String valueName) {
        return new IllegalArgumentException(valueName + ": Missing required value");
    }

    /**
     * Create a decode exception when an unmappable enum symbol id is read.
     * This means that for a given valid symbol id, there is no Java enum value.
     *
     * @param valueName the name of the value, e.g. a field name, not null.
     * @param symbolId the symbol id
     * @param enumClass the enum class, not null
     * @return the exception to be thrown.
     */
    protected static DecodeException unmappableEnumSymbolId(String valueName, int symbolId, Class<? extends Enum> enumClass) {
        return new DecodeException(valueName + ": Cannot map symbol id " + symbolId +
                " to enum value of type " + enumClass);
    }

    /**
     * Create a decode exception when an unknown enum symbol id is read.
     * @param valueName the name of the value, e.g. a field name, not null.
     * @param symbolId the symbol id
     * @return the exception to be thrown.
     */
    protected static DecodeException unknownEnumSymbol(String valueName, int symbolId) {
        return new DecodeException(valueName + ": Unknown enum symbol id " + symbolId);
    }

    /**
     * Create an encode exception when an unmappable enum value is found.
     * This means that for a given Java enum value, there is no mapping to a symbol id.
     * 
     * @param <E> the enum type
     * @param valueName the name of the value, e.g. a field name, not null.
     * @param enumValue the unmappable unum value, not null
     * @return the exception to be thrown.
     */
    protected static <E extends Enum<E>> IllegalArgumentException unmappableEnumSymbolValue(String valueName, E enumValue) {
        return new IllegalArgumentException(valueName + ": Cannot map enum value to symbol " + enumValue);
    }

    /**
     * Create an encode exception when trying to encode a group for an unknown group type.
     * This mean that the group is not present in the dictionary.
     * 
     * @param groupType the group type (e.g. a java class) of the group to be encoded, not null.
     * @return the exception to be thrown.
     */
    protected static IllegalArgumentException unknownGroupType(Object groupType) {
        return new IllegalArgumentException("Unknown group type: " + groupType);
    }

    /**
     * Create a decode exception when an unknown group id is read.
     *
     * @param groupId the group id.
     * @return the exception to be thrown.
     */
    protected static DecodeException unknownGroupId(int groupId) {
        return new DecodeException("Unknown group id: " + groupId);
    }
}
