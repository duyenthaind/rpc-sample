package com.sample.nety.rpc.protocol;

import lombok.Data;
import lombok.ToString;

/**
 * @author duyenthai
 */
@Data
@ToString
public class RpcRequest {
    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
