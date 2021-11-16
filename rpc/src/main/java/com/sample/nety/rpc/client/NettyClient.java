package com.sample.nety.rpc.client;

import com.sample.nety.rpc.codec.RpcDecoder;
import com.sample.nety.rpc.codec.RpcEncoder;
import com.sample.nety.rpc.protocol.RpcRequest;
import com.sample.nety.rpc.protocol.RpcResponse;
import com.sample.nety.rpc.serializer.JSONSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;

/**
 * @author duyenthai
 */
@Slf4j
public class NettyClient {
    private EventLoopGroup eventLoopGroup;
    private Channel channel;
    private ClientHandler clientHandler;
    private String host;
    private Integer port;

    public NettyClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws InterruptedException {
        clientHandler = new ClientHandler();
        eventLoopGroup = new NioEventLoopGroup();
        // Startup class
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                // Specifies the Channel used by the transport
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // codec
                        pipeline.addLast(new RpcEncoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(new RpcDecoder(RpcResponse.class, new JSONSerializer()));
                        // request processing class
                        pipeline.addLast(clientHandler);
                    }
                });
        channel = bootstrap.connect(host, port).sync().channel();
    }

    public RpcResponse send(final RpcRequest request) {
        try {
            log.info("Sending request {} to channel {}", request, channel);
            channel.writeAndFlush(request).await();
        } catch (Exception ex) {
            log.error("Error send rpc request, ", ex);
        }
        return clientHandler.getRpcResponse(request.getRequestId());
    }

    @PreDestroy
    public void close() {
        eventLoopGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
    }

}
