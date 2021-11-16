package com.sample.nety.rpc.client;

import com.sample.nety.rpc.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author duyenthai
 */
@Slf4j
public class DefaultFuture {
    private RpcResponse rpcResponse;
    private volatile boolean isSucceed = false;
    private final Object object = new Object();

    public RpcResponse getRpcResponse(int timeout) {
        synchronized (object) {
            while (!isSucceed) {
                try {
                    object.wait(timeout);
                } catch (InterruptedException ex) {
                    log.error("Error wait timeout", ex);
                }
            }
            return rpcResponse;
        }
    }

    public void setRpcResponse(RpcResponse response) {
        if (isSucceed) {
            return;
        }
        synchronized (object) {
            this.rpcResponse = response;
            this.isSucceed = true;
            object.notify();
        }
    }
}
