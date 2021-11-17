package com.sample.nety.rpc.client;

import com.sample.nety.rpc.protocol.RpcRequest;
import com.sample.nety.rpc.protocol.RpcResponse;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author duyenthai
 */
@Slf4j
public class ClientHandler extends ChannelDuplexHandler {

    // Using Map to maintain the mapping relationship between request object id and response result Future
    private final Map<String, DefaultFuture> futureMap = new ConcurrentHashMap<>();

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof RpcRequest) {
            RpcRequest request = (RpcRequest) msg;
            // Before sending the object, save the request id and build a mapping relationship with a default future
            futureMap.putIfAbsent(request.getRequestId(), new DefaultFuture());
            log.info("Set a default future before write and get result later");
        }
        promise.addListener((future -> {
            log.trace("write {}", future.isSuccess() ? msg : "failed");

            log.info("write {}", future.isSuccess() ? msg : "failed");
        }));
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcResponse) {
                // Get response object
                RpcResponse response = (RpcResponse) msg;
                DefaultFuture defaultFuture = futureMap.get(response.getRequestId());
                // Write result to DefaultFuture
                defaultFuture.setRpcResponse(response);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    public RpcResponse getRpcResponse(String requestId) {
        try {
            DefaultFuture future = futureMap.get(requestId);
            return future.getRpcResponse(10);
        } finally {
            // After successfully acquisition, remove it from the map
            futureMap.remove(requestId);
        }
    }
}
