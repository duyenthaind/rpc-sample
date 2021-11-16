package com.sample.netty.rpcserver;

import com.sample.nety.rpc.server.NettyServer;
import com.sample.nety.rpc.server.ServerHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class RpcServerApplication {

    @Value("${netty.rpc.server.port}")
    private Integer port;

    public static void main(String[] args) {
        SpringApplication.run(RpcServerApplication.class, args);
        log.info("Rpc started successfully");
    }

    @Bean
    public NettyServer nettyServer() {
        return new NettyServer(serverHandler(), port);
    }

    @Bean
    public ServerHandler serverHandler() {
        return new ServerHandler();
    }

}
