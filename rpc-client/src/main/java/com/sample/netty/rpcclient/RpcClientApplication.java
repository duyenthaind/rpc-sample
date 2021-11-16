package com.sample.netty.rpcclient;

import com.sample.netty.rpcserver.service.HelloService;
import com.sample.nety.rpc.proxy.RpcClientDynamicProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class RpcClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(RpcClientApplication.class, args);
		//The server host and port are directly specified here
		HelloService helloService = new RpcClientDynamicProxy<>(HelloService.class, "127.0.0.1", 3663).getProxy();
		String result = helloService.hello("thaind");
		log.info("Response results“: {}", result);
	}

}
