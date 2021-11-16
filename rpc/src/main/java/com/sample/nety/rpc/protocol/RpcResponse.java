package com.sample.nety.rpc.protocol;

import lombok.Data;

/**
 * @author duyenthai
 */
@Data
public class RpcResponse {
    private String requestId;
    private String error;
    private Object result;
}
