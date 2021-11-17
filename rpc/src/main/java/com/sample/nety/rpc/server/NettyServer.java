package com.sample.nety.rpc.server;

import com.sample.nety.rpc.codec.RpcDecoder;
import com.sample.nety.rpc.codec.RpcEncoder;
import com.sample.nety.rpc.protocol.RpcRequest;
import com.sample.nety.rpc.protocol.RpcResponse;
import com.sample.nety.rpc.serializer.JSONSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PreDestroy;
import javax.imageio.spi.ServiceRegistry;
import java.util.Objects;

/**
 * @author duyenthai
 */
@Slf4j
public class NettyServer implements InitializingBean {

    private ServerHandler serverHandler;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private Integer serverPort;

    public NettyServer(ServerHandler serverHandler, Integer serverPort) {
        this.serverHandler = serverHandler;
        this.serverPort = serverPort;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // none registry is set
        ServiceRegistry registry = null;
        if (Objects.nonNull(serverPort)) {
            start(registry);
        }
    }

    public void start(ServiceRegistry registry) throws InterruptedException {
        // Thread pool responsible for handling client connections
        bossGroup = new NioEventLoopGroup();
        // Thread pool responsible for processing read and write operations
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // codec
                        pipeline.addLast(new RpcDecoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(new RpcEncoder(RpcResponse.class, new JSONSerializer()));
                        // request processor
                        pipeline.addLast(serverHandler);
                    }
                });
        bind(serverBootstrap, serverPort);
    }

    /**
     * If port binding fails, port number + 1, rebind
     *
     * @param serverBootstrap
     * @param serverPort
     */
    public void bind(final ServerBootstrap serverBootstrap, int serverPort) throws InterruptedException {
        serverBootstrap.bind(serverPort).addListener(future -> {
            if (future.isSuccess()) {
                log.info("port [{}] Binding success", serverPort);
            } else {
                log.error("port [{}] Binding failed", serverPort);
                bind(serverBootstrap, serverPort + 1);
            }
        }).sync();
    }

    @PreDestroy
    public void close() throws InterruptedException {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        log.info("Closing application RPC Netty server");
    }
}
