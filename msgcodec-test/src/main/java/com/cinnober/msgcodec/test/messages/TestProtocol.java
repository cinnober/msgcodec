/*
 * Copyright (c) 2013 Cinnober Financial Technology AB, Stockholm,
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
package com.cinnober.msgcodec.test.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import com.cinnober.msgcodec.ProtocolDictionary;
import com.cinnober.msgcodec.ProtocolDictionaryBuilder;
import com.cinnober.msgcodec.StreamCodec;

/**
 * @author mikael.brannstrom
 *
 */
public class TestProtocol {
    public static ProtocolDictionary getProtocolDictionary() {
        return new ProtocolDictionaryBuilder().build(
            new Class<?>[] {
                Hello.class,
                Person.class,
                Employee.class,
                DatesMessage.class,
                DecimalsMessage.class,
                EnumsMessage.class,
                FloatsMessage.class,
                IntegersMessage.class,
                MiscMessage.class,
                SequencesMessage.class,
                StringsMessage.class,
            });
    }


    /**
     * Returns messages suitable for testing a codec. This includes border cases.
     * Each message is labeled with a name, e.g. "Dates.zero" or "Decimals.border1" that describes what
     * the message tries to test.
     *
     * All messages are encodable, i.e. any required fields are set.
     *
     * @return a map from message label to message.
     */
    public static Map<String, Object> createMessages() {
        Map<String, Object> messages = new LinkedHashMap<>();

        putAll(messages, "Dates.", DatesMessage.createMessages());
        putAll(messages, "Decimals.", DecimalsMessage.createMessages());

        return messages;
    }

    private static void putAll(Map<String, Object> result, String prefix, Map<String, ? extends Object> messages) {
        for (Map.Entry<String, ? extends Object> entry : messages.entrySet()) {
            result.put(prefix + entry.getKey(), entry.getValue());
        }
    }
}