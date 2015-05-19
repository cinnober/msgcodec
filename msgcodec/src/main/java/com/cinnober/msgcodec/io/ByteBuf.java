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

/**
 * TODO: javadoc
 * @author mikael.brannstrom
 */
public interface ByteBuf extends ByteSource, ByteSink {
    int position();
    ByteBuf position(int position);
    int limit();
    int capacity();
    ByteBuf limit(int limit);
    ByteBuf clear();
    ByteBuf flip();
    void shift(int position, int length, int distance);
}