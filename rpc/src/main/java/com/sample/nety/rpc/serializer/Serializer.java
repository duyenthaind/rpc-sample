package com.sample.nety.rpc.serializer;

import java.io.IOException;

/**
 * @author duyenthai
 */
public interface Serializer {
    byte[] serialize(Object object) throws IOException;

    <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException;
}
