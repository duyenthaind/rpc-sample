package com.sample.nety.rpc.proxy;

import com.sample.nety.rpc.client.NettyClient;
import com.sample.nety.rpc.protocol.RpcRequest;
import com.sample.nety.rpc.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.UUID;

/**
 * @author duyenthai
 */
@Slf4j
public class RpcClientDynamicProxy<T> implements InvocationHandler {

    private Class<T> interfaceClass;

    private String host;
    private Integer port;

    public RpcClientDynamicProxy(Class<T> interfaceClass, String host, Integer port) {
        this.interfaceClass = interfaceClass;
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        RpcRequest request = new RpcRequest();
        String requestId = UUID.randomUUID().toString();

        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();

        Class<?>[] parameterTypes = method.getParameterTypes();

        request.setRequestId(requestId);
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameterTypes(parameterTypes);
        request.setParameters(objects);
        log.info("Request content: {}", request);

        //Open the Netty client and connect directly
        //Here, the host and port of the server are specified directly, and the normal RPC framework will get it from the registry
        NettyClient nettyClient = new NettyClient(host, port);
        log.info("Start connecting to the server:{}", new Date());
        nettyClient.connect();
        RpcResponse send = nettyClient.send(request);
        log.info("Return result of request call:{}", send.getResult());
        return send.getResult();
    }

    @SuppressWarnings("unchecked")
    public T getProxy() {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, this);
    }
}
