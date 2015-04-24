/*
 * Copyright (c) 2015 Cinnober Financial Technology AB, Stockholm,
 * Sweden. All rights reserved.
 * 
 * This software is the confidential and proprietary information of
 * Cinnober Financial Technology AB, Stockholm, Sweden. You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Cinnober.
 * 
 * Cinnober makes no representations or warranties about the suitability
 * of the software, either expressed or implied, including, but not limited
 * to, the implied warranties of merchantibility, fitness for a particular
 * purpose, or non-infringement. Cinnober shall not be liable for any
 * damages suffered by licensee as a result of using, modifying, or
 * distributing this software or its derivatives.
 */

package com.cinnober.msgcodec.io;

import java.io.IOException;

/**
 * TODO: javadoc
 * TODO: limit checks
 * @author mikael.brannstrom
 */
public class ByteArrayBuf implements ByteBuf {

    private final byte[] data;
    private int pos;
    private int limit;

    public ByteArrayBuf(byte[] data) {
        this.data = data;
    }

    public byte[] array() {
        return data;
    }

    @Override
    public int capacity() {
        return data.length;
    }

    @Override
    public int position() {
        return pos;
    }
    @Override
    public ByteArrayBuf position(int position) {
        this.pos = position;
        return this;
    }
    @Override
    public int limit() {
        return limit;
    }

    @Override
    public ByteArrayBuf limit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public ByteArrayBuf clear() {
        pos = 0;
        limit = 0;
        return this;
    }

    @Override
    public ByteArrayBuf flip() {
        limit = pos;
        pos = 0;
        return this;
    }

    @Override
    public int read() throws IOException {
        return 0xff & data[pos++];
    }

    @Override
    public void read(byte[] b, int off, int len) throws IOException {
        System.arraycopy(data, pos, b, off, len);
        pos += len;
    }

    @Override
    public String readStringUtf8(int len) throws IOException {
        if (len < 128) {
            boolean ascii = true;
            int end = pos+len;
            for (int i=pos; i<end; i++) {
                if(data[i] >= 0x80) {
                    ascii = false;
                    break;
                }
            }
            if (ascii) {
                char[] chars = new char[len];
                for (int i=0; i<len; i++) {
                    chars[i] = (char) data[pos+i];
                }
                pos += len;
                return new String(chars);
            }
        }


        String s = new String(data, pos, pos+len, UTF8);
        pos += len;
        return s;
    }

    @Override
    public void write(int b) throws IOException {
        data[pos++] = (byte) b;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        System.arraycopy(b, off, data, pos, len);
        pos += len;
    }

    @Override
    public void shift(int position, int length, int distance) {
        System.arraycopy(data, position, data, position+distance, length);
    }

    public void copyTo(ByteSink out) throws IOException {
        out.write(data, pos, limit-pos);
    }

}
