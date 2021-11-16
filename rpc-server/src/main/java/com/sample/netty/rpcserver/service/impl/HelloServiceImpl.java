package com.sample.netty.rpcserver.service.impl;

import com.sample.netty.rpcserver.service.HelloService;
import org.springframework.stereotype.Service;

/**
 * @author duyenthai
 */
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return String.format("hello %s", name);
    }
}
